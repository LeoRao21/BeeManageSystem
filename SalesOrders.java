package Bee;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class SalesOrders {
    private static final String url = BeeManageSystem.url;
    private static final String user = BeeManageSystem.user;
    private static final String password = BeeManageSystem.password;
    public static class IdentityCheck7 extends JFrame{
        private JTable dataTable;
        private JScrollPane scrollPane;
        public IdentityCheck7(){
            //初始化窗口
            setTitle("销售管理");
            setSize(800,500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);
            InitNSCGUI();
            setVisible(true);
        }
        private void InitNSCGUI(){
            //创建顶部按钮面板
            JPanel topPanel=new JPanel();
            JButton addBtn=new JButton("录入数据");
            JButton delBtn=new JButton("删除数据");
            JButton updBtn=new JButton("修改数据");
            JButton seeBtn=new JButton("查看数据");
            JButton ordBtn=new JButton("排序数据");
            topPanel.add(addBtn);
            topPanel.add(delBtn);
            topPanel.add(updBtn);
            topPanel.add(seeBtn);
            topPanel.add(ordBtn);
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
            JTextField BFarmField=new JTextField();
            JTextField BtyField=new JTextField();
            JTextField ChannelField=new JTextField();
            JTextField OrdersField=new JTextField();
            JTextField PriceField=new JTextField();
            Object[] message={
                    "蜂场：",BFarmField,
                    "蜂种：",BtyField,
                    "销售渠道：",ChannelField,
                    "订单（千克）：",OrdersField,
                    "价格（元/千克）：",PriceField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"录入销售数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String Bfarm=BFarmField.getText().trim();
                String Btye=BtyField.getText().trim();
                String Channel=ChannelField.getText().trim();
                String Order=OrdersField.getText().trim();
                String price=PriceField.getText().trim();
                if(Bfarm.isEmpty() || Btye.isEmpty() || Channel.isEmpty() || Order.isEmpty() || price.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！");
                    return;
                }
                try {
                    double Ord = Double.parseDouble(Order);
                    double Pri = Double.parseDouble(price);
                    if (Ord < 0 || Pri < 0) {
                        JOptionPane.showMessageDialog(this, "订单与价格均不能为负！");
                        return;
                    }
                    String sql = """
                            INSERT INTO salesorders(BFarm,Bty,Channel,Orders,Price)
                            VALUES(?,?,?,?,?)""";
                    try (Connection conn = getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, Bfarm);
                        pstmt.setString(2, Btye);
                        pstmt.setString(3, Channel);
                        pstmt.setDouble(4, Ord);
                        pstmt.setDouble(5, Pri);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "数据录入成功！");
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
            String BeeType=dataTable.getValueAt(selectedRow,1).toString();
            Number num=(Number)dataTable.getValueAt(selectedRow,2);
            double ORDER=num.doubleValue();
            int confirm=JOptionPane.showConfirmDialog(this,"确定要删除蜂种为"+BeeType+"，订单为"+ORDER+"千克的记录吗？","确认删除",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                try {
                    String sql="""
                           DELETE FROM salesorders
                           WHERE Bty=? AND Orders=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,BeeType);
                        pstmt.setDouble(3,ORDER);
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
            String oldBFarm=dataTable.getValueAt(selectedRow,0).toString();
            String oldBty=dataTable.getValueAt(selectedRow,1).toString();
            String oldChannel=dataTable.getValueAt(selectedRow,2).toString();
            String oldOrders=dataTable.getValueAt(selectedRow,3).toString();
            String oldPrice=dataTable.getValueAt(selectedRow,4).toString();
            JTextField BfField=new JTextField(oldBFarm,15);
            JTextField BtField=new JTextField(oldBty,15);
            JTextField CnField=new JTextField(oldChannel,15);
            JTextField OrField=new JTextField(oldOrders,15);
            JTextField PrField=new JTextField(oldPrice,15);
            Object[] message={
                    "蜂场：",BfField,
                    "蜂种：",BtField,
                    "销售渠道：",CnField,
                    "订单（千克）：",OrField,
                    "价格（元/千克）：",PrField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"修改销售数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String newBFarm=BfField.getText().trim();
                String newBtype=BtField.getText().trim();
                String newChannel=CnField.getText().trim();
                String newOrders=OrField.getText().trim();
                String newPrice=PrField.getText().trim();
                if(newBFarm.isEmpty() || newBtype.isEmpty() || newChannel.isEmpty() || newOrders.isEmpty() || newPrice.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！","错误",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try{
                    double Ord = Double.parseDouble(newOrders);
                    double Pri = Double.parseDouble(newPrice);
                    if(Ord<0 || Pri<0){
                        JOptionPane.showMessageDialog(this,"订单和价格不能为负数！");
                        return;
                    }
                    String sql= """
                            UPDATE salesorders
                            SET BFarm=?,Bty=?,Channel=?,Orders=?,Price=?
                            WHERE Bty=? AND Orders=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,newBFarm);
                        pstmt.setString(2,newBtype);
                        pstmt.setString(3,newChannel);
                        pstmt.setDouble(4,Ord);
                        pstmt.setDouble(5,Pri);
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
                        SELECT BFarm,Bty,Channel,Orders,Price
                        FROM salesorders""";
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
            JButton ddBtn = new JButton("订单");
            btnPanel.add(ddBtn);
            JButton PriBtn = new JButton("价格");
            btnPanel.add(PriBtn);
            JButton CancelBtn=new JButton("取消");
            btnPanel.add(CancelBtn);
            sortDialog.add(btnPanel);
            ddBtn.addActionListener(e -> {
                ddOrder();
                sortDialog.dispose();
            });
            PriBtn.addActionListener(e -> {
                PriOrder();
                sortDialog.dispose();
            });
            CancelBtn.addActionListener(e -> sortDialog.dispose());
            sortDialog.setVisible(true);
        }
        private void ddOrder(){
            try{
                String sql= """
                        SELECT BFarm,Bty,Channel,Orders,Price
                        FROM salesorders
                        ORDER BY Orders ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按订单升序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"订单排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void PriOrder(){
            try{
                String sql= """
                        SELECT BFarm,Bty,Channel,Orders,Price
                        FROM salesorders
                        ORDER BY Price ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按价格升序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"价格排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void updateTableData(String sql) throws SQLException{
            try(Connection conn=getConnection();
                Statement stmt=conn.createStatement();
                ResultSet rs=stmt.executeQuery(sql)){
                Vector<String> columnNames=new Vector<>();
                columnNames.add("蜂场");
                columnNames.add("蜂种");
                columnNames.add("销售渠道");
                columnNames.add("订单（千克）");
                columnNames.add("价格（元/千克）");
                Vector<Vector<Object>> data=new Vector<>();
                while(rs.next()){
                    Vector<Object> row=new Vector<>();
                    row.add(rs.getString("BFarm"));
                    row.add(rs.getString("Bty"));
                    row.add(rs.getInt("Channel"));
                    row.add(rs.getInt("Orders"));
                    row.add(rs.getInt("Price"));
                    data.add(row);
                }
                dataTable.setModel(new javax.swing.table.DefaultTableModel(data,columnNames));
                dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        }
    }
}