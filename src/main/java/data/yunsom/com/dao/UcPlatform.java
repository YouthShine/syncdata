package data.yunsom.com.dao;

import java.util.List;
import java.util.Map;

import data.yunsom.com.util.DbUtils;
/**
 * 数据库uc_platform操作类
*/
public class UcPlatform {
	/**
	 * 获取平台名称和URL
	 * */
	public  Map<String, String> getPlatformName(int platform_id){
		 String sqlplatform = "select platform_name,url from uc_platform where id="+platform_id;
		 
	     List<Map<String, String>> platformrs = DbUtils.execute(sqlplatform);
	     return platformrs.get(0);
	}
}
