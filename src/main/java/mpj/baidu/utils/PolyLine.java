package mpj.baidu.utils;
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.util.Map;

import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartUtilities;  
import org.jfree.chart.JFreeChart;  
import org.jfree.chart.plot.PlotOrientation;  
import org.jfree.data.category.DefaultCategoryDataset;  
/** 
 * 
 * @author 刘毅 
 * @date 2010-2-25 
 * @ClassName PolyLine.java 
 * @Email liu_yi126@163.com 
 * @param 折线图 
 * @param 
 */  
public class PolyLine {  
 public static void createPolyLine(String []time,Map<String,Double>map1,
		 Map<String,Double>map2){  
      
  DefaultCategoryDataset dataset = new DefaultCategoryDataset();  
  for(int i=0;i<time.length;i++){
	  dataset.addValue(map1.get(time[i]), "社交网络", time[i]);
	  dataset.addValue(map2.get(time[i]), "搜索引擎", time[i]);
  }
  
  //三维折线图 createLineChart3D  
  JFreeChart chart = ChartFactory.createLineChart(  
       "长江沉船事件折线图",                    // 标题  
       "时间",                      // 横坐标  
       "热度",                     // 纵坐标  
       dataset,                    // 数据  
       PlotOrientation.VERTICAL,   // 竖直图表  
       true,                       // 是否显示legend  
       false,                      // 是否显示tooltip  
       false                       // 是否使用url链接  
   );  
  //设置字体  
//  JfreeChinese.setChineseForXY(chart);  
  FileOutputStream fos = null;  
  try {  
      fos = new FileOutputStream("/Users/MPJ/Desktop/poly.png");  
      ChartUtilities.writeChartAsPNG(fos, chart, 400, 300);  
  } catch (FileNotFoundException e) {  
   e.printStackTrace();  
  } catch (IOException e) {  
   e.printStackTrace();  
  } finally {  
      try {  
       if(fos != null){  
        fos.close();  
       }  
   } catch (IOException e) {  
    e.printStackTrace();  
   }  
  }  
 }  
   
 public static void main(String[] args) {  
 }  
} 