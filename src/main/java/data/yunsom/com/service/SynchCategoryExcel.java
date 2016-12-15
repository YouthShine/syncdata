package data.yunsom.com.service;

import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.ExcelUtil;
import data.yunsom.com.util.FileOperate;

public class SynchCategoryExcel {
	private static final Logger logger = LoggerFactory
			.getLogger(SynchCategoryExcel.class);
	public static TransportClient client = ElasticsearchUtil
			.getTransportClient();
    public static FileOperate fileOperate= new FileOperate();
	public static void synchExcel() {
		ExcelUtil poi = new ExcelUtil();
		List<List<String>> list = poi.read("D:\\document\\category_one.xlsx");
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				List<String> cellList = list.get(i);
				int num =1;
				String category_name = "";
				String category_map ="";
				for (String data :cellList) {
					if(!data.equals("") && num==1){
						category_name = data;
						category_map = data+",";
					}else if(!data.equals("")){
						category_map+=data+",";
					}	
					num++;
				}
				
				category_map = category_map.substring(0, category_map.length()-1);
				String sql ="insert into spider_category_one_map(category_name,mapping_name)value(\""+category_name+"\",\""+category_map+"\");\n";
				//System.out.println(sql);
				fileOperate.appendMethodB("d:\\code\\insert_category.sql", sql);
			}
			
		}
	}	
}
