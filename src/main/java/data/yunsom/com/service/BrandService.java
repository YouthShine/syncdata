package data.yunsom.com.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.client.transport.TransportClient;

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

import data.yunsom.com.common.Config;
import data.yunsom.com.dao.ComdBrand;
import data.yunsom.com.format.BrandMapping;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.RedisUtil;
import data.yunsom.com.util.SuggestPinyinDataUtil;

/**
 * 更新商品品牌名称
 * */
public class BrandService {

	private static RedisUtil redisutil = new RedisUtil();
	private static final TransportClient client = ElasticsearchUtil
			.getTransportClient();
	/***
	 * 更新品牌名称到redis和elasticsearch
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * */
	public void updateBrand(EventType eventType, List<Column> beforeColumns,
			List<Column> afterColumns) throws JsonGenerationException, JsonMappingException, IOException {
		String brand_id = "";
		String brand_name = "";
		for (Column data : afterColumns) {
			if (data.getName().equals("id")) {
				brand_id = data.getValue();
			}
			if (data.getName().equals("brand_name")) {
				brand_name = data.getValue();
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		ComdBrand comdBrand =  new ComdBrand();
		Map<String, String> brand_map =  comdBrand.nameToEnAndCn(brand_name);
		BrandMapping resJson =  new BrandMapping(Integer.parseInt(brand_id), brand_name, brand_map.get("brand_name_en").toLowerCase(), brand_map.get("brand_name_cn"));
		String json = mapper.writeValueAsString(resJson);
		if (eventType == EventType.INSERT) {
			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(brand_name, Config.TYPE_WEIGHT_BRAND);
			client.prepareIndex(Config.INDEXNAMEBRNAD, Config.INDEXNAMEBRNAD,brand_id).setSource(json).execute().actionGet();
			client.prepareIndex(Config.SUGGEST, Config.TYPE,"brand_" + brand_id).setSource(pinyinjson).execute().actionGet();
			redisutil.set("brandname_" + brand_id, brand_name);

		} else if (eventType == EventType.UPDATE) {
			redisutil.set("brandname_" + brand_id, brand_name);
			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(brand_name, Config.TYPE_WEIGHT_BRAND);
			client.prepareIndex(Config.SUGGEST, Config.TYPE,"brand_" + brand_id).setSource(pinyinjson).execute().actionGet();
			client.prepareIndex(Config.INDEXNAMEBRNAD, Config.INDEXNAMEBRNAD,brand_id).setSource(json).execute().actionGet();
		} else if (eventType == EventType.DELETE) {
			client.prepareDelete(Config.INDEXNAMEBRNAD, Config.INDEXNAMEBRNAD, brand_id).execute().actionGet();
			client.prepareDelete(Config.SUGGEST, Config.TYPE, "brand_"+ brand_id).execute().actionGet();
			redisutil.del("brandname_" + brand_id);
		}

	}

}
