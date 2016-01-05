package mpj.baidu.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import mpj.baidu.dao.StopWordDao;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

public class DbToFile {

	public static void toFile(String filePath, String sql) {
		Connection conn = JdbcUtil.getConnection();
		try {
			if (conn == null) {
				throw new Exception("数据库连接失败！");
			}

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			File file = new File(filePath);

			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			StringBuffer sb = new StringBuffer();
			while (rs.next()) {
				// String id = rs.getString("weiboid");
				Set<String> stopword = GetStopWord.getStopWord();
				String rt_text = rs.getString("rt_text");
				List<Term> result = ToAnalysis.parse(rt_text);
				for (Term term : result) {
					if (!stopword.contains(term.getName()))
						sb.append(term.getName().trim() + " ");
				}
				String text = rs.getString("text");
				result = ToAnalysis.parse(text);
				for (Term term : result) {
					if (!stopword.contains(term.getName()))
						sb.append(term.getName().trim() + " ");
				}
			}
			output.write(sb.toString());
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String dir = "/Users/MPJ/Applications/w2v/trunk/nouserlibrary/gudddata财经金融词汇大全.txt";
		DbToFile.toFile(dir, "select * from gsdd_weibo");
	}
}