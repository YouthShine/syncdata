package data.yunsom.com.format;

import java.util.List;

public class SpiderHcCommodity {
	private String productImagePath;
	private String productPrice;
	private String productModel;
	private List<CommoditySpiderAttr> productSpeci;
	private String productName;
	private String productUrl;
	private String productBrand;
	private String productDetails;
	public String getProductImagePath() {
		return productImagePath;
	}
	public SpiderHcCommodity(String productImagePath, String productPrice,
			String productModel, List<CommoditySpiderAttr> productSpeci,
			String productName, String productUrl, String productBrand,
			String productDetails, String productSupplier) {
		super();
		this.productImagePath = productImagePath;
		this.productPrice = productPrice;
		this.productModel = productModel;
		this.productSpeci = productSpeci;
		this.productName = productName;
		this.productUrl = productUrl;
		this.productBrand = productBrand;
		this.productDetails = productDetails;
		this.productSupplier = productSupplier;
	}
	public void setProductImagePath(String productImagePath) {
		this.productImagePath = productImagePath;
	}
	public String getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductModel() {
		return productModel;
	}
	public void setProductModel(String productModel) {
		this.productModel = productModel;
	}
	public List<CommoditySpiderAttr> getProductSpeci() {
		return productSpeci;
	}
	public void setProductSpeci(List<CommoditySpiderAttr> productSpeci) {
		this.productSpeci = productSpeci;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	public String getProductBrand() {
		return productBrand;
	}
	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}
	public String getProductDetails() {
		return productDetails;
	}
	public void setProductDetails(String productDetails) {
		this.productDetails = productDetails;
	}
	public String getProductSupplier() {
		return productSupplier;
	}
	public void setProductSupplier(String productSupplier) {
		this.productSupplier = productSupplier;
	}
	private String productSupplier;
	public SpiderHcCommodity() {

	}

}
