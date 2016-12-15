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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;











import data.yunsom.com.common.Config;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;

public class KeywordOrder {
	private static final TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static final Logger logger = LoggerFactory
			.getLogger(CommodityPlaform.class);
	public static List<Map<String,String>> getKeyWord() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		String sql ="select sum(frequency) as num,kyeword from logkeywords where type=\"search\" and kyeword<>\"\" group by kyeword having num>10";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> data:rs) {
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			
			boolQueryBuilder.must(QueryBuilders.termQuery("name", data.get("kyeword")));
			SearchResponse response = client.prepareSearch(Config.SUGGEST)
					.setTypes(Config.TYPE)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setQuery(boolQueryBuilder).execute()
					.actionGet();
			SearchHits hits = response.getHits();
			if(hits.getTotalHits()>0){
				String id = hits.getHits()[0].getId();
				
				Map<String, Object>	map = hits.getHits()[0].getSource();
			    map.put("type", data.get("num"));
			    Map<String, Object>  mapsuggest=  (Map<String, Object>) map.get("tag_suggest");
			    mapsuggest.put("weight", data.get("num"));
			    map.put("tag_suggest", mapsuggest);
				String json = mapper.writeValueAsString(map);
				
				client.prepareUpdate(Config.SUGGEST,
						Config.TYPE, id).setDoc(json).execute().actionGet();
				
			}
		}
		
		return rs;
	}
}
