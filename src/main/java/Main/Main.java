package Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import MySocket.MySocket;
import Packing.*;

/* 
 * 10.130.1.15/root
 * led-123|
 * */

public class Main {

	public static String clientName=new String("none");
	public static String IP=new String("//10.130.1.15");
	public static String DbName=new String("Smes");
	public static String OP=new String("root");
	public static String PW=new String("led-123@");
	public static String BoxNumber=new String("新包装箱，请扫描产品");
	public static int Quantity=0;

	public static String User=new String("Akite");
	
	public static f_Busy fB;
//	static String ConfigTextPath=new String("C://Program Files//SMES_Packing_Config.txt");
	static String ConfigTextPath=new String("./SMES_Packing_Config.txt");
	public static c_Packing packing;
	public static f_Welcome fW;
	public static MySocket ms;
	public static boolean first=false;
	
	public static void main(String[] args) {
		System.out.print("start!!!!!");
		first=true;
		ReadConfig();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run()
			{
				SaveConfig();
			}
		}));
		fB=new f_Busy();
		fW=new f_Welcome();//登录成功后触发包装主业务c_Packing packing=new c_Packing();
	}
	
	public static void ReadConfig(){
		StringBuffer sb= new StringBuffer("");
	    FileReader reader=null;
		try {
			reader = new FileReader(ConfigTextPath);
		} catch (FileNotFoundException e) {
			NewStation();
			try {
				reader = new FileReader(ConfigTextPath);
			} catch (FileNotFoundException e1) {
				v_MSG.show(ConfigTextPath+"\n创建失败，请手动创建后重新开启", "提示", false);
				System.exit(0);
			}
		}
	    BufferedReader br = new BufferedReader(reader);
	    String str = null;
	    String count=null;
        try {
			while((str = br.readLine()) != null) {
			      sb.append(str+"/n");
			}
	        br.close();
	        reader.close();
	        str=sb.toString();
	        count="clientName";
	        clientName=str.substring(str.indexOf(count+"=")+count.length()+1, str.indexOf("|"+count));
	        count="IP";
	        IP=str.substring(str.indexOf(count+"=")+count.length()+1, str.indexOf("|"+count));
	        count="DbName";
	        DbName=str.substring(str.indexOf(count+"=")+count.length()+1, str.indexOf("|"+count));
	        count="OP";
	        OP=str.substring(str.indexOf(count+"=")+count.length()+1, str.indexOf("|"+count));
	        count="User";
	        User=str.substring(str.indexOf(count+"=")+count.length()+1, str.indexOf("|"+count));
	        count="PW";
	        PW=str.substring(str.indexOf(count+"=")+count.length()+1, str.indexOf("|"+count));
	        count="BoxNumber";
	        BoxNumber=str.substring(str.indexOf(count+"=")+count.length()+1, str.indexOf("|"+count));
	        count="Quantity";
	        Quantity=Integer.parseInt(str.substring(str.indexOf(count+"=")+count.length()+1, str.indexOf("|"+count)));
			
		} catch (IOException e) {
			v_MSG.show(e.getMessage(), "提示",false);
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void NewStation(){
		String str=null;
		String count=null;
		count ="";
		str="clientName="+clientName+"|clientName\r\n";
		count = "IP="+IP+"|IP";
		str=str+count+"\r\n";
		count = "DbName="+DbName+"|DbName";
		str=str+count+"\r\n";
		count = "OP="+OP+"|OP";
		str=str+count+"\r\n";
		count = "User="+User+"|User";
		str=str+count+"\r\n";
		count = "PW="+PW+"|PW";
		str=str+count+"\r\n";
		count = "BoxNumber="+BoxNumber+"|BoxNumber";
		str=str+count+"\r\n";
		count = "Quantity="+Quantity+"|Quantity";
		str=str+count+"\r\n";
		
		FileWriter writer;
		try {
			writer = new FileWriter(ConfigTextPath);
	        BufferedWriter bw = new BufferedWriter(writer);
	        bw.write(str);
	       
	        bw.close();
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			v_MSG.show(e.getMessage(), "提示",false);
		}

	}
	
	public static void SaveConfig(){
		String str=null;
		str="clientName="+clientName+"|clientName\r\n";
		str=str+"IP="+IP+"|IP\r\n";
		str=str+"DbName="+DbName+"|DbName\r\n";
		str=str+"OP="+OP+"|OP\r\n";
		str=str+"User="+User+"|User\r\n";
		str=str+"PW="+PW+"|PW\r\n";
		str=str+"BoxNumber="+BoxNumber+"|BoxNumber\r\n";
		str=str+"Quantity="+Quantity+"|Quantity\r\n";
		
		FileWriter writer;
		try {
			writer = new FileWriter(ConfigTextPath);
	        BufferedWriter bw = new BufferedWriter(writer);
	        bw.write(str);
	       
	        bw.close();
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			v_MSG.show(e.getMessage(), "提示",false);
		}
	}

}
