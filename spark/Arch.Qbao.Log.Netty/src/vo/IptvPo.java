package vo;

public class IptvPo {

	private String userId;
	private String itemId;
	private String rate;
	
	
	
	
	public IptvPo() {
		super();
		// TODO Auto-generated constructor stub
	}


	public IptvPo(String userId, String itemId, String rate) {
		super();
		this.userId = userId;
		this.itemId = itemId;
		this.rate = rate;
	}
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}


	@Override
	public String toString() {
		return "IptvPo [userId=" + userId + ", itemId=" + itemId + ", rate=" + rate + "]";
	}
	


	
	
	

}
