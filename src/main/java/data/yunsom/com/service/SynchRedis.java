package data.yunsom.com.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.RedisUtil;

public class SynchRedis {
	private static final Logger logger = LoggerFactory
			.getLogger(SynchRedis.class);

	public static void mysqlToRedis() {
		
		tagToRedis();
		categoryToReids();
		categoryIDToReids();
		categoryNameToReids();
		categoryIdParentToReids();
		brandNameToReids();
		categoryIdParentToReids();
		enterpriseToRedis();
		

		
	}
	public static HashSet<Integer> dataChange(List<Map<String, String>> data,String type){
		HashSet<Integer> tag_set = new HashSet<Integer>();
		for (Map<String, String> map : data) {
			tag_set.add(Integer.parseInt(map.get(type)));
		}
		return tag_set;
	}
	public static void tagToRedis(){
		RedisUtil  redisutil = new RedisUtil();
		String sql_tag_one = "select tag_id_one from comd_tag_meta group by  tag_id_one";
		List<Map<String, String>> rs_tag_one = DbUtils.execute(sql_tag_one);
		HashSet<Integer> tag_one_set = dataChange(rs_tag_one, "tag_id_one");
		
		String sql_tag_two = "select tag_id_two from comd_tag_meta group by  tag_id_two";
		List<Map<String, String>> rs_tag_two = DbUtils.execute(sql_tag_two);
		HashSet<Integer> tag_two_set = dataChange(rs_tag_two, "tag_id_two");
		String sql_tag = "select tag_id from comd_tag_meta group by  tag_id";
		List<Map<String, String>> rs_tag = DbUtils.execute(sql_tag);
		HashSet<Integer> tag_set = dataChange(rs_tag, "tag_id");
		String sql_comd_tag = "select id,tag_name from comd_tag";
		List<Map<String, String>> rs_comd_tag = DbUtils.execute(sql_comd_tag);
		for (Map<String, String> map : rs_comd_tag) {
			String tag_name  = map.get("tag_name");
			int id = Integer.parseInt(map.get("id"));
			
			redisutil.set("categoryname_"+id, tag_name);
			int type = 0;
			type = tag_set.contains(id)?3:type;
			type = tag_two_set.contains(id)?2:type;
			type = tag_one_set.contains(id)?1:type;
			
			
			redisutil.set("categoryid_"+id, type+"");
			redisutil.set("es_"+tag_name, id+"_"+type);
			if(id==1134){
				System.out.println(tag_two_set.contains(id));
				System.out.println(tag_one_set.contains(id));
				logger.info("key=es_"+tag_name+"--"+id+"_"+type);
			}
			
			
		}
	}
	public static void categoryToReids(){
		String sql = "select a.id,category_name,max(root_step) as type  from comd_category as a,comd_category_meta  as b where  a.id=b.category_id group by a.id";
		RedisUtil  redisutil = new RedisUtil();
		List<Map<String, String>> rs = DbUtils.execute(sql);
		
		for (Map<String, String> maprs : rs) {
			
			redisutil.set("es_"+maprs.get("category_name"), maprs.get("id")+"_"+maprs.get("type"));
			logger.info("key=es_"+maprs.get("category_name")+"--"+maprs.get("id")+"_"+maprs.get("type"));
		}
	}

	/***
	 * 分类ID=》key
	 * 分类级别=>value
	 * */
	public static void categoryIDToReids(){
		RedisUtil  redisutil = new RedisUtil();
		String parent_ids = "";
		String sql = "select id,category_name from comd_category where parent_id=0";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> maprs : rs) {
			parent_ids+=maprs.get("id")+",";
			redisutil.set("categoryid_"+maprs.get("id"), 1+"");
			
		}
		parent_ids = parent_ids.substring(0, parent_ids.length()-1);
		String sqlcatetwo = "select id,category_name from comd_category where parent_id in ("+parent_ids+")";
		logger.info(sqlcatetwo);
		List<Map<String, String>> rstwo = DbUtils.execute(sqlcatetwo);
		for (Map<String, String> maprstwo : rstwo) {
			redisutil.set("categoryid_"+maprstwo.get("id"), 2+"");
		}
		String sqlcatethr = "select category_id  from comd_category_meta where leaf_node=1 and root_id=category_id";
		List<Map<String, String>> rsthr = DbUtils.execute(sqlcatethr);
		for (Map<String, String> maprsthr : rsthr) {
			redisutil.set("categoryid_"+maprsthr.get("category_id"), 3+"");
		}
	}
	/***
	 * 
	 * */
	public static void categoryIdParentToReids(){
		RedisUtil  redisutil = new RedisUtil();

		String sql = "select id,parent_id from comd_category";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> maprs : rs) {
			redisutil.set("parentid_"+maprs.get("id"),maprs.get("parent_id"));	
		}
	
	}
	/***
	 * 分类ID=》key
	 * 分类名称=>value
	 * */
	public static void categoryNameToReids(){
		RedisUtil  redisutil = new RedisUtil();
		String sql = "select id,category_name from comd_category ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> maprs : rs) {
			redisutil.set("categoryname_"+maprs.get("id"), maprs.get("category_name"));
		}
	
	}
	/***
	 * 品牌ID=》key
	 * 品牌名称=>value
	 * */
	public static void brandNameToReids(){
		RedisUtil  redisutil = new RedisUtil();
		String sql = "select id,brand_name from comd_brand  ";
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> maprs : rs) {
			redisutil.set("brandname_"+maprs.get("id"), maprs.get("brand_name"));
		}
	
	}
	public static void enterpriseToRedis(){
		String sql ="select id,enterprise_name from uc_enterprise";
		RedisUtil  redisutil = new RedisUtil();
		List<Map<String, String>> rs = DbUtils.execute(sql);
		for (Map<String, String> maprs : rs) {
			String enterprise_name = maprs.get("enterprise_name");
			String id = maprs.get("id");
			redisutil.set("es_"+enterprise_name,id);
			int num_one = enterprise_name.indexOf("有限公司");
			int num_two = enterprise_name.indexOf("公司");
			int num_thr = enterprise_name.indexOf("有限责任公司");
			redisutil.set(enterprise_name,id);
			logger.info("key=es_"+enterprise_name+"--"+id);
			if(num_one >0){
				redisutil.set("es_"+enterprise_name.substring(0, num_one),id);
				logger.info("key=es_"+enterprise_name.substring(0, num_one)+"--"+id);
			}else if(num_two >0){
				redisutil.set("es_"+enterprise_name.substring(0, num_two), id);
				logger.info("key=es_"+enterprise_name.substring(0, num_two)+"--"+id);
			}else if(num_thr>0){
				redisutil.set("es_"+enterprise_name.substring(0, num_thr), id);
				logger.info("key=es_"+enterprise_name.substring(0, num_thr)+"--"+id);
			}
			
			
			
		}
	}
}

