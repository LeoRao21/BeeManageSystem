package Bee;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class Apiaries {
    private static final String url = BeeManageSystem.url;
    private static final String user = BeeManageSystem.user;
    private static final String password = BeeManageSystem.password;
    public static class IdentityCheck2 extends JFrame{
        private JTable dataTable;
        private JScrollPane scrollPane;
        private String currentRole;
        public IdentityCheck2(String role){
            //初始化窗口
            this.currentRole=role;
            setTitle("蜂场管理");
            setSize(800,500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);
            InitAPRGUI(role);
        }
        private void InitAPRGUI(String role){
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
            JTextField nameField=new JTextField();
            JTextField latiField=new JTextField();
            JTextField longField=new JTextField();
            JTextField altiField=new JTextField();
            Object[] message={
                    "蜂场名称：",nameField,
                    "蜂场位置（纬度）：",latiField,
                    "蜂场位置（经度）：",longField,
                    "海拔：",altiField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"录入蜂场数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String name=nameField.getText().trim();
                String latitude=latiField.getText().trim();
                String longitude=longField.getText().trim();
                String altitude=altiField.getText().trim();
                if(name.isEmpty() || longitude.isEmpty() || latitude.isEmpty() || altitude.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！");
                    return;
                }
                try{
                    double lat=Double.parseDouble(latitude);
                    double lon=Double.parseDouble(longitude);
                    int alt=Integer.parseInt(altitude);
                    String sql= """
                             INSERT INTO apiaries(Fname,Latitude,Longitude,Altitude)
                             VALUES(?,?,?,?)""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,name);
                        pstmt.setDouble(2,lat);
                        pstmt.setDouble(3,lon);
                        pstmt.setInt(4,alt);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this,"数据录入成功！");
                        seeData();
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"经度、纬度和海拔必须是数字！","错误",JOptionPane.ERROR_MESSAGE);
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
            String idStr=dataTable.getValueAt(selectedRow,0).toString();
            int confirm=JOptionPane.showConfirmDialog(this,"确定要删除ID为"+idStr+"的记录吗？","确认删除",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                try {
                    String sql="""
                           DELETE FROM apiaries
                           WHERE id=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setInt(1,Integer.parseInt(idStr));
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
            String id=dataTable.getValueAt(selectedRow,0).toString();
            String oldName=dataTable.getValueAt(selectedRow,1).toString();
            String oldLati=dataTable.getValueAt(selectedRow,2).toString();
            String oldLong=dataTable.getValueAt(selectedRow,3).toString();
            String oldAlti=dataTable.getValueAt(selectedRow,4).toString();

            JTextField nameField=new JTextField(oldName);
            JTextField latField=new JTextField(oldLati);
            JTextField lonField=new JTextField(oldLong);
            JTextField altField=new JTextField(oldAlti);
            Object[] message={
                  "蜂场ID（不可修改）：",new JLabel(id),
                  "蜂场名称：",nameField,
                  "蜂场位置（纬度）：",latField,
                  "蜂场位置（经度）：",lonField,
                  "海拔：",altField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"修改蜂场数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String newName=nameField.getText().trim();
                String newLong=lonField.getText().trim();
                String newLati=latField.getText().trim();
                String newAlti=altField.getText().trim();
                if(newName.isEmpty() || newLong.isEmpty() || newLati.isEmpty() || newAlti.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！","错误",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try{
                    double lat=Double.parseDouble(newLati);
                    double lon=Double.parseDouble(newLong);
                    int alt=Integer.parseInt(newAlti);
                    String sql= """
                            UPDATE apiaries
                            SET Fname=?,Latitude=?,Longitude=?,Altitude=?
                            WHERE id=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,newName);
                        pstmt.setDouble(2,lat);
                        pstmt.setDouble(3,lon);
                        pstmt.setInt(4,alt);
                        pstmt.setInt(5,Integer.parseInt(id));
                        int rows=pstmt.executeUpdate();
                        if(rows>0){
                            JOptionPane.showMessageDialog(this,"数据修改成功！");
                            seeData();
                        }else{
                            JOptionPane.showMessageDialog(this,"修改失败：记录不存在！");
                        }
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"经度、纬度和海拔必须是数字！","错误",JOptionPane.ERROR_MESSAGE);
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(this,"修改失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private void seeData(){
            try{
                String sql= """
                        SELECT id,Fname,Latitude,Longitude,Altitude
                        FROM apiaries""";
                updateTableData(sql);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"查询失败"+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void ordData(){
            if(dataTable.getRowCount()==0){
                JOptionPane.showMessageDialog(this,"表中暂无数据，无法排序！","提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
            JDialog sortDialog=new JDialog(this,"请选择排序依据",true);
            sortDialog.setSize(300,150);
            sortDialog.setLocationRelativeTo(this);
            sortDialog.setResizable(false);
            JPanel btnPanel=new JPanel();
            JButton latiBtn=new JButton("纬度");
            btnPanel.add(latiBtn);
            JButton longBtn=new JButton("经度");
            btnPanel.add(longBtn);
            JButton altiBtn=new JButton("海拔");
            btnPanel.add(altiBtn);
            sortDialog.add(btnPanel);
            latiBtn.addActionListener(e -> {
                latiOrder();
                sortDialog.dispose();
            });
            longBtn.addActionListener(e -> {
                longOrder();
                sortDialog.dispose();
            });
            altiBtn.addActionListener(e -> {
                altiOrder();
                sortDialog.dispose();
            });
            sortDialog.setVisible(true);
        }
        private void latiOrder(){
            try{
                String sql= """
                        SELECT id,Fname,Latitude,Longitude,Altitude
                        FROM apiaries
                        ORDER BY Latitude ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按纬度升序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"纬度排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void longOrder(){
            try {
                String sql= """
                        SELECT id,Fname,Latitude,Longitude,Altitude
                        FROM apiaries
                        ORDER BY Longitude ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this, "已按经度升序排序！");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "经度排序失败：" + e.getMessage(), "数据库错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void altiOrder(){
            try {
                String sql= """
                        SELECT id,Fname,Latitude,Longitude,Altitude
                        FROM apiaries
                        ORDER BY Altitude ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this, "已按海拔升序排序！");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "海拔排序失败：" + e.getMessage(), "数据库错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void updateTableData(String sql) throws SQLException{
            try(Connection conn=getConnection();
                Statement stmt=conn.createStatement();
                ResultSet rs=stmt.executeQuery(sql)){
                Vector<String> columnNames=new Vector<>();
                columnNames.add("ID");
                columnNames.add("蜂场名称");
                columnNames.add("纬度（-90~90°N）");
                columnNames.add("经度（-180~180°E）");
                columnNames.add("海拔（米）");
                Vector<Vector<Object>> data=new Vector<>();
                while(rs.next()){
                    Vector<Object> row=new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("Fname"));
                    row.add(rs.getDouble("Latitude"));
                    row.add(rs.getDouble("Longitude"));
                    row.add(rs.getInt("Altitude"));
                    data.add(row);
                }
                dataTable.setModel(new javax.swing.table.DefaultTableModel(data,columnNames));
                dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        }
    }
}
