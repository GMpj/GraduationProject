package mpj.baidu.EventHeat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpj.baidu.dao.Word;
import mpj.baidu.dao.WordDao;

public class RecordRelation {

	WordDao  wd=WordDao.getInstance();
	
	public Map<String,Double> getRelation(List<String> keywords){
		Map<String,Double> map=new HashMap<String,Double>();
		Map<String,Integer> temp;
		for(String keyword:keywords){
			Word word=wd.find(keyword);
			
			if(null!=word){
				temp=word.getValue();
				setScore(map, temp);
				
			}
			
		}
		return map;
	}
	
	public Map<String,Double> setScore(Map<String,Double> map,Map<String,Integer> temp){
		for(String key:temp.keySet()){
			int num=temp.get(key);
			
			if(null==map.get(key)){
				map.put(key, 0.1);
			}
			else{
				double score=map.get(key);
				score=score+0.2;
				
			}
		}
		return map;
	}
}
