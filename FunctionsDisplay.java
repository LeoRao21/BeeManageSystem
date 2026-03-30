package Bee;
import javax.swing.*;
import java.awt.*;
public class FunctionsDisplay {
    public static void initFuncsGUI(String role){
        JFrame functionFrame=new JFrame();
        String roleName=BeeManageSystem.ROLE_MANAGER.equals(role) ? "管理员":"蜂农";
        functionFrame.setTitle(roleName+"功能选择");
        functionFrame.setSize(600,400);
        functionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        functionFrame.setLocationRelativeTo(null);
        JPanel panel=new JPanel();
        panel.setLayout(new GridLayout(0,1,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(30,50,30,50));
        if(BeeManageSystem.ROLE_MANAGER.equals(role)){
            addFunctionButton(panel,"用户与权限管理",BeeManageSystem.FUNC_1,role);
            addFunctionButton(panel,"蜂场管理",BeeManageSystem.FUNC_2,role);
            addFunctionButton(panel,"蜂群档案",BeeManageSystem.FUNC_3,role);
            addFunctionButton(panel,"巡检记录",BeeManageSystem.FUNC_4,role);
            addFunctionButton(panel,"蜂蜜产出",BeeManageSystem.FUNC_5,role);
            addFunctionButton(panel,"蜜源管理",BeeManageSystem.FUNC_6,role);
            addFunctionButton(panel,"销售管理",BeeManageSystem.FUNC_7,role);
            addFunctionButton(panel,"统计分析",BeeManageSystem.FUNC_8,role);
        }else if(BeeManageSystem.ROLE_FARMER.equals(role)){
            addFunctionButton(panel,"蜂场管理-仅查看、排序",BeeManageSystem.FUNC_2,role);
            addFunctionButton(panel,"蜂群档案-仅查看",BeeManageSystem.FUNC_3,role);
            addFunctionButton(panel,"巡检记录",BeeManageSystem.FUNC_4,role);
            addFunctionButton(panel,"蜂蜜产出",BeeManageSystem.FUNC_5,role);
            addFunctionButton(panel,"蜜源管理-仅查看、排序",BeeManageSystem.FUNC_6,role);
            addFunctionButton(panel,"统计分析-仅查看、排序",BeeManageSystem.FUNC_8,role);
        }
        functionFrame.add(panel);
        functionFrame.setVisible(true);
    }
    private static void addFunctionButton(JPanel panel,String btnText,String function,String role){
        JButton btn=new JButton(btnText);
        btn.addActionListener(e -> handleFunctionClick(function,role));
        panel.add(btn);
    }
    private static void handleFunctionClick(String function,String role){
        switch(function){
            case BeeManageSystem.FUNC_1:
                new UsersManage.IdentityCheck1().setVisible(true);
                break;
            case BeeManageSystem.FUNC_2:
                new Apiaries.IdentityCheck2(role).setVisible(true);
                break;
            case BeeManageSystem.FUNC_3:
                new BeeColonies.IdentityCheck3(role).setVisible(true);
                break;
            case BeeManageSystem.FUNC_4:
                new InspectionLogs.IdentityCheck4().setVisible(true);
                break;
            case BeeManageSystem.FUNC_5:
                new HoneyBatches.IdentityCheck5().setVisible(true);
                break;
            case BeeManageSystem.FUNC_6:
                new NectorSources.IdentityCheck6(role).setVisible(true);
                break;
            case BeeManageSystem.FUNC_7:
                new SalesOrders.IdentityCheck7().setVisible(true);
                break;
            case BeeManageSystem.FUNC_8:
                new SeasonRecords.IdentityCheck8(role).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null,"功能暂未实现！");
        }
    }
}
