package data.yunsom.com.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.common.Config;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.PatternUitl;
/***
 * 数据库品牌表操作
 * */
public class ComdBrand {
	private static final TransportClient client = ElasticsearchUtil
			.getTransportClient();
	static Logger logger = LoggerFactory.getLogger(ComdBrand.class);
	/***
	 * 根据品牌ID获取品牌名称
	 * map<k,v>
	 * */
	public String getIdToName(int brand_id){
		String sql ="select id,brand_name from comd_brand where id="+brand_id;
		List<Map<String, String>> tags = DbUtils.execute(sql);
		//HashMap<Integer, String> map =  new HashMap<Integer, String>();
		
		return tags.get(0).get("brand_name");
	}
	/***
	 * 获取所有品牌
	 * map<k,v>
	 * */
	public HashMap<String, String> getAllBrandName(){
		String sql ="select id,brand_name from comd_brand ";
		List<Map<String, String>> brands = DbUtils.execute(sql);
		HashMap<String, String> map =  new HashMap<String, String>();
		for (Map<String, String> brand : brands) {
			map.put(brand.get("id"), brand.get("brand_name"));
		}
		return map;
	}
	/**
	 * 根据品牌获取ID
	 * **/
	public int matchBrand( String keyword) {
		int brand_id=0;
		BoolQueryBuilder bqb = QueryBuilders.boolQuery();
		HashMap<String, String> brand_map =  PatternUitl.pattern(keyword); 
		bqb.should(QueryBuilders.termQuery("brand_name", keyword));
		bqb.should(QueryBuilders.termQuery("brand_name_cn", brand_map.get("cn")));
		bqb.should(QueryBuilders.termQuery("brand_name_en", brand_map.get("en")));
		
		SearchRequestBuilder searchRequestBuilder = client
				.prepareSearch(Config.INDEXNAMEBRNAD);
		searchRequestBuilder.setTypes(Config.INDEXNAMEBRNAD);
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setQuery(bqb);
		//logger.info(searchRequestBuilder.toString());
		SearchResponse searchResponse = searchRequestBuilder.execute()
				.actionGet();
		if (searchResponse.getHits().getTotalHits() > 0) {
			SearchHit[] searchHit = searchResponse.getHits().getHits();
			brand_id =  Integer.parseInt(searchHit[0].getSource().get("brand_id")
					.toString());
			return brand_id;
			
		} 
		return brand_id;

	}
	/**
	 * 根据品牌名称获取中英文
	 * */
	public Map<String, String> nameToEnAndCn(String brand_name){
		HashMap<String, String> map = new HashMap<String, String>();
		String brand_name_cn = "";
		String brand_name_en = "";
		if (!brand_name.equals("") && brand_name!=null && brand_name.indexOf("/") > 0) {
			String[] brand_arr = brand_name.split("/");
			brand_name_cn = brand_arr[0];
			if(brand_arr.length>1){
				brand_name_en = brand_arr[1];
			}

		} else {
			brand_name_cn = brand_name;
			brand_name_en = brand_name;
		}
		map.put("brand_name_cn", brand_name_cn);
		map.put("brand_name_en", brand_name_en.toLowerCase());
		return map;
	}
}
