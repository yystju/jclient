package Packing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import Main.v_MSG;
import MySocket.m_booleanPstr;
import PartSet.c_PartSet;
import Product.c_Product;
import SQLserver.AK_SQLreturn;
import SQLserver.ResultSetAndTabel;
import SQLserver.c_SQLserver;

public class c_Packing {
	public v_Packing view;
	public m_Packing model;
	public c_SQLserver sql = new c_SQLserver();
	c_PartSet partSet;
	String Layer=new String("2");

	ExecutorService pool=Executors.newSingleThreadExecutor();

	boolean lock=false;
	public c_Packing(){
		Main.Main.fB.setVisible(true);
		model=new m_Packing();
//		sql.model.IP=Main.Main.IP;
//		sql.model.DbName=Main.Main.DbName;
//		sql.model.OP=Main.Main.OP;
//		sql.model.PW=Main.Main.PW;
//		if(!sql.Connect()){
//			System.exit(0);
//		}
		model.packingBox.model.BoxNumber=Main.Main.BoxNumber;
		model.packingBox.model.qtyPack=Main.Main.Quantity;
		view=new v_Packing();
		Main.Main.fB.dispose();
		refresh();
		view.fieldSN.addKeyListener(SN_Enter_Click);
		view.buttonPart.addMouseListener(ButtonPart_Click);
		view.buttonPacking.addMouseListener(ButtonPacking_Click);
		view.buttonUnpack.addMouseListener(ButtonUnpack_Click);
		view.buttonCancelBox.addMouseListener(ButtonCancelBox_Click);
		view.buttonOpenBox.addMouseListener(ButtonOpenBox_Click);
		view.buttonHelp.addMouseListener(ButtonHelp_Click);
		view.buttonHistory.addMouseListener(ButtonHistory_Click);
		
		
		
	}
	
	 public String GetTimestamp(){
	    	//"2017-12-10T15:52:10+08:00";
	    	String timestamp=new String();
	    	SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd");
	    	SimpleDateFormat t1 = new SimpleDateFormat("HH:mm:ss");
	    	timestamp=""+t.format(new Date())+"T"+t1.format(new Date())+"+08:00";
	    	return timestamp;
	    }
	
	public void packageDone(){
//		Main.Main.ms.Callpackge10012("", "", Main.Main.BoxNumber, "", "1", Main.Main.User);
		String str=Main.Main.BoxNumber.substring(0, 4);
		
		if(!str.equals("1.00")) {
			v_MSG.show("箱号异常，请拍照发给小曾");
		} else {
			m_booleanPstr bP=Main.Main.ms.Callpackge10012("", "", Main.Main.BoxNumber, "", "1", "000001");
			if(bP.booleanCount) {
				view.his.append("\n"+GetTimestamp()+"\t"+model.packingBox.model.BoxNumber+"包完处理");
				model.packingBox.newBox();
			}
			refresh();
		}
	}
	
	public boolean checkBoxIsFull(){
		if(model.packingBox.model.qtyPack>=model.packingBox.model.qtyPlan){
			JOptionPane.showMessageDialog(null,"本箱已满，请封箱！", "提示",JOptionPane.INFORMATION_MESSAGE);
			return true;
		}else{
			return false;
		}
	}
	
	public void refresh(){
		view.setBoxNumber(model.packingBox.model.BoxNumber);
		view.labelQuantity.setText(""+model.packingBox.model.qtyPack);
		view.his.setCaretPosition(view.his.getRows());
		view.jscrollPane.getVerticalScrollBar().setValue(view.jscrollPane.getVerticalScrollBar().getMaximum());
		Main.Main.Quantity=model.packingBox.model.qtyPack;
	}
	
