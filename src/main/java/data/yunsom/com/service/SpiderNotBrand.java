package data.yunsom.com.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.yunsom.com.format.CommodityValue;
import data.yunsom.com.util.ElasticsearchUtil;
import data.yunsom.com.util.FileOperate;
import data.yunsom.com.util.JsonValidatorUtil;

public class SpiderNotBrand {
	private static final Logger logger = LoggerFactory
			.getLogger(SpiderNotBrand.class);
	public static TransportClient client = ElasticsearchUtil
			.getTransportClient();
	private static final FileOperate filecate = new FileOperate();

	public static void getTagS(String path) throws IOException {
	

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
							String commodity_url = commodityValue
									.getCommodity_url();
				
							java.net.URL urls = new java.net.URL(commodity_url);
							String from = urls.getHost();
							set.add(from);
						}catch (Exception e) {
							logger.error(e.getMessage());
						}
					}
				
			}
		}
		for (String data : set) {
			System.out.println(data);
		}
	}
}
