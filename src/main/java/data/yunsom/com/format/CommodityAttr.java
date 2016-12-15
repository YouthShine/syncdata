package data.yunsom.com.format;
/***
 * 商品属性数据格式
 * */
public class CommodityAttr {
	public CommodityAttr(){
		
	}

	public CommodityAttr( String attrKey, String attrValue) {
		super();
		this.attrkey = attrKey;
		this.attrname = attrValue;
	}

	public String getAttrkey() {
		return attrkey;
	}

	public void setAttrkey(String attrkey) {
		this.attrkey = attrkey;
	}

	public String getAttrname() {
		return attrname;
	}

	public void setAttrname(String attrname) {
		this.attrname = attrname;
	}

	private String attrkey;
	
	private String attrname;




}
