package data.yunsom.com.service;

import java.io.IOException;
import java.util.List;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.client.transport.TransportClient;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import data.yunsom.com.common.Config;
import data.yunsom.com.util.ElasticsearchUtil;

import data.yunsom.com.util.SuggestPinyinDataUtil;
/***
 * 更新企业名称到redis和Es
 * 
 * */
public class EnterpriseService {

	private static final TransportClient client = ElasticsearchUtil
			.getTransportClient();
	/***
	 * 更新品牌名称到redis和elasticsearch
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * */
	public void updateEnterprise(EventType eventType, List<Column> beforeColumns,
			List<Column> afterColumns) throws JsonGenerationException, JsonMappingException, IOException {
		String enterprise_id = "";
		String enterprise_name = "";
		for (Column data : afterColumns) {
			if (data.getName().equals("id")) {
				enterprise_id = data.getValue();
			}
			if (data.getName().equals("enterprise_name")) {
				enterprise_name = data.getValue();
			}
		}
		if(eventType ==EventType.INSERT){
			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(
					enterprise_name, Config.TYPE_WEIGHT_ENTERPRISE);
			client.prepareIndex(Config.SUGGEST,Config.TYPE,"enterprise_" + enterprise_id).setSource(pinyinjson);	
		
		}else if(eventType ==EventType.UPDATE){
			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(
					enterprise_name, Config.TYPE_WEIGHT_ENTERPRISE);
			client.prepareUpdate(Config.SUGGEST,Config.TYPE,"enterprise_" + enterprise_id).setDoc(pinyinjson);	
		}else if(eventType ==EventType.DELETE){
			client.prepareDelete(Config.SUGGEST, Config.TYPE, "enterprise_" + enterprise_id).execute().actionGet();
		}
		
	}
}
