import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.SecureRandom;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

public class HpsmClient {
	private final static String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) {
		System.out.println("!!! HPSM Client Started !!!");
		// Make sure cookies is turned on
		CookieHandler.setDefault(new CookieManager());

		try {
			HttpClient client = createHttpClient();
			// LOGIN TO HPSM
			String login_url = "https://hpsm.heb.com/hpsm/index.do";
			sendPostLoginRequestToHPSM(login_url, client, "username", "password");

			// LIST Tickets FROM HPSM
			String list_url = "https://hpsm.heb.com/hpsm/list.do?thread=0";
			String list_html_content = sendGetRequestToHPSM(list_url, client);

			// Parse HPSM HTML Content & Retrieve IM Details
			Document list_html_content_doc = Jsoup.parse(list_html_content);
			String hpsm_script_ticket_data = parseAndFetchHPSMScriptData(list_html_content_doc);
			String hpsm_parsed_script_ticket_data = parseAndFetcchJsonPortion(hpsm_script_ticket_data);
			//System.out.println("Fetched JSON Portion:" + hpsm_parsed_script_ticket_data);
			// JSON to Entity
			HPSMTicket[] hpsm_ticket_obj_list = parseAndGetHPSMTicketList(hpsm_parsed_script_ticket_data);
			// printHPSMTickets(hpsm_ticket_obj_list);

			String ticket_detail_url = "https://hpsm.heb.com/hpsm/wf?_dc=1538093431528&action=workflowApprovalPhases&workflowname=HEB%20Incident&tablename=probsummary&recordid=IM3464364&currentphase=Work%20In%20Progress";
			String ticket_detail_url_content = sendGetRequestToHPSM(ticket_detail_url, client);
			System.out.println("ticket_detail_url_content:"+ticket_detail_url_content);
			
			// LOGOUT FROM HPSM
			String logout_url = "https://hpsm.heb.com/hpsm/goodbye.jsp?lang=en";
			sendGetRequestToHPSM(logout_url, client);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("!!! HPSM Client Started !!!");
	}

	private static void printHPSMTickets(HPSMTicket[] hpsm_ticket_obj_list) {
		for (HPSMTicket hpsmticket : hpsm_ticket_obj_list) {
			System.out.println(hpsmticket.getRecordId() + " " + hpsmticket.getStatus());
		}
	}

	private static HPSMTicket[] parseAndGetHPSMTicketList(String hpsm_parsed_script_ticket_data) {
		Gson gson = new Gson();
		HPSMTicket[] hpsm_ticket_obj_list = gson.fromJson(hpsm_parsed_script_ticket_data, HPSMTicket[].class);
		return hpsm_ticket_obj_list;
	}

	private static String parseAndFetcchJsonPortion(String fullHtmlContentAsString) {
		String hpsm_parsed_script_ticket_data = "";
		String fullHtmlContentAsStringLastPortionIndicator = ",\"moreRecs\"";
		int startIndex = fullHtmlContentAsString.indexOf("[");
		int endIndex = fullHtmlContentAsString.lastIndexOf(fullHtmlContentAsStringLastPortionIndicator);
		//System.out.println("startIndex:" + startIndex + " endIndex:" + endIndex);
		hpsm_parsed_script_ticket_data = fullHtmlContentAsString.substring(startIndex, endIndex);
		return hpsm_parsed_script_ticket_data;
	}

	private static String parseAndFetchHPSMScriptData(Document list_html_content_doc) throws Exception {
		String script_data = "";
		Elements scripts = list_html_content_doc.getElementsByTag("script");
		int script_count = 1;
		for (Element script : scripts) {
			// Currently 34th script tag is having IM Details - need to modify
			// logic to identify script tag having listConfig js variable
			if (script_count == 34) {
				script_data = script.data();
				//System.out.println("Script" + script_count + ":" + script.data());
			}
			script_count++;
		}
		return script_data;
	}

	private static String sendGetRequestToHPSM(String url, HttpClient client) throws Exception {

		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", USER_AGENT);
		// Execute Get Request
		HttpResponse response = client.execute(request);

		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		System.out.println("GET Request For Url:" + url + " Result:" + result);
		return result.toString();
	}

	private static HttpClient createHttpClient() throws Exception {

		SSLContext sslContext = SSLContext.getInstance("SSL");

		// set up a TrustManager that trusts everything
		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				// System.out.println("getAcceptedIssuers =============");
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws java.security.cert.CertificateException {
				// System.out.println("checkClientTrusted =============");

			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws java.security.cert.CertificateException {
				// System.out.println("checkServerTrusted =============");

			}
		} }, new SecureRandom());

		SSLSocketFactory sf = new SSLSocketFactory(sslContext);
		Scheme httpsScheme = new Scheme("https", 443, sf);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpsScheme);

		// apache HttpClient version >4.2 should use
		// BasicClientConnectionManager
		ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
		HttpClient client = new DefaultHttpClient(cm);

		return client;
	}

	private static void sendPostLoginRequestToHPSM(String url, HttpClient client, String username, String pass)
			throws Exception {
		// HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("user.id", username));
		urlParameters.add(new BasicNameValuePair("type", "login"));
		urlParameters.add(new BasicNameValuePair("xHtoken", ""));
		urlParameters.add(new BasicNameValuePair("old.password", pass));
		urlParameters.add(new BasicNameValuePair("event", "0"));
		urlParameters.add(new BasicNameValuePair("L.language", "en"));
		urlParameters.add(new BasicNameValuePair("originalUrl", "https%3A%2F%2Fhpsm.heb.com%2Fhpsm%2Findex.do"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		// Execute HTTP-POST with context
		HttpResponse response = client.execute(post);

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		/*
		 * Header[] headers = response.getHeaders("Set-Cookie"); for (Header h :
		 * headers) { System.out.println(h.getValue().toString()); }
		 */

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());
	}
}
