package data.yunsom.com.kmeans;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import data.yunsom.com.format.SpiderCateGory;
import data.yunsom.com.util.FileOperate;
import data.yunsom.com.util.JsonValidatorUtil;

public class TestRead {
	private static final FileOperate filecate = new FileOperate();

	public static void readFile() throws IOException {
		List<String> datas = filecate.readTxt(
				"d:/code/ehsy_classification-test.json", "utf-8");
		int num = datas.size();
		ObjectMapper objectMapper = new ObjectMapper();
		for (int i = 0; i < num; i++) {
			//System.out.println(datas.get(i));
			//boolean validity = new JsonValidatorUtil().validate(datas.get(i));

				
				
				String[] categorys = datas.get(i).split(",");
				System.out.println(categorys[2]);
			
		}
	}
}
