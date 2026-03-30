package Bee;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class BeeColonies {
    private static final String url = BeeManageSystem.url;
    private static final String user = BeeManageSystem.user;
    private static final String password = BeeManageSystem.password;
    public static class IdentityCheck3 extends JFrame{
        private JTable dataTable;
        private JScrollPane scrollPane;
        private String currentRole;
        public IdentityCheck3(String role){
            //初始化窗口
            this.currentRole=role;
            setTitle("蜂群档案");
            setSize(800,500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);
            InitBCLGUI(role);
        }
        private void InitBCLGUI(String role){
            //创建顶部按钮面板
            JPanel topPanel=new JPanel();
            JButton addBtn=new JButton("录入数据");
            JButton delBtn=new JButton("删除数据");
            JButton updBtn=new JButton("修改数据");
            JButton seeBtn=new JButton("查看数据");
            if(BeeManageSystem.ROLE_MANAGER.equals(role)){
                topPanel.add(addBtn);
                topPanel.add(delBtn);
                topPanel.add(updBtn);
                topPanel.add(seeBtn);
            }else if(BeeManageSystem.ROLE_FARMER.equals(role)){
                topPanel.add(seeBtn);
            }
            //创建表格面板
            JPanel tablePanel=new JPanel(new BorderLayout());
            dataTable=new JTable();
            scrollPane=new JScrollPane(dataTable);
            tablePanel.add(scrollPane,BorderLayout.CENTER);
            //组装窗口布局
            setLayout(new BorderLayout());
            add(topPanel,BorderLayout.NORTH);
            add(tablePanel,BorderLayout.CENTER);
            //绑定事件按钮
            addBtn.addActionListener(e -> addData());
            delBtn.addActionListener(e -> delData());
            updBtn.addActionListener(e -> updData());
            seeBtn.addActionListener(e -> seeData());
        }
        private Connection getConnection() throws SQLException{
            return DriverManager.getConnection(url,user,password);
        }
        private void addData(){
            JTextField BnoField=new JTextField();
            JTextField BtypeField=new JTextField();
            JTextField BKingField=new JTextField();
            JTextField FlockField=new JTextField();
            Object[] message={
                    "蜂箱编号：",BnoField,
                    "蜂种：",BtypeField,
                    "蜂王状态：",BKingField,
                    "群势评估：",FlockField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"录入蜂场数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String Bno=BnoField.getText().trim();
                String Btype=BtypeField.getText().trim();
                String BKing=BKingField.getText().trim();
                String Flock=FlockField.getText().trim();
                if(Bno.isEmpty() || Btype.isEmpty() || BKing.isEmpty() || Flock.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！");
                    return;
                }
                try{
                    String sql= """
                             INSERT INTO beecolonies(Bno,BType,BKing,FlockAssess)
                             VALUES(?,?,?,?)""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,Bno);
                        pstmt.setString(2,Btype);
                        pstmt.setString(3,BKing);
                        pstmt.setString(4,Flock);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this,"数据录入成功！");
                        seeData();
                    }
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(this,"录入失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private void delData(){
            if(dataTable.getRowCount()==0){
                JOptionPane.showMessageDialog(this,"表中暂无数据，无法删除！","提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            int selectedRow=dataTable.getSelectedRow();
            if(selectedRow==-1){
                JOptionPane.showMessageDialog(this,"请先选中要删除的行！","提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String Bno=dataTable.getValueAt(selectedRow,0).toString();
            int confirm=JOptionPane.showConfirmDialog(this,"确定要删除蜂箱编号为"+Bno+"的记录吗？","确认删除",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                try {
                    String sql="""
                           DELETE FROM beecolonies
                           WHERE Bno=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,Bno);
                        int rows=pstmt.executeUpdate();
                        if(rows>0){
                            JOptionPane.showMessageDialog(this,"删除成功！");
                            seeData();
                        }else{
                            JOptionPane.showMessageDialog(this,"删除失败：记录不存在！");
                        }
                    }
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(this,"删除失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private void updData(){
            if(dataTable.getRowCount()==0){
                JOptionPane.showMessageDialog(this,"表中暂无数据，无法修改！","提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            int selectedRow=dataTable.getSelectedRow();
            if(selectedRow==-1){
                JOptionPane.showMessageDialog(this,"请先选中要修改的行！","提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String oldBno=dataTable.getValueAt(selectedRow,0).toString();
            String oldBtype=dataTable.getValueAt(selectedRow,1).toString();
            String oldBKing=dataTable.getValueAt(selectedRow,2).toString();
            String oldFlock=dataTable.getValueAt(selectedRow,3).toString();

            JTextField BnoField=new JTextField(oldBno);
            JTextField BtypeField=new JTextField(oldBtype);
            JTextField BKingField=new JTextField(oldBKing);
            JTextField FlockField=new JTextField(oldFlock);
            Object[] message={
                    "蜂箱编号：",BnoField,
                    "蜂种：",BtypeField,
                    "蜂王状态：",BKingField,
                    "群势评估：",FlockField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"修改蜂群数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String newBno=BnoField.getText().trim();
                String newBtype=BtypeField.getText().trim();
                String newBKing=BKingField.getText().trim();
                String newFlock=FlockField.getText().trim();
                if(newBno.isEmpty() || newBtype.isEmpty() || newBKing.isEmpty() || newFlock.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！","错误",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try{
                    String sql= """
                            UPDATE beecolonies
                            SET Bno=?,BType=?,BKing=?,FlockAssess=?
                            WHERE Bno=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,newBno);
                        pstmt.setString(2,newBtype);
                        pstmt.setString(3,newBKing);
                        pstmt.setString(4,newFlock);
                        pstmt.setString(5,oldBno);
                        int rows=pstmt.executeUpdate();
                        if(rows>0){
                            JOptionPane.showMessageDialog(this,"数据修改成功！");
                            seeData();
                        }else{
                            JOptionPane.showMessageDialog(this,"修改失败：记录不存在！");
                        }
                    }
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(this,"修改失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private void seeData(){
            try{
                String sql= """
                        SELECT Bno,BType,BKing,FlockAssess
                        FROM beecolonies""";
                updateTableData(sql);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"查询失败"+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void updateTableData(String sql) throws SQLException{
            try(Connection conn=getConnection();
                Statement stmt=conn.createStatement();
                ResultSet rs=stmt.executeQuery(sql)){
                Vector<String> columnNames=new Vector<>();
                columnNames.add("蜂箱编号");
                columnNames.add("蜂种");
                columnNames.add("蜂王状态");
                columnNames.add("群势评估");
                Vector<Vector<Object>> data=new Vector<>();
                while(rs.next()){
                    Vector<Object> row=new Vector<>();
                    row.add(rs.getString("Bno"));
                    row.add(rs.getString("BType"));
                    row.add(rs.getString("BKing"));
                    row.add(rs.getString("FlockAssess"));
                    data.add(row);
                }
                dataTable.setModel(new javax.swing.table.DefaultTableModel(data,columnNames));
                dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        }
    }
}
