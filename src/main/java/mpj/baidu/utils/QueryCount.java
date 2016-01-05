package mpj.baidu.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class QueryCount {

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
			while (rs.next()) {
				String text = rs.getString("text");
				Set<String> stopword = GetStopWord.getStopWord();
				List<Term> result = ToAnalysis.parse(text);
				for (Term term : result) {
					if (!stopword.contains(term.getName())) {
						String key = term.getName().trim();
						if (null == map.get(key))
							map.put(key, 1);
						else {
							int num = map.get(key);
							map.put(key, num + 1);
						}
					}

				}
			}
			List<Map.Entry<String, Integer>> list = sortString(map);

			for (int i=0;i<100;i++) {
				Map.Entry<String, Integer> e=list.get(i);
				System.out.println(e.getKey() + ":" + e.getValue() );
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.out.println("========沉船=======");
		String sql = "select text from dfzx_query";
		count(sql);
		System.out.println("========股市=======");
		sql = "select text from gsdd_query";
		count(sql);
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
