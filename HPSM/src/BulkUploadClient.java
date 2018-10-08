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

public class BulkUploadClient {
	private final static String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) {
		System.out.println("!!! HPSM Client Started !!!");
			// Make sure cookies is turned on
			CookieHandler.setDefault(new CookieManager());
	
			try {
				HttpClient client = createHttpClient();
				// LOGIN TO HPSM
				String login_url = "";
				sendPostLoginRequestToHEBdotCom(login_url, client);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		System.out.println("!!! HPSM Client Started !!!");
	}

	

	private static void sendPostLoginRequestToHEBdotCom(String url, HttpClient client)
			throws Exception {
		// HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		//urlParameters.add(new BasicNameValuePair("user.id", username));
		urlParameters.add(new BasicNameValuePair("type", "login"));
		urlParameters.add(new BasicNameValuePair("xHtoken", ""));
		//urlParameters.add(new BasicNameValuePair("old.password", pass));
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
}
