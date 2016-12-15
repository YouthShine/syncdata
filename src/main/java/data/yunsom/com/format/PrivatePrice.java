package data.yunsom.com.format;

/***
 * 私有价格数据格式
 * 
 * */
public class PrivatePrice {
	public PrivatePrice() {

	}

	public PrivatePrice(Integer enterprise_id, double price) {

		this.enterprise_id = enterprise_id;
		this.price = price;
	}

	private Integer enterprise_id;
	private double price;

	public Integer getEnterprise_id() {
		return enterprise_id;
	}

	public void setEnterprise_id(Integer enterprise_id) {
		this.enterprise_id = enterprise_id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
