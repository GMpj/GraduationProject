package test;

import java.util.List;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;

public class Analysis {
public static void main(String[] args) {
	  List<Term> parse = ToAnalysis.parse("今天SONY宣布，著名的、会跳舞的、\"滚蛋\"MP3播放器Rolly Speaker拥有了一项新功能，现在可以通过蓝牙设备，比如你的手机，来远程控制你的\"大耳朵\"的动作，并且动作可以自定义。这个新功能给你的朋友秀一秀，真的是相当的拉风啦。新款的Rolly内建2GB内存，两个1.2瓦音箱，USB2.0接口。支持的音乐格式为MP3 / ATRAC / AAC / Linear PCM，顺便说一句，Apple你咋不能跟人家SONY学一学，听听用户的意见呢。 引用来源 ");
	    System.out.println(parse);
	    
//	    System.out.println(parse.get(1).getName());
//	 // 增加新词,中间按照'\t'隔开
//        UserDefineLibrary.insertWord("战士们", "user", 1000);
//        List<Term> terms = ToAnalysis.parse("让战士们过一个欢乐祥和的新春佳节。");
//        System.out.println("增加新词例子:" + terms);
//        // 删除词语,只能删除.用户自定义的词典.
//        UserDefineLibrary.removeWord("ansj中文分词");
//        terms = ToAnalysis.parse("我觉得ansj中文分词是一个不错的系统!我是王婆!");
//        System.out.println("删除用户自定义词典例子:" + terms);
}
}
