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
	public static final int WIDTH = 1000;     //���ڿ�;
	public static final int HEIGHT = 600;    //���ڸ�;
	
	JFrame frame = new JFrame("ʥ����װϵͳ v2.2"+"\t"+Main.Main.ms.equipmentName);		//����
	
	JPanel panel=new JPanel();

	JLabel labelBoxNumber=new JLabel();
	JLabel labelQuantity=new JLabel();
	JLabel label001=new JLabel("SN:");
	
	public JTextField fieldSN=new JTextField();

	JButton buttonPacking=new JButton("����");
	JButton buttonUnpack=new JButton("ȡ��");
	JButton buttonCancelBox=new JButton("����");
	JButton buttonOpenBox=new JButton("����");
	JButton buttonHistory=new JButton("�һ�");
	JButton buttonPart=new JButton("��Ʒ");
	JButton buttonHelp=new JButton("����");
	
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

		his=new JTextArea("\n�򿪳���");
		his.setTabSize(4);
		his.setFont(new Font("�꿬��", Font.BOLD, 16));
		his.setWrapStyleWord(true);// ������в����ֹ���    
		his.setVisible(true);
		jscrollPane = new JScrollPane(his);
		frame.add(jscrollPane, BorderLayout.CENTER);
		jscrollPane.setBounds(20,90+HEIGHT/6,(WIDTH-55),HEIGHT*5/6-155);
		jscrollPane.setVisible(true);
		
		
//		tab01 = new JTable();
//		scp01.getViewport().add(tab01); 
//		frame.add(scp01);
		
		frame.add(panel);							//�������ӵ�������
		/*�������*/
		frame.setSize(WIDTH, HEIGHT);        		//���ô��ڿ�͸�          
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //����Ĭ�Ϲرղ���(�رմ����˳�����)
		frame.setLocationRelativeTo(null);			//���ô��ھ�����ʾ
		frame.setVisible(true);						//���ô��ڿɼ�
		
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
