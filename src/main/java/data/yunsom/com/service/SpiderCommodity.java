package data.yunsom.com.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

import data.yunsom.com.common.Config;
import data.yunsom.com.dao.ComdAttr;
import data.yunsom.com.dao.ComdBrand;
import data.yunsom.com.dao.ComdTagMeta;
import data.yunsom.com.format.CommodityAttr;
import data.yunsom.com.format.CommoditySpiderAttr;
import data.yunsom.com.format.PrivatePrice;
import data.yunsom.com.format.TagStructure;
import data.yunsom.com.util.CommodityUtil;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.SuggestPinyinDataUtil;
/**
 * tag 体系外网抓取数据添加和更新
 * 
 * */

public class SpiderCommodity {
	private static final Logger logger = LoggerFactory
			.getLogger(SynchBrand.class);
	public static TransportClient client = ElasticsearchUtil.getTransportClient();
	public void synchroData(EventType eventType, List<Column> beforeColumns,
			List<Column> afterColumns) throws JsonGenerationException, JsonMappingException, IOException{
		if (eventType == EventType.INSERT) {
			insertData(afterColumns);
		} else if (eventType == EventType.DELETE) {
			UpdateData(afterColumns);
		} else if (eventType == EventType.UPDATE) {
			
		}
	}
	public void insertData(List<Column> columns) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		int tag_id = Integer.parseInt(CommodityUtil.getCommodityId(columns,
				"tag_id"));
		int  brand_id = Integer.parseInt(CommodityUtil.getCommodityId(columns,
				"brand_id"));
		String commodity_name = CommodityUtil.getCommodityId(columns,
				"commodity_name");
		String commodity_model = CommodityUtil.getCommodityId(columns,
				"commodity_model");
		String commodity_url = CommodityUtil.getCommodityId(columns,
				"commodity_url");
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		/**商品型号添加到搜索提示 start*/
		String pinyinjson = SuggestPinyinDataUtil
				.getSuggestPinyin(commodity_model,
						Config.TYPE_WEIGHT_ATTR);
		bulkRequest.add(client.prepareIndex(
				Config.SUGGEST, Config.TYPE,"attr_" + commodity_url).setSource(pinyinjson));
	
		String commodity_attr =CommodityUtil.getCommodityId(columns,
				"commodity_attr"); 
		List<CommoditySpiderAttr> attrs = (List<CommoditySpiderAttr>) mapper
				.readValue(commodity_attr,
						new TypeReference<List<CommoditySpiderAttr>>() {
						});
		List<CommodityAttr> commodityAttr = new ArrayList<CommodityAttr>();
		if(attrs!=null && attrs.size() >0){
			for (CommoditySpiderAttr attr : attrs) {
				commodityAttr.add(new CommodityAttr(attr.getAttrkey(), attr
						.getKeyname()));
			}
		}
		String commodity_introduce = CommodityUtil.getCommodityId(columns,
				"commodity_introduce");
		String main_pic_url =  CommodityUtil.getCommodityId(columns,
				"main_pic_url");
		String price =  CommodityUtil.getCommodityId(columns,
				"price");
		String from = CommodityUtil.getCommodityId(columns,
				"from");
		String sql = "select tag_id_one,tag_id_two,tag_id from comd_tag_meta where id="+tag_id;
		List<Map<String, String>> tags = DbUtils.execute(sql);
		List<Integer> tag_arr = new ArrayList<Integer>();
		int tag_id_one = Integer.parseInt(tags.get(0).get("tag_id_one"));
		int tag_id_two = Integer.parseInt(tags.get(0).get("tag_id_two"));
		
		tag_arr.add(tag_id_one);
		tag_arr.add(tag_id_two);
		tag_arr.add(tag_id);
		ComdTagMeta comdtagMeta =  new ComdTagMeta();
		Map<Integer, String> mapTag = comdtagMeta.getIDTag(tag_arr);
		ComdBrand comdbrand =  new ComdBrand();
		String brand_name = comdbrand.getIdToName(brand_id);
		Map<String, String> brand_name_arr =  comdbrand.nameToEnAndCn(brand_name);
		List<PrivatePrice> private_price =  new ArrayList<PrivatePrice>();
		HashMap<String, String> froms = getFroms();
		String[] providers =froms.get(from).split("_");
		TagStructure tagstructure =  new TagStructure(0, commodity_name, brand_id, brand_name, brand_name_arr.get("brand_name_en"), brand_name_arr.get("brand_name_cn"), commodity_model,
				tag_id_one, mapTag.get("tag_id_one"), tag_id_two, mapTag.get("tag_id_two"), tag_id, mapTag.get("tag_id"), commodityAttr, main_pic_url, commodity_url, private_price, Double.parseDouble(price), 
				Integer.parseInt(providers[1]), providers[0], "", Integer.parseInt(providers[1]), providers[0], "", true, "", "", true, 0,0.0, "", from, new ArrayList<String>());
		String json = mapper.writeValueAsString(tagstructure);
		

		
		bulkRequest.add(client.prepareIndex(Config.TAGINDEXNAME,
				Config.TYPE, "commodity_" + commodity_url)
				.setSource(json));
		logger.info("addDocument datajosn--" + json);

