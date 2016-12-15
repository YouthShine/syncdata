package data.yunsom.com.format;

import java.util.List;

public class TagStructure {
	
	public TagStructure(){
		
	}

	private int commodity_id;
	private String commodity_name;
	private int brand_id;
	private String brand_name;
	private String brand_name_en;
	private String brand_name_cn;
	private String commodity_model;
	private int tag_id_one;
	private String tag_name_one;
	private int tag_id_two;
	public int getCommodity_id() {
		return commodity_id;
	}


	public TagStructure(int commodity_id, String commodity_name, int brand_id,
			String brand_name, String brand_name_en, String brand_name_cn,
			String commodity_model, int tag_id_one, String tag_name_one,
			int tag_id_two, String tag_name_two, int tag_id, String tag_name,
			List<CommodityAttr> commodity_attr, String main_pic_url,
			String commodity_url, List<PrivatePrice> private_price,
			Double price, int provider_id, String provider_name,
			String logo_url, int platform_id, String platform_name,
			String platform_url, Boolean is_display, String payment_cycle,
			String payment_method, Boolean is_recommend, int sales_volume,
			double credit, String area, String from, List<String> custom_tag) {
		super();
		this.commodity_id = commodity_id;
		this.commodity_name = commodity_name;
		this.brand_id = brand_id;
		this.brand_name = brand_name;
		this.brand_name_en = brand_name_en;
		this.brand_name_cn = brand_name_cn;
		this.commodity_model = commodity_model;
		this.tag_id_one = tag_id_one;
		this.tag_name_one = tag_name_one;
		this.tag_id_two = tag_id_two;
		this.tag_name_two = tag_name_two;
		this.tag_id = tag_id;
		this.tag_name = tag_name;
		this.commodity_attr = commodity_attr;
		this.main_pic_url = main_pic_url;
		this.commodity_url = commodity_url;
		this.private_price = private_price;
		this.price = price;
		this.provider_id = provider_id;
		this.provider_name = provider_name;
		this.logo_url = logo_url;
		this.platform_id = platform_id;
		this.platform_name = platform_name;
		this.platform_url = platform_url;
		this.is_display = is_display;
		this.payment_cycle = payment_cycle;
		this.payment_method = payment_method;
		this.is_recommend = is_recommend;
		this.sales_volume = sales_volume;
		this.credit = credit;
		this.area = area;
		this.from = from;
		this.custom_tag = custom_tag;
	}


	public void setCommodity_id(int commodity_id) {
		this.commodity_id = commodity_id;
	}


	public String getCommodity_name() {
		return commodity_name;
	}


	public void setCommodity_name(String commodity_name) {
		this.commodity_name = commodity_name;
	}


	public int getBrand_id() {
		return brand_id;
	}


	public void setBrand_id(int brand_id) {
		this.brand_id = brand_id;
	}


	public String getBrand_name() {
		return brand_name;
	}


	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}


	public String getBrand_name_en() {
		return brand_name_en;
	}


	public void setBrand_name_en(String brand_name_en) {
		this.brand_name_en = brand_name_en;
	}


	public String getBrand_name_cn() {
		return brand_name_cn;
	}


	public void setBrand_name_cn(String brand_name_cn) {
		this.brand_name_cn = brand_name_cn;
	}


	public String getCommodity_model() {
		return commodity_model;
	}


	public void setCommodity_model(String commodity_model) {
		this.commodity_model = commodity_model;
	}


	public int getTag_id_one() {
		return tag_id_one;
	}


	public void setTag_id_one(int tag_id_one) {
		this.tag_id_one = tag_id_one;
	}


	public String getTag_name_one() {
		return tag_name_one;
	}


	public void setTag_name_one(String tag_name_one) {
		this.tag_name_one = tag_name_one;
	}


	public int getTag_id_two() {
		return tag_id_two;
	}


	public void setTag_id_two(int tag_id_two) {
		this.tag_id_two = tag_id_two;
	}


	public String getTag_name_two() {
		return tag_name_two;
	}


	public void setTag_name_two(String tag_name_two) {
		this.tag_name_two = tag_name_two;
	}


	public int getTag_id() {
		return tag_id;
	}


	public void setTag_id(int tag_id) {
		this.tag_id = tag_id;
	}


	public String getTag_name() {
		return tag_name;
	}


	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
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


	public String getCommodity_url() {
		return commodity_url;
	}


	public void setCommodity_url(String commodity_url) {
		this.commodity_url = commodity_url;
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


	public int getProvider_id() {
		return provider_id;
	}


	public void setProvider_id(int provider_id) {
		this.provider_id = provider_id;
	}


	public String getProvider_name() {
		return provider_name;
	}


	public void setProvider_name(String provider_name) {
		this.provider_name = provider_name;
	}


	public String getLogo_url() {
		return logo_url;
	}


	public void setLogo_url(String logo_url) {
		this.logo_url = logo_url;
	}


	public int getPlatform_id() {
		return platform_id;
	}


	public void setPlatform_id(int platform_id) {
		this.platform_id = platform_id;
	}


	public String getPlatform_name() {
		return platform_name;
	}


	public void setPlatform_name(String platform_name) {
		this.platform_name = platform_name;
	}


	public String getPlatform_url() {
		return platform_url;
	}


	public void setPlatform_url(String platform_url) {
		this.platform_url = platform_url;
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


	public int getSales_volume() {
		return sales_volume;
	}


	public void setSales_volume(int sales_volume) {
		this.sales_volume = sales_volume;
	}


	public double getCredit() {
		return credit;
	}


	public void setCredit(double credit) {
		this.credit = credit;
	}


	public String getArea() {
		return area;
	}


	public void setArea(String area) {
		this.area = area;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public List<String> getCustom_tag() {
		return custom_tag;
	}


	public void setCustom_tag(List<String> custom_tag) {
		this.custom_tag = custom_tag;
	}


	private String tag_name_two;
	private int tag_id;
	private String tag_name;
	private List<CommodityAttr> commodity_attr;
	private String main_pic_url;
	private String commodity_url;
	private List<PrivatePrice> private_price;
	private Double price;
	private int provider_id;
	private String provider_name;
	private String logo_url;
	private int platform_id;
	private String platform_name;
	private String platform_url;
	private Boolean is_display;
	private String payment_cycle;
	private String payment_method;
	private Boolean is_recommend;
	private int sales_volume;
	private double credit;
	private String area;
	private String from;
	
	
	private List<String> custom_tag;
}
