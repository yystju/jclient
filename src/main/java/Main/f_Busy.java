package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class f_Busy extends JFrame{
	
	JLabel TEXT=new JLabel("Ã¦ÂµÖÐ£¬ÇëµÈ´ý");
	JLabel [] Text=new JLabel[8];
	static boolean runTextFlag=false;
	public f_Busy(){
		Text[0]=new JLabel("S");
		Text[1]=new JLabel("u");
		Text[2]=new JLabel("n");
		Text[3]=new JLabel("l");
		Text[4]=new JLabel("i");
		Text[5]=new JLabel("g");
		Text[6]=new JLabel("h");
		Text[7]=new JLabel("t");
		this.setLayout(null);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int WFwidth=500;
		int WFheight=200;
		this.setBounds((width-WFwidth)/2,(height-WFheight)/2, WFwidth, WFheight);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setUndecorated(true);
		this.add(TEXT);
		this.add(Text[0]);
		this.add(Text[1]);
		this.add(Text[2]);
		this.add(Text[3]);
		this.add(Text[4]);
		this.add(Text[5]);
		this.add(Text[6]);
		this.add(Text[7]);
		TEXT.setBounds(0, 0, WFwidth,WFheight/3*2);
		TEXT.setFont(new Font("",1,60));
		TEXT.setHorizontalAlignment(SwingConstants.CENTER);
		TEXT.setVerticalAlignment(SwingConstants.CENTER);
		TEXT.setVisible(true);
		for(int i=0;i<8;i++){
			Text[i].setFont(new Font("",1,25));
			Text[i].setBounds(WFwidth-50-20*(8-i), WFheight-50, 20, 30);
			Text[i].setHorizontalAlignment(SwingConstants.CENTER);
			Text[i].setVerticalAlignment(SwingConstants.CENTER);
//			Text[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
			Text[i].setVisible(true);
		}
		runingText rT=new runingText();
		runTextFlag=true;
		rT.start();
		this.setAlwaysOnTop(true);
		this.setBackground(new Color(0,0,0,0));
		this.setVisible(true);
	}
	
	class runingText extends Thread{
		long FQ=100;
		public void run(){
			Color V=Color.RED;
			while(f_Busy.runTextFlag){
				V=Color.RED;
				TEXT.setForeground(V);
				for(int i=0;i<8;i++){
					try {
						Thread.sleep(FQ);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Text[i].setForeground(V);
				}
				V=Color.GREEN;
				TEXT.setForeground(V);
				for(int i=0;i<8;i++){
					try {
						Thread.sleep(FQ);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Text[i].setForeground(V);
				}
				V=Color.BLUE;
				TEXT.setForeground(V);
				for(int i=0;i<8;i++){
					try {
						Thread.sleep(FQ);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Text[i].setForeground(V);
				}
				V=Color.BLACK;
				TEXT.setForeground(V);
				for(int i=0;i<8;i++){
					try {
						Thread.sleep(FQ);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Text[i].setForeground(V);
				}
			}
		}
	}
}
