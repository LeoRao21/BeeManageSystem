package Bee;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class HoneyBatches {
    private static final String url = BeeManageSystem.url;
    private static final String user = BeeManageSystem.user;
    private static final String password = BeeManageSystem.password;
    public static class IdentityCheck5 extends JFrame{
        private JTable dataTable;
        private JScrollPane scrollPane;
        public IdentityCheck5(){
            //初始化窗口
            setTitle("蜂蜜产出");
            setSize(800,500);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(true);
            setResizable(true);
            InitHBTGUI();
        }
        private void InitHBTGUI(){
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
            JButton ordBtn=new JButton("排序数据");
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
            JTextField HnoField=new JTextField();
            JTextField ProField=new JTextField();
            JTextField WaterField=new JTextField();
            JTextField FarmField=new JTextField();
            JTextField BeeField=new JTextField();
            Object[] message={
                    "取蜜批次号：",HnoField,
                    "产量（千克）：",ProField,
                    "含水率（%）：",WaterField,
                    "相关蜂场：",FarmField,
                    "产蜜蜂种：",BeeField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"录入产蜜数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String Hno=HnoField.getText().trim();
                String Prod=ProField.getText().trim();
                String Water=WaterField.getText().trim();
                String Farm=FarmField.getText().trim();
                String Bee=BeeField.getText().trim();
                if(Hno.isEmpty() || Prod.isEmpty() || Water.isEmpty() || Farm.isEmpty() || Bee.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！");
                    return;
                }
                double Pro=Double.parseDouble(Prod);
                double water=Double.parseDouble(Water);
                try {
                    if(Pro<0 || water<0 || water>100){
                        JOptionPane.showMessageDialog(this,"产量必须为非负数，含水率必须在0~100之间！");
                        return;
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"产量和含水率必须输入有效数字！");
                    return;
                }
                try{
                    String sql= """
                             INSERT INTO honeybatches(Hno,Pro,Water,Farm,Bee)
                             VALUES(?,?,?,?,?)""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,Hno);
                        pstmt.setDouble(2,Pro);
                        pstmt.setDouble(3,water);
                        pstmt.setString(4,Farm);
                        pstmt.setString(5,Bee);
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
            String Hno=dataTable.getValueAt(selectedRow,0).toString();
            int confirm=JOptionPane.showConfirmDialog(this,"确定要删除取蜜批次号为"+Hno+"的记录吗？","确认删除",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                try {
                    String sql="""
                           DELETE FROM honeybatches
                           WHERE Hno=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,Hno);
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
            String oldHno=dataTable.getValueAt(selectedRow,0).toString();
            String oldPro=dataTable.getValueAt(selectedRow,1).toString();
            String oldWater=dataTable.getValueAt(selectedRow,2).toString();
            String oldFarm=dataTable.getValueAt(selectedRow,3).toString();
            String oldBee=dataTable.getValueAt(selectedRow,4).toString();

            JTextField HnoField=new JTextField(oldHno);
            JTextField ProField=new JTextField(oldPro);
            JTextField WaterField=new JTextField(oldWater);
            JTextField FarmField=new JTextField(oldFarm);
            JTextField BeeField=new JTextField(oldBee);
            Object[] message={
                    "取蜜批次号：",HnoField,
                    "产量（千克）：",ProField,
                    "含水率（%）：",WaterField,
                    "相关蜂场：",FarmField,
                    "产蜜蜂种：",BeeField
            };
            int option=JOptionPane.showConfirmDialog(this,message,"修改产蜜数据",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
                String newHno=HnoField.getText().trim();
                String newProd=ProField.getText().trim();
                String newWa=WaterField.getText().trim();
                String newFarm=FarmField.getText().trim();
                String newBee=BeeField.getText().trim();
                if(newHno.isEmpty() || newProd.isEmpty() || newWa.isEmpty() || newFarm.isEmpty() || newBee.isEmpty()){
                    JOptionPane.showMessageDialog(this,"以上数据不能为空！","错误",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double newPro=Double.parseDouble(newProd);
                double newWater=Double.parseDouble(newWa);
                try {
                    if(newPro<0 || newWater<0 || newWater>100){
                        JOptionPane.showMessageDialog(this,"产量必须为非负数，含水率必须在0~100之间！");
                        return;
                    }
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(this,"产量和含水率必须输入有效数字！");
                    return;
                }
                try{
                    String sql= """
                            UPDATE honeybatches
                            SET Hno=?,Pro=?,Water=?,Farm=?,Bee=?
                            WHERE Hno=?""";
                    try(Connection conn=getConnection();
                        PreparedStatement pstmt=conn.prepareStatement(sql)){
                        pstmt.setString(1,newHno);
                        pstmt.setDouble(2,newPro);
                        pstmt.setDouble(3,newWater);
                        pstmt.setString(4,newFarm);
                        pstmt.setString(5,newBee);
                        pstmt.setString(6,oldHno);
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
                        SELECT Hno,Pro,Water,Farm,Bee
                        FROM honeybatches""";
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
            JButton ProBtn=new JButton("产量");
            btnPanel.add(ProBtn);
            JButton WaterBtn=new JButton("含水率");
            btnPanel.add(WaterBtn);
            sortDialog.add(btnPanel);
            ProBtn.addActionListener(e -> {
                ProOrder();
                sortDialog.dispose();
            });
            WaterBtn.addActionListener(e -> {
                WaterOrder();
                sortDialog.dispose();
            });
            sortDialog.setVisible(true);
        }
        private void updateTableData(String sql) throws SQLException{
            try(Connection conn=getConnection();
                Statement stmt=conn.createStatement();
                ResultSet rs=stmt.executeQuery(sql)){
                Vector<String> columnNames=new Vector<>();
                columnNames.add("取蜜批次号");
                columnNames.add("产量（千克）");
                columnNames.add("含水率");
                columnNames.add("相关蜂场");
                columnNames.add("产蜜蜂种");
                Vector<Vector<Object>> data=new Vector<>();
                while(rs.next()){
                    Vector<Object> row=new Vector<>();
                    row.add(rs.getString("Hno"));
                    row.add(rs.getDouble("Pro"));
                    row.add(rs.getDouble("Water"));
                    row.add(rs.getString("Farm"));
                    row.add(rs.getString("Bee"));
                    data.add(row);
                }
                dataTable.setModel(new javax.swing.table.DefaultTableModel(data,columnNames));
                dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        }
        private void ProOrder(){
            try{
                String sql= """
                        SELECT Hno,Pro,Water,Farm,Bee
                        FROM honeybatches
                        ORDER BY Pro ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按产量升序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"产量排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
        private void WaterOrder(){
            try{
                String sql= """
                        SELECT Hno,Pro,Water,Farm,Bee
                        FROM honeybatches
                        ORDER BY Water ASC;
                        """;
                updateTableData(sql);
                JOptionPane.showMessageDialog(this,"已按含水率升序排序！");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this,"含水率排序失败："+e.getMessage(),"数据库错误",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}