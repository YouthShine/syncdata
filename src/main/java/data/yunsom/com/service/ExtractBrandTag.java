package data.yunsom.com.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.dao.ComdBrand;
import data.yunsom.com.dao.ComdTagMeta;
import data.yunsom.com.format.CommodityValue;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.FileOperate;
import data.yunsom.com.util.JsonValidatorUtil;

public class ExtractBrandTag {
	private static final Logger logger = LoggerFactory
			.getLogger(SyncFile.class);
	public static TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static final FileOperate filecate = new FileOperate();

	public static void getTagS(int day, String hour) throws IOException {

		List<String> Tagdata = getTagJson();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		if (day == 0) {
			day = Integer.parseInt(df.format(new Date()));
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY,
					calendar.get(Calendar.HOUR_OF_DAY) - 1);
			SimpleDateFormat hourdf = new SimpleDateFormat("HH");
			hour = hourdf.format(calendar.getTime());
		}

		for (String value : Tagdata) {
			// String path = "D:\\code\\data" + "\\" + day + "\\" + hour + "\\"
			// + value;
			String path = "/data/spider/commondity/commondity/public/json"
					+ "/" + day + "/" + hour + "/" + value;
			logger.info(path);
			ComdTagMeta comdTagmeta = new ComdTagMeta();
			Map<String, String> tagsmap = comdTagmeta.getAllTag(2);
			HashSet<String> set = new HashSet<String>();
			List<String> datas = filecate.readTxt(path, "utf-8");
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			objectMapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
			objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,
					true);
			objectMapper
					.configure(
							DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
							true);
			int num = datas.size();
			if (num > 0) {
				for (int i = 0; i < num; i++) {
					boolean validity = new JsonValidatorUtil().validate(datas
							.get(i));
					if (validity && !datas.get(i).equals("")) {
						logger.info(datas.get(i) + "-----");
						try {

							CommodityValue commodityValue = objectMapper
									.readValue(datas.get(i),
											CommodityValue.class);
							String category_name_one = commodityValue
									.getCategory_name_one();
							String category_name_two = commodityValue
									.getCategory_name_two();
							String category_name = commodityValue
									.getCategory_name();
							if (!category_name_one.equals("")&& !tagsmap.containsKey(category_name_one)) {
								set.add(category_name_one);
							}
							if (!category_name_two.equals("") && !tagsmap.containsKey(category_name_two)) {
								set.add(category_name_two);
							}
							if (!category_name.equals("") && !tagsmap.containsKey(category_name)) {
								set.add(category_name);
							}
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
					}
				}
			}
			String sql = "insert into comd_tag (tag_name)values";
			if (set.size() > 0) {

				for (String data : set) {
					sql += "(\"" + data + "\"),";
				}
				sql = sql.substring(0, sql.length() - 1);
				logger.info(sql);
				int insert_num = DbUtils.insertSql(sql);
				if (insert_num > 0) {
					insertData(path);
				}
			} else {
				insertData(path);
			}
		}

	}

	public static void insertData(String path) throws IOException {
		HashSet<String> brands = new HashSet<String>();
		ComdTagMeta comdTagmeta = new ComdTagMeta();
		Map<String, String> tagsmap = comdTagmeta.getAllTag(2);

		List<String> datas = filecate.readTxt(path, "utf-8");
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		objectMapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		objectMapper
				.configure(
						DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
						true);
		int num = datas.size();
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				boolean validity = new JsonValidatorUtil().validate(datas
						.get(i));
				if (validity && !datas.get(i).equals("")) {
					try {
						CommodityValue commodityValue = objectMapper.readValue(
								datas.get(i), CommodityValue.class);
						String commodity_url = commodityValue
								.getCommodity_url();
						/*** 判断数据库是否存在 */
						String sql_spider = "select id from spider_commodity  where commodity_url = \""
								+ commodity_url + "\"";
						List<Map<String, String>> listspider = DbUtils
								.execute(sql_spider);

						int tag_auto_id = 0;
						logger.info(datas.get(i) + "-----");

						String category_name_one = commodityValue
								.getCategory_name_one();

						int tag_id_one = category_name_one.equals("")?0:Integer.parseInt(tagsmap
								.get(category_name_one));
						String category_name_two = commodityValue
								.getCategory_name_two();
						int tag_id_two = category_name_two.equals("")?0:Integer.parseInt(tagsmap
								.get(category_name_two));
						String category_name = commodityValue
								.getCategory_name();
						int tag_id = category_name.equals("")?0:Integer.parseInt(tagsmap
								.get(category_name));
						String brand_name = commodityValue.getBrand_name();
						if (brand_name.equals("")) {
							filecate.appendMethodB(
									"./public/brand/new_notbrandData.json",
									datas.get(i) + "\r\n");
						} else {
							ComdBrand comdbrand = new ComdBrand();
							int brand_id = comdbrand.matchBrand(brand_name);
							if (brand_id == 0) {
								brands.add(brand_name);
								filecate.appendMethodB(
										"./public/brand/new_brandData.json",
										datas.get(i) + "\r\n");
							} else {
								String sql = "select id from comd_tag_meta where tag_id_one = "
										+ tag_id_one
										+ " and tag_id_two="
										+ tag_id_two + " and tag_id=" + tag_id;
								List<Map<String, String>> listtag = DbUtils
										.execute(sql);
								if (listtag.size() > 0) {
									tag_auto_id = Integer.parseInt(listtag.get(
											0).get("id"));
								} else {
									String sqlinsert = "insert into comd_tag_meta  (tag_id_one,tag_id_two,tag_id) values("
											+ tag_id_one
											+ ","
											+ tag_id_two
											+ "," + tag_id + ")";
									tag_auto_id = DbUtils.insertSql(sqlinsert);
								}
							}

							String commodity_name = commodityValue
									.getCommodity_name();
							String commodity_model = commodityValue
									.getCommodity_attr().get(0).getAttrname();
							String commodity_attr = objectMapper
									.writeValueAsString(commodityValue
											.getSpeci());
							String commodity_introduce = objectMapper
									.writeValueAsString(commodityValue
											.getIntro());
							String main_pic_url = commodityValue
									.getMain_pic_url();
							double price = commodityValue.getPrice();
							String url = commodityValue.getCommodity_url();
							java.net.URL urls = new java.net.URL(url);
							String from = urls.getHost();
							if(brand_id >0 ){
							if (listspider.size() == 0) {
								String sql_insert_spider = " insert into spider_commodity  (commodity_name,commodity_model,commodity_url,tag_id,brand_id,commodity_attr,commodity_introduce,main_pic_url,price,`from`) values(?,?,?,?,?,?,?,?,?,?)";
								int tag_id_spider = DbUtils.insert_spider(
										sql_insert_spider, commodity_name,
										commodity_model, commodity_url,
										tag_auto_id, brand_id, commodity_attr,
										commodity_introduce, main_pic_url,
										price, from);
								if (tag_id_spider > 0) {
									logger.info("insert to success id="
											+ tag_id_spider);
								}
							} else {
								String sql_update_spider = "update spider_commodity set commodity_name=\""
										+ commodity_name
										+ "\",commodity_model=\""
										+ commodity_model
										+ "\",tag_id="
										+ tag_auto_id
										+ ",brand_id="
										+ brand_id
										+ ",commodity_attr=\""
										+ commodity_attr
										+ "\",commodity_introduce=\""
										+ commodity_introduce
										+ "\",main_pic_url=\""
										+ main_pic_url
										+ "\",price="
										+ price
										+ " where commodity_url=\""
										+ commodity_url + "\"";
								logger.info(sql_update_spider);
								int tag_id_spider = DbUtils
										.update(sql_update_spider);
								if (tag_id_spider > 0) {
									logger.info("update to success id="
											+ tag_id_spider);
								}
							}
							}
							
						}

					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}

			}
		}
		for (String brand : brands) {
			filecate.appendMethodB("./public/brand/new_brand.txt", brand
					+ "\r\n");
		}
	}

	public static List<String> getTagJson() {

		List<String> dataList = new ArrayList<String>();
		dataList.add("315mro.json");
		dataList.add("4006770558.json");
		dataList.add("91yilong.json");
		dataList.add("btone-mro.json");
		dataList.add("deppre.json");
		dataList.add("gongchang.json");
		dataList.add("ispek.json");
		dataList.add("isweek.json");
		dataList.add("mrobay.json");
		dataList.add("makepolo.json");
		dataList.add("rolymro.json");
		dataList.add("seton.json");
		dataList.add("zkh360.json");
		dataList.add("ehsy.json");
		dataList.add("haocaimao.json");
		dataList.add("8shop.json");
		dataList.add("mctmall.json");
		dataList.add("grainger.json");
		dataList.add("wwmro.json");
		dataList.add("iacmall.json");
		dataList.add("vipmro.json");
		dataList.add("1ez.json");
		dataList.add("axmro.json");
		dataList.add("gomro.json");
		dataList.add("hc360.json");
		dataList.add("zgw.json");
		dataList.add("huaaomro.json");
		dataList.add("zhaogongye.json");
		dataList.add("sssmro.json");
		dataList.add("ieou.json");
		
		return dataList;
	}

}
