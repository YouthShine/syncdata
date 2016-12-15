package data.yunsom.com.format;

public class SpiderCateGory {
	private String productClassification;
	private String productBrand;
	public SpiderCateGory(String productClassification, String productBrand) {
		super();
		this.productClassification = productClassification;
		this.productBrand = productBrand;
	}
	public String getProductClassification() {
		return productClassification;
	}
	public void setProductClassification(String productClassification) {
		this.productClassification = productClassification;
	}
	public String getProductBrand() {
		return productBrand;
	}
	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}
}
