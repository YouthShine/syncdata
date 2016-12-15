package data.yunsom.com.format;

public class CategoryMapOne {
	private  int id;
	private  int type;
	private String name;
	public CategoryMapOne(int id, int type, String name, String name_web) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
		this.name_web = name_web;
	}
	private String name_web;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName_web() {
		return name_web;
	}
	public void setName_web(String name_web) {
		this.name_web = name_web;
	}
}
