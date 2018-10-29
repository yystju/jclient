package Main;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import MySocket.MySocket;
import Packing.c_Packing;
import SQLserver.AK_SQLreturn;
import SQLserver.c_SQLserver;

@SuppressWarnings("serial")
public class f_Welcome extends JFrame{
	static String PW,PW1,Mode;
	int TimeCount=0;
	
	boolean flag=false;
	
	JLabel Welcome_Label=new JLabel("欢迎登录");
	JLabel MachineNumber_Label=new JLabel("机器号:");
	JLabel OP_Label=new JLabel("用户名:");
	JLabel PW_Label=new JLabel("密   码:");
	JLabel PW1_Label=new JLabel("验证密码:");
	JLabel TimeOver=new JLabel("初始化");

	JTextField MachineNumber_Text=new JTextField();
	JTextField OP_Text=new JTextField();
	JTextField PW_Text=new JPasswordField ();
	JTextField PW1_Text=new JPasswordField ();
	
	JButton Login_Button=new JButton("登录");
	JButton Exit_Button=new JButton("取消");
	JButton Register_Button=new JButton("注册账户");
	JButton ChangePW_Button=new JButton("修改密码");
	
	c_SQLserver sql = new c_SQLserver();
	
	public f_Welcome(){
		if(!sql.Connect(Main.IP,Main.DbName,Main.OP,Main.PW)){
			System.exit(0);
		}
		Mode="Login";
		this.setLayout(null);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int WFwidth=600;
		int WFheight=350;
		this.setBounds((width-WFwidth)/2,(height-WFheight)/2, WFwidth, WFheight);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("包装系统,请登录");
		
		TimeCount=180;
		TimeOver.setBounds(0, WFheight-50, WFwidth/2,15);
		TimeOver.setFont(new Font("",1,8));
		TimeOver.setVisible(true);
		this.add(TimeOver);
		
		Welcome_Label.setBounds(0, 0, WFwidth,WFheight/3);
		Welcome_Label.setFont(new Font("",1,70));
		Welcome_Label.setHorizontalAlignment(SwingConstants.CENTER);
		Welcome_Label.setVerticalAlignment(SwingConstants.CENTER);
		Welcome_Label.setVisible(true);
	    this.add(Welcome_Label);
		
		
	    MachineNumber_Label.setBounds(WFwidth/2-160,WFheight/3+30, 90,25);
	    MachineNumber_Label.setFont(new Font("",1,20));
	    MachineNumber_Label.setVisible(true);
	    this.add(MachineNumber_Label);
		
	    OP_Label.setBounds(WFwidth/2-160,WFheight/3+60, 90,25);
	    OP_Label.setFont(new Font("",1,20));
	    OP_Label.setVisible(true);
	    this.add(OP_Label);
		
	    PW_Label.setBounds(WFwidth/2-160,WFheight/3+90, 90,25);
	    PW_Label.setFont(new Font("",1,20));
	    PW_Label.setVisible(true);
	    this.add(PW_Label);
		
	    PW1_Label.setBounds(WFwidth/2-160,WFheight/3+120, 90,25);
	    PW1_Label.setFont(new Font("",1,18));
	    PW1_Label.setVisible(false);
	    this.add(PW1_Label);
		
	    MachineNumber_Text.setBounds(WFwidth/2-70,WFheight/3+30, 180,25);
	    MachineNumber_Text.setFont(new Font("",3,16));
	    MachineNumber_Text.setVisible(true);
	    this.add(MachineNumber_Text);
		
	    OP_Text.setBounds(WFwidth/2-70,WFheight/3+60, 180,25);
	    OP_Text.setFont(new Font("",3,16));
	    OP_Text.setVisible(true);
	    this.add(OP_Text);
		
	    PW_Text.setBounds(WFwidth/2-70,WFheight/3+90, 180,25);
	    PW_Text.setFont(new Font("",3,16));
	    PW_Text.setVisible(true);
	    this.add(PW_Text);
		
	    PW1_Text.setBounds(WFwidth/2-70,WFheight/3+120, 180,25);
	    PW1_Text.setFont(new Font("",3,16));
	    PW1_Text.setVisible(false);
	    this.add(PW1_Text);

	    Login_Button.setBounds(WFwidth/2-100-20, WFheight-80, 100, 30);
	    Login_Button.setFont(new Font("",1,20));
	    Login_Button.setVisible(true);
	    this.add(Login_Button);
	    
	    Exit_Button.setBounds(WFwidth/2+20, WFheight-80, 100, 30);
	    Exit_Button.setFont(new Font("",1,20));
	    Exit_Button.setVisible(true);
	    this.add(Exit_Button);
	    
	    Register_Button.setBounds(WFwidth-80-20, WFheight-110, 80, 20);
	    Register_Button.setFont(new Font("",1,10));
	    Register_Button.setVisible(true);
	    this.add(Register_Button);
	    
	    ChangePW_Button.setBounds(WFwidth-80-20, WFheight-80, 80, 20);
	    ChangePW_Button.setFont(new Font("",1,10));
	    ChangePW_Button.setVisible(true);
	    this.add(ChangePW_Button);
	    
	    this.Login_Button.addMouseListener(Login_Button_Click);
	    this.Register_Button.addMouseListener(Register_Button_Click);
	    this.ChangePW_Button.addMouseListener(ChangePW_Button_Click);
	    this.Exit_Button.addMouseListener(Exit_Button_Click);
	    this.PW_Text.addKeyListener(Enter_Click);
	    this.PW1_Text.addKeyListener(Enter_Click);
	    
	    
	    
	    Main.fB.dispose();
	    this.setVisible(true);
	    
	    timeRunning tR=new timeRunning();
	    tR.start();
	    
	    PW_Text.requestFocus();
	    if(Main.User.equals("")){
	    	OP_Text.requestFocus();
	    }else{
	    	OP_Text.setText(Main.User);
	    }
	    if(Main.clientName.equals("")){
	    	MachineNumber_Text.requestFocus();
	    }else{
	    	MachineNumber_Text.setText(Main.clientName);
	    }
	    
	}
	
