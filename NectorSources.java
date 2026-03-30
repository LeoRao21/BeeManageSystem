package Bee;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class NectorSources {
    private static final String url = BeeManageSystem.url;
    private static final String user = BeeManageSystem.user;
    private static final String password = BeeManageSystem.password;
    public static class IdentityCheck6 extends JFrame{
        private JTable dataTable;
        private JScrollPane scrollPane;
        private String currentRole;
        public IdentityCheck6(String role){
            //初始化窗口
            this.currentRole=role;
            setTitle("蜜源管理");
            setSize(800,500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);
            InitNSCGUI(role);
            setVisible(true);
        }
        private void InitNSCGUI(String role){
            //创建顶部按钮面板
            JPanel topPanel=new JPanel();
            JButton addBtn=new JButton("录入数据");
            JButton delBtn=new JButton("删除数据");
            JButton updBtn=new JButton("修改数据");
            JButton seeBtn=new JButton("查看数据");
            JButton ordBtn=new JButton("排序数据");
            if(BeeManageSystem.ROLE_MANAGER.equals(role)){
                topPanel.add(addBtn);
                topPanel.add(delBtn);
                topPanel.add(updBtn);
                topPanel.add(seeBtn);
                topPanel.add(ordBtn);
            }else if(BeeManageSystem.ROLE_FARMER.equals(role)){
                topPanel.add(seeBtn);
                topPanel.add(ordBtn);
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
            ordBtn.addActionListener(e -> ordData());
        }
        private Connection getConnection() throws SQLException{
            return DriverManager.getConnection(url,user,password);
        }
        private void addData(){
            JTextField PnameField=new JTextField();
            JTextField PTypeField=new JTextField();
            JTextField start_month=new JTextField();
            JTextField end_month=new JTextField();
            JTextField DistanceField=new JTextField();
            Object[] message={
                    "蜜源植物名称：",PnameField,
                    "蜜源植物种类：",PTypeField,
                    "花期开始月份：",start_month,
                    "花期结束月份：",end_month,
                    "距蜂场距离（米）：",DistanceField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"录入蜜源植物数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String Pname=PnameField.getText().trim();
                String PType=PTypeField.getText().trim();
                String s_month=start_month.getText().trim();
                String e_month=end_month.getText().trim();
                String Distance=DistanceField.getText().trim();
                if(Pname.isEmpty() || PType.isEmpty() || s_month.isEmpty() || e_month.isEmpty() || Distance.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！");
                    return;
                }
                try{
                    int sMonth=Integer.parseInt(s_month);
                    int eMonth=Integer.parseInt(e_month);
                    int distance=Integer.parseInt(Distance);
                    if(sMonth<1 || sMonth>12 || eMonth<1 || eMonth>12 || distance<0){
                        JOptionPane.showMessageDialog(this,"月份必须是1~12月且距离不能为负数！");
                        return;
                    }
                    String sql= """
                             INSERT INTO nectorsources(Pname,PType,start_month,end_month,Distance)
                             VALUES(?,?,?,?,?)""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,Pname);
                        pstmt.setString(2,PType);
                        pstmt.setInt(3,sMonth);
                        pstmt.setInt(4,eMonth);
                        pstmt.setInt(5,distance);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this,"数据录入成功！");
                        seeData();
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"月份和距离必须是整数！","输入格式错误",JOptionPane.ERROR_MESSAGE);
                }
                catch(SQLException e){
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
            int confirm=JOptionPane.showConfirmDialog(this,"确定要删除蜜源植物ID为"+idstr+"的记录吗？","确认删除",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                try {
                    String sql="""
                           DELETE FROM nectorsources
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
                    JOptionPane.showMessageDialog(this,"蜜源植物id格式错误："+e.getMessage(),"输入错误",JOptionPane.ERROR_MESSAGE);
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
            String oldPname=dataTable.getValueAt(selectedRow,1).toString();
            String oldPType=dataTable.getValueAt(selectedRow,2).toString();
            String old_s_month=dataTable.getValueAt(selectedRow,3).toString();
            String old_e_month=dataTable.getValueAt(selectedRow,4).toString();
            String oldDistance=dataTable.getValueAt(selectedRow,5).toString();
            JTextField PnField=new JTextField(oldPname,15);
            JTextField PtField=new JTextField(oldPType,15);
            JTextField SmField=new JTextField(old_s_month,15);
            JTextField EmField=new JTextField(old_e_month,15);
            JTextField DisField=new JTextField(oldDistance,15);
            Object[] message={
                    "蜜源植物ID（不可修改）：",new JLabel(id),
                    "蜜源植物名称：",PnField,
                    "蜜源植物种类：",PtField,
                    "花期开始月份：",SmField,
                    "花期结束月份：",EmField,
                    "距蜂场距离（米）：",DisField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"修改蜜源植物数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String newPname=PnField.getText().trim();
                String newPtype=PtField.getText().trim();
                String new_s_month=SmField.getText().trim();
                String new_e_month=EmField.getText().trim();
                String newDist=DisField.getText().trim();
                if(newPname.isEmpty() || newPtype.isEmpty() || new_s_month.isEmpty() || new_e_month.isEmpty() || newDist.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！","错误",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try{
                    int sMonth=Integer.parseInt(new_s_month);
                    int eMonth=Integer.parseInt(new_e_month);
                    int distance=Integer.parseInt(newDist);
                    if(sMonth<1 || sMonth>12 || eMonth<1 || eMonth>12 || distance<0){
                        JOptionPane.showMessageDialog(this,"月份必须是1~12月且距离不能为负数！");
                        return;
                    }
                    String sql= """
                            UPDATE nectorsources
                            SET Pname=?,PType=?,start_month=?,end_month=?,Distance=?
                            WHERE id=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,newPname);
                        pstmt.setString(2,newPtype);
                        pstmt.setInt(3,sMonth);
                        pstmt.setInt(4,eMonth);
                        pstmt.setInt(5,distance);
                        pstmt.setInt(6,Integer.parseInt(id));
                        int rows=pstmt.executeUpdate();
                        if(rows>0){
                            JOptionPane.showMessageDialog(this,"数据修改成功！");
                            seeData();
                        }else{
                            JOptionPane.showMessageDialog(this,"修改失败：记录不存在！");
                        }
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"蜜源植物id格式错误：","输入错误",JOptionPane.ERROR_MESSAGE);
                }
                catch(SQLException e){
                    JOptionPane.showMessageDialog(this,"修改失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private void seeData(){
            try{
                String sql= """
                        SELECT id,Pname,PType,start_month,end_month,Distance
                        FROM nectorsources""";
                updateTableData(sql);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"查询失败"+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void ordData() {
            if (dataTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "表中暂无数据，无法排序！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JDialog sortDialog = new JDialog(this, "请选择排序依据", true);
            sortDialog.setSize(300, 150);
            sortDialog.setLocationRelativeTo(this);
            sortDialog.setResizable(false);
            JPanel btnPanel = new JPanel();
            JButton DisBtn = new JButton("距蜂场距离（米）");
            btnPanel.add(DisBtn);
            JButton TimeBtn = new JButton("花期");
            btnPanel.add(TimeBtn);
            JButton CancelBtn=new JButton("取消");
            btnPanel.add(CancelBtn);
            sortDialog.add(btnPanel);
            DisBtn.addActionListener(e -> {
                DisOrder();
                sortDialog.dispose();
            });
            TimeBtn.addActionListener(e -> {
                TimeOrder();
                sortDialog.dispose();
            });
            CancelBtn.addActionListener(e -> sortDialog.dispose());
            sortDialog.setVisible(true);
        }
        private void DisOrder(){
            try{
                String sql= """
                        SELECT id,Pname,PType,start_month,end_month,Distance
                        FROM nectorsources
                        ORDER BY Distance ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按距离升序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"距离排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void TimeOrder(){
            try{
                String sql= """
                        SELECT id,Pname,PType,start_month,end_month,Distance
                        FROM nectorsources
                        ORDER BY
                            CASE
                                WHEN end_month>=start_month THEN (end_month-start_month)
                                ELSE (12+end_month-start_month)
                            END DESC,
                            start_month ASC
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按花期时长降序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"花期时长排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void updateTableData(String sql) throws SQLException{
            try(Connection conn=getConnection();
                Statement stmt=conn.createStatement();
                ResultSet rs=stmt.executeQuery(sql)){
                Vector<String> columnNames=new Vector<>();
                columnNames.add("ID");
                columnNames.add("蜜源植物名称");
                columnNames.add("蜜源植物种类");
                columnNames.add("花期开始月份");
                columnNames.add("花期结束月份");
                columnNames.add("距蜂场距离（米）");
                Vector<Vector<Object>> data=new Vector<>();
                while(rs.next()){
                    Vector<Object> row=new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("Pname"));
                    row.add(rs.getString("PType"));
                    row.add(rs.getInt("start_month"));
                    row.add(rs.getInt("end_month"));
                    row.add(rs.getInt("Distance"));
                    data.add(row);
                }
                dataTable.setModel(new javax.swing.table.DefaultTableModel(data,columnNames));
                dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        }
    }
}
