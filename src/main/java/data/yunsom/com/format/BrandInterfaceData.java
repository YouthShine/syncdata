package data.yunsom.com.format;

import java.util.List;

public class BrandInterfaceData {
	public BrandInterfaceData(){
		
	}
	private String msg;
	private int code;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public List<BrandDetail> getData() {
		return data;
	}
	public BrandInterfaceData(String msg, int code, List<BrandDetail> data) {
		this.msg = msg;
		this.code = code;
		this.data = data;
	}
	public void setData(List<BrandDetail> data) {
		this.data = data;
	}

	private List<BrandDetail> data;
}
