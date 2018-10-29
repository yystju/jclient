package PackingBox;


import javax.swing.JOptionPane;

import MySocket.m_booleanPstr;
import Product.c_Product;


public class c_PackingBox {
	public m_PackingBox model;
	public c_PackingBox(){
		model=new m_PackingBox();
	}
	
	public void newBox(){
		model.partNumber="";
		model.partName="";
		model.BIN="";
		model.qtyPlan=10;
		model.qtyPack=0;
		model.customerNumber="";
		model.controlLevel=0;
		model.BoxNumber="新包装箱，请扫描产品";  
		Main.Main.BoxNumber=model.BoxNumber;
		Main.Main.SaveConfig();
	}
	
	public void newBox(c_Product product){
		model.partNumber=product.model.partNumber;
		model.partName=product.model.partName;
		model.BIN=product.model.BIN;
		model.qtyPlan=product.model.packingQty;
		model.qtyPack=0;
		model.customerNumber=product.model.customerNumber;
		model.controlLevel=product.model.controlLevel;

		m_booleanPstr r=Main.Main.ms.Callpackge10009(model.partNumber, model.BIN);//MES 申请新箱号
		if(!r.booleanCount){
			JOptionPane.showMessageDialog(null,product.model.SN+"\n"+
					r.strCount, "提示",JOptionPane.INFORMATION_MESSAGE);
			return;
		}else{
			Main.Main.BoxNumber=r.strCount;
			model.BoxNumber=r.strCount;
			model.qtyPlan=Integer.parseInt(r.strCount1);
		}
	}
}