	public boolean scanSN(String sn){
		c_Product product=new c_Product();
		product.model.SN=sn;
		
		if(model.packingBox.model.BoxNumber.equals("新包装箱，请扫描产品")){
			if(true){//SQL
				ResultSetAndTabel RS =sql.Recordset("select SN from LabelHistory where LABEL ='"+
						product.model.SN+"' order by CreateTime Desc");
				if(RS.RowLength>0){
					product.model.SN=(String) RS.rsTable.getValueAt(0, 0);
					view.fieldSN.setText(product.model.SN);
				}
			}
			
			if(!product.getSNinfo()){
				v_MSG.Nshow(""+product.model.SN+"获取条码信息失败", "报错",false);
				return false;
			}
			AK_SQLreturn RS=sql.SMES_GetSNinfo(product);
			if(RS.result){
				product.model.partName=RS.AK_Data[0];
				product.model.customerNumber=RS.AK_Data[1];
				product.model.packingQty=Integer.parseInt(RS.AK_Data[2]);
				this.model.packingBox.model.qtyPlan=product.model.packingQty;
				product.model.controlLevel=Integer.parseInt(RS.AK_Data[3]);
			}else{
				JOptionPane.showMessageDialog(null,"产品基础信息不全！不可包装", "提示",JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			sql.SMES_NewBox(product);
			view.his.append("\n"+GetTimestamp()+"\t新建包装箱"+model.packingBox.model.BoxNumber);
		}
		m_booleanPstr mbp=Main.Main.ms.CallPCB5018(Main.Main.BoxNumber, product.model.SN, product.model.SN, Layer);
		if(!mbp.booleanCount){
			v_MSG.show(product.model.SN+"\n"+mbp.strCount);
			v_MSG.Nshow(""+product.model.SN+"\n"+mbp.strCount, "报错",false);
			return false;
		}

		view.his.append("\n"+GetTimestamp()+"\t"+product.model.SN+"\t入箱");
		Main.Main.packing.model.packingBox.model.qtyPack+=1;
		view.labelQuantity.setText(""+model.packingBox.model.qtyPack);
		return true;
	}
	
	public void PackOut(){
		//TODO 获取上岗证信息
		if(sql.SMES_PackOut(model.packingBox.model.BoxNumber,Main.Main.User)){
			Main.Main.packing.model.packingBox.model.qtyPack-=1;
		}
		refresh();
	}
	
	public void CancelBox(){
		String Box=Main.v_MSG.input("确认要重新包装的箱号：", "拆箱", 0, false);
		String User=Main.Main.User;
		if((Box==null)||(Box.length()<1)){
			return;
		}
		
				m_booleanPstr mbp=Main.Main.ms.Callpackge10012("", "", Box, "", "3", User);
				if(!mbp.booleanCount){
					v_MSG.show(mbp.strCount);
					return;
				}
				v_MSG.show("拆箱成功");
				if(Box.equals(model.packingBox.model.BoxNumber)){
					model.packingBox.newBox();
				}

		view.his.append("\n"+GetTimestamp()+"\t"+Box+"拆箱处理");
		refresh();
	}
	
	public void OpenBox(){
		String Box=Main.v_MSG.input("确认要打开包装继续包装的箱号：", "开箱", 1, false);
		String User=Main.Main.User;
		if((Box==null)||(Box.length()<1)){
			return;
		}
				m_booleanPstr mbp=Main.Main.ms.Callpackge10012("", "", Box, "", "0", User);
				if(mbp.booleanCount){
					Main.Main.BoxNumber=mbp.strCount;
					model.packingBox.model.BoxNumber=mbp.strCount;
					model.packingBox.model.qtyPack=Integer.parseInt(mbp.strCount1);
					model.packingBox.model.qtyPlan=Integer.parseInt(mbp.strCount2);
					Main.Main.SaveConfig();
					view.his.append("\n"+GetTimestamp()+"\t"+Box+"开箱处理");
				}else{
					v_MSG.show(mbp.strCount);
					Main.Main.BoxNumber=mbp.strCount1;
					model.packingBox.model.BoxNumber=mbp.strCount1;
					model.packingBox.model.qtyPack=Integer.parseInt(mbp.strCount2);
					model.packingBox.model.qtyPlan=Integer.parseInt(mbp.strCount3);
					Main.Main.SaveConfig();
					//TODO java fx 赶紧学习!
				}
		refresh();
	}
	
	KeyListener SN_Enter_Click=new KeyListener() { 
        public void keyPressed(KeyEvent e) {
        	if(e.getKeyChar() == KeyEvent.VK_ENTER){
        	//	lock=true;
        		final String str=view.fieldSN.getText();
        		view.fieldSN.setText("");
                ProcessTask task = new ProcessTask(str);
        		pool.submit(task);
        		refresh();
        	}
        }
        
        class ProcessTask implements Runnable {
        	private String wipno;

        	public ProcessTask(String wipno) {
        		this.wipno = wipno;
        	}

			@Override
			public void run() {
        		scanSN(wipno);

        		if(checkBoxIsFull()){
	       			packageDone();
	        	}else{
		       		refresh();
	        	}
			}
        	
        }

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			
		}  
	};
	
	MouseAdapter ButtonPart_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	partSet=new c_PartSet();
        }  
	};
	
	MouseAdapter ButtonPacking_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	packageDone();
        }  
	};
	
	MouseAdapter ButtonUnpack_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	PackOut();
        }  
	};
	
	MouseAdapter ButtonCancelBox_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	CancelBox();
        }  
	};
	
	MouseAdapter ButtonOpenBox_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	OpenBox();
        }  
	};
	
	MouseAdapter ButtonHistory_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	m_booleanPstr mbp=Main.Main.ms.Callpackge10011("", "", "", "");
        	if(mbp.booleanCount){
        		Main.Main.BoxNumber=mbp.strCount;
				model.packingBox.model.BoxNumber=mbp.strCount;
				model.packingBox.model.qtyPack=Integer.parseInt(mbp.strCount1);
				model.packingBox.model.qtyPlan=Integer.parseInt(mbp.strCount2);
				Main.Main.SaveConfig();
				refresh();
        	}else{
        		v_MSG.show(mbp.strCount);
        	}
        }  
	};
	
	MouseAdapter ButtonHelp_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	view.his.append("\n"+GetTimestamp()+"\t打开帮助");
        	v_MSG.show("2018/01/07\t\tv1.0\t\t包装反扫第一版"+
        			"\n2018/04/28\t\tv1.2\t\t支持扫描客户标签转换成SN"+
        			"");
        	refresh();
        }  
	};
	
}
