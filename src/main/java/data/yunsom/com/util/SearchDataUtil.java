package data.yunsom.com.util;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.common.Config;



public class SearchDataUtil {
	Logger logger = LoggerFactory.getLogger(SearchDataUtil.class);
	public static TransportClient client = ElasticsearchUtil.getTransportClient();
	public static SearchHits getSearchData(int commodity){
		BoolQueryBuilder qbq = QueryBuilders.boolQuery();
		qbq.must(QueryBuilders.termQuery("commodity_id", commodity));
		SearchResponse response = client.prepareSearch(Config.INDEXNAME)
				.setTypes(Config.TYPE)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(qbq)
				.setExplain(true)
				.execute().actionGet();
		SearchHits hits = response.getHits();
		return hits;
	}
	public static SearchHits getSearchBrandData(String brand_name){
		BoolQueryBuilder qbq = QueryBuilders.boolQuery();
		qbq.must(QueryBuilders.termQuery("brand_name_en", brand_name));
		qbq.must(QueryBuilders.termQuery("brand_name_cn", brand_name));
		qbq.must(QueryBuilders.termQuery("brand_name", brand_name));
		qbq.must(QueryBuilders.termQuery("na_brand_name", brand_name));
		SearchResponse response = client.prepareSearch(Config.INDEXNAME)
				.setTypes(Config.TYPE)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(qbq)
				.execute().actionGet();
		SearchHits hits = response.getHits();
		return hits;
	}
}
