package data.yunsom.com.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

import data.yunsom.com.common.Config;
import data.yunsom.com.format.CommodityAttr;
import data.yunsom.com.format.PrivatePrice;
import data.yunsom.com.format.TagStructure;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.JsonValidatorUtil;
import data.yunsom.com.util.SuggestPinyinDataUtil;

public class SynchroTagData {

	private static final Logger logger = LoggerFactory
			.getLogger(SynchroTagData.class);
	public static TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static List<PrivatePrice> privatePrice = new ArrayList<PrivatePrice>();

	public static int gettotal() {
		int maxId = 0;
		String sql = "select id  from comd_commodity order by id desc limit 1";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		maxId = Integer.parseInt(rs.get(0).get("id"));
		return maxId;
	}

	public static HashMap<String, String> getCategory() {

		String sql = "select id,parent_id,category_name from comd_category ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		HashMap<String, String> catemap = new HashMap<String, String>();
		for (Map<String, String> map : rs) {
			catemap.put(map.get("id") + "_p", map.get("parent_id"));
			catemap.put(map.get("id"), map.get("category_name"));

		}
		return catemap;
	}

	public static HashMap<String, String> getBrand() {

		String sql = "select id,brand_name from comd_brand ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		HashMap<String, String> catemap = new HashMap<String, String>();
		for (Map<String, String> map : rs) {
			catemap.put(map.get("id"), map.get("brand_name"));

		}
		return catemap;
	}

