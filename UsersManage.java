package Bee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class UsersManage {
    private static final String url = BeeManageSystem.url;
    private static final String user = BeeManageSystem.user;
    private static final String password = BeeManageSystem.password;
    //主GUI程序
    public static class IdentityCheck1 extends JFrame {
        //界面组件
        private JTextField nameField;
        private JPasswordField passField;
        private JTable userTable;
        private DefaultTableModel tableModel;
        public IdentityCheck1() {
            //初始化窗口
            setTitle("用户管理");
            setSize(800, 500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);
            //构建界面布局
            initGUI();
            //初始化时加载数据到表格
            loadUserData();
        }
        //初始化GUI组件
        private void initGUI() {
            JPanel topPanel = new JPanel();
            topPanel.add(new JLabel("用户名："));
            nameField = new JTextField(10);
            topPanel.add(nameField);
            topPanel.add(new JLabel("密码："));
            passField = new JPasswordField(20);
            topPanel.add(passField);

            JButton addBtn = new JButton("新增用户");
            topPanel.add(addBtn);
            JButton delBtn = new JButton("删除用户");
            topPanel.add(delBtn);
            JButton updBtn = new JButton("修改用户");
            topPanel.add(updBtn);

            tableModel = new DefaultTableModel(new String[]{"ID", "用户名", "密码"}, 0);
            userTable = new JTable(tableModel);
            JScrollPane sp=new JScrollPane(userTable);
            add(topPanel, BorderLayout.NORTH);
            add(sp, BorderLayout.CENTER);
            //新增用户按钮事件
            addBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addUser();
                }
            });
            delBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteUser();
                }
            });
            updBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateUser();
                }
            });
        }
        //增加用户
        private void addUser() {
            String username=nameField.getText().trim();
            String pwd=new String(passField.getPassword()).trim();
            if(username.isEmpty() || pwd.isEmpty()){
                JOptionPane.showMessageDialog(this,"用户名和密码不能为空！");
                return;
            }
            String sql="INSERT INTO USERS(username,passwords) VALUES(?,?)";
            Connection conn=null;
            try{
                conn=DriverManager.getConnection(url,user,password);
                PreparedStatement pst=conn.prepareStatement(sql);
                pst.setString(1,username);
                pst.setString(2,pwd);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this,"新增成功！");
                clearInput();
                loadUserData();
            }catch(SQLIntegrityConstraintViolationException e){
                JOptionPane.showMessageDialog(this,"用户名已存在！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"新增失败："+e.getMessage());
            }finally {
                BeeManageSystem.closeConnection(conn);
            }
        }
        //删除用户
        private void deleteUser(){
            String username=nameField.getText().trim();
            if(username.isEmpty()){
                JOptionPane.showMessageDialog(this,"请输入要删除的用户名！");
                return;
            }
            int confirm=JOptionPane.showConfirmDialog(this,"确定删除用户："+username+"？");
            if(confirm!=JOptionPane.YES_OPTION){
                return;
            }
            String sql="DELETE FROM USERS WHERE username=?";
            Connection conn=null;
            try{
                conn=DriverManager.getConnection(url,user,password);
                PreparedStatement pst=conn.prepareStatement(sql);
                pst.setString(1,username);
                int rows=pst.executeUpdate();
                if(rows>0){
                    JOptionPane.showMessageDialog(this,"删除成功！");
                    clearInput();
                    loadUserData();
                }else{
                    JOptionPane.showMessageDialog(this,"该用户不存在！");
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"删除失败："+e.getMessage());
            }finally {
                BeeManageSystem.closeConnection(conn);
            }
        }
        //修改用户
        private void updateUser(){
            String username=nameField.getText().trim();
            String newPWD=new String(passField.getPassword()).trim();
            if(username.isEmpty() || newPWD.isEmpty()){
                JOptionPane.showMessageDialog(this,"请输入用户名或新密码！");
                return;
            }
            String sql="UPDATE USERS SET passwords=? WHERE username=?";
            Connection conn=null;
            try{
                conn=DriverManager.getConnection(url,user,password);
                PreparedStatement pst=conn.prepareStatement(sql);
                pst.setString(1,newPWD);
                pst.setString(2,username);
                int rows=pst.executeUpdate();
                if(rows>0){
                    JOptionPane.showMessageDialog(this,"修改成功！");
                    clearInput();
                    loadUserData();
                }else{
                    JOptionPane.showMessageDialog(this,"该用户不存在！");
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"修改失败："+e.getMessage());
            }finally {
                BeeManageSystem.closeConnection(conn);
            }
        }
        //清空输入框
        private void clearInput(){
            nameField.setText("");
            passField.setText("");
        }
        //加载用户数据
        private void loadUserData(){
            tableModel.setRowCount(0);
            String sql="SELECT * FROM USERS";
            Connection conn=null;
            try{
                conn=DriverManager.getConnection(url,user,password);
                Statement st=conn.createStatement();
                ResultSet rs=st.executeQuery(sql);
                while(rs.next()){
                    int id=rs.getInt("id");
                    String name=rs.getString("username");
                    String pwd=rs.getString("passwords");
                    tableModel.addRow(new Object[]{id,name,pwd});
                }
            }catch(SQLException e){
                e.printStackTrace();
            }finally {
                BeeManageSystem.closeConnection(conn);
            }
        }
    }
}