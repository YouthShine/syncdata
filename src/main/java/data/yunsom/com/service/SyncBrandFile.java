package data.yunsom.com.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.common.Config;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.FileOperate;
import data.yunsom.com.util.PatternUitl;

public class SyncBrandFile {
	private static final Logger logger = LoggerFactory
			.getLogger(SyncBrandFile.class);
	public static TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static final FileOperate filecate = new FileOperate();
	public static void fileToElastic() throws IOException {
		List<String> datas = filecate.readTxt("D:\\code\\data\\new_brand.txt", "utf-8");
		HashSet<String> set = new HashSet<String>();
		for (String brand_name : datas) {
			int len=brand_name.length();
			//System.out.println(len);
			if(len>2 && len<15){
				
				set.add(brand_name);
			}
		}
		for (String data : set) {
			
				BoolQueryBuilder bqb = QueryBuilders.boolQuery();
				HashMap<String, String> map = PatternUitl.pattern(data);
				bqb.should(QueryBuilders.termQuery("brand_name", data));
				bqb.should(QueryBuilders.termQuery("brand_name_cn", map.get("cn")));
				bqb.should(QueryBuilders.termQuery("brand_name_en", map.get("en")));
				
				SearchRequestBuilder searchRequestBuilder = client
						.prepareSearch(Config.INDEXNAMEBRNAD);
				searchRequestBuilder.setTypes(Config.INDEXNAMEBRNAD);
				searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
				searchRequestBuilder.setQuery(bqb);
				logger.info(searchRequestBuilder.toString());
				SearchResponse searchResponse = searchRequestBuilder.execute()
						.actionGet();
				
				if(searchResponse.getHits().totalHits()==0){
					String sql ="insert into comd_brand(brand_name,mfrs_name,mfrs_url)values('"+data+"','允升科技','www.yunsom.com')";
					filecate.appendMethodB("D:\\code\\data\\insert_brands.txt", sql+";\r\n");
					//System.out.println(sql+";");
				}
			}
		}
	
}
