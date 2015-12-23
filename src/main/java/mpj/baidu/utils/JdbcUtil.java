package mpj.baidu.utils;
 
import java.sql.Connection;   
import java.sql.DriverManager;     
import java.sql.SQLException;  
/**    
* JdbcUtil.java  
* @version 1.0  
* @createTime JDBC获取Connection工具类  
*/  
public class JdbcUtil {   
    private static Connection conn = null;   
    //设置数据库连接地址
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/baidu?autoReconnect=true&characterEncoding=utf8";   
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";   
    //设置你的数据库用户名
    private static final String USER_NAME = "root";  
    //设置你的数据库密码
    private static final String PASSWORD = "";
     
    public static Connection getConnection() {   
        try {   
            Class.forName(JDBC_DRIVER);   
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);   
        } catch (ClassNotFoundException e) {   
            e.printStackTrace();   
        } catch (SQLException e) {   
            e.printStackTrace();   
        }   
        return conn;   
    }
}