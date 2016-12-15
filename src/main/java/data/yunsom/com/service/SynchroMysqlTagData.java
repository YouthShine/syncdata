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
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.common.Config;
import data.yunsom.com.dao.ComdAttr;
import data.yunsom.com.dao.ComdBrand;
import data.yunsom.com.dao.ComdCategory;
import data.yunsom.com.dao.ComdCommodity;
import data.yunsom.com.dao.ComdPlatformRef;
import data.yunsom.com.dao.ComdTagMeta;
import data.yunsom.com.dao.SpiderCommodityDao;
import data.yunsom.com.dao.UcPlatform;
import data.yunsom.com.format.CommodityAttr;
import data.yunsom.com.format.CommoditySpiderAttr;
import data.yunsom.com.format.PrivatePrice;
import data.yunsom.com.format.TagStructure;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.SuggestPinyinDataUtil;

/***
 * 数据库数据批量同步到Elasticsearch
 * 
 * */
public class SynchroMysqlTagData {
	private static final Logger logger = LoggerFactory
			.getLogger(SynchroMysqlTagData.class);
	private static TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static List<PrivatePrice> privatePrice = new ArrayList<PrivatePrice>();

	public static void synchroData(int commodity_id) throws JsonGenerationException,
			JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		ComdAttr comdAttr = new ComdAttr();
		ComdCommodity comdCommodity = new ComdCommodity();
		int maxId = comdCommodity.getCommodityMaxID();
		int size = 1000;
		ComdCategory comdCategory = new ComdCategory();
		HashMap<String, String> categroyMap = comdCategory.getCategory();
		ComdPlatformRef comdPlatfromref = new ComdPlatformRef();
		HashMap<Integer, List<Integer>> platfrommap = comdPlatfromref
				.getPlatformAll();
		logger.info("maxId==" + maxId);
		while (maxId >= commodity_id) {
			String sql = "select a.deleted_at,a.status,a.id,a.commodity_name,a.main_pic_url,a.price,a.provider_id,a.brand_id,a.category_id,a.is_display,b.enterprise_name,c.brand_name,c.logo_url,d.parent_id,d.category_name,e.commodity_attr  from"
					+ " comd_commodity as a left join uc_enterprise as b on a.provider_id =b.id left join  comd_brand as c on  a.brand_id = c.id left join  comd_category  as d on a.category_id=d.id left join comd_commodity_ext as e   on a.id=e.id"
					+ " where a.id> " + commodity_id + "   limit " + size;
			logger.info("sql--" + sql);
			List<Map<String, String>> rs = DbUtils.execute(sql);
			logger.info("rs size--" + rs.size());

			List<String> custom_tag = new ArrayList<String>();
			String payment_cycle = "";
			String payment_method = "";
			boolean is_recommend = false;
			int sales_volume = 0;
			double credit = 0.0;
			String area = "";
			if (rs.size() > 0) {
				BulkRequestBuilder bulkRequest = client.prepareBulk();
				for (Map<String, String> map : rs) {
					int tmp_commodity_id = Integer.parseInt(map.get("id"));
					int display = Integer.parseInt(map.get("is_display"));
					boolean is_display = display == 1 ? true : false;
					int status = Integer.parseInt(map.get("status"));
					String deleted_at = map.get("deleted_at");
					if (deleted_at == null && status != 2 && display == 1) {
						commodity_id = tmp_commodity_id;
						String commodity_attr = map.get("commodity_attr");
						List<CommodityAttr> commodityAttr = comdAttr
								.attrStringToObect(commodity_attr);
						String commodity_model = comdAttr
								.getCommodityModel(commodity_attr);
						/** 商品型号添加到搜索提示 start */
						String pinyinjson = SuggestPinyinDataUtil
								.getSuggestPinyin(commodity_model,
										Config.TYPE_WEIGHT_ATTR);
						bulkRequest.add(client.prepareIndex(Config.SUGGEST,
								Config.TYPE, "attr_" + tmp_commodity_id)
								.setSource(pinyinjson));
						/** 商品型号添加到搜索提示 end */
						String commodity_name = map.get("commodity_name");
						String category_name = map.get("category_name");
						String brand_name = map.get("brand_name");
						if(brand_name==null || brand_name.equals("")){
							continue;
						}
						int brand_id = Integer.parseInt(map.get("brand_id"));
						ComdBrand comdbrand = new ComdBrand();
						Map<String, String> brand_name_attr = comdbrand
								.nameToEnAndCn(brand_name);
						List<Integer> platforms = platfrommap
								.get(tmp_commodity_id);
						int categroy_id = Integer.parseInt(map
								.get("category_id"));
						HashMap<String, String> catemap = comdCategory
								.getCategoryMap(categroyMap, categroy_id,
										category_name);
						if (platforms != null) {
							for (Integer platform : platforms) {
								UcPlatform ucplatforms = new UcPlatform();
								Map<String, String> platform_params = ucplatforms
										.getPlatformName(platform);
								TagStructure tagObject = new TagStructure(
										tmp_commodity_id,
										commodity_name,
										brand_id,
										brand_name,
										brand_name_attr.get("brand_name_en"),
										brand_name_attr.get("brand_name_cn"),
										commodity_model,
										Integer.parseInt(catemap
												.get("category_id_one")),
										catemap.get("category_name_one"),
										Integer.parseInt(catemap
												.get("category_id_two")),
										catemap.get("category_name_two"),
										categroy_id,
										category_name,
										commodityAttr,
										map.get("main_pic_url"),
										"",
										privatePrice,
										Double.parseDouble(map.get("price")),
										Integer.parseInt(map.get("provider_id")),
										map.get("enterprise_name"), map
												.get("logo_url"), platform,
										platform_params.get("platform_name"),
										platform_params.get("url"), is_display,
										payment_cycle, payment_method,
										is_recommend, sales_volume, credit,
										area, "www.yunsom.com", custom_tag);
								String json = mapper
										.writeValueAsString(tagObject);
								// logger.info("---"+json);
								bulkRequest.add(client.prepareIndex(
										Config.TAGINDEXNAME, Config.TYPE,
										tmp_commodity_id + "_" + platform)
										.setSource(json));
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

	public static void synchroDataSpider(int commodity_id)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		SpiderCommodityDao spiderCommoditydao = new SpiderCommodityDao();
		int maxId = spiderCommoditydao.getCommodityMaxID();
		int size = 1000;
		ComdTagMeta comdtagMeta = new ComdTagMeta();
		Map<String, String> tagMetas = comdtagMeta.getAllTagMeta();
		Map<String, String> tags = comdtagMeta.getAllTag(1);
		ComdBrand comdBrand = new ComdBrand();
		Map<String, String> brands = comdBrand.getAllBrandName();
		HashMap<String, String> froms = getSpiderPlatform();
		while (maxId >= commodity_id) {
			String sql = "select id,commodity_name,commodity_model,commodity_url,tag_id,brand_id,commodity_attr,main_pic_url,price,`from` from spider_commodity "
					+ " where id> " + commodity_id + "   limit " + size;
			logger.info("sql--" + sql);
			List<Map<String, String>> rs = DbUtils.execute(sql);
			logger.info("rs size--" + rs.size());
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for (Map<String, String> data : rs) {
				commodity_id = Integer.parseInt(data.get("id"));
				String tag_meta = tagMetas.get(data.get("tag_id"));
				int tag_id_one=0,tag_id_two=0,tag_id=0;
				String tag_name_one="",tag_name_two="",tag_name ="";
				if (tag_meta != null) {
					
				
					String[] tag_metas = tag_meta.split("_");
					tag_id_one = Integer.parseInt(tag_metas[0]);
					tag_name_one = tags.get(tag_metas[0]);
					tag_id_two = Integer.parseInt(tag_metas[1]);
					tag_name_two = tags.get(tag_metas[1]);
					tag_id = Integer.parseInt(tag_metas[2]);
					tag_name = tags.get(tag_metas[2]);
				}
				int brand_id = Integer.parseInt(data.get("brand_id"));
			
				if(brand_id==0){
					continue;
				}
				String brand_name = brands.get(data.get("brand_id"));
				
				Map<String, String> brand_name_arr = comdBrand
						.nameToEnAndCn(brand_name);
				
				List<PrivatePrice> private_price = new ArrayList<PrivatePrice>();
				String commodity_name = data.get("commodity_name");
				String commodity_model = data.get("commodity_model");
				/**商品型号加入搜索提示**/
				String pinyinjson = SuggestPinyinDataUtil
						.getSuggestPinyin(commodity_model,
								Config.TYPE_WEIGHT_ATTR);
				bulkRequest.add(client.prepareIndex(
						Config.SUGGEST, Config.TYPE,"attr_" + commodity_id).setSource(pinyinjson));
				/**商品型号加入搜索提示**/
				String[] providers = froms.get(data.get("from")).split("_");
				String main_pic_url = data.get("main_pic_url");
				double price = Double.parseDouble(data.get("price"));
				String commodity_url = data.get("commodity_url");
				String commodity_attr = data.get("commodity_attr");
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
				TagStructure tagstructure = new TagStructure(0, commodity_name,
						brand_id, brand_name,
						brand_name_arr.get("brand_name_en"),
						brand_name_arr.get("brand_name_cn"), commodity_model,
						tag_id_one, tag_name_one, tag_id_two, tag_name_two,
						tag_id, tag_name, commodityAttr, main_pic_url,
						commodity_url, private_price, price,
						Integer.parseInt(providers[1]), providers[0], "",
						Integer.parseInt(providers[1]), providers[0], "", true,
						"", "", true, 0, 0.0, "", data.get("from"),
						new ArrayList<String>());
				String json = mapper.writeValueAsString(tagstructure);
				bulkRequest.add(client.prepareIndex(Config.TAGINDEXNAME,
						Config.TYPE, commodity_url).setSource(json));
				logger.info("addDocument datajosn--" + json);

			}
			if(bulkRequest.numberOfActions()>0){
				BulkResponse bulkResponse = bulkRequest.execute().actionGet();
				if (bulkResponse.hasFailures()) {
					logger.error(bulkResponse.buildFailureMessage());
				}
			}
			if(rs.size()==0){
				break;
			}
		}
	}

	public static HashMap<String, String> getSpiderPlatform() {

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("www.315mro.com", "传奇商城_100000");
		data.put("www.4006770558.com", "网尚购_100001");
		data.put("www.91yilong.com", "易隆商城_100002");
		data.put("www.btone-mro.com", "丙通MRO_100003");
		data.put("www.deppre.cn", "德普瑞工业商城_100004");
		data.put("www.gongchang.com", "工厂易购_100005");
		data.put("www.ispek.cn", "思贝壳_100006");
		data.put("www.isweek.cn", "工采网_100007");
		data.put("zc.mrobay.com", "陌贝网_100008");
		data.put("china.makepolo.com", "马可波罗_100009");
		data.put("www.rolymro.com", "苏州雷利_100010");
		data.put("www.seton.com.cn", "赛盾_100011");
		data.put("www.zkh360.com", "震坤行工业超市_100012");
		data.put("www.ehsy.com", "西域_100013");
		data.put("www.haocaimao.com", "好采猫_100014");
		data.put("www.8shop.cc", "都工业网_100015");
		data.put("www.mctmall.cn", "中国工量具商城_100016");
		data.put("item.grainger.cn", "固安捷_100017");
		data.put("www.wwmro.com", "土狼_100018");
		data.put("www.iacmall.com", "艾驰商城_100019");
		// data.put("vipmro", "工品汇_100020");
		data.put("www.1ez.com.cn", "一站工材_100021");
		data.put("www.axmro.com", "艾逊_100022");
		data.put("www.gomro.cn", "固买网_100023");
		data.put("b2b.hc360.com", "慧聪网_100024");
		// data.put("zgw", "中钢网_100025");
		data.put("www.sssmro.com", "好快省工业超市_100027");
		data.put("www.huaaomro.com", "华澳MRO商城_100026");
		return data;
	}

}
