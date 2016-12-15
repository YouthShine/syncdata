package data.yunsom.com.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import data.yunsom.com.format.PrivatePrice;
import data.yunsom.com.util.DbUtils;

public class ComdPrivPrice {
	/***
	 * 
	 * 获取商品私有价格
	 * 
	 * @throws SQLException
	 * */
	public  List<PrivatePrice> getPrivatePrice(String commodity_id) {
		List<PrivatePrice> priceList = new ArrayList<PrivatePrice>();
		String sql = "select enterprise_id,price from comd_commodity_priv_price_ref where commodity_id ="
				+ commodity_id;
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			priceList.add(new PrivatePrice(Integer.parseInt(map
					.get("enterprise_id")),
					Double.parseDouble(map.get("price"))));
		}
		return priceList;

	}
}
