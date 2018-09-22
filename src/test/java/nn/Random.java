package nn;

import java.util.ArrayList;
import java.util.List;

public class Random {
	public static void main(String args[]){
		List a=new ArrayList<String>();
		a.add("1");
		a.add("2");
		a.add("3");
		a.add("4");
		a.add("5");
		a.add("6");
		a.add("7");
		a.add("8");
		a.add("9");
		
//		Math.random()*this.room.getOnlineGamers().size())%bankers.size()
		for(int i=0;i<1000;i++){
			
			int d=(int)(Math.random()*9);
//			int m=d%9;
			System.out.println(d);
			if(d==0){				
//				int m=d%9;
//				System.out.println(a.get(m));
			}
			
		}
		
		
	}
}
