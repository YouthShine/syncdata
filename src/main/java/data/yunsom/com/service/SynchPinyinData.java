package data.yunsom.com.service;



import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.common.Config;
import data.yunsom.com.format.CategoryMapOne;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.SuggestPinyinDataUtil;

public class SynchPinyinData {

	private static final Logger logger = LoggerFactory
			.getLogger(SynchPinyinData.class);
	public static TransportClient client = ElasticsearchUtil.getTransportClient();

	/**
	 * 写入分类拼音数据
	 * 
	 * **/
	public static void pushCategoryData()  {

		BulkRequestBuilder bulkRequest = client.prepareBulk();

		String sql = "select id,category_name   from comd_category ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			String category_name = map.get("category_name");

			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(
					category_name, Config.TYPE_WEIGHT_CATE);
			logger.info(pinyinjson);
			bulkRequest.add(client.prepareIndex(Config.SUGGEST,
					Config.TYPE, "category_" + map.get("id"))
					.setSource(pinyinjson));
		}

		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			logger.info(bulkResponse.buildFailureMessage());
		}

	}
	/**
	 * 写入分类拼音数据
	 * 
	 * **/
	public static void pushTagData()  {

		BulkRequestBuilder bulkRequest = client.prepareBulk();

		String sql = "select id,tag_name   from comd_tag ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			String category_name = map.get("tag_name");
			
			if(!category_name.equals("") && category_name!=null){
			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(
					category_name, Config.TYPE_WEIGHT_CATE);
			logger.info(pinyinjson);
			bulkRequest.add(client.prepareIndex(Config.SUGGEST,
					Config.TYPE, "tag_" + map.get("id"))
					.setSource(pinyinjson));
			}
		}

		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			logger.info(bulkResponse.buildFailureMessage());
		}

	}
	/**
	 * 写入分类拼音数据
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * 
	 * **/
	public static void pushCategoryOne() throws JsonGenerationException, JsonMappingException, IOException  {

		BulkRequestBuilder bulkRequest = client.prepareBulk();
		ObjectMapper mapper = new ObjectMapper();

		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		String sql = "select id,category_name,mapping_name   from spider_category_one_map ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			String category_name = map.get("category_name");
			String category_mapping = map.get("mapping_name");
			int id = Integer.parseInt(map.get("id"));
			String[] categorys = category_mapping.split(",");
			String category_nameweb = "";
			int type=0;
			for (int i = 0; i < categorys.length; i++) {
				int num = categorys[i].indexOf("|");
				if(num>0){
					 category_nameweb=categorys[i].substring(0, num);
					 type=1;
				}else{
					category_nameweb=categorys[i];
				}
				CategoryMapOne data = new CategoryMapOne(id, type, category_name, category_nameweb);
				String json =mapper.writeValueAsString(data);
				logger.info(json);
				bulkRequest.add(client.prepareIndex(Config.INDEXNAMECATEGORYONE,
						Config.TYPE)
						.setSource(json));
			}
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			logger.info(bulkResponse.buildFailureMessage());
		}

	}
	/**
	 * 写入品牌拼音数据
	 * 
	 * **/
	public static void pushBrandData()  {

		BulkRequestBuilder bulkRequest = client.prepareBulk();

		String sql = "select id,brand_name   from comd_brand  ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			String brand_name = map.get("brand_name");
			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(brand_name,
					Config.TYPE_WEIGHT_BRAND);
			logger.info(pinyinjson);

			bulkRequest.add(client.prepareIndex(Config.SUGGEST,
					Config.TYPE, "brand_" + map.get("id"))
					.setSource(pinyinjson));

		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			logger.info(bulkResponse.buildFailureMessage());
		}

	}

	/**
	 * 写入企业拼音数据
	 * 
	 * **/
	public static void pushEnterpriseData() {

		BulkRequestBuilder bulkRequest = client.prepareBulk();

		String sql = "select id,enterprise_name   from uc_enterprise  ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			String enterprise_name = map.get("enterprise_name");

			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(
					enterprise_name, Config.TYPE_WEIGHT_ENTERPRISE);
			logger.info(pinyinjson);
			bulkRequest.add(client
					.prepareIndex(Config.SUGGEST,
							Config.TYPE,
							"enterprise_" + map.get("id"))
					.setSource(pinyinjson));
		}

		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			logger.info(bulkResponse.buildFailureMessage());
		}

	}
}
