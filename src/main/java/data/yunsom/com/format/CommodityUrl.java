package data.yunsom.com.format;

import java.util.List;

/**
 * 商品信息数据格式
 * */

public class CommodityUrl {
	public CommodityUrl(){
		
	}
	public CommodityUrl(Integer commodity_id, String commodity_name,
			 List<CommodityAttr> commodity_attr,
			String main_pic_url, List<PrivatePrice> private_price,
			Double price, Integer provider_id, String provider_name,
			Integer category_id, String category_name, String na_category_name,
			Integer category_id_two, String category_name_two,
			String na_category_name_two, Integer category_id_one,
			String category_name_one, String na_category_name_one,
			Integer brand_id, String brand_name,
			 String logo_url,
			Boolean is_display, Integer platform_id,String platform_name,String platform_url, String payment_cycle,
			String payment_method, Boolean is_recommend, Integer sales_volume,
			Double credit, String area, String commodity_url) {
		super();
		this.commodity_url = commodity_url;
		this.commodity_id = commodity_id;
		this.commodity_name = commodity_name;
		
		this.commodity_attr = commodity_attr;
		this.main_pic_url = main_pic_url;
		this.private_price = private_price;
		this.price = price;
		this.provider_id = provider_id;
		this.provider_name = provider_name;
		this.category_id = category_id;
		this.category_name = category_name;
		this.na_category_name = na_category_name;
		this.category_id_two = category_id_two;
		this.category_name_two = category_name_two;
		this.na_category_name_two = na_category_name_two;
		this.category_id_one = category_id_one;
		this.category_name_one = category_name_one;
		this.na_category_name_one = na_category_name_one;
		this.brand_id = brand_id;
		this.brand_name = brand_name;
	
		this.logo_url = logo_url;
		this.is_display = is_display;
		this.platform_id = platform_id;
		this.platform_name = platform_name;
		this.platform_url = platform_url;
		this.payment_cycle = payment_cycle;
		this.payment_method = payment_method;
		this.is_recommend = is_recommend;
		this.sales_volume = sales_volume;
		this.credit = credit;
		this.area = area;
		
	}

	public String getPlatform_url() {
		return platform_url;
	}
	public void setPlatform_url(String platform_url) {
		this.platform_url = platform_url;
	}

	private Integer commodity_id;
	private String commodity_name;
	
	private List<CommodityAttr> commodity_attr;
	private String main_pic_url;
	private List<PrivatePrice> private_price;
	private Double price;
	private Integer provider_id;
	private String provider_name;
	private Integer category_id;
	private String category_name;
	private String na_category_name;
	private Integer category_id_two;
	private String category_name_two;
	private String na_category_name_two;
	private Integer category_id_one;
	private String category_name_one;
	private String na_category_name_one;
	private Integer brand_id;
	private String brand_name;
	
	private String logo_url;
	private Boolean is_display;
	private Integer platform_id;
	private String platform_name;
	private String platform_url;
	public String getPlatform_name() {
		return platform_name;
	}
	public void setPlatform_name(String platform_name) {
		this.platform_name = platform_name;
	}

	private String payment_cycle;
	private String payment_method;
	private Boolean is_recommend;
	private Integer sales_volume;
	private Double credit;
	private String area;
	public String getCommodity_url() {
		return commodity_url;
	}

	public void setCommodity_url(String commodity_url) {
		this.commodity_url = commodity_url;
	}

	private String commodity_url;

	public Integer getCategory_id_two() {
		return category_id_two;
	}

	public void setCategory_id_two(Integer category_id_two) {
		this.category_id_two = category_id_two;
	}

	public String getCategory_name_two() {
		return category_name_two;
	}

	public void setCategory_name_two(String category_name_two) {
		this.category_name_two = category_name_two;
	}

	public Integer getCategory_id_one() {
		return category_id_one;
	}

	public void setCategory_id_one(Integer category_id_one) {
		this.category_id_one = category_id_one;
	}

	public String getCategory_name_one() {
		return category_name_one;
	}

	public void setCategory_name_one(String category_name_one) {
		this.category_name_one = category_name_one;
	}



	public String getNa_category_name() {
		return na_category_name;
	}

	public void setNa_category_name(String na_category_name) {
		this.na_category_name = na_category_name;
	}

	public String getNa_category_name_two() {
		return na_category_name_two;
	}

	public void setNa_category_name_two(String na_category_name_two) {
		this.na_category_name_two = na_category_name_two;
	}

	public String getNa_category_name_one() {
		return na_category_name_one;
	}

	public void setNa_category_name_one(String na_category_name_one) {
		this.na_category_name_one = na_category_name_one;
	}

	
	public Integer getPlatform_id() {
		return platform_id;
	}

	public void setPlatform_id(Integer platform_id) {
		this.platform_id = platform_id;
	}

	public Integer getCommodity_id() {
		return commodity_id;
	}

	public void setCommodity_id(Integer commodity_id) {
		this.commodity_id = commodity_id;
	}

	public String getCommodity_name() {
		return commodity_name;
	}

	public void setCommodity_name(String commodity_name) {
		this.commodity_name = commodity_name;
	}

	public List<CommodityAttr> getCommodity_attr() {
		return commodity_attr;
	}

	public void setCommodity_attr(List<CommodityAttr> commodity_attr) {
		this.commodity_attr = commodity_attr;
	}

	public String getMain_pic_url() {
		return main_pic_url;
	}

	public void setMain_pic_url(String main_pic_url) {
		this.main_pic_url = main_pic_url;
	}

	

	
	public List<PrivatePrice> getPrivate_price() {
		return private_price;
	}
	public void setPrivate_price(List<PrivatePrice> private_price) {
		this.private_price = private_price;
	}
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getProvider_id() {
		return provider_id;
	}

	public void setProvider_id(Integer provider_id) {
		this.provider_id = provider_id;
	}

	public String getProvider_name() {
		return provider_name;
	}

	public void setProvider_name(String provider_name) {
		this.provider_name = provider_name;
	}

	public Integer getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public Integer getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getLogo_url() {
		return logo_url;
	}

	public void setLogo_url(String logo_url) {
		this.logo_url = logo_url;
	}

	public Boolean getIs_display() {
		return is_display;
	}

	public void setIs_display(Boolean is_display) {
		this.is_display = is_display;
	}

	public String getPayment_cycle() {
		return payment_cycle;
	}

	public void setPayment_cycle(String payment_cycle) {
		this.payment_cycle = payment_cycle;
	}

	public String getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}

	public Boolean getIs_recommend() {
		return is_recommend;
	}

	public void setIs_recommend(Boolean is_recommend) {
		this.is_recommend = is_recommend;
	}

	public Integer getSales_volume() {
		return sales_volume;
	}

	public void setSales_volume(Integer sales_volume) {
		this.sales_volume = sales_volume;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
