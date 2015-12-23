package mpj.baidu.EventHeat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import mpj.baidu.bean.Result;
import mpj.baidu.utils.JdbcUtil;

public class EventHeat {
	private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;
	
	public double setHeartFromSQL(List<Result> list,String creat_time,String table) throws Exception{
		double heart;
		conn = JdbcUtil.getConnection();
        if (conn == null) {
            throw new Exception("数据库连接失败！");
        }
        String sql="select count(create_time) as num from `"+table+"` "
        		+ "where create_time like '"+creat_time+"%'";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        rs.next();
        int num=rs.getInt("num");
        if(num==0)
        	return 0;
        heart=list.size()/(double)num;
		return  heart;
	}
}
