package data.yunsom.com.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import data.yunsom.com.format.SpiderEnterpriseHc;
import data.yunsom.com.format.SpiderHcCommodity;
import data.yunsom.com.util.DbUtils;
import data.yunsom.com.util.FileOperate;
import data.yunsom.com.util.JsonValidatorUtil;

public class SyncSpiderHc {
	private static final Logger logger = LoggerFactory
			.getLogger(SyncSpiderHc.class);
	private static final FileOperate filecate = new FileOperate();

	private static List<String> getPath(int day, String hour, int type) {
		// String dir = "D:\\code\\data\\";

		String dir = "/data/spider/hc360/commondity/commondity/public/json/";

		if (type == 1) {
			dir += "produc/";
		} else {
			dir += "company/";
		}
		List<String> paths = new ArrayList<String>();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		if (day == 0) {
			day = Integer.parseInt(df.format(new Date()));
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY,
					calendar.get(Calendar.HOUR_OF_DAY) - 1);
			SimpleDateFormat hourdf = new SimpleDateFormat("HH");
			hour = hourdf.format(calendar.getTime());
		}
		for (int i = 0; i < 11; i++) {
			if (i < 10) {
				paths.add(type == 1 ? dir + day + "/" + hour + "/hc360_0" + i
						+ "_product_product.json" : dir + day + "/" + hour
						+ "/hc360_0" + i + "_company_company.json");

			} else {
				paths.add(type == 1 ? dir + day + "/" + hour + "/hc360_" + i
						+ "_product_product.json" : dir + day + "/" + hour
						+ "/hc360_" + i + "_company_company.json");
			}
		}

		return paths;
	}

	public static void enterpriseData(int day, String hour) throws IOException {
		List<String> paths = getPath(day, hour, 2);
		for (String path : paths) {

			logger.info(path);
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
						// logger.info(datas.get(i) + "-----");
						try {
							
						
						SpiderEnterpriseHc enterpriseValue = objectMapper
								.readValue(datas.get(i),
										SpiderEnterpriseHc.class);
						String name = enterpriseValue.getCompanyName();
						String title = enterpriseValue.getCompanyTile();
						String logo = enterpriseValue.getCompanyLogo();
						List<String> focus = enterpriseValue.getCompanyFocus();
						String url = enterpriseValue.getCompanyUrl();
						String info = enterpriseValue.getCompanyIntro();
						String contact = objectMapper
								.writeValueAsString(enterpriseValue
										.getCompanyContact());
						String detail = objectMapper
								.writeValueAsString(enterpriseValue
										.getCompanyDetails());
						String sql_check = "select *from spider_enterprise where url=\""
								+ url + "\"";
						List<Map<String, String>> list = DbUtils
								.execute(sql_check);
						if (list.size() == 0) {
							String sql = "insert into spider_enterprise(enterprise_name,enterprise_title,logo_url,focus_url,enterprise_brief,enterprise_detail,contact,url)values"
									+ "('"
									+ name
									+ "','"
									+ title
									+ "','"
									+ logo
									+ "','"
									+ focus
									+ "','"
									+ info
									+ "','"
									+ detail
									+ "','"
									+ contact
									+ "','"
									+ url + "')";
							int insert_num = DbUtils.insertSql(sql);
							if (insert_num > 0) {
								logger.info("insert success");
							}
						} else {
							logger.info("update start -------");
							String sql = "update spider_enterprise set enterprise_name='"
									+ name
									+ "' , enterprise_title='"
									+ title
									+ "' , logo_url='"
									+ logo
									+ "' , focus_url='"
									+ focus
									+ "' , enterprise_brief='"
									+ info
									+ "' , enterprise_detail='"
									+ detail
									+ "',contact='"
									+ contact
									+ "' where url='"
									+ url + "'";
							logger.info(sql);
							int insert_num = DbUtils.update(sql);
							if (insert_num > 0) {
								logger.info("update success");
							}
						}
						} catch (Exception e) {
							logger.info(e.getMessage());
						}
					}
				}
			}
		}
	}

	public static void commodityData(int day, String hour) throws IOException {
		List<String> paths = getPath(day, hour, 1);
		for (String path : paths) {

			logger.info(path);
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
						// logger.info(datas.get(i) + "-----");
						try {

							SpiderHcCommodity commodityValue = objectMapper
									.readValue(datas.get(i),
											SpiderHcCommodity.class);
							String name = commodityValue.getProductName();
							String brand_name = commodityValue
									.getProductBrand();
							String model = commodityValue.getProductModel();

							String price = commodityValue.getProductPrice()
									.replace("¥", "");

							String url = commodityValue.getProductUrl();
							String from = commodityValue.getProductSupplier();
							String detail = commodityValue.getProductDetails();
							String info = objectMapper
									.writeValueAsString(commodityValue
											.getProductSpeci());
							String logo_url = commodityValue
									.getProductImagePath();
							String sql_check = "select *from spider_commodity_hc where commodity_url=\""
									+ url + "\"";
							logger.info(sql_check);
							List<Map<String, String>> list = DbUtils
									.execute(sql_check);

							if (list.size() == 0) {
								String sql = "insert into spider_commodity_hc"
										+ "(commodity_name,commodity_model,commodity_url,brand_name,commodity_attr,commodity_introduce,main_pic_url,price,`from`)values"
										+ "('" + name + "','" + model + "','"
										+ url + "','" + brand_name + "','"
										+ info + "','" + detail + "','"
										+ logo_url + "','" + price + "','"
										+ from + "')";
								logger.info(sql);
								int insert_num = DbUtils.insertSql(sql);
								if (insert_num > 0) {
									logger.info("insert success");
								}
							} else {
								logger.info("update start -------");
								String sql = "update spider_commodity_hc set commodity_name='"
										+ name
										+ "' , brand_name='"
										+ brand_name
										+ "' , commodity_attr='"
										+ info
										+ "' , commodity_introduce='"
										+ detail
										+ "' , main_pic_url='"
										+ logo_url
										+ "' , price='"
										+ price
										+ "' where commodity_url='" + url + "'";
								logger.info(sql);
								int insert_num = DbUtils.update(sql);
								if (insert_num > 0) {
									logger.info("update success");
								}
							}
						} catch (Exception e) {
							logger.info(e.getMessage());
						}
					}
				}
			}
		}
	}
}
