package mpj.baidu.EventHeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import mpj.baidu.bean.Result;
import mpj.baidu.utils.JdbcUtil;

public class EventHeat {
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;

	public double setHeartFromSQL(List<Result> list, String creat_time,
			String table) throws Exception {
		double heart;
		conn = JdbcUtil.getConnection();
		if (conn == null) {
			throw new Exception("数据库连接失败！");
		}
		String sql = "select count(create_time) as num from `" + table + "` "
				+ "where create_time like '" + creat_time + "%'";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		rs.next();
		int num = rs.getInt("num");
		if (num == 0)
			return 0;
		heart = list.size() / (double) num;
		return heart;
	}

	public double setHeartFromSQL(Map<String, Integer> dayRecordCount,
			String creat_time, String table) throws Exception {
		double heart;
		conn = JdbcUtil.getConnection();
		if (conn == null) {
			throw new Exception("数据库连接失败！");
		}
		String sql = "select count(create_time) as num from `" + table + "` "
				+ "where create_time like '" + creat_time + "%'";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		rs.next();
		int num = rs.getInt("num");
		System.out.print(num + " ");

		if (num == 0)
			return 0;
		if (table == "dfzx_weibo")
			heart = (double) dayRecordCount.get(creat_time) / (double) 15990;
		else
			heart = (double) dayRecordCount.get(creat_time) / (double) 35217;
		return heart;
	}

	public Map<String, Double> setHeartFromFile(Map<String, Double> wordweight,
			String filePath) throws IOException {
		System.out.println(filePath);
		Map<String, Double> heart = new HashMap<String, Double>();
		FileInputStream in = new FileInputStream(new File(filePath));
		Reader _reader = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(_reader);
		String tempString = null;
		Set<String> set = new HashSet<String>();

		int cc = 0;
		Set<String> hset = new HashSet<String>();
		while ((tempString = reader.readLine()) != null) {
			// String par[] = tempString.split("|");

			String par[] = StringUtils.split(tempString, "|");
			String create_time = par[par.length - 1];
			CollectionUtils.addAll(set, par);

			double score = 0.0;

			
			
			for (String keyword : wordweight.keySet()) {
				if (set.contains(keyword)) {
					score = score + wordweight.get(keyword);
				}
			}
			
			create_time = StringUtils.substring(create_time, 0, 10);

			if (null == heart.get(create_time)) {
				heart.put(create_time, score);
			} else {
				double temp = heart.get(create_time);
				heart.put(create_time, temp + score);
			}
			
			hset.add(create_time);
			// if (create_time == "2015-07-01") {
			// System.out.print("2015-07-01");
			// System.out.println(heart.get(create_time));
			// }
			set.clear();
		}
		return heart;
	}
}
