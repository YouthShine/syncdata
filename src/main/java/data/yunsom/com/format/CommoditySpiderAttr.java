package data.yunsom.com.format;
/***
 * 商品属性数据格式
 * */
public class CommoditySpiderAttr {
	public CommoditySpiderAttr(){
		
	}

	

	private String attrkey;
	
	public String getAttrkey() {
		return attrkey;
	}

	public CommoditySpiderAttr(String attrkey, String keyname) {
		super();
		this.attrkey = attrkey;
		this.keyname = keyname;
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



	private String keyname;




}
