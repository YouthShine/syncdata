package data.yunsom.com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;

public class DeleteCommodity {
	private static final TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static final Logger logger = LoggerFactory
			.getLogger(CommodityPlaform.class);
	public static void delete(){
		String sql = "select id,deleted_at from comd_commodity where deleted_at!=''";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		HashMap<Integer, List<Integer>> platmap = SynchroData.getPlatform();
		int num = rs.size();
		for (int i = 0; i < num; i++) {
			int commodity_id =  Integer.parseInt(rs.get(i).get("id"));
			List<Integer> platforms = platmap.get(commodity_id);
			for (Integer platform : platforms){
				logger.info("commodity_id="+commodity_id+"--platform_id="+platform);
				CommodityPlaform.deleteData(commodity_id+"", platform+"");
			}
		}
		
		
	}
}
