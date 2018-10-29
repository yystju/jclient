package PartSet;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class v_PartSet {
	public static final int WIDTH = 1020;     //���ڿ�;
	public static final int HEIGHT = 620;    //���ڸ�;
	public int selectCounter=0;
	JFrame frame = new JFrame("��Ʒ��Ϣ��ѯ");		//����
	
	JPanel panel=new JPanel();

	JLabel label001=new JLabel("��Ʒ���:");
	JLabel label002=new JLabel("��Ʒ����:");
	JLabel label003=new JLabel("��������:");
	JLabel label004=new JLabel("��װ�ȼ�:");
	JLabel label005=new JLabel("�ͻ���:");
	JLabel label006=new JLabel("��Ʒ���:");

	JTextField fieldPartNumber=new JTextField();
	JTextField fieldPartNumber1=new JTextField();
	JTextField fieldPartName=new JTextField();
	JTextField fieldQtyPlan=new JTextField();
	JTextField fieldControlLevel=new JTextField();
	JTextField fieldCustomerNumber=new JTextField();

	JButton buttonSearch=new JButton("��ѯ");
	JButton buttonGet=new JButton("��ȡ");
	JButton buttonSave=new JButton("����");
	JButton buttonClose=new JButton("X");
	
	JScrollPane scp01 = new JScrollPane();
	JTable tab01;
	
	public v_PartSet(){
		frame.add(label001);
		label001.setBounds(20, 20, 60, 30);
//		label001.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		label001.setHorizontalAlignment(SwingConstants.CENTER);
		label001.setVisible(true);

		frame.add(fieldPartNumber);
		fieldPartNumber.setBounds(80,20, 200, 30);
		fieldPartNumber.setVisible(true);
	
		frame.add(buttonSearch);
		buttonSearch.setBounds(290, 20, 60, 30);
		buttonSearch.setVisible(true);
	
		frame.add(buttonClose);
		buttonClose.setBounds(WIDTH-50, 0, 50, 30);
		buttonClose.setHorizontalAlignment(SwingConstants.CENTER);
		buttonClose.setForeground(Color.WHITE);
		buttonClose.setBackground(Color.RED);
		buttonClose.setVisible(true);
		
		scp01.setBounds(20,60,(WIDTH-355),HEIGHT-60-55);
		scp01.setVisible(true);
		
		frame.add(label006);
		label006.setBounds((WIDTH-335)+20, 120-50, 60, 30);
//		label006.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		label006.setHorizontalAlignment(SwingConstants.CENTER);
		label006.setVisible(true);
		
		frame.add(fieldPartNumber1);
		fieldPartNumber1.setBounds((WIDTH-335)+80,120-50, 200, 30);
		fieldPartNumber1.setEnabled(false);
		fieldPartNumber1.setVisible(true);
		
		frame.add(label002);
		label002.setBounds((WIDTH-335)+20, 120, 60, 30);
//		label002.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		label002.setHorizontalAlignment(SwingConstants.CENTER);
		label002.setVisible(true);
		
		frame.add(fieldPartName);
		fieldPartName.setBounds((WIDTH-335)+80,120, 200, 30);
		fieldPartName.setVisible(true);
		
		frame.add(label003);
		label003.setBounds((WIDTH-335)+20, 120+50, 60, 30);
//		label003.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		label003.setHorizontalAlignment(SwingConstants.CENTER);
		label003.setVisible(true);
		
		frame.add(fieldQtyPlan);
		fieldQtyPlan.setBounds((WIDTH-335)+80,120+50, 200, 30);
		fieldQtyPlan.setVisible(true);
		
		frame.add(label004);
		label004.setBounds((WIDTH-335)+20, 120+2*50, 60, 30);
//		label004.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		label004.setHorizontalAlignment(SwingConstants.CENTER);
		label004.setVisible(true);
		
		frame.add(fieldControlLevel);
		fieldControlLevel.setBounds((WIDTH-335)+80,120+2*50, 200, 30);
		fieldControlLevel.setVisible(true);
		
		frame.add(label005);
		label005.setBounds((WIDTH-335)+20, 120+3*50, 60, 30);
//		label005.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		label005.setHorizontalAlignment(SwingConstants.CENTER);
		label005.setVisible(true);
		
		frame.add(fieldCustomerNumber);
		fieldCustomerNumber.setBounds((WIDTH-335)+80,120+3*50, 200, 30);
		fieldCustomerNumber.setVisible(true);

		frame.add(buttonGet);
		buttonGet.setBounds((WIDTH-335)+150, 120+4*50, 60, 30);
		buttonGet.setVisible(true);
		
		frame.add(buttonSave);
		buttonSave.setBounds((WIDTH-335)+220, 120+4*50, 60, 30);
		buttonSave.setVisible(true);
		
		tab01 = new JTable();
		scp01.getViewport().add(tab01); 
		frame.add(scp01);
	    
		frame.add(panel);							//�������ӵ�������
		/*�������*/
		frame.setUndecorated(true);
		frame.setSize(WIDTH, HEIGHT);        		//���ô��ڿ�͸�          
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);   //����Ĭ�Ϲرղ���(�رմ����˳�����)
		frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);			//���ô��ھ�����ʾ
		frame.setVisible(true);						//���ô��ڿɼ�
		
		buttonClose.addMouseListener(ButtonClose_Click);
	}
	
	public void showTable(JTable t){
		tab01=t;
		scp01.getViewport().add(tab01); 
	}

	
	MouseAdapter ButtonClose_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	frame.dispose();
        }  
	};
}
