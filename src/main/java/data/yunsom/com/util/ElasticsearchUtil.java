package data.yunsom.com.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * elastic search 集群连接
 *
 * 
 */
public class ElasticsearchUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(ElasticsearchUtil.class);
	private  static final String CLUSTER_NAME = "yunsom"; 
	private static final String IP = "服务器地址";	
	private static final int PORT = 9300; 
	private static Settings settings = Settings.settingsBuilder()
			.put("cluster.name", CLUSTER_NAME)
			.put("client.transport.sniff", true)  
			.build();
	private static TransportClient client;
	static {
		try {
			
			client = TransportClient
					.builder()
					.settings(settings)
					.build()
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress
									.getByName(IP), PORT));
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
		}
	}
	public static synchronized TransportClient getTransportClient() {
		return client;
	}

	// 为集群添加新的节点
	public static synchronized void addNode(String name) {
		try {
			client.addTransportAddress(new InetSocketTransportAddress(
					InetAddress.getByName(name), 9300));
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
		}
	} 

	// 删除集群中的某个节点
	public static synchronized void removeNode(String name) {
		try {
			client.removeTransportAddress(new InetSocketTransportAddress(
					InetAddress.getByName(name), 9300));
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
		}
	}
	public static void main(String args[]){
        String index="logstash-2016.02.16";    
        String type="logs"; 
        TransportClient client = ElasticsearchUtil.getTransportClient();
        System.out.println(client);
        SearchResponse response=ElasticsearchUtil.getTransportClient().prepareSearch(index)//设置要查询的索引(index)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setTypes(type)//设置type, 这个在建立索引的时候同时设置了, 或者可以使用head工具查看
        .setQuery(QueryBuilders.matchQuery("message", "Accept")) //在这里"message"是要查询的field,"Accept"是要查询的内容
        .setFrom(0)
        .setSize(10)
        .setExplain(true)
        .execute()
        .actionGet();
        for(SearchHit hit:response.getHits()){
            System.out.println(hit.getSourceAsString());
        }
    }  
}
