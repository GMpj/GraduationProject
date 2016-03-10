package mpj.baidu.Luence.Index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mpj.baidu.bean.Result;

import org.ansj.lucene4.AnsjAnalysis;
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

import test.Product;

public class Search {

	public List<Result> getResult(List<Result> set, String time)
			throws ParseException, IOException {

		List<Result> temp = new ArrayList<Result>();
		for (Result r : set) {
			if (r.getCreate_time().substring(0, 10).equals(time)) {
				temp.add(r);
			}
		}
		System.out.println(time + ":" + temp.size());
		// for(int i=0;i<30;i++){
		// System.out.println(temp.get(i).getRt_text()+":"+temp.get(i).getText()+":"+temp.get(i).getCreate_time());
		// }
		return temp;
	}

	public List<Result> search(String[] queryStr, String dir, String[] field)
			throws ParseException, IOException {

		File indexFile = new File(dir);
		IndexReader reader;
		reader = DirectoryReader.open(FSDirectory.open(indexFile));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new AnsjAnalysis();
		/**
		 * 同时搜索name和descr两个field，并设定它们在搜索结果排序过程中的权重，权重越高，排名越靠前
		 * 为了后面解释score问题的方便，这里设置相同的权重
		 * */
		Map<String, Float> boosts = new HashMap<String, Float>();
		boosts.put("rt_text", 1.0f);
		boosts.put("text", 1.0f);
		/**
		 * 用MultiFieldQueryParser类实现对同一关键词的跨域搜索
		 * */
		MultiFieldQueryParser parser = new MultiFieldQueryParser(
				Version.LUCENE_47, field, analyzer, boosts);
		List<Result> result = new ArrayList<Result>();

		for (int i = 0; i < queryStr.length; i++) {
			Query query = parser.parse(queryStr[i]);

			TopDocs topDocs = searcher.search(query, 1000000);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			System.out.println("QueryParser :" + query.toString() + "  查询数据："
					+ scoreDocs.length);
			List<Result> temp = this.addHits2List(scoreDocs, searcher);

			result = temp;
			// for (Result r : temp) {
			// if (!catain(result, r))
			// result.add(r);
			// }
		}
		System.out.println("总数:" + result.size());
		return result;
	}

	

	public List<Result> addHits2List(ScoreDoc[] scoreDocs,
			IndexSearcher searcher) throws IOException {
		List<Result> listBean = new ArrayList<Result>();
		Result result = null;
		for (int i = 0; i < scoreDocs.length; i++) {
			int docId = scoreDocs[i].doc;
			Document doc = searcher.doc(docId);
			result = new Result();
			if (null != doc.get("weiboid")) {
				result.setId(doc.get("weiboid"));
			} else {
				result.setId(doc.get("qid"));
			}
			result.setRt_text(doc.get("rt_text"));
			result.setText(doc.get("text"));
			result.setCreate_time(doc.get("create_time"));
			listBean.add(result);
			/**
			 * 打印对结果score的解析，用于分析排序的依据，通过观察结果和搜索相关信息，得到以下结论
			 * 1.结果排序的score依据是fieldWeight的大小，fieldWeight的计算公式为fieldWeight = tf *
			 * idf * fieldNorm；
			 * 
			 * tf表示的是查询条件中，每个查询词（t：term）在本文档（d）中的出现频率。查询关键词出现的频率越高，文档的得分就越高。
			 * 这个部分的默认计算公式是： tf(t in d) = frequency½
			 * 
			 * idf表示的是反转文档频率（ Inverse Document
			 * Frequency）.这个函数表示的是（t：term）在所有文档中一共在多少个文档中出现过。
			 * 因为文档出现的次数越少就越容易定位，所以文档数越少，得分就越高。这个函数的默认计算公式如下： idf =
			 * log(numDocs/(docFreq+1)) + 1
			 * 在以下的例子里，可以简单的理解为，在全部的文档里，name这个field里出现查询词“飞剑侠”的文档越多该文档的idf值就越低
			 * bean.id 24 : bean.name 飞剑侠 : bean.descr 飞剑侠！ bean.id 27 :
			 * bean.name 飞剑侠 : bean.descr 飞剑侠武器！ bean.id 25 : bean.name 飞剑侠飞剑侠 :
			 * bean.descr 测试修改数据新！ bean.id 8 : bean.name 飞剑侠 : bean.descr 钢笔
			 * bean.id 22 : bean.name 3钢笔 : bean.descr 飞剑侠飞剑侠！飞剑侠 bean.id 23 :
			 * bean.name 4钢笔 : bean.descr 飞剑侠飞剑侠！ bean.id 26 : bean.name 钢笔 :
			 * bean.descr 飞剑侠 searchBean.result.size : 7
			 * 
			 * 以上例子中name和descr的权重相同，ID为8的文档name这个field里命中1个查询词“飞剑侠”，
			 * 为什么会比ID为26的文档中descr这个field里 命中一个查询词“飞剑侠”排名靠前哪么多？
			 * 这就是因为在最终得到的结果里，有4个文档是在name这个field里查到了“飞剑侠”，而有5个文档在descr这个
			 * field里查到了 “飞剑侠”，所以在name里查到“飞剑侠”的文档8比在descr里查到“飞剑侠”的文档26的idf要高，
			 * 所以文档8排在了26前面
			 * 
			 * fieldNorm是事先计算好了的，它等于1/sqrt(wordsNum -
			 * 1)。我们可以简单的理解为在tf和idf不变的情况下，文档的包含的内容越少
			 * fieldNorm的值就越高，这也是为什么上面例子的结果里id24比27排名靠前。
			 * */
			// System.out.println("**************************************************************************");
			// System.out.println(searcher.explain(query, docId));
			// System.out.println("**************************************************************************");

		}
		return listBean;
	}

}
