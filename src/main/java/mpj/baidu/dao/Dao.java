package mpj.baidu.dao;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

public class Dao {

	private static Mongo mongo;
	private static DB myMongo;
	private static final Dao dd = new Dao();

	// 静态工厂方法
	public static Dao getInstance() {
		return dd;
	}

	public  Dao() {

		try {
			MongoOptions options = new MongoOptions();
			options.connectionsPerHost = 100;
		    options.maxWaitTime = 2000;
		    options.socketKeepAlive = true;
		    options.threadsAllowedToBlockForConnectionMultiplier = 50;
			mongo = new Mongo("localhost",options);
			myMongo = mongo.getDB("baidu");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public static DB getDb(){
//		return myMongo;
//	}
	public  DB getDb(){
		return myMongo;
	}
}
