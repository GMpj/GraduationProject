package mpj.baidu.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import love.cq.util.IOUtil;

public class GetStopWord {

	public static Set<String> getStopWord(){
		Set<String> set=new HashSet<String>();
		try {
			BufferedReader reader = IOUtil.getReader(
					"stopword.txt", "UTF-8");
			String temp=null;
			while (( temp = reader.readLine()) != null) {
				temp = temp.trim();
				set.add(temp);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return set;
	}
	public static void main(String[] args) {
		Set<String> set=getStopWord();
		System.out.println(set);
	}
}
