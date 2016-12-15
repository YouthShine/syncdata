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

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

import data.yunsom.com.common.Config;
import data.yunsom.com.dao.ComdAttr;
import data.yunsom.com.dao.ComdBrand;
import data.yunsom.com.dao.ComdCategory;
import data.yunsom.com.dao.ComdPrivPrice;
import data.yunsom.com.dao.UcPlatform;
import data.yunsom.com.format.CommodityAttr;
import data.yunsom.com.format.PrivatePrice;
import data.yunsom.com.format.TagStructure;
import data.yunsom.com.util.CommodityUtil;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.SuggestPinyinDataUtil;
/***
 * 
 * 删除和写入elasticsearch
 * */
public class ComdPlaformTag {

	private static final TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static final Logger logger = LoggerFactory
			.getLogger(ComdPlaformTag.class);

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
	public  void deletePlaform(List<Column> columns) {
		String comm_id = CommodityUtil.getCommodityId(columns, "commodity_id");
		String platform_id = CommodityUtil.getCommodityId(columns,
				"platform_id");
		deleteData(comm_id, platform_id);
	}
	/**
	 * 删除搜索中的商品数据
	 * 删除搜索提示中的型号数据
	 * 	 * */
	public  void deleteData(String comm_id, String platform_id) {
		DeleteResponse responsesh = client.prepareDelete(Config.INDEXNAME, Config.TYPE,
						comm_id + "_" + platform_id).execute().actionGet();
		DeleteResponse responsesuggest = client.prepareDelete(Config.SUGGEST, Config.TYPE,
						"attr_" + comm_id).execute().actionGet();
		logger.info("commodity--id=" + responsesh.getId() + "--suggest--id="
				+ responsesuggest.getId());
		if (responsesh.getId().equals(null)||responsesuggest.equals(null)) {
			logger.info("faild delete " + comm_id + "_" + platform_id);
		}

	}
	public  void insertPlaform(List<Column> columns)
			throws JsonGenerationException, JsonMappingException, IOException,
			InterruptedException {

		String commodity_id = CommodityUtil.getCommodityId(columns,
				"commodity_id");
		String platform_id = CommodityUtil.getCommodityId(columns,
				"platform_id");
		insertData(commodity_id, platform_id);

	}
	public  void insertData(String commodity_id, String platform_id)
			throws JsonGenerationException, JsonMappingException, IOException,
			InterruptedException {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, String>> rs = new ArrayList<Map<String,String>>();
		UcPlatform ucplatform = new UcPlatform();
		
		Map<String, String> platform_param = ucplatform.getPlatformName(Integer.parseInt(platform_id));
        String platform_url = platform_param.get("url");
        String platform_name = platform_param.get("platform_name");
		String sql = "select a.status,a.deleted_at,a.id,a.commodity_name,a.main_pic_url,a.price,a.provider_id,a.brand_id,a.category_id,a.is_display,b.enterprise_name,c.brand_name,c.logo_url,d.parent_id,d.category_name,e.commodity_attr  from"
				+ " comd_commodity as a left join uc_enterprise as b on a.provider_id =b.id left join  comd_brand as c on  a.brand_id = c.id left join  comd_category  as d on a.category_id=d.id left join comd_commodity_ext as e   on a.id=e.id"
				+ " where a.id = " + commodity_id;
		logger.info(sql);
		rs = DbUtils.execute(sql);
		ComdPrivPrice comdprivprice = new ComdPrivPrice();
		List<PrivatePrice> privatePrice = comdprivprice.getPrivatePrice(commodity_id);
		
		if (rs.size() > 0) {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for (Map<String, String> map : rs) {
				int status = Integer.parseInt(map.get("status"));
				String deleted_at = map.get("deleted_at");
				int display = Integer.parseInt(map.get("is_display"));
				if ( status!= 2 &&deleted_at==null && display == 1){
					String commodity_attr = map.get("commodity_attr");
					ComdAttr comdAttr =  new ComdAttr();
					List<CommodityAttr> commodityAttr= comdAttr.attrStringToObect(commodity_attr);
					String commodity_model = comdAttr.getCommodityModel(commodity_attr);
					/**商品型号添加到搜索提示 start*/
					String pinyinjson = SuggestPinyinDataUtil
							.getSuggestPinyin(commodity_model,
									Config.TYPE_WEIGHT_ATTR);
					bulkRequest.add(client.prepareIndex(
							Config.SUGGEST, Config.TYPE,"attr_" + commodity_id).setSource(pinyinjson));
					boolean is_display = display == 1 ? true
							: false;

					String payment_cycle = "";
					String payment_method = "";
					boolean is_recommend = false;
					int sales_volume = 0;
					double credit = 0.0;
					String area = "";
					String commodity_name = map.get("commodity_name");
					String brand_name = map.get("brand_name");
					String category_name = map.get("category_name");
					int categroy_id = Integer.parseInt(map.get("category_id"));
					int brand_id = Integer.parseInt(map.get("brand_id"));
					ComdBrand comdbrand =  new ComdBrand();
					Map<String, String> brand_name_attr = new HashMap<String, String>();
					if(brand_name!=null){
						 brand_name_attr =  comdbrand.nameToEnAndCn(brand_name);
					}
					ComdCategory comdCategory = new ComdCategory();
					HashMap<String, String> categroyMap = comdCategory.getCategory();
					HashMap<String, String> catemap =comdCategory.getCategoryMap(categroyMap, categroy_id, category_name);
					List<String> custom_tag = new ArrayList<String>();
					TagStructure tagObject = new TagStructure(Integer.parseInt(commodity_id), commodity_name, brand_id, brand_name, 
							brand_name_attr.get("brand_name_en"), brand_name_attr.get("brand_name_cn"), commodity_model,
							Integer.parseInt(catemap.get("category_id_one")), catemap.get("category_name_one"), Integer.parseInt(catemap.get("category_id_two")), catemap.get("category_name_two"), categroy_id, category_name, commodityAttr,
							map.get("main_pic_url"), "", privatePrice, Double
							.parseDouble(map.get("price")), Integer.parseInt(map
									.get("provider_id")), map.get("enterprise_name"), map
									.get("logo_url"), Integer.parseInt(platform_id), platform_name, platform_url, is_display, payment_cycle, payment_method, is_recommend, sales_volume, credit, area, "www.yunsom.com", custom_tag);
					String json = mapper.writeValueAsString(tagObject);

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
