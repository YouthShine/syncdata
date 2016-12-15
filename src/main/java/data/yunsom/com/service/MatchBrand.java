package data.yunsom.com.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHits;

import com.fasterxml.jackson.databind.ObjectMapper;

import data.yunsom.com.format.BrandFile;
import data.yunsom.com.util.FileOperate;
import data.yunsom.com.util.JsonValidatorUtil;
import data.yunsom.com.util.SearchDataUtil;

public class MatchBrand {
	private static final FileOperate filecate = new FileOperate();
	public static void getBrandNoMatchList(String filepath) throws IOException{
		List<String> datas = filecate.readTxt(
				"D:\\document\\brandjson\\data\\1ez_brand.json",
				"utf-8");
		HashMap<String, String> brandmap = new HashMap<String, String>();
		ObjectMapper objectMapper = new ObjectMapper();
		for (String data : datas) {
			boolean validity = new JsonValidatorUtil().validate(data);

			if (validity) {
				BrandFile brandObject = objectMapper.readValue(data, BrandFile.class);
				String brand_name = brandObject.getProductBrand();
				SearchHits searchHits = SearchDataUtil.getSearchBrandData(brand_name);
				if(searchHits.getTotalHits() >0){
					Map<String, Object> map=searchHits.getHits()[0].getSource();
					String brand_id = map.get("brand_id").toString();
					brandmap.put(brand_name, brand_id);
					System.out.println(brand_name+"---"+brand_id);
				}
			}
		}
		
		
	}
}
