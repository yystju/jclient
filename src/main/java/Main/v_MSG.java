package Main;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class v_MSG {
	static public void show(Object msgText, String title,boolean Warming){
		if(Warming){
			//TODO	控制盒嗷嗷叫
		}
		JOptionPane.showMessageDialog(null,msgText, title,JOptionPane.INFORMATION_MESSAGE);
		if(Warming){
			//TODO	控制盒闭嘴
		}
	}
	
	static public void show(Object msgText){
		JOptionPane.showMessageDialog(null,msgText, "注意",JOptionPane.INFORMATION_MESSAGE);
	}
	
	static public void Nshow(String msgText, String title,boolean Warming){
		if(Warming){
			//TODO	控制盒嗷嗷叫
		}
		JFrame frame = new JFrame(title);		//窗口
		JPanel panel=new JPanel();
		JTextArea label001=new JTextArea(msgText);
		frame.add(label001);
		label001.setEnabled(false);
		label001.setBounds(20, 20, 560, 360);
		label001.setLineWrap(true);
		label001.setVisible(true);
		frame.add(panel);							//将面板添加到窗口上
		/*面板设置*/
		frame.setAlwaysOnTop(true);
		frame.setSize(600, 400);        		//设置窗口宽和高          
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);   //设置默认关闭操作(关闭窗口退出程序)
		frame.setLocationRelativeTo(null);			//设置窗口居中显示
		frame.setVisible(true);						//设置窗口可见
		if(Warming){
			//TODO	控制盒闭嘴
		}
	}
	
	static public String input(Object msgText,String title,int style,boolean Warming){
		String Text =new String();
		if(Warming){
			//TODO	控制盒嗷嗷叫
		}
		Text=JOptionPane.showInputDialog( null,msgText,title,style);
		if(Warming){
			//TODO	控制盒闭嘴
		}
		return Text;
	}
	
	
}
