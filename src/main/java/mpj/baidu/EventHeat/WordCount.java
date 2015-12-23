package mpj.baidu.EventHeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import love.cq.util.IOUtil;
import mpj.baidu.dao.Word;
import mpj.baidu.dao.WordDao;
import mpj.baidu.utils.GetStopWord;

public class WordCount {
	public static final String TAG_START_CONTENT = "<content>";
	public static final String TAG_END_CONTENT = "</content>";
	private WordDao wd = WordDao.getInstance();
	
	public void saveWordCount(){
		BufferedReader reader = null;
		PrintWriter pw = null;
		String temp = null;
		Set<String> stopwords=GetStopWord.getStopWord();
		Map<String,Integer> map=new HashMap<String,Integer>();
		
		try {
			reader = IOUtil.getReader(
					"/Users/MPJ/Applications/w2v/trunk/corpus.txt", "UTF-8");
			
			int line=1;
			pw = new PrintWriter("/Users/MPJ/resultbig.txt");
			while ((temp = reader.readLine()) != null) {
				temp = temp.trim();
				if (temp.startsWith(TAG_START_CONTENT)) {
					int end = temp.indexOf(TAG_END_CONTENT);
					String content = temp.substring(TAG_START_CONTENT.length(),
							end);
					// System.out.println(content);
					if (content.length() > 0) {
						List<Term> result = ToAnalysis.parse(content);
						for (Term term : result) {

							String item = term.getName().trim();
							if (!stopwords.contains(item)) {

								if (item.length() > 0) {
									pw.print(item.trim() + " ");
									
									putmap(item, map);
								}
							}
						}
						pw.println();
					}
					saveOrUpdate(map, line);
					line++;
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != pw) {
				pw.close();
			}
		}
	}
	
	
	public void putmap(String word,Map<String,Integer>map){
		if(null==map.get(word)){
			map.put(word, 1);
		}
		else{
			int num=map.get(word);
			num=num+1;
			map.put(word, num);
		}
	}
	
	public void saveOrUpdate(Map<String, Integer> map, int line) {

		if(null==map){
			return;
		}
		//取出map中的索引词和分数
		for (String key : map.keySet()) {

			//查询数据库中是否有该索引项
			Word word = wd.find(key);
			LinkedHashMap<String, Integer> index;
			//没有的话插入
			if (null == word) {
				Word temp = new Word();
				index = new LinkedHashMap<String, Integer>();
				int num = map.get(key);
				index.put(Integer.toString(line), num);
				temp.setKey(key);
				temp.setValue(index);
				wd.save(temp);
				index.clear();//及时对集合进行清空
			} //有的话更新
			else {
				index = word.getValue();
				int num = map.get(key);
				
				index.put(Integer.toString(line), num);
				Word temp = new Word();
				temp.setKey(key);
				temp.setValue(index);
				wd.update(temp);
				word.getValue().clear();
				index.clear();
			}
		}
		map.clear();//清空集合
	}
	
	public static void main(String[] args) {
		WordCount wc=new WordCount();
		wc.saveWordCount();
	}
}
