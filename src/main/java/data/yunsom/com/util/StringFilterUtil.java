package data.yunsom.com.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringFilterUtil {
	public static String filter(String str) throws PatternSyntaxException {
		
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	public static void main(String args[]) {
		String  str = "韩国养志园/Y";
		System.out.println(filter(str));
	}

}
