package data.yunsom.com.dao;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

import data.yunsom.com.format.CommodityAttr;
import data.yunsom.com.util.JsonValidatorUtil;
/**
 * 商品属性操作
 * */
public class ComdAttr {
	/**
	 * 合法json转换为OBject对象
	 * 
	 * */
	public  List<CommodityAttr> attrStringToObect(String commodity_attr){
		List<CommodityAttr> attr = new ArrayList<CommodityAttr>();
		if (!"".equals(commodity_attr) && commodity_attr != null) {
			boolean validity = new JsonValidatorUtil()
					.validate(commodity_attr);
			if (validity) {
				JSONArray array = JSONArray
						.parseArray(commodity_attr);
				int arrsize = array.size();
				for (int i = 0; i < arrsize; i++) {
					String attrKey = array.getJSONObject(i)
							.get("key").toString();
					String attrValue = array.getJSONObject(i)
							.get("value").toString();
					if(!attrKey.equals("商品型号")){
					attr.add(new CommodityAttr(attrKey, attrValue));
					}
				}
			}
		}
		return attr;
	}
	/***
	 * 获取商品型号
	 * */
	public String getCommodityModel(String commodity_attr){
		String commodity_model = "";
		if (!"".equals(commodity_attr) && commodity_attr != null) {
			boolean validity = new JsonValidatorUtil().validate(commodity_attr);
			if (validity) {
				JSONArray array = JSONArray.parseArray(commodity_attr);
				int arrsize = array.size();
				for (int i = 0; i < arrsize; i++) {
					String attrKey = array.getJSONObject(i)
							.get("key").toString();
					if(attrKey.equals("商品型号")){
						return array.getJSONObject(i)
								.get("value").toString();
					}
					
				}
			}
		}
		return commodity_model;
	}
	/***
	 * 
	 * */
}
