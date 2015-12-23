package test;
 
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpj.baidu.utils.JdbcUtil;

import org.ansj.lucene4.AnsjAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
 
/**
 * SearchLogic.java
 * 
 * @version 1.0
 * @createTime Lucene数据库检索
 */
public class SearchLogic {
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;
    //设定索引的存放路径
    private String searchDir = "/Users/MPJ/index";
    private static String[] field = null;
    private static File indexFile = null;
    private static IndexSearcher searcher = null;
    private static Analyzer analyzer =  new AnsjAnalysis();
    public Query query = null;
 
    /** 索引页面缓冲 */
    /**
     * 获取数据库数据
     * 
     * @return ResultSet
     * @throws Exception
     */
    public List<Product> getResult(String queryStr) throws Exception {
        List<Product> result = null;
        TopDocs topDocs = this.search(queryStr);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        result = this.addHits2List(scoreDocs);
        return result;
    }
 
    /**
     * 为数据库检索数据创建索引
     * 
     * @param rs
     * @throws Exception
     */
    private void createIndex() throws Exception {
        conn = JdbcUtil.getConnection();
        Directory directory = null;
        IndexWriter indexWriter = null;
        if (conn == null) {
            throw new Exception("数据库连接失败！");
        }
        String sql = "select id, name, descr from product";
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
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String descr = rs.getString("descr");
 
                doc = new Document();
                /**此处是相对lucene4.*之前的版本改动比较大的地方，不能再直接new Field，而是new IntField，StringField，TextField等，其中
                 * TextField默认分词，StringField默认不分词，因为我这里name和descr都需要分词，所以都用的TextField
                 * */
                doc.add(new IntField("id", id, Field.Store.YES));
                doc.add(new TextField("name", name, Field.Store.YES));
                doc.add(new TextField("descr", descr, Field.Store.YES));
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
 
    /**
     * 搜索索引
     * 
     * @param queryStr
     * @return
     * @throws Exception
     */
    private TopDocs search(String queryStr) throws Exception {
        if (searcher == null) {
            indexFile = new File(searchDir);
            IndexReader reader = DirectoryReader.open(FSDirectory
                    .open(indexFile));
            searcher = new IndexSearcher(reader);
        }
        /**同时搜索name和descr两个field，并设定它们在搜索结果排序过程中的权重，权重越高，排名越靠前
         *为了后面解释score问题的方便，这里设置相同的权重
         * */
        Map<String , Float> boosts = new HashMap<String, Float>();
        boosts.put("name", 1.0f);
        boosts.put("descr", 1.0f);
        /**用MultiFieldQueryParser类实现对同一关键词的跨域搜索 
         * */
        MultiFieldQueryParser  parser = new MultiFieldQueryParser(Version.LUCENE_47, field,
        		analyzer,boosts);
        query = parser.parse(queryStr);
        System.out.println("QueryParser :" + query.toString());
        TopDocs topDocs = searcher.search(query, 10000);
        return topDocs;
    }
 
    /**
     * 返回结果并添加到List中
     * 
     * @param scoreDocs
     * @return
     * @throws Exception
     */
    private List<Product> addHits2List(ScoreDoc[] scoreDocs) throws Exception {
        List<Product> listBean = new ArrayList<Product>();
        Product proudct = null;
        for (int i = 0; i < scoreDocs.length; i++) {
            int docId = scoreDocs[i].doc;
            Document doc = searcher.doc(docId);
            proudct = new Product();
            proudct.setId(Integer.parseInt(doc.get("id")));
            proudct.setName(doc.get("name"));
            proudct.setDescr(doc.get("descr"));
        /**
         * 打印对结果score的解析，用于分析排序的依据，通过观察结果和搜索相关信息，得到以下结论
         * 1.结果排序的score依据是fieldWeight的大小，fieldWeight的计算公式为fieldWeight = tf * idf * fieldNorm；
         *  
*  tf表示的是查询条件中，每个查询词（t：term）在本文档（d）中的出现频率。查询关键词出现的频率越高，文档的得分就越高。这个部分的默认计算公式是：
         *  tf(t in d)   =   frequency½ 
             *  
             *  idf表示的是反转文档频率（ Inverse Document Frequency）.这个函数表示的是（t：term）在所有文档中一共在多少个文档中出现过。
             *      因为文档出现的次数越少就越容易定位，所以文档数越少，得分就越高。这个函数的默认计算公式如下：
             *  idf = log(numDocs/(docFreq+1)) + 1
             *  在以下的例子里，可以简单的理解为，在全部的文档里，name这个field里出现查询词“飞剑侠”的文档越多该文档的idf值就越低 
         * bean.id 24 : bean.name 飞剑侠 : bean.descr 飞剑侠！
         * bean.id 27 : bean.name 飞剑侠 : bean.descr 飞剑侠武器！
             * bean.id 25 : bean.name 飞剑侠飞剑侠 : bean.descr 测试修改数据新！
             * bean.id 8 : bean.name 飞剑侠 : bean.descr 钢笔
             * bean.id 22 : bean.name 3钢笔 : bean.descr 飞剑侠飞剑侠！飞剑侠
             * bean.id 23 : bean.name 4钢笔 : bean.descr 飞剑侠飞剑侠！
             * bean.id 26 : bean.name 钢笔 : bean.descr 飞剑侠
             * searchBean.result.size : 7
         * 
         * 以上例子中name和descr的权重相同，ID为8的文档name这个field里命中1个查询词“飞剑侠”，为什么会比ID为26的文档中descr这个field里
             * 命中一个查询词“飞剑侠”排名靠前哪么多？
         * 这就是因为在最终得到的结果里，有4个文档是在name这个field里查到了“飞剑侠”，而有5个文档在descr这个
         * field里查到了“飞剑侠”，所以在name里查到“飞剑侠”的文档8比在descr里查到“飞剑侠”的文档26的idf要高，所以文档8排在了26前面
             * 
         * fieldNorm是事先计算好了的，它等于1/sqrt(wordsNum - 1)。我们可以简单的理解为在tf和idf不变的情况下，文档的包含的内容越少
         * fieldNorm的值就越高，这也是为什么上面例子的结果里id24比27排名靠前。 
         * */
//            System.out.println("**************************************************************************"); 
//            System.out.println(searcher.explain(query, docId));
//            System.out.println("**************************************************************************");
            listBean.add(proudct);
        }
        return listBean;
    }
 
    public static void main(String[] args) throws Exception {
 
        SearchLogic logic = new SearchLogic();
        //设定查询词为“飞剑侠，并且在所有文档的name 和 descr两个field里找包含这个查询词的文档”
        String queryStr = "沉船";
        field = new String[]{"name","descr"};
        try {
            Long startTime = System.currentTimeMillis();
            logic.createIndex();
            List<Product> result = logic.getResult(queryStr);
            int i = 0;
            for (Product bean : result) {
                if (i == 100)
                    break;
                /**
                 * 打印完整的结果
                 * */
                System.out.println("bean.id " + bean.getId() + " : bean.name "
                        + bean.getName() + " : bean.descr " + bean.getDescr());
                i++;
            }
 
            System.out.println("searchBean.result.size : " + result.size());
            Long endTime = System.currentTimeMillis();
            System.out.println("查询所花费的时间为：" + (endTime - startTime) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}