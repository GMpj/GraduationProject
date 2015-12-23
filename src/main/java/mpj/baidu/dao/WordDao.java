package mpj.baidu.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Test;




import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class WordDao {
	private DB myMongo;
	private DBCollection collection;
	private Mongo mongo;

	private static final WordDao wd = new WordDao();

	// 静态工厂方法
	public static WordDao getInstance() {
		return wd;
	}

	private WordDao() {
		try {
//			mongo = new Mongo();
//			myMongo = mongo.getDB("myMongo");
//			myMongo=Dao.getDb();
			Dao d=Dao.getInstance();
			myMongo=d.getDb();
			collection = myMongo.getCollection("word");
			collection.createIndex(new BasicDBObject("key", 1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 保存
	 * 
	 * @param user
	 * @throws UnknownHostException
	 */
	@Test
	public void save(Word word) {

		JSONObject json = JSONObject.fromObject(word);
//		System.out.println(json);
		DBObject dbo = (DBObject) JSON.parse(json.toString());
		collection.insert(dbo);
	}

	/**
	 * 更新
	 * 
	 * @param user
	 * @throws UnknownHostException
	 */
	public void update(Word word) {

		BasicDBObject baseDBO = new BasicDBObject();
		baseDBO.put("key", word.getKey());

		JSONObject json = JSONObject.fromObject(word);
//		System.out.println("update:" + json);
		DBObject newDBO = (DBObject) JSON.parse(json.toString());

		collection.update(baseDBO, newDBO);
	}

	/**
	 * 查询
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public Word find(String word) {

		
		DBObject query = (DBObject) JSON.parse("{\"key\":\"" + word + "\"}");
		DBCursor cursor = collection.find(query);
		if (cursor.hasNext()) {
			DBObject result = cursor.next();
			String key = (String) result.get("key");
			LinkedHashMap<String, Integer> map = (LinkedHashMap<String, Integer>) result
					.get("value");

			Word temp = new Word();
			temp.setKey(key);
			temp.setValue(map);
			return temp;
		} else
			return null;
	}

	public void findAll() {
		DBCursor cursor = collection.find();

		while (cursor.hasNext()) {
			DBObject result = cursor.next();
//			System.out.println(result.get("_id"));
//			System.out.println(result);
		}
//		System.out.println(collection.getCount());

	}
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		mongo.close();
	}
}
