package Main;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class v_MSG {
	static public void show(Object msgText, String title,boolean Warming){
		if(Warming){
			//TODO	���ƺ��໽�
		}
		JOptionPane.showMessageDialog(null,msgText, title,JOptionPane.INFORMATION_MESSAGE);
		if(Warming){
			//TODO	���ƺб���
		}
	}
	
	static public void show(Object msgText){
		JOptionPane.showMessageDialog(null,msgText, "ע��",JOptionPane.INFORMATION_MESSAGE);
	}
	
	static public void Nshow(String msgText, String title,boolean Warming){
		if(Warming){
			//TODO	���ƺ��໽�
		}
		JFrame frame = new JFrame(title);		//����
		JPanel panel=new JPanel();
		JTextArea label001=new JTextArea(msgText);
		frame.add(label001);
		label001.setEnabled(false);
		label001.setBounds(20, 20, 560, 360);
		label001.setLineWrap(true);
		label001.setVisible(true);
		frame.add(panel);							//�������ӵ�������
		/*�������*/
		frame.setAlwaysOnTop(true);
		frame.setSize(600, 400);        		//���ô��ڿ�͸�          
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);   //����Ĭ�Ϲرղ���(�رմ����˳�����)
		frame.setLocationRelativeTo(null);			//���ô��ھ�����ʾ
		frame.setVisible(true);						//���ô��ڿɼ�
		if(Warming){
			//TODO	���ƺб���
		}
	}
	
	static public String input(Object msgText,String title,int style,boolean Warming){
		String Text =new String();
		if(Warming){
			//TODO	���ƺ��໽�
		}
		Text=JOptionPane.showInputDialog( null,msgText,title,style);
		if(Warming){
			//TODO	���ƺб���
		}
		return Text;
	}
	
	
}
