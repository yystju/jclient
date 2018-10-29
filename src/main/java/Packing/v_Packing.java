package Packing;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class v_Packing {
	public static final int WIDTH = 1000;     //窗口宽;
	public static final int HEIGHT = 600;    //窗口高;
	
	JFrame frame = new JFrame("圣阑包装系统 v2.2"+"\t"+Main.Main.ms.equipmentName);		//窗口
	
	JPanel panel=new JPanel();

	JLabel labelBoxNumber=new JLabel();
	JLabel labelQuantity=new JLabel();
	JLabel label001=new JLabel("SN:");
	
	public JTextField fieldSN=new JTextField();

	JButton buttonPacking=new JButton("封箱");
	JButton buttonUnpack=new JButton("取出");
	JButton buttonCancelBox=new JButton("拆箱");
	JButton buttonOpenBox=new JButton("开箱");
	JButton buttonHistory=new JButton("找回");
	JButton buttonPart=new JButton("产品");
	JButton buttonHelp=new JButton("帮助");
	
//	JScrollPane scp01 = new JScrollPane();
//	JTable tab01;
	public JTextArea his=null;
	JScrollPane jscrollPane;
	
	public v_Packing(){
		frame.add(labelBoxNumber);
		labelBoxNumber.setBounds(20, 20, (WIDTH-55)*4/5, HEIGHT/6);
		labelBoxNumber.setFont(new Font("",1,HEIGHT/13));
//		labelBoxNumber.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		labelBoxNumber.setHorizontalAlignment(SwingConstants.CENTER);
		labelBoxNumber.setVisible(true);

		frame.add(labelQuantity);
		labelQuantity.setBounds(20+(WIDTH-55)*4/5, 20, (WIDTH-55)/5, HEIGHT/6);
		labelQuantity.setFont(new Font("",1,HEIGHT/6-20));
		labelQuantity.setHorizontalAlignment(SwingConstants.CENTER);
		labelQuantity.setVisible(true);
		
		frame.add(label001);
		label001.setBounds(20, 40+HEIGHT/6, 30, 20);
		label001.setHorizontalAlignment(SwingConstants.CENTER);
		label001.setVisible(true);
		
		frame.add(fieldSN);
		fieldSN.setBounds(50, 40+HEIGHT/6, 200, 20);
		fieldSN.setVisible(true);
		
		frame.add(buttonPacking);
		buttonPacking.setBounds(290, 40+HEIGHT/6, 60, 30);
		buttonPacking.setVisible(true);
		
		frame.add(buttonUnpack);
		buttonUnpack.setBounds(290+60, 40+HEIGHT/6, 60, 30);
		buttonUnpack.setVisible(true);
		
		frame.add(buttonCancelBox);
		buttonCancelBox.setBounds(290+2*60, 40+HEIGHT/6, 60, 30);
		buttonCancelBox.setVisible(true);
		
		frame.add(buttonOpenBox);
		buttonOpenBox.setBounds(290+3*60, 40+HEIGHT/6, 60, 30);
		buttonOpenBox.setVisible(true);
		
		frame.add(buttonHistory);
		buttonHistory.setBounds(290+4*60, 40+HEIGHT/6, 60, 30);
		buttonHistory.setVisible(true);
		
		frame.add(buttonPart);
		buttonPart.setBounds(290+5*60, 40+HEIGHT/6, 60, 30);
		buttonPart.setVisible(true);
		
		frame.add(buttonHelp);
		buttonHelp.setBounds(290+6*60, 40+HEIGHT/6, 60, 30);
		buttonHelp.setVisible(true);

		his=new JTextArea("\n打开程序");
		his.setTabSize(4);
		his.setFont(new Font("标楷体", Font.BOLD, 16));
		his.setWrapStyleWord(true);// 激活断行不断字功能    
		his.setVisible(true);
		jscrollPane = new JScrollPane(his);
		frame.add(jscrollPane, BorderLayout.CENTER);
		jscrollPane.setBounds(20,90+HEIGHT/6,(WIDTH-55),HEIGHT*5/6-155);
		jscrollPane.setVisible(true);
		
		
//		tab01 = new JTable();
//		scp01.getViewport().add(tab01); 
//		frame.add(scp01);
		
		frame.add(panel);							//将面板添加到窗口上
		/*面板设置*/
		frame.setSize(WIDTH, HEIGHT);        		//设置窗口宽和高          
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //设置默认关闭操作(关闭窗口退出程序)
		frame.setLocationRelativeTo(null);			//设置窗口居中显示
		frame.setVisible(true);						//设置窗口可见
		
	}
	
	public void setBoxNumber(String str){
		labelBoxNumber.setText(str);
		labelBoxNumber.setToolTipText(str);
	}
	
	public void showTable(JTable t){
//		tab01=t;
//		scp01.getViewport().add(tab01); 
	}
}
