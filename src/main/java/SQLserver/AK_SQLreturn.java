package SQLserver;
import java.util.Arrays;


public class AK_SQLreturn {
	public boolean result=false;
	public String [] AK_Data=new String[0];
	public int length=0;
	
	public void SetLength(int length){
		this.length=length;
		AK_Data=Arrays.copyOf(AK_Data, length);
	}
}
