import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HPSMTicket {

	@SerializedName("___rowid")
	@Expose
	private Integer rowid;
	@SerializedName("record_id")
	@Expose
	private String recordId;
	@SerializedName("___forecolor")
	@Expose
	private String forecolor;
	@SerializedName("___bold")
	@Expose
	private String bold;
	@SerializedName("itemType")
	@Expose
	private String itemType;
	@SerializedName("target_date")
	@Expose
	private String targetDate;
	@SerializedName("urgency")
	@Expose
	private String urgency;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("assignee")
	@Expose
	private String assignee;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("___italic")
	@Expose
	private String italic;
	@SerializedName("status")
	@Expose
	private String status;

	public Integer getRowid() {
		return rowid;
	}

	public void setRowid(Integer rowid) {
		this.rowid = rowid;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getForecolor() {
		return forecolor;
	}

	public void setForecolor(String forecolor) {
		this.forecolor = forecolor;
	}

	public String getBold() {
		return bold;
	}

	public void setBold(String bold) {
		this.bold = bold;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(String targetDate) {
		this.targetDate = targetDate;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getItalic() {
		return italic;
	}

	public void setItalic(String italic) {
		this.italic = italic;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "HPSMTicket [rowid=" + rowid + ", recordId=" + recordId + ", forecolor=" + forecolor + ", bold=" + bold
				+ ", itemType=" + itemType + ", targetDate=" + targetDate + ", urgency=" + urgency + ", description="
				+ description + ", assignee=" + assignee + ", type=" + type + ", italic=" + italic + ", status="
				+ status + "]";
	}

}