	public void Login(){
		TimeCount=180;
    	Main.clientName=MachineNumber_Text.getText();
    	Main.User=OP_Text.getText();
    	Main.SaveConfig();
    	PW=PW_Text.getText();
    	if(Mode.equals("Login")){
        	AK_SQLreturn RS=new AK_SQLreturn();
        	RS=sql.GetDataFromTableWhereKey("LoginPassWord", "ADMINlist", "Name=\'"+Main.User+"\'");
        	if(RS.length<1){
        		JOptionPane.showMessageDialog(null,"账户不存在", "提示",JOptionPane.INFORMATION_MESSAGE);
        		return;
        	}
        	if(!PW.equals(RS.AK_Data[0])){
        		JOptionPane.showMessageDialog(null,"密码错误", "提示",JOptionPane.INFORMATION_MESSAGE);
        		return;
        	}else{
        		Main.ms=new MySocket();
        		Main.ms.equipmentName=Main.clientName;//TODO 站点号
        		flag=true;
        		TimeCount=0;
        		sql.Close();
        		Main.packing=new c_Packing();
        		dispose();
        	}
    	}else if(Mode.equals("Register")){
    		PW1=PW1_Text.getText();
    		if(sql.GetDataFromTableWhereKey("LoginPassWord", "ADMINlist", "Name=\'"+Main.User+"\'").length>0){
    			JOptionPane.showMessageDialog(null,"账户已存在", "提示",JOptionPane.INFORMATION_MESSAGE);
        		return;
    		}
    		if((PW.equals(PW1))&&(PW.length()>0)){
    			if(!sql.NewADMIN(Main.User, PW)){
    				JOptionPane.showMessageDialog(null,"注册失败", "提示",JOptionPane.INFORMATION_MESSAGE);
    				return;
    			}
    		}else{
    			JOptionPane.showMessageDialog(null,"密码验证错误或者位数太少，注册失败", "提示",JOptionPane.INFORMATION_MESSAGE);
    		}
    		JOptionPane.showMessageDialog(null,"注册成功", "提示",JOptionPane.INFORMATION_MESSAGE);
    		Mode="Login";
        	PW_Text.setText("");
        	PW1_Text.setText("");
        	PW1_Label.setVisible(false);
        	PW1_Text.setVisible(false);
    	}else{
    		PW1=PW1_Text.getText();
    		if(sql.GetDataFromTableWhereKey("LoginPassWord", "ADMINlist", "Name=\'"+Main.User+"\'").length<1){
    			JOptionPane.showMessageDialog(null,"账户不存在", "提示",JOptionPane.INFORMATION_MESSAGE);
        		return;
    		}
    		if((PW.equals(PW1))&&(PW.length()>0)){
    			if(!sql.ChangePWADMIN(Main.User, PW)){
    				JOptionPane.showMessageDialog(null,"密码修改失败", "提示",JOptionPane.INFORMATION_MESSAGE);
    				return;
    			}
    		}else{
    			JOptionPane.showMessageDialog(null,"重复密码不一致或者位数太少，密码修改失败", "提示",JOptionPane.INFORMATION_MESSAGE);
    		}
    		JOptionPane.showMessageDialog(null,"密码修改成功", "提示",JOptionPane.INFORMATION_MESSAGE);
    		Mode="Login";
        	PW_Text.setText("");
        	PW1_Text.setText("");
        	PW1_Label.setVisible(false);
        	PW1_Text.setVisible(false);
        	OP_Text.setEnabled(true);
    	}
	}
	
