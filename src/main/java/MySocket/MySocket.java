package MySocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import Main.v_MSG;

public class MySocket{
	
	static byte STX=0x02;
	static byte SOU=0x01;
	static byte ETX=0x03;
	Socket socket=null;
	//心跳定时发送时间(s)
    public static final int _sleepTime = 165 ;
    //业务包间隔发送时间(s) 1-N秒
    public static final int _intervalTime = 10 ;
    //socket连接超时时间(s)
    public static final int _connectTimeOut = 2 ;
    //socket 回复超时时间(s)
    public static final int _receiveTimeOut = 160 ;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
//    String hostName="192.168.1.198";  
    String hostName="10.130.1.32";//"192.168.1.198";  //TODO
    int port=58060;
    int timeCounter=0;
    static int reconnectCount=0;
    String header=new String("");
    String messageClass=new String("");
    String transactionID=new String("");
    String reply=new String("");
    String location=new String("");
    String routeID=new String("1");
    String routeName=new String("1");
    String equipmentID=new String("1");
    public String equipmentName=new String("S03-L01-Packing-M1");
    String zoneID=new String("1");
    String zonePos=new String("1");
    String zoneName=new String("1");
    String laneNo=new String("1");
    String controllerGuid=new String("");
    
    public  MySocket(){
		try{
				if(!Reconnet()){
					//TODO MSG
				}else{
					new CheckSocketHeartBeat();
				}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

    public boolean Reconnet(){
    	try{
    		Socket _s=socket;
    		socket=new Socket();
    		if((_s!=null)&&(!_s.isClosed())){
    			_s.close();
    		}
    		SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(hostName), port);
			socket.connect(socketAddress, _connectTimeOut * 1000);
            socket.setSoTimeout(_receiveTimeOut * 1000);	
            timeCounter=_sleepTime;
            timeCounter=3;
            System.out.print("Connect:"+hostName+":"+port+"\n");
            reconnectCount+=1;
            if(reconnectCount>5) {
            	v_MSG.show("重连大于5次！自动退出，请重新打开软件");
            	System.exit(0);
            }
            if(Main.Main.first) {
            	Main.Main.first=false;
            }else {
            	Main.Main.packing.view.his.append("\n"+GetTimestamp()+"\tConnect:"+hostName+":"+port);
            }
            return true;
		}catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,hostName+" : "+port+"\n"+e.getMessage());
			return false;
		}
    }
	
    private boolean HeartBeat(){
		if(!socket.isClosed()){
			try {
				InputStream in = socket.getInputStream();
		        OutputStream os = socket.getOutputStream();
		        os.write(STX);//HEAD
                os.write("8".getBytes());
                os.write(SOU);//CONTENT
                os.write("PING_REQ".getBytes());
                os.write(ETX); //Delimit...
                System.out.print("PING\n");
                os.flush();
                
                byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                if (recvLen == -1){
                    return false;
                }
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
                if(!recvMsg.equals("PING_RSP")){
                	return false;
                }else{
                	timeCounter=_sleepTime;
                    System.out.print("PONG\n");
                	return true;
                }
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

    private class CheckSocketHeartBeat extends Thread{   	
    	CheckSocketHeartBeat(){
    		this.start();
    	}
    	public void run() {
    		try{
    			while(!socket.isClosed()){
    				timeCounter-=1;
    				if(timeCounter<=0){
    					if(!HeartBeat()){
    						Reconnet();
    					}
    				}
    				Thread.sleep(1000);
//                    System.out.print(""+timeCounter+"\n");
    			}
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public m_booleanPstr CallPCBIDrequest(String barcode,String pcbSide){
		m_booleanPstr Result =new m_booleanPstr();
		m_intPstr result=new m_intPstr();
    	try{
    		while(this.timeCounter<=1){
    			Thread.sleep(1000);
    		}
    		result.intCount=2;
    		while(result.intCount==2){
    			result=PCBIDrequest(barcode,pcbSide);
    			if(result.intCount==2){
	    			Socket _s=socket;
					_s.close();
					socket=new Socket();
					Reconnet();
    			}
    		}
			Result.booleanCount=(result.intCount==0);
    		Result.strCount=result.strCount;
    		return Result;
    	}catch(Exception e){
			e.printStackTrace();
			Result.booleanCount=false;
			Result.strCount=e.getMessage();
    		return Result;
    	}
    }

    private m_intPstr PCBIDrequest(String barcode,String pcbSide){
    	messageClass="501";
    	reply="1";
    	m_intPstr result=new m_intPstr();
    	result.strCount="NONE";
    	String msg=new String();
    	String body=new String();
    	this.timeCounter=_sleepTime;
    	body="<body>\n"
    		+"<pcb barcode=\""+barcode+"\" modelCode=\"\" serialNo=\""+barcode+"\" pcbSide=\""+pcbSide+"\" scannerMountSide=\"T\"/>"
    		+"\n</body>\n";
    	msg="<message>\n"+GetHeaderData()+body+"</message>\n";
    	String len=new String();
    	len=""+msg.length();
    	OutputStream os;
    	InputStream in;
		try {
			if(!socket.isClosed()){
				in = socket.getInputStream();
				os = socket.getOutputStream();
		        os.write(STX);//HEAD
		        os.write(len.getBytes());
		        os.write(SOU);//CONTENT
		        os.write(msg.getBytes());
		        os.write(ETX); //Delimit...
		        System.out.print("PCBIDrequest:\n");
		        System.out.print(msg+"\n");
		        os.flush();
		        
		        byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                
                if (recvLen == -1){
                    result.intCount= 2;
                    return result;
                }
                
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
                String transactionIDcount=GetElement(recvMsg,"transactionID");
                if(transactionIDcount.equals(transactionID)){
	                String errorCode=GetElement(recvMsg,"errorCode");
	                String errorText=GetElement(recvMsg,"errorText");
	                if(errorCode.equals("0")){
	                	result.intCount=0;
	                	return result;
	                }else{
	                	System.out.println(recvMsg);
	                	result.strCount="错误代码："+errorCode+"   "+errorText;
	                }
                }else{
                    result.intCount=2;
                    return result;
                }
                result.intCount=1;
                return result;
			}else{
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.intCount=2;
        	result.strCount=e.getMessage();
			return result;
		}
    }
    
    /**
     * 包装预检，通过后做SN入箱操作
     * @param BoxNo
     * @param barcode
     * @param label
     * @param pcbSide
     * @return
     */
    public m_booleanPstr CallPCB5018(String boxNo,String barcode,String label,String pcbSide){
		m_booleanPstr Result =new m_booleanPstr();
		m_intPstr result=new m_intPstr();
    	try{
    		while(this.timeCounter<=1){
    			Thread.sleep(1000);
    		}
    		result.intCount=2;
    		while(result.intCount==2){
    			result=PCB5018(boxNo,barcode,label,pcbSide);
    			if(result.intCount==2){
	    			Socket _s=socket;
					_s.close();
					socket=new Socket();
					Reconnet();
    			}
    		}
			Result.booleanCount=(result.intCount==0);
    		Result.strCount=result.strCount;
    		return Result;
    	}catch(Exception e){
			e.printStackTrace();
			Result.booleanCount=false;
			Result.strCount=e.getMessage();
    		return Result;
    	}
    }

    /**
     * 包装预检，通过后做SN入箱操作
     * @param BoxNo
     * @param barcode
     * @param label
     * @param pcbSide
     * @return
     */
    private m_intPstr PCB5018(String BoxNo,String barcode,String label,String pcbSide){
    	messageClass="5018";
    	reply="1";
    	m_intPstr result=new m_intPstr();
    	result.strCount="NONE";
    	String msg=new String();
    	String body=new String();
    	this.timeCounter=_sleepTime;
    	body="<body>\n"
    			+"<packageInfo>\n"
    				+"<sn>"+BoxNo+"</sn>\n"
    			+"</packageInfo>\n"
    			+"<pcb barcode=\""+barcode+"\" label=\""+label+"\" modelCode=\"\" serialNo=\""+barcode+"\" pcbSide=\""+pcbSide+"\" scannerMountSide=\"T\"/>"
    		+"\n</body>\n";
    	msg="<message>\n"+GetHeaderData()+body+"</message>\n";
    	String len=new String();
    	len=""+msg.length();
    	OutputStream os;
    	InputStream in;
		try {
			if(!socket.isClosed()){
				in = socket.getInputStream();
				os = socket.getOutputStream();
		        os.write(STX);//HEAD
		        os.write(len.getBytes());
		        os.write(SOU);//CONTENT
		        os.write(msg.getBytes());
		        os.write(ETX); //Delimit...
		        System.out.print("PCB5018:\n");
		        System.out.print(msg+"\n");
		        os.flush();
		        
		        byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                
                if (recvLen == -1){
                    result.intCount= 2;
                    return result;
                }
                
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
                String transactionIDcount=GetElement(recvMsg,"transactionID");
                if(transactionIDcount.equals(transactionID)){
	                String errorCode=GetElement(recvMsg,"errorCode");
	                String errorText=GetElement(recvMsg,"errorText");
	                if(errorCode.equals("0")){
	                	result.intCount=0;
	                	barcode=GetElement(recvMsg,"result barcode");
	                	result.strCount=barcode;
	                	return result;
	                }else{
	                	System.out.println(recvMsg);
	                	result.strCount="错误代码："+errorCode+"   "+errorText;
	                }
                }else{
                    result.intCount=2;
                    return result;
                }
                result.intCount=1;
                return result;
			}else{
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.intCount=2;
        	result.strCount=e.getMessage();
			return result;
		}
    }
    
    /**
     * 包装箱初始化
     * @param modleCode
     * @param BIN
     * @return
     */
    public m_booleanPstr Callpackge10009(String modleCode,String BIN){
		m_booleanPstr Result =new m_booleanPstr();
		m_intPstr result=new m_intPstr();
    	try{
    		while(this.timeCounter<=1){
    			Thread.sleep(1000);
    		}
    		result.intCount=2;
    		while(result.intCount==2){
    			result=packge10009(modleCode,BIN);
    			if(result.intCount==2){
	    			Socket _s=socket;
					_s.close();
					socket=new Socket();
					Reconnet();
    			}
    		}
			Result.booleanCount=(result.intCount==0);
    		Result.strCount=result.strCount;
    		Result.strCount1=result.strCount1;
    		return Result;
    	}catch(Exception e){
			e.printStackTrace();
			Result.booleanCount=false;
			Result.strCount=e.getMessage();
    		return Result;
    	}
    }

    /**
     * 包装箱初始化
     * @param modleCode
     * @param BIN
     * @return
     */
    private m_intPstr packge10009(String modleCode,String BIN){
    	messageClass="10009";
    	reply="1";
    	m_intPstr result=new m_intPstr();
    	result.strCount="NONE";
    	String msg=new String();
    	String body=new String();
    	this.timeCounter=_sleepTime;
    	body="<body>\n"
    			+"<product name=\""+modleCode+"\" bin=\""+BIN+"\" />"
    		+"\n</body>\n";
    	msg="<message>\n"+GetHeaderData()+body+"</message>\n";
    	String len=new String();
    	len=""+msg.length();
    	OutputStream os;
    	InputStream in;
		try {
			if(!socket.isClosed()){
				in = socket.getInputStream();
				os = socket.getOutputStream();
		        os.write(STX);//HEAD
		        os.write(len.getBytes());
		        os.write(SOU);//CONTENT
		        os.write(msg.getBytes());
		        os.write(ETX); //Delimit...
		        System.out.print("packge10009:\n");
		        System.out.print(msg+"\n");
		        os.flush();
		        
		        byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                
                if (recvLen == -1){
                    result.intCount= 2;
                    return result;
                }
                
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
                String transactionIDcount=GetElement(recvMsg,"transactionID");
                if(transactionIDcount.equals(transactionID)){
	                String errorCode=GetElement(recvMsg,"errorCode");
	                String errorText=GetElement(recvMsg,"errorText");
	                if(errorCode.equals("0")){
	                	result.intCount=0;
	                	result.strCount=GetElement(recvMsg,"packageContainer number");
	                	result.strCount1=GetElement(recvMsg,"capacity");
	                	return result;
	                }else{
	                	System.out.println(recvMsg);
	                	result.strCount="错误代码："+errorCode+"   "+errorText;
	                }
                }else{
                    result.intCount=2;
                    return result;
                }
                result.intCount=1;
                return result;
			}else{
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.intCount=2;
        	result.strCount=e.getMessage();
			return result;
		}
    }
    
    /**
     * 把产品从包装箱中做取出操作
     * @param barcode
     * @param label
     * @param boxNo
     * @param operator
     * @return
     */
    public m_booleanPstr Callpackge10010(String barcode,String label,String boxNo,String operator,String side){
		m_booleanPstr Result =new m_booleanPstr();
		m_intPstr result=new m_intPstr();
		operator="000001";
    	try{
    		while(this.timeCounter<=1){
    			Thread.sleep(1000);
    		}
    		result.intCount=2;
    		while(result.intCount==2){
    			result=packge10010(barcode,label,boxNo,operator,side);
    			if(result.intCount==2){
	    			Socket _s=socket;
					_s.close();
					socket=new Socket();
					Reconnet();
    			}
    		}
			Result.booleanCount=(result.intCount==0);
    		Result.strCount=result.strCount;
    		Result.strCount1=result.strCount1;
    		return Result;
    	}catch(Exception e){
			e.printStackTrace();
			Result.booleanCount=false;
			Result.strCount=e.getMessage();
    		return Result;
    	}
    }

    /**
     * 把产品从包装箱中做取出操作
     * @param barcode
     * @param label
     * @param boxNo
     * @param operator
     * @return
     */
    private m_intPstr packge10010(String barcode,String label,String boxNo,String operator,String side){
    	messageClass="10010";
    	reply="1";
		operator="000001";
    	m_intPstr result=new m_intPstr();
    	result.strCount="NONE";
    	String msg=new String();
    	String body=new String();
    	this.timeCounter=_sleepTime;
    	body="<body>\n"
    			+"<product number=\""+barcode+"\" label=\""+label+"\" side=\""+side+"\" />\n"
                +"<packageContainer number=\""+boxNo+"\"  type=\"0\"  operator=\""+operator+"\"  />\n"
    		+"\n</body>\n";
    	msg="<message>\n"+GetHeaderData()+body+"</message>\n";
    	String len=new String();
    	len=""+msg.length();
    	OutputStream os;
    	InputStream in;
		try {
			if(!socket.isClosed()){
				in = socket.getInputStream();
				os = socket.getOutputStream();
		        os.write(STX);//HEAD
		        os.write(len.getBytes());
		        os.write(SOU);//CONTENT
		        os.write(msg.getBytes());
		        os.write(ETX); //Delimit...
		        System.out.print("packge10010:\n");
		        System.out.print(msg+"\n");
		        os.flush();
		        
		        byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                
                if (recvLen == -1){
                    result.intCount= 2;
                    return result;
                }
                
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
                String transactionIDcount=GetElement(recvMsg,"transactionID");
                if(transactionIDcount.equals(transactionID)){
	                String errorCode=GetElement(recvMsg,"errorCode");
	                String errorText=GetElement(recvMsg,"errorText");
	                if(errorCode.equals("0")){
	                	result.intCount=0;
	                	return result;
	                }else{
	                	System.out.println(recvMsg);
	                	result.strCount="错误代码："+errorCode+"   "+errorText;
	                }
                }else{
                    result.intCount=2;
                    return result;
                }
                result.intCount=1;
                return result;
			}else{
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.intCount=2;
        	result.strCount=e.getMessage();
			return result;
		}
    }
    

    public m_booleanPstr Callpackge10011(String barcode,String label,String boxNo,String Plabel){
		m_booleanPstr Result =new m_booleanPstr();
		m_intPstr result=new m_intPstr();
    	try{
    		while(this.timeCounter<=1){
    			Thread.sleep(1000);
    		}
    		result.intCount=2;
    		while(result.intCount==2){
    			result=packge10011(barcode,label,boxNo,Plabel);
    			if(result.intCount==2){
	    			Socket _s=socket;
					_s.close();
					socket=new Socket();
					Reconnet();
    			}
    		}
			Result.booleanCount=(result.intCount==0);
    		Result.strCount=result.strCount;
    		Result.strCount1=result.strCount1;
    		Result.strCount2=result.strCount2;
    		return Result;
    	}catch(Exception e){
			e.printStackTrace();
			Result.booleanCount=false;
			Result.strCount=e.getMessage();
    		return Result;
    	}
    }

    
    private m_intPstr packge10011(String barcode,String label,String boxNo,String Plabel){
    	messageClass="10011";
    	reply="1";
    	m_intPstr result=new m_intPstr();
    	result.strCount="NONE";
    	String msg=new String();
    	String body=new String();
    	this.timeCounter=_sleepTime;
    	body="<body>\n"
    			+"<product number=\""+barcode+"\" label=\""+label+"\" />\n"
                +"<packageContainer number=\""+boxNo+"\"  label=\""+Plabel+"\"  />\n"
    		+"\n</body>\n";
    	msg="<message>\n"+GetHeaderData()+body+"</message>\n";
    	String len=new String();
    	len=""+msg.length();
    	OutputStream os;
    	InputStream in;
		try {
			if(!socket.isClosed()){
				in = socket.getInputStream();
				os = socket.getOutputStream();
		        os.write(STX);//HEAD
		        os.write(len.getBytes());
		        os.write(SOU);//CONTENT
		        os.write(msg.getBytes());
		        os.write(ETX); //Delimit...
		        System.out.print("packge10011:\n");
		        System.out.print(msg+"\n");
		        os.flush();
		        
		        byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                
                if (recvLen == -1){
                    result.intCount= 2;
                    return result;
                }
                
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
                String transactionIDcount=GetElement(recvMsg,"transactionID");
                if(transactionIDcount.equals(transactionID)){
	                String errorCode=GetElement(recvMsg,"errorCode");
	                String errorText=GetElement(recvMsg,"errorText");
	                if(errorCode.equals("0")){
	                	result.intCount=0;
	                	result.strCount=GetElement(recvMsg,"packageContainer number");
	                	result.strCount1=GetElement(recvMsg,"quantity");
	                	result.strCount2=GetElement(recvMsg,"capacity");
	                	return result;
	                }else{
	                	System.out.println(recvMsg);
	                	result.strCount="错误代码："+errorCode+"   "+errorText;
	                }
                }else{
                    result.intCount=2;
                    return result;
                }
                result.intCount=1;
                return result;
			}else{
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.intCount=2;
        	result.strCount=e.getMessage();
			return result;
		}
    }
    
    /**
     * 
     * @param barcode
     * @param label
     * @param packge
     * @param packgeLabel
     * @param state:0 = 装箱中,state:1 = 装完,state:2 = 封箱,state:3 = 报废
     * @param operator
     * @return
     */
    public m_booleanPstr Callpackge10012(String barcode,String label,String packge,String packgeLabel,
    		String state,String operator){
				m_booleanPstr Result =new m_booleanPstr();
				m_intPstr result=new m_intPstr();
				operator="000001";
    	try{
    		while(this.timeCounter<=1){
    			Thread.sleep(1000);
    		}
    		result.intCount=2;
    		while(result.intCount==2){
    			result=packge10012(barcode,label,packge,packgeLabel,state,operator);
    			if(result.intCount==2){
	    			Socket _s=socket;
					_s.close();
					socket=new Socket();
					Reconnet();
    			}
    		}
			Result.booleanCount=(result.intCount==0);
    		Result.strCount=result.strCount;
    		Result.strCount1=result.strCount1;
    		Result.strCount2=result.strCount2;
    		Result.strCount3=result.strCount3;
    		return Result;
    	}catch(Exception e){
			e.printStackTrace();
			Result.booleanCount=false;
			Result.strCount=e.getMessage();
    		Result.strCount1=result.strCount1;
    		Result.strCount2=result.strCount2;
    		Result.strCount3=result.strCount3;
    		return Result;
    	}
    }

    /**
     * 
     * @param barcode
     * @param label
     * @param packge
     * @param packgeLabel
     * @param state:0 = 装箱中,state:1 = 装完,state:2 = 封箱,state:3 = 报废
     * @param operator
     * @return
     */
    private m_intPstr packge10012(String barcode,String label,String packge,String packgeLabel,
    		String state,String operator){
    	operator="000001";
    	messageClass="10012";
    	reply="1";
    	m_intPstr result=new m_intPstr();
    	result.strCount="NONE";
    	String msg=new String();
    	String body=new String();
    	this.timeCounter=_sleepTime;
    	body="<body>\n"
    			+"<product number=\""+barcode+"\" label=\""+label+"\" />\n"
                +"<packageContainer number=\""+packge+"\" label=\""+packgeLabel+"\" state=\""+state+"\" operator=\""+operator+"\"  />"
    		+"\n</body>\n";
    	msg="<message>\n"+GetHeaderData()+body+"</message>\n";
    	try {
			msg=new String(msg.getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	String len=new String();
    	len=""+msg.length();
    	OutputStream os;
    	InputStream in;
		try {
			if(!socket.isClosed()){
				in = socket.getInputStream();
				os = socket.getOutputStream();
		        os.write(STX);//HEAD
		        os.write(len.getBytes());
		        os.write(SOU);//CONTENT
		        os.write(msg.getBytes());
		        os.write(ETX); //Delimit...
		        String strTime=sdf.format(System.currentTimeMillis());
		        System.out.println(strTime);
		        System.out.print("packge10012:\n");
		        System.out.print(msg+"\n");
		        os.flush();
		        
		        byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                
                if (recvLen == -1){
                    result.intCount= 2;
                    return result;
                }
                
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
		        strTime=sdf.format(System.currentTimeMillis());
		        System.out.println(strTime);
                System.out.println(recvMsg);
                String transactionIDcount=GetElement(recvMsg,"transactionID");
                if(transactionIDcount.equals(transactionID)){
	                String errorCode=GetElement(recvMsg,"errorCode");
	                String errorText=GetElement(recvMsg,"errorText");
	                if(errorCode.equals("0")){
	                	result.intCount=0;
	                	result.strCount=GetElement(recvMsg,"packageContainer number");
	                	result.strCount1=GetElement(recvMsg,"quantity");
	                	result.strCount2=GetElement(recvMsg,"capacity");
	                	return result;
	                }else{
//	                	System.out.println(recvMsg);
	                	result.strCount="错误代码："+errorCode+"   "+errorText;
	                	if(errorText.startsWith("MSGE1904")){
		                	result.strCount1=GetElement(recvMsg,"packageContainer number");
		                	result.strCount2=GetElement(recvMsg,"quantity");
		                	result.strCount3=GetElement(recvMsg,"capacity");
	                	}
	                }
                }else{
                    result.intCount=2;
                    return result;
                }
                result.intCount=1;
                return result;
			}else{
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.intCount=2;
        	result.strCount=e.getMessage();
			return result;
		}
    }
    
    public m_booleanPstr CallTraceData(String barcode,String pcbSide,int counter){
		m_booleanPstr Result =new m_booleanPstr();
		m_intPstr result=new m_intPstr();
    	try{
    		while(this.timeCounter<=1){
    			Thread.sleep(1000);
    		}
    		result.intCount=2;
    		while(result.intCount==2){
    			result=TraceData(barcode,pcbSide,counter);
    			if(result.intCount==2){
	    			Socket _s=socket;
					_s.close();
					socket=new Socket();
					Reconnet();
    			}
    		}
			Result.booleanCount=(result.intCount==0);
    		Result.strCount=result.strCount;
    		return Result;
    	}catch(Exception e){
			e.printStackTrace();
			Result.booleanCount=false;
			Result.strCount=e.getMessage();
    		return Result;
    	}
    }

    private m_intPstr TraceData(String barcode,String pcbSide,int counter){
    	messageClass="550";
    	reply="1";
    	m_intPstr result=new m_intPstr();
    	result.strCount="NONE";
    	String msg=new String();
    	String timestamp=new String();
    	timestamp=GetTimestamp();
    	String body=new String();
    	this.timeCounter=_sleepTime;
    	body="<body>\n"+
    			"<panel timestamp=\""+timestamp+"\" productSide=\""+pcbSide+
    			"\" repairType=\"0\" productName=\"SVW378 1LED\" pcbID=\""+
    			barcode+"\" state=\"0\">\n";
		    	for(int i=0;i<counter;i++){
		    		body=body+"<subPanel state=\"0\" pos=\""+i+"\" />\n";
		    	}
		    	body=body+
				"\n</panel>"+
    		"\n</body>\n";
		msg="<message>\n"+GetHeaderData()+body+"</message>\n";
    	String len=new String();
    	len=""+msg.length();
    	OutputStream os;
    	InputStream in;
		try {
			if(!socket.isClosed()){
				in = socket.getInputStream();
				os = socket.getOutputStream();
		        os.write(STX);//HEAD
		        os.write(len.getBytes());
		        os.write(SOU);//CONTENT
		        os.write(msg.getBytes());
		        os.write(ETX); //Delimit...
		        System.out.print("TraceData:\n");
		        System.out.print(msg+"\n");
		        os.flush();
		        
		        byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                
                if (recvLen == -1){
                    result.intCount= 2;
                    return result;
                }
                
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
                String transactionIDcount=GetElement(recvMsg,"transactionID");
                if(transactionIDcount.equals(transactionID)){
	                String errorCode=GetElement(recvMsg,"errorCode");
	                String errorText=GetElement(recvMsg,"errorText");
	                if(errorCode.equals("0")){
	                	result.intCount=0;
	                	return result;
	                }else{
	                	System.out.println("TraceData Fail:"+errorCode+"   "+errorText);
	                	result.strCount="错误代码："+errorCode+"   "+errorText;
	                }
                }else{
                    result.intCount=2;
                    return result;
                }
                result.intCount=1;
                return result;
			}else{
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.intCount=2;
        	result.strCount=e.getMessage();
			return result;
		}
    }
    
    public m_booleanPstr CallChangeOver(){
		m_booleanPstr Result =new m_booleanPstr();
		m_intPstr result=new m_intPstr();
    	try{
    		while(this.timeCounter<=1){
    			Thread.sleep(1000);
    		}
    		result.intCount=2;
    		while(result.intCount==2){
    			result=ChangeOver();
    			if(result.intCount==2){
	    			Socket _s=socket;
					_s.close();
					Reconnet();
    			}
    		}
			Result.booleanCount=(result.intCount==0);
    		Result.strCount=result.strCount;
    		return Result;
    	}catch(Exception e){
			e.printStackTrace();
			Result.booleanCount=false;
			Result.strCount=e.getMessage();
    		return Result;
    	}
    }

    private m_intPstr ChangeOver(){
    	messageClass="580";
    	reply="1";
    	m_intPstr result=new m_intPstr();
    	result.strCount="NONE";
    	String msg=new String();
//    	String timestamp=new String();
//    	timestamp=GetTimestamp();
    	String body=new String();
    	this.timeCounter=_sleepTime;
    	body="<body>\n"+
//    			VMain.Bom+//TODO BOM
    		"\n</body>\n";
    	msg="<message>\n"+GetHeaderData()+body+"</message>\n";
    	String len=new String();
    	len=""+msg.length();
    	OutputStream os;
    	InputStream in;
		try {
			if(!socket.isClosed()){
				in = socket.getInputStream();
				os = socket.getOutputStream();
		        os.write(STX);//HEAD
		        os.write(len.getBytes());
		        os.write(SOU);//CONTENT
		        os.write(msg.getBytes());
		        os.write(ETX); //Delimit...
		        System.out.print("TraceData:\n");
		        System.out.print(msg+"\n");
		        os.flush();
		        
		        byte[] buf = new byte[9999];
                int recvLen = in.read(buf);
                
                if (recvLen == -1){
                    result.intCount= 2;
                    return result;
                }
                
                String recvMsg = new String(buf, 0, recvLen, "UTF-8");
//                String recvMsg = new String(buf);
                recvMsg=analyseMSG(buf);
                String transactionIDcount=GetElement(recvMsg,"transactionID");
                if(transactionIDcount.equals(transactionID)){
	                String errorCode=GetElement(recvMsg,"errorCode");
	                String errorText=GetElement(recvMsg,"errorText");
	                if(errorCode.equals("0")){
	                	result.intCount=0;
	                	return result;
	                }else{
	                	System.out.println("ChangeOver Fail:"+errorCode+"   "+errorText);
	                	result.strCount="错误代码："+errorCode+"   "+errorText;
	                }
                }else{
                    result.intCount=2;
                    return result;
                }
                result.intCount=1;
                return result;
			}else{
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.intCount=2;
        	result.strCount=e.getMessage();
			return result;
		}
    }
    
    public String GetLocationData(){
    	location="<location routeID=\""+routeID+"\" routeName=\""+routeName+"\" equipmentID=\""+equipmentID+"\""
    			+" equipmentName=\""+equipmentName+"\" zoneID=\""+zoneID+"\" zonePos=\""+zonePos+"\""
    	    	+" zoneName=\""+zoneName+"\" laneNo=\""+laneNo+"\" controllerGuid=\""+controllerGuid+"\""
    			+" />\n";
    	return location;
    }
    
    public String GetHeaderData(){
    	header="<header messageClass=\""+messageClass+"\" transactionID=\""+GetTransactionIDData()+"\" reply=\""+reply+"\">\n"
    			+GetLocationData()
    			+"</header>\n";
    	return header;
    }

    public String GetTransactionIDData(){
    	SimpleDateFormat t = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    	transactionID=""+equipmentName+"_"+t.format(new Date());
    	return transactionID;
    }
    
    public String GetTimestamp(){
    	//"2017-12-10T15:52:10+08:00";
    	String timestamp=new String();
    	SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd");
    	SimpleDateFormat t1 = new SimpleDateFormat("HH:mm:ss");
    	timestamp=""+t.format(new Date())+"T"+t1.format(new Date())+"+08:00";
    	return timestamp;
    }

    public static String GetElement(String Msg,String ElementName){
    	String ElementValue=new String();
    	int beginIndex=0,endIndex=0;
    	beginIndex=Msg.lastIndexOf(ElementName+"=\"")+ElementName.length()+2;
    	endIndex=Msg.indexOf("\"", beginIndex);
//    	System.out.print(Msg+"\n"+ElementName+"\n"+beginIndex+"\n"+endIndex+"\n");
    	ElementValue=Msg.substring(beginIndex, endIndex);
    	return ElementValue;
    }
    
    public static String analyseMSG(String MSG){
		String str;
		str=new String("Error");
		byte[] msg=MSG.getBytes();
		int intSOU=0,intETX=0;
		try{
			for(int i=0;i<msg.length;i++){
				if(msg[i]==SOU){
					intSOU=i;
				}else if(msg[i]==ETX){
					intETX=i;
					str=new String(msg,intSOU+1,(intETX-intSOU-1),"UTF-8");
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return str;
	}
    
    public static String analyseMSG(byte [] MSG){
		String str,len;
		str=new String("Error");
		len=new String();
		byte[] msg=MSG;
		int intSTX=0,intSOU=0,intETX=0;
		try{
			for(int i=0;i<msg.length;i++){
				if(msg[i]==STX){
					intSTX=i;
				}else if(msg[i]==SOU){
					intSOU=i;
					len=new String(msg,intSTX+1,(intSOU-intSTX-1),"UTF-8");
				}else if(msg[i]==ETX){
					intETX=i;
					if(len.equals(""+(intETX-intSOU-1))){
						str=new String(msg,intSOU+1,(intETX-intSOU-1),"UTF-8");
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return str;
	}
}
