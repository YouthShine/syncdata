package data.yunsom.com.format;
/***
 * 商品规格参数格式
 * */
public class SpeciAttr {
	public SpeciAttr(){
		
	}

	public SpeciAttr( String attrKey, String attrValue) {
		super();
		this.attrkey = attrKey;
		this.keyname = attrValue;
	}

	public String getAttrkey() {
		return attrkey;
	}

	public void setAttrkey(String attrkey) {
		this.attrkey = attrkey;
	}

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	private String attrkey;
	
	private String keyname;




}
