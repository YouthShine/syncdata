package data.yunsom.com.service;

import java.util.List;
import java.util.Map;

import data.yunsom.com.util.DbUtils;

/**
 * 分类数据库查询操作
 * 
 * */
public class DataCategory {
	/**
	 *获取分类属于几级分类
	 * */
	public static String getCategory(String category_id){
		String sql="select max(root_step) as type from comd_category_meta where category_id="+category_id;
		List<Map<String, String>> rs = DbUtils.execute(sql);
		return rs.get(0).get("type");
		
	}
	/**
	 *获取分类父ID
	 * */
	public static String getParentCategory(String category_id){
		String sql="select parent_id as type from comd_category where id="+category_id;
		List<Map<String, String>> rs = DbUtils.execute(sql);
		return rs.get(0).get("parent_id");
		
	}
}
