package data.yunsom.com.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.yunsom.com.util.DbUtils;
/**
 * 数据库操作comd_category
 * */
public class ComdCategory {
	/**
	 * 获取所有的分类map<id,name>
	 * map<id_p,parent>
	 * */
	public  HashMap<String, String> getCategory() {

		String sql = "select id,parent_id,category_name from comd_category ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		HashMap<String, String> catemap = new HashMap<String, String>();
		for (Map<String, String> map : rs) {
			catemap.put(map.get("id") + "_p", map.get("parent_id"));
			catemap.put(map.get("id"), map.get("category_name"));

		}
		return catemap;
	}
	/***
	 * 根据三级分类ID和name 获取一二级ID和name
	 * */
	public  HashMap<String, String> getCategoryMap(
			HashMap<String, String> catemap, int categroy_id,
			String category_name) {
		HashMap<String, String> categorymap = new HashMap<String, String>();
		String category_two = catemap.get(categroy_id + "_p");
		category_two = category_two == null ? "0" : category_two;
		String category_name_two = catemap.get(category_two);
		String category_one = catemap.get(category_two + "_p");
		category_one = category_one == null ? "0" : category_one;
		String category_name_one = catemap.get(category_one);
		if (Integer.parseInt(category_two) > 0
				&& Integer.parseInt(category_one) > 0) {
			categorymap.put("category_id_two", category_two);
			categorymap.put("category_name_two", category_name_two);
			categorymap.put("category_id_one", category_one);
			categorymap.put("category_name_one", category_name_one);
			categorymap.put("category_id", categroy_id + "");
			categorymap.put("category_name", category_name);

		} else if (Integer.parseInt(category_two) > 0
				&& Integer.parseInt(category_one) == 0) {
			categorymap.put("category_id_two", categroy_id + "");
			categorymap.put("category_name_two", category_name);
			categorymap.put("category_id_one", category_two);
			categorymap.put("category_name_one", category_name_two);
			categorymap.put("category_id", "0");
			categorymap.put("category_name", "");
		} else {
			categorymap.put("category_id_two", "0");
			categorymap.put("category_name_two", "");
			categorymap.put("category_id_one", categroy_id + "");
			categorymap.put("category_name_one", category_name);
			categorymap.put("category_id", "0");
			categorymap.put("category_name", "");
		}
		return categorymap;
	}
}
