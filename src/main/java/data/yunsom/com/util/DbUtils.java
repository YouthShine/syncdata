package data.yunsom.com.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//数据库查询
public class DbUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(ConnectionPool.class);
	private static Connection conn =ConnectionPool.getInstance().getConnection();
	private static Statement stmt = null;
	private static ResultSet rs = null;

	public static List<Map<String, String>> execute(String sql) {
		List<Map<String, String>> queryList = new ArrayList<Map<String,String>>();
		try {
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			queryList = getQueryList(rs);  
			return queryList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				

			} catch (SQLException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		return queryList;
	}
	private static List<Map<String, String>> getQueryList(ResultSet rs) throws Exception {  
        if(rs == null) {  
            return null;  
        }  
        ResultSetMetaData rsMetaData = rs.getMetaData();  
        int columnCount = rsMetaData.getColumnCount();  
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();  
        while (rs.next()) {  
            Map<String, String> dataMap = new HashMap<String, String>();  
            for (int i = 0; i < columnCount; i++) {  
            	
                dataMap.put(rsMetaData.getColumnName(i+1), rs.getString(i+1));  
            }  
            dataList.add(dataMap);  
        }  
        return dataList;  
    }  
	public static Integer update(String sql){
			
         
	
	        try {
	            //从工具类中获取连接
	        	stmt = conn.createStatement();
	          
	            int result = stmt.executeUpdate(sql);
	            //处理结果
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally{
	        	try {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					
					

				} catch (SQLException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
	        }
	        return 0;
	}
	public static Integer insertSql(String sql){
		int i = 0;
		try {
			PreparedStatement preStmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			preStmt.executeUpdate();
			ResultSet rs = preStmt.getGeneratedKeys(); // 获取结果
			if (rs.next()) {
				i = rs.getInt(1);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	// 插入数据@comd_tag
		public static Integer insert(String sql_insert, String category) {
			int i = 0;

			try {
				PreparedStatement preStmt = conn.prepareStatement(sql_insert,Statement.RETURN_GENERATED_KEYS);
				preStmt.setString(1, category);
				// preStmt.setString(2,值);//或者：preStmt.setInt(1,值);
				preStmt.executeUpdate();
				ResultSet rs = preStmt.getGeneratedKeys(); // 获取结果
				if (rs.next()) {
					i = rs.getInt(1);
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return i;
		}

		// 插入数据@comd_tag_meta
		public static Integer insert_meta(String sql_insert, int tag_id_one,
				int tag_id_two, int tag_id) {
			int i = 0;

			try {
				PreparedStatement preStmt = conn.prepareStatement(sql_insert,Statement.RETURN_GENERATED_KEYS);
				preStmt.setInt(1, tag_id_one);
				preStmt.setInt(2, tag_id_two);
				preStmt.setInt(3, tag_id);
				// preStmt.setString(2,值);//或者：preStmt.setInt(1,值);
				preStmt.executeUpdate();//一定要执行更新
				ResultSet rs = preStmt.getGeneratedKeys(); // 获取结果
				if (rs.next()) {
					i = rs.getInt(1);
					return i;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return i;

		}

		// 查询@comd_tag_meta
		public static int executeIdd(String sql) {
			int id = 0;
			try {

				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);

				while (rs.next()) {
					id = rs.getInt(1);// 或者为rs.getString(1)，根据数据库中列的值类型确定，参数为第一列
					// String m2 = rs.getString(2);

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					if (stmt != null) {
						stmt.close();
						stmt = null;
					}

				} catch (SQLException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
			return id;
		}

		// 插入数据@spider_commodity
		public static Integer insert_spider(String sql_insert,  String commodity_name,
				String commodity_model, String commodity_url, int tag_id,
				int brand_id, String commodity_attr, String commodity_introduce,
				String main_pic_url, double price, String from) {
			int i = 0;

			try {
				PreparedStatement preStmt = conn.prepareStatement(sql_insert,Statement.RETURN_GENERATED_KEYS);
				preStmt.setString(1, commodity_name);
				preStmt.setString(2,commodity_model);
				preStmt.setString(3, commodity_url);
				preStmt.setInt(4, tag_id);
				preStmt.setInt(5, brand_id);
				preStmt.setString(6, commodity_attr);
				preStmt.setString(7, commodity_introduce);
				preStmt.setString(8, main_pic_url);
				preStmt.setDouble(9, price);
				preStmt.setString(10, from);
				// preStmt.setString(2,值);//或者：preStmt.setInt(1,值);
				preStmt.executeUpdate();
				ResultSet rs = preStmt.getGeneratedKeys(); // 获取结果
				if (rs.next()) {
					i = rs.getInt(1);
					logger.info("tag_id_spider_s:"+i);
					return i;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return i;

		}

		// 查询@spider_commodity
		public static int executeIdd_spider(String sql) {
			int id = 0;
			try {

				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);

				while (rs.next()) {
					id = rs.getInt(1);// 或者为rs.getString(1)，根据数据库中列的值类型确定，参数为第一列
					// String m2 = rs.getString(2);
					return id;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					if (stmt != null) {
						stmt.close();
						stmt = null;
					}

				} catch (SQLException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
			return id;
		}
		

}
