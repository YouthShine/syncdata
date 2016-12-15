package data.yunsom.com.format;

public class CategoryMap {
	private int category_id;
	private String category_name;
	private int category_id_two;
	private String category_name_two;
	private int category_id_one;
	private String category_name_one;
	
	public CategoryMap(int category_id, String category_name,
			int category_id_two, String category_name_two, int category_id_one,
			String category_name_one) {
		super();
		this.category_id = category_id;
		this.category_name = category_name;
		this.category_id_two = category_id_two;
		this.category_name_two = category_name_two;
		this.category_id_one = category_id_one;
		this.category_name_one = category_name_one;
	}
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public int getCategory_id_two() {
		return category_id_two;
	}
	public void setCategory_id_two(int category_id_two) {
		this.category_id_two = category_id_two;
	}
	public String getCategory_name_two() {
		return category_name_two;
	}
	public void setCategory_name_two(String category_name_two) {
		this.category_name_two = category_name_two;
	}
	public int getCategory_id_one() {
		return category_id_one;
	}
	public void setCategory_id_one(int category_id_one) {
		this.category_id_one = category_id_one;
	}
	public String getCategory_name_one() {
		return category_name_one;
	}
	public void setCategory_name_one(String category_name_one) {
		this.category_name_one = category_name_one;
	}
}
