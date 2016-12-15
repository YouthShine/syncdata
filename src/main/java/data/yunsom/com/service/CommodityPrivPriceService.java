package data.yunsom.com.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;




import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

import data.yunsom.com.util.CommodityUtil;
import data.yunsom.com.util.DbUtils;


/**
 * 私有价格更新逻辑
 * **/
public class CommodityPrivPriceService {
	
	

	public void updatePrivPrice(EventType eventType,
			List<Column> beforeColumns, List<Column> afterColumns) throws JsonGenerationException, JsonMappingException, IOException, InterruptedException
			 {

		if (eventType == EventType.INSERT || eventType == EventType.UPDATE) {
			insertPrice(afterColumns);
		}else if(eventType == EventType.DELETE){
			insertPrice(beforeColumns);
		}

	}

	/***
	 * 修改私有价格
	 * @throws InterruptedException 
	 * */
	public void insertPrice(List<Column> columns)
			throws JsonGenerationException, JsonMappingException, IOException, InterruptedException {

		String comm_id = CommodityUtil.getCommodityId(columns, "commodity_id");
		String sql = "select  platform_id from  comd_commodity_platform_ref where commodity_id="+comm_id;
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> map : rs) {
			String platform_id = map.get("platform_id");
			CommodityPlaform.deleteData(comm_id, platform_id); 
			CommodityPlaform.insertData(comm_id, platform_id); 
		}
	}

}
