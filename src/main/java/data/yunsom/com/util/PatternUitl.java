package data.yunsom.com.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUitl {
	public static HashMap<String, String> pattern(String brand_name) {
		HashMap<String, String> map = new HashMap<String, String>();
		String brand_en = "";
		String brand_cn = "";
		Pattern p_cn = Pattern.compile("[\\u4e00-\\u9fa5]+");
		Pattern p_en = Pattern.compile("[a-zA-Z]+");
		if (brand_name.indexOf("/") > 0) {

			brand_en = patternEn(brand_name).toLowerCase();
			brand_cn = patternCn(brand_name);
		}else if(hasDigit(brand_name)==false){
			brand_en = patternEn(brand_name).toLowerCase();
			brand_cn = patternCn(brand_name);
		}else{
			brand_en = brand_name.toLowerCase();
			brand_cn = brand_name;
		}
		map.put("cn", brand_cn);
		map.put("en", brand_en);
		return map;
	}

	public static String patternCn(String keyword) {
		Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+");
		Matcher m = p.matcher(keyword);
		while (m.find()) {
			return m.group();
		}
		return "";
	}

	public static String patternEn(String keyword) {
		Pattern p = Pattern.compile("[a-zA-Z]+");
		Matcher m = p.matcher(keyword);
		while (m.find()) {
			return m.group();
		}
		return "";
	}

	public static boolean hasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())
			flag = true;
		return flag;

	}
}
