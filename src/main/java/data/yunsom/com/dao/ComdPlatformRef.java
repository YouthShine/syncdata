package data.yunsom.com.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.yunsom.com.util.DbUtils;
/***
 * 数据库comd_commodity_platform_ref操作类
 * */
public class ComdPlatformRef {
	/**
	 * 获取所有商品对应的发布平台
	 * */
	public HashMap<Integer, List<Integer>> getPlatformAll() {
		HashMap<Integer, List<Integer>> platmap = new HashMap<Integer, List<Integer>>();
		String sql = "select  commodity_id,platform_id from  comd_commodity_platform_ref where status=1 order by commodity_id asc";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			int commodity_id = Integer.parseInt(map.get("commodity_id"));
			int platform_id = Integer.parseInt(map.get("platform_id"));

			List<Integer> nums = new ArrayList<Integer>();
			if (platmap.get(commodity_id) != null) {

				nums = platmap.get(commodity_id);
				nums.add(platform_id);
				platmap.put(commodity_id, nums);
			} else {
				nums.add(platform_id);
				platmap.put(commodity_id, nums);
			}
		}

		return platmap;
	}
	/**
	 * 根据商品ID获取发布的平台
	 * */
	public List<String> getIdPlatforms(String commodity_id){
		
		String sql = "select  platform_id from  comd_commodity_platform_ref where status=1 and commodity_id="+commodity_id;
		List<Map<String, String>> rs = DbUtils.execute(sql);
		List<String> platforms = new ArrayList<String>();
		for (Map<String, String> map : rs) {
			String platform_id = map.get("platform_id");
			platforms.add(platform_id);
		}
		return platforms;
	}
}
