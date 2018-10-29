package SQLserver;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import Main.v_MSG;
import MySocket.m_booleanPstr;
import PackingBox.m_PackingBox;
import Product.c_Product;

public class c_SQLserver {
	
	public m_SQLserver model=new m_SQLserver();
	Connection conn;
	Statement stmt;
	
	public boolean Connect(String Server,String DbName,String OP,String PW){
		String url = "jdbc:sqlserver:"+Server+";DatabaseName="+DbName+";";
		try {
			conn = DriverManager.getConnection(url, OP,PW);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "注意",JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean Connect(){
		String url = "jdbc:sqlserver:"+model.IP+";DatabaseName="+model.DbName+";";
		try {
			conn = DriverManager.getConnection(url, model.OP,model.PW);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "注意",JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void Close(){
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "注意",JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public ResultSetAndTabel Recordset(String str){
		ResultSet rs;
		JTable rsTable;
		ResultSetAndTabel RS=new ResultSetAndTabel();
		try {
			rs=stmt.executeQuery(str);
			RS.rs=rs;
			int count = 1 ;
        	int count1=1;
        	rs.last();
    		count= rs.getRow();
    		RS.RowLength=count;
    		rs.beforeFirst();
    		count1=rs.getMetaData().getColumnCount();
    		
        	Object[][] info = new Object[count][count1];
        	count=0;
    		while(rs.next()){
    			for(int i=0;i<count1;i++){
    				info[count][i] = rs.getString(rs.getMetaData().getColumnLabel(i+1));
    			}
    			count++;
    		}
        	
        	String[] title = new String[count1];
        	for(int i=0;i<count1;i++){
    			title[i] = rs.getMetaData().getColumnLabel(i+1);
    		}
        	rsTable = new JTable(info,title);
        	RS.rsTable=rsTable;
        	return RS;
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
		}
		return null;
	}
	
	
	public boolean Command(String str){
		int result;
		try {
			result=stmt.executeUpdate(str);
			if(result>=0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		JOptionPane.showMessageDialog(null, "未知错误","提示",JOptionPane.INFORMATION_MESSAGE);
		return false;
	}
	
	public AK_SQLreturn GetDataFromTableWhereKey(String DataName,String Table,String Key){
		AK_SQLreturn RS=new AK_SQLreturn();
		String str="Select "+DataName+" From "+Table+" Where "+Key;
		ResultSet rs;
		try {
			rs=stmt.executeQuery(str);
			rs.last();
			RS.SetLength(rs.getRow());
			rs.first();
			for(int i=0;i<RS.length;i++){
				RS.AK_Data[i]=rs.getString(rs.getMetaData().getColumnLabel(1));
				rs.next();
			}
			RS.result=true;
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
			RS.result=false;
		}
		
		return RS;
	}
	
	public AK_SQLreturn SMES_GetPackingBoxInfo(String BoxNumber){
		AK_SQLreturn RS=new AK_SQLreturn();
		/* 
		 * RS.AK_Data[0]	partNumber
		 * RS.AK_Data[1]	partName
		 * RS.AK_Data[2]	BIN
		 * RS.AK_Data[3]	customerNumber
		 * RS.AK_Data[4]	qtyPlan
		 * RS.AK_Data[5]	qtyPack
		 * RS.AK_Data[6]	controlLevel
		 * */
		String str="Select * From PackingLabelList Where BoxNO='"+BoxNumber+"' order by CreateTime desc";
		ResultSet rs;
		try {
			rs=stmt.executeQuery(str);
			RS.SetLength(7);
			rs.first();
			RS.AK_Data[0]=rs.getString("PartNumber");
			RS.AK_Data[1]=rs.getString("PartName");
			RS.AK_Data[2]=rs.getString("BIN");
			RS.AK_Data[3]=rs.getString("CustomerNO");
			str="select * from PackingHistory_Last where BoxNO='"+BoxNumber+"'";
			rs=stmt.executeQuery(str);
			rs.last();
			RS.AK_Data[5]=""+rs.getRow();
			str="select * from PartMaster where PartNumber='"+RS.AK_Data[0]+"'";
			rs=stmt.executeQuery(str);
			rs.last();
			RS.AK_Data[4]=rs.getString("PackingCounter");
			RS.AK_Data[6]=rs.getString("PackingControlLevel");
			RS.result=true;
			for(int i=0;i<7;i++){
				if(RS.AK_Data[i]==null){
					JOptionPane.showMessageDialog(null, "箱号信息缺失，请完善产品资料和包装箱资料", "提示",JOptionPane.INFORMATION_MESSAGE);
					RS.result=false;
					i=7;
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
			RS.result=false;
		}
		
		return RS;
	}
	
	public void SMES_NewBox(c_Product product){
		Main.Main.packing.model.packingBox.newBox(product);
//		Command("insert into PackingLabelList (CreateTime,BoxNO,PartName,CustomerNO,Quantity,LState,PartNumber,BIN) "
//				+"values (GETDATE(),'"+Main.Main.packing.model.packingBox.model.BoxNumber+
//				"','"+Main.Main.packing.model.packingBox.model.partName+"','"+
//				Main.Main.packing.model.packingBox.model.customerNumber+
//				"','"+Main.Main.packing.model.packingBox.model.qtyPack+"',3,'"+
//				Main.Main.packing.model.packingBox.model.partNumber+
//				"','"+Main.Main.packing.model.packingBox.model.BIN+"')");
	}
	
	public AK_SQLreturn SMES_GetSNinfo(c_Product product){
		AK_SQLreturn RS=new AK_SQLreturn();
		RS.SetLength(4);
		String str="Select * From PartMaster where PartNumber='"+product.model.partNumber+"'";
		ResultSet rs;
		try {
			rs=stmt.executeQuery(str);
			rs.first();
			RS.AK_Data[0]=rs.getString("PartDesc");
			RS.AK_Data[1]=rs.getString("CustomerNO");
			RS.AK_Data[2]=rs.getString("PackingCounter");
			RS.AK_Data[3]=rs.getString("PackingControlLevel");
			RS.result=true;
			for(int i=0;i<4;i++){
				if(RS.AK_Data[i]==null){
					JOptionPane.showMessageDialog(null, "箱号信息缺失，请完善产品资料和包装箱资料", "提示",JOptionPane.INFORMATION_MESSAGE);
					RS.result=false;
					i=4;
				}
			}
		}catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
			RS.result=false;
			e.printStackTrace();
		}
		return RS;
	}
	
	public void SMES_SetPackingInfo(String part,String partName,String packingCounter,
								String controlLevel,String customerNumber){
		Command("update PartMaster set PartDesc='"+partName+"',PackingCounter="+packingCounter+
				",PackingControlLevel="+controlLevel+",CustomerNO='"+customerNumber+
				"' where PartNumber='"+part+"'");
	}
	
	public boolean SMES_PackIn(c_Product product){
		ResultSetAndTabel RS= Recordset("Select * From UploadState_Last where Result='Pass' and "+
										"Station='10112010' and SN='"+product.model.SN+"'");
		if(RS.RowLength<1){
			JOptionPane.showMessageDialog(null, product.model.SN+"\t没测FCT或没PASS", "提示",JOptionPane.INFORMATION_MESSAGE);
			Main.Main.packing.refresh();
			return false;
		}
		RS= Recordset("select * from PackingHistory_Last where SN='"+
				product.model.SN+"' order by CreateTime desc");
		if(RS.RowLength>0){
			try {
				RS.rs.first();
				String BoxNO=RS.rs.getString("BoxNO");
				String Sequence=RS.rs.getString("Sequence");
				String MNo=RS.rs.getString("MNo");
				JOptionPane.showMessageDialog(null, "该产品已在机器编号:“"+
						MNo+"”处完成包装\n位于包装箱"+BoxNO+"的第"+Sequence+
						"个", "提示",JOptionPane.INFORMATION_MESSAGE);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
			}
			Main.Main.packing.refresh();
			return false;
		}
		boolean B=true;
		if(Command("insert into PackingHistory_Last (CreateTime,SN,Sequence,BoxNO,PartNumber,BState,MNo) "+
		"values (GETDATE(),'"+product.model.SN+"',"+(Main.Main.packing.model.packingBox.model.qtyPack+1)+
		",'"+Main.Main.packing.model.packingBox.model.BoxNumber+"','"+product.model.partNumber+"','1','"+
		Main.Main.clientName+"')")){
			if(Command("insert into PackingHistory (CreateTime,SN,Sequence,BoxNO,PartNumber,BState,MNo) "+
					"values (GETDATE(),'"+product.model.SN+"',"+(Main.Main.packing.model.packingBox.model.qtyPack+1)+
					",'"+Main.Main.packing.model.packingBox.model.BoxNumber+"','"+product.model.partNumber+"','1','"+
					Main.Main.clientName+"')")){
				Main.Main.packing.model.packingBox.model.qtyPack+=1;
			}else{B=false;}
		}else{B=false;}
		Main.Main.packing.refresh();
		return B;
	}
	
	/**
	 * LState:0=封箱；1=包完；2=封箱；4=包装中
	 * @param box
	 * @return
	 */
	public boolean SMES_UpdatePackingList(m_PackingBox box){
		String str="select BoxNO,PartName,Quantity,CreateTime from PackingLabelList where BoxNO='"+
				box.BoxNumber+"'";
		ResultSetAndTabel RS=Recordset(str);
		if(RS.RowLength>0){
			str="update PackingLabelList set PartName='"+box.partName+
					"' ,CustomerNO='"+box.customerNumber+"' ,Quantity='"+box.qtyPack+"' ,"+
					"LState=4,CreateTime=GetDate(),PartNumber='"+box.partNumber+"' ,BIN='"+box.BIN+"' "+
					"where BoxNO='"+box.BoxNumber+"'";
		}else{
			str="insert PackingLabelList (BoxNO,PartName,CustomerNO,Quantity,LState,CreateTime,"+
					"PartNumber,BIN) values ('"+box.BoxNumber+"','"+box.partName+"','"+box.customerNumber+
					"','"+box.qtyPack+"',4,GetDate(),'"+box.partNumber+"','"+box.BIN+"')";
		}
		return Command(str);
	}
	
	public boolean SMES_PackOut(String Box,String User){
		String SN=Main.v_MSG.input("要取出的SN", "从包装箱中取出产品", 1, false);
		if((SN==null)||(SN.length()<1)){
			return false;
		}
		
				m_booleanPstr mbp =Main.Main.ms.Callpackge10010(SN, "",Box, User,"2");
				if(!mbp.booleanCount){
					v_MSG.show(SN+"\n"+mbp.strCount);
					return false;
				}

				Main.Main.packing.view.his.append("\n"+Main.Main.packing.GetTimestamp()+"\t"+SN+"\t出箱");
		return true;
	}
	
	public boolean NewADMIN(String Name,String PW){
		int i=0;
		String str="Select * from ADMINlist where Number=(Select COUNT(Number)+"+i+" from ADMINlist)";
		ResultSet rs;
		try {
			do{
				i+=1;
				str="Select * from ADMINlist where Number=(Select COUNT(Number)+"+i+" from ADMINlist)";
				rs=stmt.executeQuery(str);
				rs.last();
			}while(rs.getRow()>0);
			str="insert ADMINlist values((Select COUNT(Number)+"+i+" from ADMINlist),'"+Name+"','OP',GETDATE(),GETDATE(),'"+PW+"','-')";
			int result=stmt.executeUpdate(str);
			if(result>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
		return false;
	}
	
	public boolean ChangePWADMIN(String Name,String PW){
		String str="update ADMINlist set LoginPassWord='"+PW+"' where Name='"+Name+"'";
		try {
			int result=stmt.executeUpdate(str);
			if(result>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
		return false;
	}
	
	public boolean RegisterReal(String RealNumber,int Qty){
		String str="insert into ML (ContainerNumber,PartNumber,CreateTime,LastEdit,Supplier,SupplierDesc,Quantity,"+
				"DateCode,LotNumber,Tips,MLState,BIN,SupplierPartNumber,Location)values"+
				"('"+RealNumber+"','"+RealNumber.substring(0, 15)+"',GETDATE(),GETDATE(),'-','-','"+Qty+
				"','-','-','"+Main.Main.clientName+"','0','-','-','-')";
		try {
			int result=stmt.executeUpdate(str);
			if(result>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
		return false;
	}
	
	public boolean RegisterEquip(String EquipmentID,String PartNumber){
		String str="Insert into TestParameter (equipPN,equipName,equipNumber,"+
				"TParameter,equipLocation,detail,Estate,LastEdit,MaintenanceTimeThreshold,BinCode,equipPN_BinCode,Layer)"+
				"Values('"+EquipmentID+"','"+PartNumber+"','"+PartNumber+"','-','-'"+
				",'"+Main.Main.clientName+"',1,GETDATE(),90,'NONE','"+EquipmentID+"_NONE',2)";
		try {
			int result=stmt.executeUpdate(str);
			if(result>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
		return false;
	}
	
	public boolean OffLoadMaterial(String RealNumber){
		String str="update SetupCheckList set ContainerNumber='-' where ContainerNumber='"+RealNumber+"'";
		try {
			int result=stmt.executeUpdate(str);
			if(result>0){
				str="update ML set Location='-' where ContainerNumber='"+RealNumber+"'";
				try {
					result=stmt.executeUpdate(str);
					if(result>0){
						return true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
				}
				
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
		return false;
	}

	
	public boolean OffLoadEquipment(String EquipmentID){
		String str="update SetupCheckList set Equipment_Setup='-' where Equipment_Setup='"+EquipmentID+"'";
		try {
			int result=stmt.executeUpdate(str);
			if(result>0){
				str="update TestParameter set Location='-' where equipPN='"+EquipmentID+"'";
				try {
					result=stmt.executeUpdate(str);
					if(result>0){
						return true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
				}
				
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.INFORMATION_MESSAGE);
		}
		
		return false;
	}
}
