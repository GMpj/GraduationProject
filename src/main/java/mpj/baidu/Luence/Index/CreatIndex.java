package mpj.baidu.Luence.Index;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import mpj.baidu.utils.JdbcUtil;

import org.ansj.lucene4.AnsjAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class CreatIndex {
	
	private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;
  //设定索引的存放路径
//    private String searchDir = "/Users/MPJ/index";
    private static File indexFile = null;
    private static IndexSearcher searcher = null;
    private static Analyzer analyzer =  new AnsjAnalysis();;
    public Query query = null;
    
    
	public void creatIndex(String sql,String searchDir) throws Exception{
		conn = JdbcUtil.getConnection();
        Directory directory = null;
        IndexWriter indexWriter = null;
        if (conn == null) {
            throw new Exception("数据库连接失败！");
        }
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            indexFile = new File(searchDir);
            if (!indexFile.exists()) {
                indexFile.mkdir();
            }
            directory = FSDirectory.open(indexFile);
//            analyzer = new StandardAnalyzer(Version.LUCENE_47);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47,
                    analyzer);
            
            indexWriter = new IndexWriter(directory, iwc);
            Document doc = null;
            while (rs.next()) {
//                String id = rs.getString("weiboid");
            	String rt_text;
            	if(searchDir.equals("/Users/MPJ/WeiboIndex"))
            		rt_text= rs.getString("rt_text");
            	else rt_text="";
                String create_time = rs.getString("create_time");
                String text = rs.getString("text");
 
                doc = new Document();
                
                /**此处是相对lucene4.*之前的版本改动比较大的地方，不能再直接new Field，而是new IntField，StringField，TextField等，其中
                 * TextField默认分词，StringField默认不分词，因为我这里name和descr都需要分词，所以都用的TextField
                 * */
                doc.add(new StringField("weiboid", "", Field.Store.YES));
                doc.add(new TextField("rt_text", rt_text, Field.Store.YES));
                doc.add(new TextField("text", text, Field.Store.YES));
                doc.add(new StringField("create_time", create_time, Field.Store.YES));
                indexWriter.addDocument(doc);
            }
            indexWriter.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
	}
}
