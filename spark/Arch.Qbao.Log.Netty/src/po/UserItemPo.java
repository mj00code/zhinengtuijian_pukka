package po;

import java.util.ArrayList;

public class UserItemPo {
	private String userId;
	private String size;
	private String programId;
	private ArrayList<String>  dataExclude	;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public ArrayList<String> getDataExclude() {
		return dataExclude;
	}
	public void setDataExclude(ArrayList<String> dataExclude) {
		this.dataExclude = dataExclude;
	}
	
	

}
