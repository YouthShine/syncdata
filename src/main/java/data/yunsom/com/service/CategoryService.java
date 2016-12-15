package data.yunsom.com.service;

import java.util.List;

import org.elasticsearch.client.transport.TransportClient;

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

import data.yunsom.com.common.Config;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.RedisUtil;
import data.yunsom.com.util.SuggestPinyinDataUtil;

/**
 * 
 * 更新商品分类名称
 * */
public class CategoryService {
	private static RedisUtil redisutil = new RedisUtil();
	private static final TransportClient client = ElasticsearchUtil
			.getTransportClient();

	public void updateCategory(EventType eventType, List<Column> beforeColumns,
			List<Column> afterColumns) {
		String category_id = "";
		String category_name = "";
		for (Column data : afterColumns) {
			if (data.getName().equals("id")) {
				category_id = data.getValue();
			}
			if (data.getName().equals("category_name")) {
				category_name = data.getValue();
			}
		}
		if (eventType == EventType.INSERT) {

			String type = DataCategory.getCategory(category_id);
			String parent_id = DataCategory.getParentCategory(category_id);
			// 更新到搜索提示
			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(
					category_name, Config.TYPE_WEIGHT_CATE);
			client.prepareIndex(Config.SUGGEST, Config.TYPE,
					"category_" + category_id).setSource(pinyinjson).execute()
					.actionGet();
			redisutil.set("es_" + category_name, category_id + "_" + type);
			redisutil.set("categoryname_" + category_id, category_name);
			redisutil.set("categoryid_" + category_id, type);
			redisutil.set("parentid_" + category_id, parent_id);

		}
		if (eventType == EventType.UPDATE) {

			// 更新到搜索提示
			String pinyinjson = SuggestPinyinDataUtil.getSuggestPinyin(
					category_name, Config.TYPE_WEIGHT_CATE);
			client.prepareUpdate(Config.SUGGEST, Config.TYPE,
					"category_" + category_id).setDoc(pinyinjson).execute()
					.actionGet();
			String type = DataCategory.getCategory(category_id);
			String parent_id = DataCategory.getParentCategory(category_id);
			redisutil.set("es_" + category_name, category_id + "_" + type);
			redisutil.set("categoryname_" + category_id, category_name);
			redisutil.set("categoryid_" + category_id, type);
			redisutil.set("parentid_" + category_id, parent_id);

		} else if (eventType == EventType.DELETE) {

			client.prepareDelete(Config.SUGGEST, Config.TYPE,
					"category_" + category_id).execute().actionGet();
			redisutil.del("es_" + category_name);
			redisutil.del("categoryname_" + category_id);
			redisutil.del("categoryid_" + category_id);
			redisutil.del("parentid_" + category_id);
		}
	}
}
