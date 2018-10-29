package Product;

import javax.swing.JOptionPane;

import SQLserver.AK_SQLreturn;

public class c_Product {
	public m_Product model;
	public c_Product(){
		model=new m_Product();
	}
	public boolean getSNinfo(){//配合SMES_GetPackingBoxInfo一起才能查询，详看c_Packing
		/*  String SN=new String();
		 *  String partNumber=new String();
		 *	String partName=new String();
		 *	String BIN=new String();
		 *	String customerNumber=new String();
		 *	int packingQty;
		 *	int controlLevel;
		 * */
		if(model.SN.length()!=26){
			AK_SQLreturn RS=Main.Main.packing.sql.GetDataFromTableWhereKey("SN", "LabelHistory", "LABEL='"+model.SN+"'");
			if((RS.result)&&(RS.length>0)){
				if(RS.AK_Data[0].length()==26){
					model.SN=RS.AK_Data[0];
					model.partNumber=model.SN.substring(0, 15);
					model.BIN=model.SN.substring(15, 18);
					Main.Main.packing.view.fieldSN.setText(RS.AK_Data[0]);
					return true;
				}
			}
			JOptionPane.showMessageDialog(null,"产品SN位数错误\n无法获取产品信息！", "提示",JOptionPane.INFORMATION_MESSAGE);
			return false;
		}else{
			model.partNumber=model.SN.substring(0, 15);
			model.BIN=model.SN.substring(15, 18);
		}
		return true;
	}
}
