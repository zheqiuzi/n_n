package nn;

import java.util.ArrayList;
import java.util.List;


class A{
	private String value;
	public void setValue(String val){
		this.value=val;
	}
	public String getValue(){
		return this.value;
	}
}

public class TestList {
	
	
	public static void main(String args[]){
		List<A> list=new ArrayList<A>();
		A a1=new A();
		a1.setValue("a1");
		
		A a2=new A();
		a2.setValue("a2");
		
		list.add(a1);
		list.remove(0);
		list.add(a2);
		System.out.println(list.get(0).getValue());
		
	}
	
}
