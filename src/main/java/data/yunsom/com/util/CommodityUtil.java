package data.yunsom.com.util;

import java.util.List;

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
/**
 * 获取公共信息
 * */

public class CommodityUtil {
	
	/**
	 * 获取商品ID
	 * */
	public static String getCommodityId(List<Column> columns,String fieldName){
		String commodity_id = "";
		if(columns.size() > 0){
			for (Column column : columns) {
			
				if(column.getName().equals(fieldName)){
					
					commodity_id = column.getValue();
					break;
				}
				
				
			}
		}
		return commodity_id;
	}
}
