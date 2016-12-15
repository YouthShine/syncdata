package data.yunsom.com.format;

public class BrandMapping {
	private int brand_id;
	private String brand_name;
	private String brand_name_en;
	private String brand_name_cn;

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

	public BrandMapping(int brand_id, String brand_name, String brand_name_en,
			String brand_name_cn) {
		this.brand_id = brand_id;
		this.brand_name =  brand_name;
		this.brand_name_cn = brand_name_cn;
		this.brand_name_en = brand_name_en;
	}



	
}
