package mpj.baidu.Luence.Index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mpj.baidu.EventHeat.EventHeat;
import mpj.baidu.bean.Result;
import mpj.baidu.utils.API;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import test.Product;

public class Main {

	public static String outdata() {
		CreatIndex index = new CreatIndex();
		String querysql = "select * from gsdd_query";
		String weibosql = "select * from gsdd_weibo";
		String queryDir = "/Users/MPJ/BaidugsddWordIndex";
		String weiboDir = "/Users/MPJ/WeibogsddWordIndex";
		Search search = new Search();
		String[] field = { "rt_text", "text" };
		String[] time = API.TIME;
		Map<String, Double> map1 = new HashMap<String, Double>();
		Map<String, Double> map2 = new HashMap<String, Double>();
		List<Double> scores1 = new ArrayList<Double>();
		List<Double> scores2 = new ArrayList<Double>();
		EventHeat eh = new EventHeat();
		// PolyLine pl=new PolyLine();

		String[] queryStr = API.word;
		
		try {
			index.creatIndex(weibosql, weiboDir,"weibo");
			index.creatIndex(querysql, queryDir,"baidu");

			List<Result> list1 = search.search(queryStr, weiboDir, field);
			for (int i = 0; i < time.length; i++) {
				List<Result> result = search.getResult(list1, time[i]);
				double score = eh
						.setHeartFromSQL(result, time[i], "gsdd_weibo");
				// map1.put(time[i], score);
				scores1.add(score);
			}
			JSONArray data1 = JSONArray.fromObject(scores1);
			System.out.println("weibo" + data1);
			
			List<Result> list2 = search.search(queryStr, queryDir, field);
			for (int i = 0; i < time.length; i++) {
				List<Result> result = search.getResult(list2, time[i]);
				double score = eh
						.setHeartFromSQL(result, time[i], "gsdd_query");
				// map2.put(time[i], score);
				scores2.add(score);
			}
			JSONArray data2 = JSONArray.fromObject(scores2);
			System.out.println("baidu" + data2);
			
			JSONArray label = JSONArray.fromObject(time);
			JSONObject json = new JSONObject();
			json.put("label", label);
			json.put("data1", data1);
			json.put("data2", data2);
			return json.toString();
			// for(int i=0;i<time.length;i++){
			// System.out.println("社交网络:"+time[i]+":"+map1.get(time[i]));
			// System.out.println("搜索引擎:"+time[i]+":"+map1.get(time[i]));
			// }

			// pl.createPolyLine(time, map1, map2);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args) {
		outdata();
	}
}
