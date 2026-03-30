package Bee;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class BeeManageSystem {
    public static final String url = "jdbc:mysql://localhost:3306/beesystem?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    public static final String user = "root";
    public static final String password = "LeoRao";
    public static final String ROLE_MANAGER="管理员";
    public static final String ROLE_FARMER="蜂农";
    public static final String FUNC_1="func1";
    public static final String FUNC_2="func2";
    public static final String FUNC_3="func3";
    public static final String FUNC_4="func4";
    public static final String FUNC_5="func5";
    public static final String FUNC_6="func6";
    public static final String FUNC_7="func7";
    public static final String FUNC_8="func8";
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL驱动加载成功！");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL驱动加载失败！请检查驱动包是否引入", "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1); // 驱动加载失败直接退出程序
        }
    }
    public static void main(String[] args) {
        //测试数据库连接
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("MySQL连接成功！");
        } catch (SQLException e) {
            System.out.println("连接失败！");
            e.printStackTrace();
            return;
        }
        //在EDT线程中启动Swing GUI
        SwingUtilities.invokeLater(()->{
            UserLogin.LoginFrame app=new UserLogin.LoginFrame();
            app.setVisible(true);
        });
    }
    public static void closeConnection(Connection conn){
        if(conn!=null){
            try{
                conn.close();
            }catch(SQLException e){
                System.out.println("关闭连接失败：" + e.getMessage());
            }
        }
    }
}
