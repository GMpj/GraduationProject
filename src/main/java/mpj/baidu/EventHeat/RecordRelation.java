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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.lucene4.AnsjAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import mpj.baidu.bean.Result;
import mpj.baidu.utils.API;
import mpj.baidu.utils.JdbcUtil;

public class RecordRelation {

	public Map<String, Double> setScore(Map<String, Double> map,
			Map<String, Integer> temp) {
		for (String key : temp.keySet()) {
			int num = temp.get(key);

			if (null == map.get(key)) {
				map.put(key, 0.1);
			} else {
				double score = map.get(key);
				score = score + 0.2;
			}
		}
		return map;
	}

	public Map<String, Double> getWordWeight(String kind) {
		Map<String, Double> weight = new HashMap<String, Double>();
		if (kind .equals("dfzx_weibo") ) {
			weight.put("蜡烛", 0.15058792);
			weight.put("遇难", 0.146844488);
			weight.put("东方之星", 0.14511578);
			weight.put("沉船", 0.11815503);
			weight.put("事故", 0.05899772);
			weight.put("默哀", 0.05189141);
			weight.put("救援", 0.04707577);
			weight.put("确认", 0.04631019);
			weight.put("家属", 0.03505281);
			weight.put("祭奠", 0.03460236);
			weight.put("客轮", 0.03036443);
			weight.put("下落不明", 0.02913627);
			weight.put("悼念", 0.02851008);
			weight.put("人数", 0.0260457);
			weight.put("7天", 0.02593662);
			weight.put("12", 0.02537304);

		} else if (kind .equals( "dfzx_query")) {
			weight.put("东方之星", 0.52998017);
			weight.put("生还", 0.12763025);
			weight.put("12", 0.12649424);
			weight.put("事故", 0.10851431);
			weight.put("长江沉船", 0.10738103);
		}
		else if(kind.equals("gsdd_weibo")){
			weight.put("股市", 0.22091940);
			weight.put("暴跌", 0.15848455);
			weight.put("t", 0.14749081);
			weight.put("沪指", 0.07966187);
			weight.put("大跌", 0.06014021);
			weight.put("股市暴跌", 0.05909586);
			weight.put("股票", 0.04850176);
			weight.put("跌幅", 0.04785784);
			weight.put("股市大跌", 0.04308514);
			weight.put("股民", 0.03892474);
			weight.put("下跌", 0.03678386);
			weight.put("创业板", 0.03050788);
			weight.put("证监会", 0.02854607);
			
		}
		else if(kind.equals("gsdd_query")){
			weight.put("暴跌", 0.25401870);
			weight.put("沪指", 0.20269908);
			weight.put("股市暴跌", 0.19262255);
			weight.put("4500点", 0.12909655);
			weight.put("股市大跌", 0.09573108);
			weight.put("原因", 0.04651695);
			weight.put("a股", 0.04173053);
			weight.put("股市", 0.03758456);
		}
		return weight;
	}

	public Map<String, Double> getWordWeightOld(String[] word, String keyword,
			String dir, String[] field) throws IOException, ParseException {

		Map<String, Double> weight = new HashMap<String, Double>();
		File indexFile = new File(dir);
		IndexReader reader;
		reader = DirectoryReader.open(FSDirectory.open(indexFile));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new AnsjAnalysis();

		Map<String, Float> boosts = new HashMap<String, Float>();
		boosts.put("rt_text", 1.0f);
		boosts.put("text", 1.0f);

		MultiFieldQueryParser parser = new MultiFieldQueryParser(
				Version.LUCENE_47, field, analyzer, boosts);
		List<Result> result = new ArrayList<Result>();

		Query query = parser.parse(keyword);
		TopDocs topDocs = searcher.search(query, 1000000);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		int kwc = scoreDocs.length;
		int hwc = 0;
		double sum = 0.0;
		System.out.println("kwc:" + kwc);

		for (int i = 0; i < word.length; i++) {
			query = parser.parse(word[i]);
			Set<String> kidset = addHits2SetId(scoreDocs, searcher);
			topDocs = searcher.search(query, 1000000);
			scoreDocs = topDocs.scoreDocs;
			hwc = hwc + scoreDocs.length;

			int count = 0;
			Set<String> hidset = addHits2SetId(scoreDocs, searcher);
			// for (String id : hidset) {
			// if (kidset.contains(id))
			// count++;
			// }
			count = scoreDocs.length;
			double temp = (double) count / kwc;
			System.out.println(word[i] + " count:" + count + " temp:" + temp);
			sum = sum + temp;
			weight.put(word[i], temp);
		}

		System.out.println("hwc:" + hwc);
		for (int i = 0; i < word.length; i++) {
			double temp = weight.get(word[i]);
			temp = temp * 1 / sum * hwc / (kwc + hwc);
			weight.put(word[i], temp);
		}
		weight.put(keyword, (double) kwc / (kwc + hwc));
		for (String key : weight.keySet()) {
			System.out.println(key + ":" + weight.get(key) + " ");
		}
		return weight;
	}

	public Double getAverageRecordHot(Map<String, Double> wordweight,
			String filePath) throws IOException {
		double sum = 0.0;
		double average = 0.0;
		FileInputStream in = new FileInputStream(new File(filePath));
		Reader _reader = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(_reader);
		String tempString = null;
		StringBuffer bf = new StringBuffer();
		Set<String> set = new HashSet<String>();
		StringBuffer sb = new StringBuffer();

		int count = 0;
		while ((tempString = reader.readLine()) != null) {
			count++;
			String par[] = StringUtils.split(tempString, "|");
			String create_time = par[par.length - 1];
			CollectionUtils.addAll(set, par);

			double score = 0.0;

			for (String keyword : wordweight.keySet()) {
				if (set.contains(keyword)) {
					score = score + wordweight.get(keyword);
				}
			}
			sum = sum + score;
		}
		System.out.println("sum:" + sum);
		average = sum / count;
		System.out.println("average:" + average);
		return average;
	}

	public Map<String, Integer> getRelationRecordCountFile(
			Map<String, Double> wordweight, String filePath, String kind)
			throws Exception {
		Map<String, Integer> dayRecordCount = new HashMap<String, Integer>();

		FileInputStream in = new FileInputStream(new File(filePath));
		Reader _reader = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(_reader);
		String tempString = null;
		Set<String> set = new HashSet<String>();

		int cc = 0;
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
			// create_time = create_time.substring(0, 10);
			double level;

			if (kind.equals("dfzx_weibo"))
				level = API.thresholdWeiboScore;
			else
				level = API.thresholdQueryScore;

			
			if (score > level) {
				if (cc < 20) {
//					System.out.println(tempString);
					cc++;
				}

				if (null == dayRecordCount.get(create_time)) {
					dayRecordCount.put(create_time, 1);
				} else {
					int num = dayRecordCount.get(create_time);
					num = num + 1;
					dayRecordCount.put(create_time, num);
				}
			}

			set.clear();

		}
		reader.close();
		System.out.println(dayRecordCount);
		return dayRecordCount;
	}
	
	
	public Set<String> addHits2SetId(ScoreDoc[] scoreDocs,
			IndexSearcher searcher) throws IOException {
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < scoreDocs.length; i++) {
			int docId = scoreDocs[i].doc;
			Document doc = searcher.doc(docId);
			if (null != doc.get("weiboid")) {
				set.add(doc.get("weiboid"));
			} else {
				set.add(doc.get("qid"));
			}
		}
		return set;
	}
}
