package data.yunsom.com.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.util.DbUtils;
/***
 * tag 操作类
 * 
 * */
public class ComdTagMeta {
	private static final Logger logger = LoggerFactory
			.getLogger(ComdTagMeta.class);
	/***
	 * 获取所有的TAG 
	 * map<k,v>
	 * */
	public Map<String, String> getAllTag(int type){
		String sql ="select id,tag_name from comd_tag";
		List<Map<String, String>> tags = DbUtils.execute(sql);
		HashMap<String, String> map =  new HashMap<String, String>();
		for (Map<String, String> ele :tags) {
			if(type==1){
				map.put(ele.get("id"), ele.get("tag_name"));
			}else{
				map.put(ele.get("tag_name"), ele.get("id"));
			}
		}
		return map;
	}
	/***
	 * 获取所有的TAG_meta 
	 * map<k,v>
	 * */
	public Map<String, String> getAllTagMeta(){
		String sql ="select id,tag_id_one,tag_id_two,tag_id  from comd_tag_meta";
		List<Map<String, String>> tags = DbUtils.execute(sql);
		HashMap<String, String> map =  new HashMap<String, String>();
		for (Map<String, String> ele :tags) {	
				map.put(ele.get("id"), ele.get("tag_id_one")+"_"+ele.get("tag_id_two")+"_"+ele.get("tag_id"));
		}
		return map;
	}
	/**
	 * 获取一条tag映射关系的 map<id,name>
	 * */
	public  Map<Integer, String> getIDTag(List<Integer> tags){
		String tag = "( "+StringUtils.join(tags, ",")+")";
		String sql ="select id,tag_name from comd_tag where id in "+tag;
		List<Map<String, String>> taglist = DbUtils.execute(sql);
		HashMap<Integer, String> map =  new HashMap<Integer, String>();
		for (Map<String, String> ele :taglist) {
			map.put(Integer.parseInt(ele.get("id")), ele.get("tag_name"));
		}
		return map;
	}
}
