package mpj.baidu.Luence.Index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpj.baidu.EventHeat.EventHeat;
import mpj.baidu.bean.Result;
import mpj.baidu.utils.API;
import mpj.baidu.utils.PolyLine;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import test.Product;

public class Main {

	public static void main(String[] args) {
		CreatIndex index = new CreatIndex();
		String querysql = "select * from dfzx_query";
		String weibosql = "select * from dfzx_weibo";
		String queryDir = "/Users/MPJ/BaiduIndex";
		String weiboDir = "/Users/MPJ/WeiboIndex";
		Search search=new Search();
		String queryStr="长江沉船";
		String []field={"rt_text","text"};
		String []time=API.TIME;
		Map<String,Double> map1=new HashMap<String,Double>();
		Map<String,Double> map2=new HashMap<String,Double>();
		EventHeat eh=new EventHeat();
		PolyLine pl=new PolyLine();
		try {
//			index.creatIndex(weibosql, weiboDir);
//			index.creatIndex(querysql, queryDir);
			
			for(int i=0;i<time.length;i++){
			List<Result> results=search.getResult(queryStr, weiboDir,field,time[i]);
			double score =eh.setHeartFromSQL(results, time[i],"dfzx_weibo");
			map1.put(time[i], score);
			}
			
			
			for(int i=0;i<time.length;i++){
				List<Result> results=search.getResult(queryStr, queryDir,field,time[i]);
				double score =eh.setHeartFromSQL(results, time[i],"dfzx_query");
				map2.put(time[i], score);
				}
			
			for(int i=0;i<time.length;i++){
				System.out.println("社交网络:"+time[i]+":"+map1.get(time[i]));
				System.out.println("搜索引擎:"+time[i]+":"+map1.get(time[i]));
			}
			
			pl.createPolyLine(time, map1, map2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
