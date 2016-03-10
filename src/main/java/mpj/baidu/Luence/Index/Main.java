package mpj.baidu.Luence.Index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mpj.baidu.EventHeat.EventHeat;
import mpj.baidu.EventHeat.RecordRelation;
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

	public static String outdata(String kind) {
		String[] time = API.TIME;
		List<Double> scores = new ArrayList<Double>();
		EventHeat eh = new EventHeat();
		RecordRelation rr = new RecordRelation();

		// PolyLine pl=new PolyLine();

		try {
			String filepath = "";
			if (kind == "dfzx_weibo")
				filepath = "/Users/MPJ/Desktop/毕设相关资料/dfzx_weibo.txt";
			else 
				filepath = "/Users/MPJ/Desktop/毕设相关资料/dfzx_query.txt";
			
			Map<String, Double> wordweight = rr.getWordWeight(kind);
			Map<String, Double> heart = eh.setHeartFromFile(wordweight,
					filepath);
			for (int i = 0; i < time.length; i++) {
				scores.add(heart.get(time[i]));

			}
			JSONArray data = JSONArray.fromObject(scores);
			System.out.println(kind+":" + data);

			JSONArray label = JSONArray.fromObject(time);
			JSONObject json = new JSONObject();
			json.put("label", label);
			json.put("data1", data);
			return json.toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static String outdata() {
		String[] time = API.TIME;
		List<Double> scores1 = new ArrayList<Double>();
		List<Double> scores2 = new ArrayList<Double>();
		EventHeat eh = new EventHeat();
		RecordRelation rr = new RecordRelation();

		// PolyLine pl=new PolyLine();

		try {

			String kind = "dfzx";
			
			String str=kind+"_weibo";
			Map<String, Double> wordweight1 = rr.getWordWeight(str);
			Map<String, Double> heart1 = eh.setHeartFromFile(wordweight1,
					"/Users/MPJ/Desktop/毕设相关资料/"+kind+"/"+kind+"_weibo.txt");
			for (int i = 0; i < time.length; i++) {
				scores1.add(heart1.get(time[i])/7048);

			}
			JSONArray data1 = JSONArray.fromObject(scores1);
			System.out.println("weibo" + data1);

			str=kind+"_query";
			Map<String, Double> wordweight2 = rr.getWordWeight(str);
			Map<String, Double> heart2 = eh.setHeartFromFile(wordweight2,
					"/Users/MPJ/Desktop/毕设相关资料/"+kind+"/"+kind+"_query.txt");
			for (int i = 0; i < time.length; i++) {
				scores2.add(heart2.get(time[i])/22908);

			}
			JSONArray data2 = JSONArray.fromObject(scores2);
			System.out.println("baidu" + data2);

			JSONArray label = JSONArray.fromObject(time);
			JSONObject json = new JSONObject();
			json.put("label", label);
			json.put("data1", data1);
			json.put("data2", data2);
			return json.toString();

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
