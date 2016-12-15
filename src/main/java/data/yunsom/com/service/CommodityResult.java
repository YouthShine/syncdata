package data.yunsom.com.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;

import data.yunsom.com.format.Commodity;
import data.yunsom.com.format.CommodityAttr;
import data.yunsom.com.format.PrivatePrice;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.JsonValidatorUtil;

/**
 * data result
 * 
 * @author yangxuchuan
 * 
 */
public class CommodityResult {

	private static final Logger logger = LoggerFactory
			.getLogger(CommodityResult.class);

	public HashMap<String, String> getCommodResult(int commodity_id) {
		HashMap<String, String> map = new HashMap<String, String>();
		String sql = "select b.enterprise_name,c.brand_name,c.logo_url,d.parent_id,d.category_name,e.commodity_attr  from comd_commodity as a left join uc_enterprise as b on a.provider_id =b.id left join comd_brand as c on a.brand_id = c.id left join comd_category as d on a.category_id=d.id left join comd_commodity_ext as e on  a.id=e.id  where   a.id="
				+ commodity_id;
		logger.info("sql--" + sql);
		List<Map<String, String>> rs = DbUtils.execute(sql);
		if (rs.size() > 0) {
			map = (HashMap<String, String>) rs.get(0);
		}
		return map;
	}

	/***
	 * 
	 * 获取商品私有价格
	 * 
	 * @throws SQLException
	 * */
	public static List<PrivatePrice> getPrivatePrice(String commodity_id) {
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

	/***
	 * 
	 * 获取商品发布平台
	 * 
	 * @throws SQLException
	 * */
	public List<Map<String, String>> getPlatfrom(int commodity_id) {

		String sql = "select  platform_id,platform_name,url  from comd_commodity_platform_ref  as a left join uc_platform  as b on a.platform_id=b.id where commodity_id="
				+ commodity_id;
		List<Map<String, String>> rs = DbUtils.execute(sql);
		return rs;
	}

	/**
	 * 
	 * 返回insert 结果数据
	 * 
	 * @throws IOException
	 * 
	 * @throws SQLException
	 * **/
	public HashMap<String, String> combinData(List<Column> columns)
			throws IOException {
		JSONObject jsonData = new JSONObject();
		if (columns.size() > 0) {
			for (Column column : columns) {
				jsonData.put(column.getName(), column.getValue());
			}
		} else {
			logger.warn("update warn columns size 0");
		}
		HashMap<String, String> resmap = new HashMap<String, String>();
		HashMap<String, String> catemap = SynchroData.getCategory();
		String commodity_url = "";
		String commodity_name = jsonData.getString("commodity_name");
		String main_pic_url = jsonData.getString("main_pic_url");
		Double price = jsonData.getDouble("price");
		int provider_id = jsonData.getIntValue("provider_id");
		int commodity_id = jsonData.getIntValue("id");
		List<CommodityAttr> attr = new ArrayList<CommodityAttr>();
		HashMap<String, String> foreignData = getCommodResult(commodity_id);
		
			String provider_name = foreignData.get("enterprise_name");
			int category_id = jsonData.getIntValue("category_id");
			String category_name = foreignData.get("category_name");
			int brand_id = jsonData.getIntValue("brand_id");
			String brand_name = foreignData.get("brand_name");
			String brand_name_cn = "";
			String brand_name_en = "";
			logger.info("brand_name=" + brand_name);
			if (brand_name.indexOf("/") > 0) {
				String[] brand_arr = brand_name.split("/");
				brand_name_cn = brand_arr[0];
				brand_name_en = brand_arr[1];

			} else {
				brand_name_cn = brand_name;
				brand_name_en = brand_name;
			}
			String logo_url = foreignData.get("logo_url");
			boolean is_display = jsonData.getIntValue("is_display") > 0 ? true
					: false;
			String payment_cycle = "";
			String payment_method = "";
			boolean is_recommend = false;
			int sales_volume = 0;
			double credit = 0.0;
			String area = "";

			List<PrivatePrice> privatePrice = getPrivatePrice(commodity_id + "");
			List<Map<String, String>> platform = getPlatfrom(commodity_id);
			// logger.info("platform=="+platform.size());
			String commodityAttr = foreignData.get("commodity_attr");

			if (commodityAttr != null) {
				boolean validity = new JsonValidatorUtil()
						.validate(commodityAttr);
				if (validity) {
					JSONArray array = JSONArray.parseArray(commodityAttr);
					int sizenum = array.size();
					if (sizenum > 0) {
						for (int i = 0; i < sizenum; i++) {
							String attrKey = array.getJSONObject(i).get("key")
									.toString();
							String attrValue = array.getJSONObject(i)
									.get("value").toString();

							attr.add(new CommodityAttr(attrKey, attrValue));
						}
					}
				}
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
			mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			
			HashMap<String, String> categorysMap = SynchroData.getCategoryMap(
					catemap, category_id, category_name);
			for (Map<String, String> ele : platform) {
				Commodity comObject = new Commodity(commodity_id,
						commodity_name, commodity_name, attr, main_pic_url,
						privatePrice, price, provider_id, provider_name,
						category_id, category_name, category_name,
						Integer.parseInt(categorysMap.get("category_id_two")),
						categorysMap.get("category_name_two"),
						categorysMap.get("category_name_two"),
						Integer.parseInt(categorysMap.get("category_id_one")),
						categorysMap.get("category_name_one"),
						categorysMap.get("category_name_one"), brand_id,
						brand_name, brand_name, brand_name_cn, brand_name_en,
						logo_url, is_display, Integer.parseInt(ele
								.get("platform_id")), ele.get("platform_name"),
						ele.get("url"), payment_cycle, payment_method,
						is_recommend, sales_volume, credit, area);
				String json = mapper.writeValueAsString(comObject);

				resmap.put(commodity_id + "_" + ele, json);
			}
		
			return resmap;
		
	}
}
