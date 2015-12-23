package test;

import mpj.baidu.utils.DbToFile;

public class Test {
public static void main(String[] args) {
	String filePath="/Users/MPJ/Desktop/new.txt";
	String sql="select weiboid, rt_text, text from dfzx_weibo";
	DbToFile.toFile(filePath, sql);
}
}
