package Bee;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class UserLogin {
    private static final String url=BeeManageSystem.url;
    private static final String user=BeeManageSystem.user;
    private static final String password=BeeManageSystem.password;
    private static final String ROLE_MANAGER = "管理员";
    private static final String ROLE_FARMER = "蜂农";
    public static class LoginFrame extends JFrame{
        private JTextField usernameField;
        private JPasswordField pwdField;
        private JComboBox<String> roleComboBox;
        public LoginFrame(){
            setTitle("用户登录");
            setSize(600,400);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);
            initLoginGUI();
        }
        //初始化登录界面
        private void initLoginGUI(){
            //面板布局
            JPanel panel=new JPanel();
            panel.setLayout(new GridLayout(4,2,10,10));
            panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            //组件添加
            panel.add(new JLabel("登录身份："));
            roleComboBox=new JComboBox<>(new String[]{"管理员","蜂农"});
            panel.add(roleComboBox);
            panel.add(new JLabel("用户名："));
            usernameField=new JTextField();
            panel.add(usernameField);
            panel.add(new JLabel("密码："));
            pwdField=new JPasswordField();
            panel.add(pwdField);
            JButton loginBtn=new JButton("登录");
            panel.add(loginBtn);
            //登录按钮事件
            loginBtn.addActionListener(e -> doLogin());
            add(panel);
        }
        //登录验证逻辑
        private void doLogin(){
            String selectedRoleText=(String)roleComboBox.getSelectedItem();
            String selectedRole=selectedRoleText.equals("管理员") ? BeeManageSystem.ROLE_MANAGER : BeeManageSystem.ROLE_FARMER;
            String username=usernameField.getText().trim();
            String pwd=new String(pwdField.getPassword()).trim();

            if(username.isEmpty() || pwd.isEmpty()){
                JOptionPane.showMessageDialog(this,"用户名/密码不能为空！");
                pwdField.setText("");
                return;
            }
            Connection conn=null;
            PreparedStatement pst=null;
            ResultSet rs=null;
            try{
                conn=DriverManager.getConnection(url,user,password);
                String sql="SELECT role FROM USERS WHERE username=? AND passwords=?";
                pst=conn.prepareStatement(sql);
                pst.setString(1,username);
                pst.setString(2,pwd);
                rs=pst.executeQuery();
                if(rs.next()){
                    String dbRole=rs.getString("role").trim();
                    if(dbRole.equals(selectedRoleText)){
                        JOptionPane.showMessageDialog(this,selectedRoleText+"登录成功！");
                        this.dispose();
                        FunctionsDisplay.initFuncsGUI(dbRole);
                    }else{
                        String realRoleText=dbRole.equals(ROLE_MANAGER)? "管理员":"蜂农";
                        JOptionPane.showMessageDialog(this,"身份选择错误！该账号实际为："+realRoleText);
                        roleComboBox.setSelectedItem(realRoleText);
                        pwdField.setText("");
                    }
                }else{
                    JOptionPane.showMessageDialog(this, "用户名或密码错误！");
                    pwdField.setText("");
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"登录失败："+e.getMessage());
                e.printStackTrace();
            }finally {
                try {
                    if(rs!=null) rs.close();
                    if(pst!=null) pst.close();
                    if(conn!=null) conn.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
