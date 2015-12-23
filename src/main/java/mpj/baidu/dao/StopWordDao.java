package mpj.baidu.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class StopWordDao {
	private DB myMongo;
	private DBCollection collection;

	private Mongo mongo;

	private static final StopWordDao dd = new StopWordDao();

	// 静态工厂方法
	public static StopWordDao getInstance() {
		return dd;
	}

	private StopWordDao() {

		try {
//			mongo = new Mongo();
//			myMongo = mongo.getDB("myMongo");
			Dao d=Dao.getInstance();
			myMongo=d.getDb();
//			myMongo=Dao.getDb();
			collection = myMongo.getCollection("stopword");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save() throws IOException {
		collection.createIndex(new BasicDBObject("word", 1));
		String fileName = "/Users/MPJ/Desktop/stopword";
		FileInputStream in = new FileInputStream(new File(fileName));
		Reader _reader = new InputStreamReader(in, "utf-8");
		BufferedReader reader = new BufferedReader(_reader);
		String tempString = null;
		StringBuffer bf = new StringBuffer();
		// 一次读入一行，直到读入null为文件结束
		while ((tempString = reader.readLine()) != null) {
			DBObject dbo = (DBObject) JSON.parse("{\"word\":\"" + tempString
					+ "\"}");
			collection.insert(dbo);
		}
		reader.close();

	}

	public boolean isIngore(String word) {
		DBObject query;
		if (word.equals("\"") || word.equals("\\"))
			query = (DBObject) JSON.parse("{\"word\":\"\\" + word + "\"}");
		else
			query = (DBObject) JSON.parse("{\"word\":\"" + word + "\"}");

		DBCursor cursor = collection.find(query);

		if (cursor.hasNext()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		mongo.close();
	}
}
