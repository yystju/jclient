package PartSet;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import SQLserver.ResultSetAndTabel;
import SQLserver.c_SQLserver;

public class c_PartSet {
	v_PartSet view;
	c_SQLserver sql = new c_SQLserver();
	
	public c_PartSet(){
		Main.Main.fB.setVisible(true);
		sql.model.IP=Main.Main.IP;
		sql.model.DbName=Main.Main.DbName;
		sql.model.OP=Main.Main.OP;
		sql.model.PW=Main.Main.PW;
		if(!sql.Connect()){
			System.exit(0);
		}
		view=new v_PartSet();
		Main.Main.fB.dispose();
		view.selectCounter=0;
		view.fieldPartNumber.addKeyListener(PART_Enter_Click);
		view.buttonSearch.addMouseListener(ButtonSearch_Click);
		view.buttonGet.addMouseListener(ButtonGet_Click);
		view.buttonSave.addMouseListener(ButtonSave_Click);
		
	}
	
	public void Search(){
		String str=view.fieldPartNumber.getText();
		ResultSetAndTabel RS =sql.Recordset("Select PartNumber as 编号,PartDesc as 描述,PackingCounter as"+
				" 满箱数量,PackingControlLevel as 包装等级,CustomerNO as 客户号,LastEdit From PartMaster "+
				"where PartNumber like '"+str+"'");
		if(RS!=null){
			RS.rsTable.getColumnModel().getColumn(0).setPreferredWidth(150);
			RS.rsTable.getColumnModel().getColumn(1).setPreferredWidth(300);
			view.showTable(RS.rsTable);
		}else{
			JOptionPane.showMessageDialog(null,"网络中断，请重新连接", "提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	KeyListener PART_Enter_Click=new KeyListener() { 
        public void keyPressed(KeyEvent e) {
        	if(e.getKeyChar() == KeyEvent.VK_ENTER){
        		Search();
        	}
        }

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			
		}  
	};
	
	MouseAdapter ButtonSearch_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	Search();
        }  
	};
	
	MouseAdapter ButtonGet_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	String str=new String();
        	str=((String) view.tab01.getValueAt(view.tab01.getSelectedRow(), 0));
        	view.fieldPartNumber1.setText((str==null)?"-":str.trim());
        	str=((String) view.tab01.getValueAt(view.tab01.getSelectedRow(), 1));
        	view.fieldPartName.setText((str==null)?"-":str.trim());
        	str=((String) view.tab01.getValueAt(view.tab01.getSelectedRow(), 2));
        	view.fieldQtyPlan.setText((str==null)?"-":str.trim());
        	str=((String) view.tab01.getValueAt(view.tab01.getSelectedRow(), 3));
        	view.fieldControlLevel.setText((str==null)?"-":str.trim());
        	str=((String) view.tab01.getValueAt(view.tab01.getSelectedRow(), 4));
        	view.fieldCustomerNumber.setText((str==null)?"-":str.trim());
        	view.selectCounter=view.tab01.getSelectedRow();
        }  
	};
	
	MouseAdapter ButtonSave_Click=new MouseAdapter() { 
        public void mousePressed(MouseEvent e) {
        	sql.SMES_SetPackingInfo(view.fieldPartNumber1.getText(), view.fieldPartName.getText(), 
				        			view.fieldQtyPlan.getText(), view.fieldControlLevel.getText(), 
				        			view.fieldCustomerNumber.getText());
        	
        	Search();
        	view.tab01.setRowSelectionInterval(view.selectCounter, view.selectCounter);
        }  
	};
}
