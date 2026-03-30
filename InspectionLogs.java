package Bee;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class InspectionLogs {
    private static final String url = BeeManageSystem.url;
    private static final String user = BeeManageSystem.user;
    private static final String password = BeeManageSystem.password;
    public static class IdentityCheck4 extends JFrame{
        private JTable dataTable;
        private JScrollPane scrollPane;
        public IdentityCheck4(){
            //初始化窗口
            setTitle("巡检记录");
            setSize(800,500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);
            InitIPLGUI();
            setVisible(true);
        }
        private void InitIPLGUI(){
            //创建顶部按钮面板
            JPanel topPanel=new JPanel();
            JButton addBtn=new JButton("录入数据");
            topPanel.add(addBtn);
            JButton delBtn=new JButton("删除数据");
            topPanel.add(delBtn);
            JButton updBtn=new JButton("修改数据");
            topPanel.add(updBtn);
            JButton seeBtn=new JButton("查看数据");
            topPanel.add(seeBtn);
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
            JTextField BTypeField=new JTextField();
            JTextField PADField=new JTextField();
            JTextField FeedField=new JTextField();
            Object[] message={
                    "蜂种：",BTypeField,
                    "病虫害情况：",PADField,
                    "饲喂状态：",FeedField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"录入蜂场数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String BType=BTypeField.getText().trim();
                String Pad=PADField.getText().trim();
                String Feed=FeedField.getText().trim();
                if(BType.isEmpty() || Pad.isEmpty() || Feed.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！");
                    return;
                }
                try{
                    String sql= """
                             INSERT INTO inspectionlogs(BeeType,PAD,Feed)
                             VALUES(?,?,?)""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,BType);
                        pstmt.setString(2,Pad);
                        pstmt.setString(3,Feed);
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
            String idstr=dataTable.getValueAt(selectedRow,0).toString();
            int confirm=JOptionPane.showConfirmDialog(this,"确定要删除巡检ID为"+idstr+"的记录吗？","确认删除",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                try {
                    String sql="""
                           DELETE FROM inspectionlogs
                           WHERE id=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setInt(1,Integer.parseInt(idstr));
                        int rows=pstmt.executeUpdate();
                        if(rows>0){
                            JOptionPane.showMessageDialog(this,"删除成功！");
                            seeData();
                        }else{
                            JOptionPane.showMessageDialog(this,"删除失败：记录不存在！");
                        }
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"巡检id格式错误："+e.getMessage(),"输入错误",JOptionPane.ERROR_MESSAGE);
                } catch(SQLException e){
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
            String id=dataTable.getValueAt(selectedRow,0).toString();
            String oldBType=dataTable.getValueAt(selectedRow,1).toString();
            String oldPad=dataTable.getValueAt(selectedRow,2).toString();
            String oldFeed=dataTable.getValueAt(selectedRow,3).toString();

            JTextField BTypeField=new JTextField(oldBType);
            JTextField PADField=new JTextField(oldPad);
            JTextField FeedField=new JTextField(oldFeed);
            Object[] message={
                    "巡检ID（不可修改）：",new JLabel(id),
                    "蜂种：",BTypeField,
                    "病虫害情况：",PADField,
                    "饲喂状态：",FeedField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"修改巡检数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String newBType=BTypeField.getText().trim();
                String newPad=PADField.getText().trim();
                String newFeed=FeedField.getText().trim();
                if(newBType.isEmpty() || newPad.isEmpty() || newFeed.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！","错误",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try{
                    String sql= """
                            UPDATE inspectionlogs
                            SET BeeType=?,PAD=?,Feed=?
                            WHERE id=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,newBType);
                        pstmt.setString(2,newPad);
                        pstmt.setString(3,newFeed);
                        pstmt.setInt(4,Integer.parseInt(id));
                        int rows=pstmt.executeUpdate();
                        if(rows>0){
                            JOptionPane.showMessageDialog(this,"数据修改成功！");
                            seeData();
                        }else{
                            JOptionPane.showMessageDialog(this,"修改失败：记录不存在！");
                        }
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"巡检id格式错误：","输入错误",JOptionPane.ERROR_MESSAGE);
                }
                catch(SQLException e){
                    JOptionPane.showMessageDialog(this,"修改失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private void seeData(){
            try{
                String sql= """
                        SELECT id,BeeType,PAD,Feed
                        FROM inspectionlogs""";
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
                columnNames.add("巡检ID");
                columnNames.add("蜂种");
                columnNames.add("病虫害情况");
                columnNames.add("饲喂状态");
                Vector<Vector<Object>> data=new Vector<>();
                while(rs.next()){
                    Vector<Object> row=new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("BeeType"));
                    row.add(rs.getString("PAD"));
                    row.add(rs.getString("Feed"));
                    data.add(row);
                }
                dataTable.setModel(new javax.swing.table.DefaultTableModel(data,columnNames));
                dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        }
    }
}