	public static HashMap<Integer, List<Integer>> getPlatform() {
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
				logger.info("nums---" + nums.size());
				platmap.put(commodity_id, nums);
			} else {
				nums.add(platform_id);
				platmap.put(commodity_id, nums);
			}
		}

		return platmap;
	}

	public static void getData(int commodity_id)
			throws JsonGenerationException, JsonMappingException, IOException {
		logger.info("start sync data");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		int maxId = gettotal();
		logger.info("maxId==" + maxId);
		int size = 1000;
		HashMap<String, String> catemap = getCategory();
		HashMap<Integer, List<Integer>> platmap = getPlatform();
		while (maxId >= commodity_id) {

			String sql = "select a.deleted_at,a.status,a.id,a.commodity_name,a.main_pic_url,a.price,a.provider_id,a.brand_id,a.category_id,a.is_display,b.enterprise_name,c.brand_name,c.logo_url,d.parent_id,d.category_name,e.commodity_attr  from"
					+ " comd_commodity as a left join uc_enterprise as b on a.provider_id =b.id left join  comd_brand as c on  a.brand_id = c.id left join  comd_category  as d on a.category_id=d.id left join comd_commodity_ext as e   on a.id=e.id"
					+ " where a.id> " + commodity_id + "   limit " + size;
			logger.info("sql--" + sql);
			List<Map<String, String>> rs = DbUtils.execute(sql);
			logger.info("rs size--" + rs.size());
			if (rs.size() > 0) {
				BulkRequestBuilder bulkRequest = client.prepareBulk();
				for (Map<String, String> map : rs) {
					int tmp_commodity_id = Integer.parseInt(map.get("id"));
					int display = Integer.parseInt(map.get("is_display"));
					String deleted_at = map.get("deleted_at");
					if (deleted_at == null) {
						if (Integer.parseInt(map.get("status")) != 2
								&& display == 1) {

							List<CommodityAttr> attr = new ArrayList<CommodityAttr>();
							String commodity_attr = map.get("commodity_attr");
							if (!"".equals(commodity_attr)
									&& commodity_attr != null) {
								boolean validity = new JsonValidatorUtil()
										.validate(commodity_attr);
								if (validity) {
									JSONArray array = JSONArray
											.parseArray(commodity_attr);
									int arrsize = array.size();
									for (int i = 0; i < arrsize; i++) {
										String attrKey = array.getJSONObject(i)
												.get("key").toString();
										String attrValue = array
												.getJSONObject(i).get("value")
												.toString();
										attr.add(new CommodityAttr(attrKey,
												attrValue));
										String pinyinjson = SuggestPinyinDataUtil
												.getSuggestPinyin(attrValue,
														Config.TYPE_WEIGHT_ATTR);
										bulkRequest.add(client.prepareIndex(
												Config.SUGGEST, Config.TYPE,
												"attr_" + tmp_commodity_id)
												.setSource(pinyinjson));
									}
								}
							}
							boolean is_display = Integer.parseInt(map
									.get("is_display")) == 1 ? true : false;

							String payment_cycle = "";
							String payment_method = "";
							boolean is_recommend = false;
							int sales_volume = 0;
							double credit = 0.0;
							String area = "";
							String commodity_name = map.get("commodity_name");
							String category_name = map.get("category_name");
							String brand_name = map.get("brand_name");
							String brand_name_cn = "";
							String brand_name_en = "";
							if (brand_name.indexOf("/") > 0) {
								String[] brand_arr = brand_name.split("/");
								brand_name_cn = brand_arr[0];
								brand_name_en = brand_arr[1];

							} else {
								brand_name_cn = brand_name;
								brand_name_en = brand_name;
							}
							List<Integer> platforms = platmap
									.get(tmp_commodity_id);
							int categroy_id = Integer.parseInt(map
									.get("category_id"));
							HashMap<String, String> categorysMap = getCategoryMap(
									catemap, categroy_id, category_name);
							int brand_id = Integer
									.parseInt(map.get("brand_id"));
							if (platforms != null) {
								if (platforms.size() > 0) {
									for (Integer platform : platforms) {
										List<String> custom_tag = new ArrayList<String>();
										/*
										 * 测试自定义标签
										 * custom_tag.add(brand_name_cn);
										 * custom_tag
										 * .add(categorysMap.get("category_name_two"
										 * )); custom_tag.add(categorysMap.get(
										 * "category_name_one"));
										 * custom_tag.add(
										 * categorysMap.get("category_name"));
										 * custom_tag.add(tmp_commodity_id+"");
										 */
										Map<String, String> platform_params = CommodityPlaform
												.getPlatformName(platform + "");
										TagStructure tagObject = new TagStructure(
												tmp_commodity_id,
												commodity_name,
												brand_id,
												brand_name_en,
												brand_name_en,
												brand_name_cn,
												"",
												Integer.parseInt(categorysMap
														.get("category_id_one")),
												categorysMap
														.get("category_name_one"),
												Integer.parseInt(categorysMap
														.get("category_id_two")),
												categorysMap
														.get("category_id_two"),
												Integer.parseInt(categorysMap
														.get("category_id")),
												categorysMap
														.get("category_id_two"),
												attr, map.get("main_pic_url"),
												"", privatePrice, Double
														.parseDouble(map
																.get("price")),
												Integer.parseInt(map
														.get("provider_id")),
												map.get("enterprise_name"), map
														.get("logo_url"),
												platform, platform_params
														.get("platform_name"),
												platform_params.get("url"),
												is_display, payment_cycle,
												payment_method, is_recommend,
												sales_volume, credit, area,
												"www.yunsom.com", custom_tag);

										String json = mapper
												.writeValueAsString(tagObject);
										// logger.info("---"+json);
										bulkRequest.add(client.prepareIndex(
												Config.TAGINDEXNAME,
												Config.TYPE,
												tmp_commodity_id + "_"
														+ platform).setSource(
												json));
									}
								}
							}

						}
					}
					commodity_id = tmp_commodity_id;
				}
				if (bulkRequest.numberOfActions() > 1) {
					BulkResponse bulkResponse = bulkRequest.execute()
							.actionGet();
					if (bulkResponse.hasFailures()) {
						logger.info(bulkResponse.buildFailureMessage());
					}
				}

			} else {
				break;
			}
		}

	}

	public static HashMap<String, String> getCategoryMap(
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
