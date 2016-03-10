package mpj.baidu.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class WeiboCount {

	public static void count(String sql) {
		Connection conn = JdbcUtil.getConnection();
		Statement stmt;
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			if (conn == null) {
				throw new Exception("数据库连接失败！");
			}
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			StringBuffer buffer = new StringBuffer();
			while (rs.next()) {
				StringBuffer sb = new StringBuffer();
				String text = rs.getString("text");
				String rt_text = rs.getString("rt_text");
				String create_time=rs.getString("create_time");
				sb.append(text);
				sb.append(rt_text);
				Set<String> stopword = GetStopWord.getStopWord();
				List<Term> result = ToAnalysis.parse(sb.toString());
				Set<String> set = new HashSet<String>();

				for (Term term : result) {
					String name = term.getName();
					if (!set.contains(name)) {
						set.add(name);
						buffer.append(name+"|");
					}

				}
				buffer.append(create_time+"\n");

				// for (String name : set) {
				// if (!stopword.contains(name)) {
				// String key = name;
				// if (null == map.get(key))
				// map.put(key, 1);
				// else {
				// int num = map.get(key);
				// map.put(key, num +1);
				// }
				// }
				//
				// }
			}
			String filePath="/Users/MPJ/Desktop/毕设相关资料/gsdd/gsdd_weibo.txt";
			File file = new File(filePath);

			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(buffer.toString());
			rs.close();
			stmt.close();
			conn.close();
//			List<Map.Entry<String, Integer>> list = sortString(map);
//
//			for (int i = 0; i < 50; i++) {
//				Map.Entry<String, Integer> e = list.get(i);
//				System.out.println(e.getKey() + ":" + e.getValue());
//			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.out.println("========沉船=======");
		String sql = "select create_time,rt_text, text from gsdd_weibo";
		count(sql);
//		System.out.println("========股市=======");
//		sql = "select rt_text, text from gsdd_weibo";
//		count(sql);
	}

	/**
	 * 对map进行排序
	 * 
	 * @param value
	 * @return
	 */
	public static List<Map.Entry<String, Integer>> sortString(
			Map<String, Integer> value) {
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
				value.entrySet());

		Comparator<Map.Entry<String, Integer>> com = new Comparator<Map.Entry<String, Integer>>() {

			public int compare(Map.Entry<String, Integer> left,
					Map.Entry<String, Integer> right) {

				return (right.getValue() - left.getValue());
			}
		};

		Collections.sort(list, com);
		return list;
	}
}
