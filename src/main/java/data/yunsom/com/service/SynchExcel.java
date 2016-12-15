package data.yunsom.com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import data.yunsom.com.common.Config;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.ExcelUtil;
import data.yunsom.com.util.FileOperate;

public class SynchExcel {
	private static final Logger logger = LoggerFactory
			.getLogger(SynchExcel.class);
	public static TransportClient client = ElasticsearchUtil
			.getTransportClient();
    public static FileOperate fileOperate= new FileOperate();
	public static void synchExcelPrint() {
		ExcelUtil poi = new ExcelUtil();
		String sql = "select id,brand_name from comd_brand";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		// System.out.println(rs.size());
		HashMap<String, String> brandmap = new HashMap<String, String>();
		for (Map<String, String> map : rs) {
			brandmap.put(map.get("brand_name").toLowerCase(), map.get("id"));
		}
		HashMap<String, String> map = new HashMap<String, String>();
		List<List<String>> list = poi.read("D:\\download\\QQ\\tb_brand_detail.xlsx");
		
		int num = 0;
		if (list != null) {
			

			for (int i = 1; i < list.size(); i++) {

				List<String> cellList = list.get(i);
				String entprise = cellList.get(0).trim();
				String brand_names = cellList.get(1).trim();
				String url = cellList.get(2).trim();
				if (!brand_names.equals("")) {
					if (brand_names.indexOf(",") > 0) {
						String[] brands = brand_names.split(",");
						for (int j = 0; j < brands.length; j++) {

							
								String pinyin = PinyinHelper
										.convertToPinyinString(brands[j], "",
												PinyinFormat.WITHOUT_TONE);
								pinyin = pinyin.replace("/", "");
								
								String sqlupdate = "update comd_brand set mfrs_name=\""+entprise+"\" and mfrs_url=\""+url+"\" where brand_name=\""+brands[j].trim()+"\";\n";
								fileOperate.appendMethodB("d:\\code\\insert_brand.sql", sqlupdate);
								System.out.println(sqlupdate);
								num++;
							
						}
					} else {
						
							map.put(brand_names.toLowerCase(), "1223");
							String pinyin = PinyinHelper.convertToPinyinString(
									brand_names, "", PinyinFormat.WITHOUT_TONE);
							pinyin = pinyin.replace("/", "");
							String sqlupdate = "update comd_brand set mfrs_name=\""+entprise+"\" and mfrs_url=\""+url+"\" where brand_name=\""+brand_names+"\";\n";
							System.out.println(sqlupdate);
							fileOperate.appendMethodB("d:\\code\\insert_brand.sql", sqlupdate);
						
					}
				}
			}
			
			
			

		}

	}

	public static boolean searchBrand(String brand_name) {
		BoolQueryBuilder qbq = QueryBuilders.boolQuery();
		BoolQueryBuilder qbqdl = QueryBuilders.boolQuery();
		String[] brands = null;
		if (brand_name.indexOf("/") > 0) {
			brands = brand_name.split("/");
			qbqdl.should(QueryBuilders.termQuery("brand_name", brand_name));
			qbqdl.should(QueryBuilders.termQuery("brand_name_cn", brands[0]));
			qbqdl.should(QueryBuilders.termQuery("brand_name_en", brands[1]));
			qbq.must(qbqdl);
		} else {
			qbqdl.should(QueryBuilders.termQuery("brand_name", brand_name));
			qbqdl.should(QueryBuilders.termQuery("brand_name_cn", brand_name));
			qbqdl.should(QueryBuilders.termQuery("brand_name_en", brand_name));
			qbq.must(qbqdl);
		}

		SearchResponse response = client.prepareSearch(Config.INDEXNAMEBRNAD)
				.setTypes("brand").setSearchType(SearchType.DEFAULT)
				.setQuery(qbq).execute().actionGet();
		if (response.getHits().getTotalHits() > 0) {
			return true;
		} else {
			return false;
		}

	}

}
