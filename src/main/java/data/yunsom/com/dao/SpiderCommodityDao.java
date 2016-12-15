package data.yunsom.com.dao;

import java.util.List;
import java.util.Map;

import data.yunsom.com.util.DbUtils;

/***
 * 外网抓取商品数据操作
 * */
public class SpiderCommodityDao {
	/**
	 * 获取商品表最大ID
	 * */
	public  int getCommodityMaxID() {
		int maxId = 0;
		String sql = "select id  from spider_commodity order by id desc limit 1";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		maxId = Integer.parseInt(rs.get(0).get("id"));
		return maxId;
	}
}
