package MySocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPclient extends Thread{

	static Socket s01=null;
	static InputStream in=null;
	
	public TCPclient(String Sever,int Port){
		try {
//			s01=new Socket("10.130.0.42",9004);
			s01=new Socket(Sever,Port);
			in = s01.getInputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run() {  
        try {  
           while(true){  
        	   byte[] buf = new byte[9999];
               int recvLen = in.read(buf);
               if (recvLen == -1){
            	   
               }else{
            	   String recvMsg = new String(buf, 0, recvLen, "UTF-8");
            	   recvMsg=recvMsg.trim();
               }
           }  
 
        } catch (IOException e) {  
           e.printStackTrace();  
        }  
     }  
}
