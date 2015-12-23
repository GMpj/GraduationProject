package mpj.baidu.dao;

import java.util.LinkedHashMap;
import java.util.Map;

public class Word {

	private String key;
	private LinkedHashMap<String,Integer> value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public LinkedHashMap<String, Integer> getValue() {
		return value;
	}
	public void setValue(LinkedHashMap<String, Integer> value) {
		this.value = value;
	}
	
}
