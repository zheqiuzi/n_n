package com.hangzhou.nn.common;

public class Log {
	static boolean debugger=true;
	public static void d(String msg){
		if(debugger)System.out.println(msg);
	}
}