		BulkResponse bulkResponse = bulkRequest.execute()
				.actionGet();
		if (bulkResponse.hasFailures()) {
			logger.error(bulkResponse.buildFailureMessage());
		}
	}
	
	public void UpdateData(List<Column> columns) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		int tag_id = Integer.parseInt(CommodityUtil.getCommodityId(columns,
				"tag_id"));
		int  brand_id = Integer.parseInt(CommodityUtil.getCommodityId(columns,
				"brand_id"));
		String commodity_name = CommodityUtil.getCommodityId(columns,
				"commodity_name");
		String commodity_model = CommodityUtil.getCommodityId(columns,
				"commodity_model");
		String commodity_url = CommodityUtil.getCommodityId(columns,
				"commodity_url");
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		/**商品型号添加到搜索提示 start*/
		String pinyinjson = SuggestPinyinDataUtil
				.getSuggestPinyin(commodity_model,
						Config.TYPE_WEIGHT_ATTR);
		bulkRequest.add(client.prepareIndex(
				Config.SUGGEST, Config.TYPE,"attr_" + commodity_url).setSource(pinyinjson));
		String commodity_attr =CommodityUtil.getCommodityId(columns,
				"commodity_attr"); 
		ComdAttr comdAttr = new ComdAttr();
		List<CommodityAttr> commodityAttr = comdAttr.attrStringToObect(commodity_attr);
		String commodity_introduce = CommodityUtil.getCommodityId(columns,
				"commodity_introduce");
		String main_pic_url =  CommodityUtil.getCommodityId(columns,
				"main_pic_url");
		String price =  CommodityUtil.getCommodityId(columns,
				"price");
		String from = CommodityUtil.getCommodityId(columns,
				"from");
		String sql = "select tag_id_one,tag_id_two,tag_id from comd_tag_meta where id="+tag_id;
		List<Map<String, String>> tags = DbUtils.execute(sql);
		List<Integer> tag_arr = new ArrayList<Integer>();
		int tag_id_one = Integer.parseInt(tags.get(0).get("tag_id_one"));
		int tag_id_two = Integer.parseInt(tags.get(0).get("tag_id_two"));
		
		tag_arr.add(tag_id_one);
		tag_arr.add(tag_id_two);
		tag_arr.add(tag_id);
		ComdTagMeta comdtagMeta =  new ComdTagMeta();
		Map<Integer, String> mapTag = comdtagMeta.getIDTag(tag_arr);
		ComdBrand comdbrand =  new ComdBrand();
		String brand_name = comdbrand.getIdToName(brand_id);
		Map<String, String> brand_name_arr =  comdbrand.nameToEnAndCn(brand_name);
		List<PrivatePrice> private_price =  new ArrayList<PrivatePrice>();
		HashMap<String, String> froms = getFroms();
		String[] providers =froms.get(from).split("_");
		TagStructure tagstructure =  new TagStructure(0, commodity_name, brand_id, brand_name, brand_name_arr.get("brand_name_en"), brand_name_arr.get("brand_name_cn"), commodity_model,
				tag_id_one, mapTag.get("tag_id_one"), tag_id_two, mapTag.get("tag_id_two"), tag_id, mapTag.get("tag_id"), commodityAttr, main_pic_url, commodity_url, private_price, Double.parseDouble(price), 
				Integer.parseInt(providers[1]), providers[0], "", Integer.parseInt(providers[1]), providers[0], "", true, "", "", true, 0,0.0, "", from, new ArrayList<String>());
		String json = mapper.writeValueAsString(tagstructure);
		

		
		bulkRequest.add(client.prepareUpdate(Config.TAGINDEXNAME,
				Config.TYPE, "commodity_" + commodity_url)
				.setDoc(json));
		logger.info("addDocument datajosn--" + json);

		BulkResponse bulkResponse = bulkRequest.execute()
				.actionGet();
		if (bulkResponse.hasFailures()) {
			logger.error(bulkResponse.buildFailureMessage());
		}
	}
	
	public  HashMap<String, String> getFroms() {
		
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("315mro", "传奇商城_100000");
		data.put("4006770558", "网尚购_100001");
		data.put("91yilong", "易隆商城_100002");
		data.put("btone-mro", "丙通MRO_100003");
		data.put("deppre", "德普瑞工业商城_100004");
		data.put("gongchang", "工厂易购_100005");
		data.put("ispek", "思贝壳_100006");
		data.put("isweek", "工采网_100007");
		data.put("mrobay", "陌贝网_100008");
		data.put("makepolo", "马可波罗_100009");
		data.put("rolymro", "苏州雷利_100010");
		data.put("seton", "赛盾_100011");
		data.put("zkh360", "震坤行工业超市_100012");
		data.put("ehsy", "西域_100013");
		data.put("haocaimao", "好采猫_100014");
		data.put("8shop", "都工业网_100015");
		data.put("mctmall", "中国工量具商城_100016");
		data.put("grainger", "固安捷_100017");
		data.put("wwmro", "土狼_100018");
		data.put("iacmall", "艾驰商城_100019");
		data.put("vipmro", "工品汇_100020");
		data.put("1ez", "一站工材_100021");
		data.put("axmro", "艾逊_100022");
		data.put("gomro", "固买网_100023");
		data.put("hc360", "慧聪网_100024");
		data.put("zgw", "中钢网_100025");
		data.put("huaaomro", "华澳MRO商城_100026");
		return data;
	}
}