	MouseAdapter Login_Button_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	Login();
        }  
	};
	
	MouseAdapter Register_Button_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	Mode="Register";
        	PW1_Label.setVisible(true);
        	PW1_Text.setVisible(true);
        	TimeCount=180;
        }  
	};
	
	MouseAdapter Exit_Button_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	if(Mode.equals("Login")){
        		System.exit(0);
        	}else{
	        	Mode="Login";
	        	PW_Text.setText("");
	        	PW1_Text.setText("");
	        	PW1_Label.setVisible(false);
	        	PW1_Text.setVisible(false);
	        	OP_Text.setEnabled(true);
        	}
        	
        }  
	};
	
	MouseAdapter ChangePW_Button_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	TimeCount=180;
        	Main.User=OP_Text.getText();
        	PW=PW_Text.getText();
        	AK_SQLreturn RS1=new AK_SQLreturn();
        	RS1=sql.GetDataFromTableWhereKey("LoginPassWord", "ADMINlist", "Name=\'"+Main.User+"\'");
        	if(RS1.length<1){
        		JOptionPane.showMessageDialog(null,"账户不存在", "提示",JOptionPane.INFORMATION_MESSAGE);
        		return;
        	}
        	if(!PW.equals(RS1.AK_Data[0])){
        		JOptionPane.showMessageDialog(null,"密码错误", "提示",JOptionPane.INFORMATION_MESSAGE);
        		return;
        	}else{
        		Mode="ChangePW";
        		PW_Text.setText("");
        		PW1_Text.setText("");
            	PW1_Label.setVisible(true);
            	PW1_Text.setVisible(true);
            	OP_Text.setEnabled(false);
        	}
        }  
	};
	
	KeyListener Enter_Click=new KeyListener() { 
        public void keyPressed(KeyEvent e) {
        	if(e.getKeyChar() == KeyEvent.VK_ENTER){
        		Login();
        	}
        }

		@Override
		public void keyReleased(KeyEvent arg0) {
			// Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// Auto-generated method stub
			
		}  
	};
	
	class timeRunning extends Thread{
		long FQ=1000;
		public void run(){
			while(TimeCount>=0){
				TimeOver.setText("程序将在"+TimeCount+"秒后自动关闭");
				try {
					Thread.sleep(FQ);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				TimeCount=TimeCount-1;
			}
			if(!flag){
				System.exit(0);
			}
		}
	}
}
