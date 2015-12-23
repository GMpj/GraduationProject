package mpj.baidu.Luence.Index;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Iterator;

import love.cq.util.IOUtil;
import mpj.baidu.dao.StopWordDao;
import mpj.baidu.utils.GetStopWord;

import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.*;
import org.ansj.recognition.*;

public class test {
	public static final String TAG_START_CONTENT = "<content>";
	public static final String TAG_END_CONTENT = "</content>";

	public static void main(String[] args) {
		String temp = null;
//		StopWordDao sw = StopWordDao.getInstance();

		BufferedReader reader = null;
		PrintWriter pw = null;
		try {
			reader = IOUtil.getReader(
					"/Users/MPJ/Applications/w2v/trunk/corpus.txt", "UTF-8");
			// ToAnalysis.parse("test 123 孙") ;
			pw = new PrintWriter("resultbig.txt");
			long start = System.currentTimeMillis();
			int allCount = 0;
			int termcnt = 0;
			Set<String> set = new HashSet<String>();
			Set<String> stopwords=GetStopWord.getStopWord();
			while ((temp = reader.readLine()) != null) {
				temp = temp.trim();
				if (temp.startsWith(TAG_START_CONTENT)) {
					int end = temp.indexOf(TAG_END_CONTENT);
					String content = temp.substring(TAG_START_CONTENT.length(),
							end);
					// System.out.println(content);
					if (content.length() > 0) {
						allCount += content.length();
						List<Term> result = ToAnalysis.parse(content);
						for (Term term : result) {

							String item = term.getName().trim();
							if (!stopwords.contains(item)) {

								if (item.length() > 0) {
									termcnt++;
									pw.print(item.trim() + " ");
									set.add(item);
								}
							}
						}
						pw.println();
					}
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("共" + termcnt + "个term，" + set.size()
					+ "个不同的词，共 " + allCount + " 个字符，每秒处理了:"
					+ (allCount * 1000.0 / (end - start)));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != pw) {
				pw.close();
			}
		}
	}
}
