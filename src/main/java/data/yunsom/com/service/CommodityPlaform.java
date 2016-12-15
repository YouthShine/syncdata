package data.yunsom.com.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

import data.yunsom.com.common.Config;
import data.yunsom.com.format.Commodity;
import data.yunsom.com.format.CommodityAttr;
import data.yunsom.com.format.PrivatePrice;
import data.yunsom.com.util.CommodityUtil;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.JsonValidatorUtil;
import data.yunsom.com.util.SuggestPinyinDataUtil;

/**
 * 发布平台更新 -- 整体更新数据
 * **/

public class CommodityPlaform {

	private static final TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static final Logger logger = LoggerFactory
			.getLogger(CommodityPlaform.class);

	public void updatePlaform(EventType eventType, List<Column> beforeColumns,
			List<Column> afterColumns) throws JsonGenerationException,
			JsonMappingException, IOException, InterruptedException {
		if (eventType == EventType.INSERT) {
			insertPlaform(afterColumns);
		} else if (eventType == EventType.DELETE) {
			deletePlaform(beforeColumns);
		} else if (eventType == EventType.UPDATE) {
			int status = Integer.parseInt(CommodityUtil.getCommodityId(
					afterColumns, "status"));
			if (status == 0 || status==2) {
				deletePlaform(afterColumns);
			}else if(status == 1){
				insertPlaform(afterColumns);
			}
		}

	}

	public static void deletePlaform(List<Column> columns) {
		String comm_id = CommodityUtil.getCommodityId(columns, "commodity_id");
		String platform_id = CommodityUtil.getCommodityId(columns,
				"platform_id");
		deleteData(comm_id, platform_id);

	}

	public static void deleteData(String comm_id, String platform_id) {
		DeleteResponse responsesh = client
				.prepareDelete(Config.INDEXNAME, Config.TYPE,
						comm_id + "_" + platform_id).execute().actionGet();
		DeleteResponse responsesuggest = client
				.prepareDelete(Config.SUGGEST, Config.TYPE,
						"commodity_" + comm_id).execute().actionGet();
		logger.info("commodity--id=" + responsesh.getId() + "--suggest--id="
				+ responsesuggest.getId());
		if (responsesh.getId().equals(null)) {
			logger.info("faild delete " + comm_id + "_" + platform_id);
		}

	}

	public static void insertPlaform(List<Column> columns)
			throws JsonGenerationException, JsonMappingException, IOException,
			InterruptedException {

		String commodity_id = CommodityUtil.getCommodityId(columns,
				"commodity_id");
		String platform_id = CommodityUtil.getCommodityId(columns,
				"platform_id");
		insertData(commodity_id, platform_id);

	}
	public static Map<String, String> getPlatformName(String platform_id){
		 String sqlplatform = "select platform_name,url from uc_platform where id="+platform_id;
		 logger.info(sqlplatform);
	     List<Map<String, String>> platformrs = DbUtils.execute(sqlplatform);
	     return platformrs.get(0);
	}
	public static void insertData(String commodity_id, String platform_id)
			throws JsonGenerationException, JsonMappingException, IOException,
			InterruptedException {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, String>> rs = new ArrayList<Map<String,String>>();
		Map<String, String> platform_param = getPlatformName(platform_id);
        String platform_url = platform_param.get("url");
        String platform_name = platform_param.get("platform_name");
		String sql = "select a.status,a.deleted_at,a.id,a.commodity_name,a.main_pic_url,a.price,a.provider_id,a.brand_id,a.category_id,a.is_display,b.enterprise_name,c.brand_name,c.logo_url,d.parent_id,d.category_name,e.commodity_attr  from"
				+ " comd_commodity as a left join uc_enterprise as b on a.provider_id =b.id left join  comd_brand as c on  a.brand_id = c.id left join  comd_category  as d on a.category_id=d.id left join comd_commodity_ext as e   on a.id=e.id"
				+ " where a.id = " + commodity_id;
		logger.info(sql);
		rs = DbUtils.execute(sql);
		logger.info("frist rs  size--" + rs.size());
		
		List<PrivatePrice> privatePrice = CommodityResult
				.getPrivatePrice(commodity_id);
		
		if (rs.size() > 0) {
			for (Map<String, String> map : rs) {
				
				if (Integer.parseInt(map.get("status")) != 2) {
					List<CommodityAttr> attr = new ArrayList<CommodityAttr>();
					String commodity_attr = map.get("commodity_attr");
					if (!"".equals(commodity_attr) && commodity_attr != null) {
						boolean validity = new JsonValidatorUtil()
								.validate(commodity_attr);
						if (validity) {
							JSONArray array = JSONArray
									.parseArray(commodity_attr);
							int arrsize = array.size();
							for (int i = 0; i < arrsize; i++) {
								String attrKey = array.getJSONObject(i)
										.get("key").toString();
								String attrValue = array.getJSONObject(i)
										.get("value").toString();
								attr.add(new CommodityAttr(attrKey, attrValue));
							}
						}
					}
					boolean is_display = Integer
							.parseInt(map.get("is_display")) == 1 ? true
							: false;

					String payment_cycle = "";
					String payment_method = "";
					boolean is_recommend = false;
					int sales_volume = 0;
					double credit = 0.0;
					String area = "";
					String commodity_name = map.get("commodity_name");
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
					int categroy_id = Integer.parseInt(map.get("category_id"));
					HashMap<String, String> catemap = SynchroData.getCategory();
					HashMap<String, String> categorysMap = SynchroData
							.getCategoryMap(catemap, categroy_id,
									commodity_name);

					Commodity comObject = new Commodity(
							Integer.parseInt(commodity_id), commodity_name,
							commodity_name, attr, map.get("main_pic_url"),
							privatePrice, Double.parseDouble(map.get("price")),
							Integer.parseInt(map.get("provider_id")),
							map.get("enterprise_name"),
							Integer.parseInt(categorysMap.get("category_id")),
							categorysMap.get("category_name"),
							categorysMap.get("category_name"),
							Integer.parseInt(categorysMap
									.get("category_id_two")),
							categorysMap.get("category_name_two"),
							categorysMap.get("category_name_two"),
							Integer.parseInt(categorysMap
									.get("category_id_one")),
							categorysMap.get("category_name_one"),
							categorysMap.get("category_name_one"),
							Integer.parseInt(map.get("brand_id")), brand_name,
							brand_name, brand_name_cn, brand_name_en,
							map.get("logo_url"), is_display,
							Integer.parseInt(platform_id),platform_name,platform_url, payment_cycle,
							payment_method, is_recommend, sales_volume, credit,
							area);

					String json = mapper.writeValueAsString(comObject);
					BulkRequestBuilder bulkRequest = client.prepareBulk();

					String pinyinJson = SuggestPinyinDataUtil.getSuggestPinyin(
							commodity_name, Config.TYPE_WEIGHT_COMMONDITY);
					logger.info("addDocument pinyinJson--" + pinyinJson);
					bulkRequest.add(client.prepareIndex(Config.SUGGEST,
							Config.TYPE, "commodity_" + commodity_id)
							.setSource(pinyinJson));

					bulkRequest.add(client.prepareIndex(Config.INDEXNAME,
							Config.TYPE, commodity_id + "_" + platform_id)
							.setSource(json));
					logger.info("addDocument datajosn--" + json);

					BulkResponse bulkResponse = bulkRequest.execute()
							.actionGet();
					if (bulkResponse.hasFailures()) {
						logger.error(bulkResponse.buildFailureMessage());
					}
				}
			}
		}
	}
}
