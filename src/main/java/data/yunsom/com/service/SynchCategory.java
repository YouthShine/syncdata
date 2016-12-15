package data.yunsom.com.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import data.yunsom.com.format.CategoryMap;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;

/*
 * 新增品牌INDEX
 * **/
public class SynchCategory {

	private static final Logger logger = LoggerFactory
			.getLogger(SynchCategory.class);
	public static TransportClient client = ElasticsearchUtil.getTransportClient();
	
	public static void insertCategory() throws JsonGenerationException, JsonMappingException, IOException{
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		HashMap<Integer, Integer> mapone = new HashMap<Integer, Integer>();
		HashMap<Integer, String> maptwo = new HashMap<Integer, String>();
		HashMap<Integer, String> mapthr = new HashMap<Integer, String>();
		List<Integer> catelist =  getCategoryMeta();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		String sql = "select id,parent_id,category_name from comd_category";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			int id = Integer.parseInt(map.get("id"));
			int parent_id = Integer.parseInt(map.get("parent_id"));
			String category_name = map.get("category_name");
			mapone.put(id,parent_id);
			maptwo.put(id,category_name);
		}
		for (int key : catelist) {
			  String keyname = maptwo.get(key);
			  String parentname=null;
			  int root_id=10000;
			  String root_name=null;
			  int parent_id = mapone.get(key);
			  if(parent_id!=0){
				  parentname = maptwo.get(parent_id);
				  root_id = mapone.get(parent_id);
				  if(root_id==0){
					  root_name = maptwo.get(root_id);
				  }else{
					  root_name = maptwo.get(root_id);
				  }
			  }
			  CategoryMap catmap =  new CategoryMap(key, keyname, parent_id, parentname, root_id, root_name);
			  String json = mapper.writeValueAsString(catmap);
				 bulkRequest.add(client.prepareIndex(
				 Config.INDEXNAMECATEGORY, Config.INDEXNAMECATEGORY,
				 key+"")
				 .setSource(json));
			  System.out.println("category_id="+key+"---category_name="+keyname+"--category_tow_id="+parent_id+"--category_tow_name="+parentname+"--category_one_id="+root_id+"--category_one_name="+root_name);
			 
	    }
		
		if (bulkRequest.numberOfActions() > 1) {
			BulkResponse bulkResponse = bulkRequest.execute()
					.actionGet();
			if (bulkResponse.hasFailures()) {
				logger.info(bulkResponse.buildFailureMessage());
			}
		}
	}
	public static List<Integer> getCategoryMeta(){
		List<Integer> catelist = new ArrayList<Integer>();
		String sql ="select root_id,category_id from comd_category_meta where category_id=root_id and leaf_node=1";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			catelist.add(Integer.parseInt(map.get("category_id")));
		}
		return catelist;
	}
	
	
}
