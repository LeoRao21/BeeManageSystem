package Bee;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class SeasonRecords {
    private static final String url = BeeManageSystem.url;
    private static final String user = BeeManageSystem.user;
    private static final String password = BeeManageSystem.password;
    public static class IdentityCheck8 extends JFrame{
        private JTable dataTable;
        private JScrollPane scrollPane;
        private String currentRole;
        public IdentityCheck8(String role){
            //初始化窗口
            this.currentRole=role;
            setTitle("统计分析");
            setSize(800,500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);
            InitSRDGUI(role);
            setVisible(true);
        }
        private void InitSRDGUI(String role){
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
            JTextField BeefarmField=new JTextField();
            JTextField BeetypeField=new JTextField();
            JTextField MonthsField=new JTextField();
            JTextField ProdField=new JTextField();
            JTextField MoneyField=new JTextField();
            Object[] message={
                    "蜂场：",BeefarmField,
                    "蜂种：",BeetypeField,
                    "月份：",MonthsField,
                    "月产量：",ProdField,
                    "月销售额：",MoneyField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"录入统计数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String Beefarm=BeefarmField.getText().trim();
                String Beetype=BeetypeField.getText().trim();
                String Months=MonthsField.getText().trim();
                String Pro=ProdField.getText().trim();
                String Money=MoneyField.getText().trim();
                if(Beefarm.isEmpty() || Beetype.isEmpty() || Months.isEmpty() || Pro.isEmpty() || Money.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！");
                    return;
                }
                int month=Integer.parseInt(Months);
                double Prod=Double.parseDouble(Pro);
                double money=Double.parseDouble(Money);
                try {
                    if(month<1 || month>12 || Prod<0 || money<0){
                        JOptionPane.showMessageDialog(this,"月产量和月销售额必须为非负数，月份必须为1~12月！");
                        return;
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"月份、月产量和月销售额必须输入有效数字！");
                    return;
                }
                try{
                    String sql= """
                             INSERT INTO seasonrecords(BeeFarm,BeType,Months,Prod,Money)
                             VALUES(?,?,?,?,?)""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,Beefarm);
                        pstmt.setString(2,Beetype);
                        pstmt.setInt(3,month);
                        pstmt.setDouble(4,Prod);
                        pstmt.setDouble(5,money);
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
            String BeeF=dataTable.getValueAt(selectedRow,0).toString();
            String BeeT=dataTable.getValueAt(selectedRow,1).toString();
            String timestr=dataTable.getValueAt(selectedRow,2).toString();
            int time=Integer.parseInt(timestr);
            int confirm=JOptionPane.showConfirmDialog(this,"确定要删除蜂场为"+BeeF+"，蜂种为"+BeeT+"，月份为"+time+"月的记录吗？","确认删除",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                try {
                    String sql="""
                           DELETE FROM seasonrecords
                           WHERE BeeFarm=? AND BeType=? AND Months=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,BeeF);
                        pstmt.setString(2,BeeT);
                        pstmt.setInt(3,time);
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
            String oldBF=dataTable.getValueAt(selectedRow,0).toString();
            String oldBT=dataTable.getValueAt(selectedRow,1).toString();
            String oldMon=dataTable.getValueAt(selectedRow,2).toString();
            String oldPrd=dataTable.getValueAt(selectedRow,3).toString();
            String oldMny=dataTable.getValueAt(selectedRow,4).toString();

            JTextField BFField=new JTextField(oldBF);
            JTextField BTField=new JTextField(oldBT);
            JTextField MonField=new JTextField(oldMon);
            JTextField PrdField=new JTextField(oldPrd);
            JTextField MnyField=new JTextField(oldMny);
            Object[] message={
                    "蜂场：",BFField,
                    "蜂种：",BTField,
                    "月份：",MonField,
                    "月产量：",PrdField,
                    "月销售额：",MnyField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"修改统计数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String newBeF=BFField.getText().trim();
                String newBeT=BTField.getText().trim();
                String newMon=MonField.getText().trim();
                String newPrd=PrdField.getText().trim();
                String newMny=MnyField.getText().trim();
                if(newBeF.isEmpty() || newBeT.isEmpty() || newMon.isEmpty() || newPrd.isEmpty() || newMny.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！","错误",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int newMonth=Integer.parseInt(newMon);
                double newPro=Double.parseDouble(newPrd);
                double newmoney=Double.parseDouble(newMny);
                try {
                    if(newMonth<1 || newMonth>12 || newPro<0 || newmoney<0){
                        JOptionPane.showMessageDialog(this,"月产量和月销售额必须为非负数，月份必须为1~12月！");
                        return;
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"月份、月产量和月销售额必须输入有效数字！");
                    return;
                }
                try{
                    String sql= """
                            UPDATE seasonrecords
                            SET BeeFarm=?,BeType=?,Months=?,Prod=?,Money=?
                            WHERE BeeFarm=? AND BeType=? AND Months=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,newBeF);
                        pstmt.setString(2,newBeT);
                        pstmt.setInt(3,newMonth);
                        pstmt.setDouble(4,newPro);
                        pstmt.setDouble(5,newmoney);
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
                        SELECT BeeFarm,BeType,Months,Prod,Money
                        FROM seasonrecords
                        ORDER BY
                            BeeFarm ASC,
                            BeType ASC,
                            Months ASC;
                        """;
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
            JButton ProdBtn=new JButton("月产量");
            btnPanel.add(ProdBtn);
            JButton MoneyBtn=new JButton("月销售额");
            btnPanel.add(MoneyBtn);
            sortDialog.add(btnPanel);
            ProdBtn.addActionListener(e -> {
                ProdOrder();
                sortDialog.dispose();
            });
            MoneyBtn.addActionListener(e -> {
                MoneyOrder();
                sortDialog.dispose();
            });
            sortDialog.setVisible(true);
        }
        private void updateTableData(String sql) throws SQLException{
            try(Connection conn=getConnection();
                Statement stmt=conn.createStatement();
                ResultSet rs=stmt.executeQuery(sql)){
                Vector<String> columnNames=new Vector<>();
                columnNames.add("蜂场");
                columnNames.add("蜂种");
                columnNames.add("月份");
                columnNames.add("月产量");
                columnNames.add("月销售额");
                Vector<Vector<Object>> data=new Vector<>();
                while(rs.next()){
                    Vector<Object> row=new Vector<>();
                    row.add(rs.getString("BeeFarm"));
                    row.add(rs.getString("BeType"));
                    row.add(rs.getInt("Months"));
                    row.add(rs.getDouble("Prod"));
                    row.add(rs.getDouble("Money"));
                    data.add(row);
                }
                dataTable.setModel(new javax.swing.table.DefaultTableModel(data,columnNames));
                dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        }
        private void ProdOrder(){
            try{
                String sql= """
                        SELECT BeeFarm,BeType,Months,Prod,Money
                        FROM seasonrecords
                        ORDER BY
                            BeeFarm ASC,
                            BeType ASC,
                            Prod ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按月产量升序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"月产量排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void MoneyOrder(){
            try{
                String sql= """
                        SELECT BeeFarm,BeType,Months,Prod,Money
                        FROM seasonrecords
                        ORDER BY
                            BeeFarm ASC,
                            BeType ASC,
                            Money ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按月销售额升序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"月销售额排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
