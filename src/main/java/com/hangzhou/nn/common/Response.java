package com.hangzhou.nn.common;

public class Response {	
	
	//常规输出内容
	public static final int NORMAL=200;
	//警告
	public static final int WARNING=300;
	//错误
	public static final int ERROR=400;
	//需要登陆
	public static final int NEED_LOGIN=402;
	//无权限
	public static final int NO_PERMISSIONS=403;
	//为空
	public static final int EMPTY=404;	
	//无效
	public static final int INVALID=500;
//	//Token失效
//	private final int INVALTOKEN=501; 已经有一个需要登陆的 402
	//已存在
	public final int ISEXISTS=600;
	
	
	
	
	public String normal(String content){
		String json="{status:"+NORMAL+",data:"+content+"}";
		//System.out.println(json);
		return json;
	}
	public String normal(String key,String content){
		String json="{status:"+NORMAL+",data:"+content+"}";
		//System.out.println(json);
		return json;
	}
	public String normal(String key,int content){
		String json="{status:"+NORMAL+",data:"+content+"}";
		//System.out.println(json);
		return json;
	}
	public String normal(String key,boolean content){
		String json="{status:"+NORMAL+",data:"+content+"}";
		//System.out.println(json);
		return json;
	}
	public String warning(String key,String content){
		String json="{status:"+WARNING+",data:"+content+"}";
		//System.out.println(json);
		return json;
		
	}
	public String needLogin(){
		String json="{status:"+NEED_LOGIN+",data:'please login'}";
		//System.out.println(json);
		return json;
	}
	public String error(String reason){
		String json="{status:"+ERROR+",data:'"+reason+"'}";
		//System.out.println(json);
		return json;
	}
	public String noPermissions(String permissions){
		String json="{status:"+NO_PERMISSIONS+",data:'Without jurisdiction,lack of "+permissions+" permissions'}";
		//System.out.println(json);
		return json;
	}
	public String empty(String key){
		String json="{status:"+EMPTY+",data:'"+key+" can not be empty'}";
		//System.out.println(json);
		return json;
	}
	public String invalid(String key){
		String json="{status:"+INVALID+",data:'invalid "+key+"'}";
		//System.out.println(json);
		return json;
	}
	public String isExists(String key){
		String json="{status:"+ISEXISTS+",data:'"+key+" is isExists'}";
		//System.out.println(json);
		return json;
	}
	
	
}