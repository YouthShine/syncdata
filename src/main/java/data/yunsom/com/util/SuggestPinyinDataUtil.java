package data.yunsom.com.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import data.yunsom.com.util.StringFilterUtil;

/**
 * 获取提示数据
 * 
 * @author yangxuchuan 2016/7/26
 * */
public class SuggestPinyinDataUtil {
	private static Logger logger = LoggerFactory
			.getLogger(SuggestPinyinDataUtil.class);
	public static void main(String args[]){
		String str = "吉时利/Keithely";
		System.out.println(str.toLowerCase());
	}
	public static String getSuggestPinyin(String name, int type) {

		HashMap<String, Object> suggestData = new HashMap<String, Object>();
		List<String> listname = new ArrayList<String>();
		HashMap<String, Object> input = new HashMap<String, Object>();
		suggestData.put("name", name);
		suggestData.put("type", type);
		listname.add(name);
		listname.add(name.toLowerCase());
		if (!"".equals(name)) {
			String pinyin = PinyinHelper.convertToPinyinString(name, "",
					PinyinFormat.WITHOUT_TONE);
			listname.add(pinyin);
			listname.add(pinyin.toLowerCase());
			String pinyinf = PinyinHelper.getShortPinyin(name);
			listname.add(pinyinf);
			listname.add(pinyinf.toLowerCase());

			input.put("input", listname);
			input.put("output", name);
			input.put("weight", type);
			suggestData.put("tag_suggest", input);
		}
		ObjectMapper mapper = new ObjectMapper();

		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		String json = "";
		try {
			json = mapper.writeValueAsString(suggestData);
		} catch (JsonGenerationException e) {

			logger.error(e.getMessage());
		} catch (JsonMappingException e) {

			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return json;
	}
	public static String getSuggestCategory(String name, int id,String categorys,int type ) {

		HashMap<String, Object> suggestData = new HashMap<String, Object>();
		List<String> listname = new ArrayList<String>();
		HashMap<String, Object> input = new HashMap<String, Object>();
		suggestData.put("name", name);
		suggestData.put("id", id);
		if(type!=1){
			listname.add(name);
		}
		//listname.add(name.toLowerCase());
		String[] category_names = categorys.split(",");
		for (int i = 0; i < category_names.length; i++) {
			listname.add(category_names[i]);
		}
		
			input.put("weight", id);
			input.put("input", listname);
			if(type==1){
				input.put("output", name+"_spilt");
			}else{
				input.put("output", name);
			}
			//input.put("weight", type);
			suggestData.put("tag_suggest", input);
		
		ObjectMapper mapper = new ObjectMapper();

		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		String json = "";
		try {
			json = mapper.writeValueAsString(suggestData);
		} catch (JsonGenerationException e) {

			logger.error(e.getMessage());
		} catch (JsonMappingException e) {

			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return json;
	}

	public static String getPrefixPinyin(String name, int type) {

		HashMap<String, Object> suggestData = new HashMap<String, Object>();
		List<String> listname = new ArrayList<String>();

		suggestData.put("name", name);
		suggestData.put("type", type);

		listname.add(StringFilterUtil.filter(name).toLowerCase());
		listname.add(PinyinHelper.convertToPinyinString(name, "",
				PinyinFormat.WITHOUT_TONE));
		listname.add(PinyinHelper.getShortPinyin(name));
		suggestData.put("prefix", listname);
		ObjectMapper mapper = new ObjectMapper();

		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		String json = "";
		try {
			json = mapper.writeValueAsString(suggestData);
		} catch (JsonGenerationException e) {

			logger.error(e.getMessage());
		} catch (JsonMappingException e) {

			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return json;
	}
}
