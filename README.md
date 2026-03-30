项目名称：智慧养蜂场地理位置与蜂蜜产出系统
相关工具及版本：
    Navicat Premium 16（MySQL8.0）
    IntelliJ IDEA Community Edition 2024.2.6(Java)
项目流程：IDEA与数据库连接->用户登录->功能选择->模块运行->结束
模块：
    用户与权限管理（UserManage）
    蜂场管理（Apiaries）
    蜂群档案（BeeColonies）
    巡检记录（InspectionLogs）
    蜂蜜产出（HoneyBatches）
    蜜源管理（NectorSources）
    销售管理（SalesOrders）
    统计分析（SeasonRecords）
配置文件：
    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.sql.*;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;
    import java.util.Vector;
数据库连接信息及线程启动GUI：
    public static final String url = "jdbc:mysql://localhost:3306/beesystem?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    public static final String user = "root";
    public static final String password = "LeoRao";
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("MySQL连接成功！");
        } catch (SQLException e) {
            System.out.println("连接失败！");
            e.printStackTrace();
            return;
        }
        SwingUtilities.invokeLater(()->{
            UserLogin.LoginFrame app=new UserLogin.LoginFrame();
            app.setVisible(true);
        });
    }
数据库驱动加载：
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL驱动加载成功！");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL驱动加载失败！请检查驱动包是否引入", "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
用户登录账号信息：
管理员（manager）密码：123456
蜂农（farmer1）密码：111111