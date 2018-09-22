package com.hangzhou.nn.timer;

import java.util.ArrayList;
import java.util.List;

public class Timer {
	//计时器状态
	private final int RUNNING=0,INTERRUPT=1,DESTROY=2,TIMEOUT=3;
	private Thread thread;
	private TimerCallback timerCallback;
	private int duration=0;//需要持续等待的时间
	private int wastingTime=0;//已经消耗的时间
	private int status=RUNNING;//定时器状态
	
	
	//启动计时器
	public void startTimer(int _duration,TimerCallback _timerCallback){
		this.timerCallback=_timerCallback;
		this.duration=_duration;
		
		thread=new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				try {
					while(wastingTime<duration&&status==RUNNING){						
						Thread.sleep(1000);
						wastingTime++;
						
						System.out.print(duration-wastingTime);
						System.out.println("              "+this.hashCode());
						
						if(wastingTime<duration&&status==RUNNING){
							timerCallback.heartbeat(duration-wastingTime);
						}						
						
					}
					
					if(wastingTime>=duration){
						status=TIMEOUT;
					}
					
					
					
					switch(status){
						case INTERRUPT:{
							//中断
							timerCallback.interrupt();
						}break;
						case TIMEOUT:{
							//时间到
							timerCallback.timeOut();
						}break;
					}
//					
					
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					//销毁线程
					destroy();					
				}
				
			}
			
		});
		
		thread.start();
		
		timerCallback.start();
	}
	//中断时钟
	public void interrupt(){
		status=INTERRUPT;
	}
	//时间到
	public void timeOut(){
		status=TIMEOUT;
	}
	//销毁时钟 不销毁就等CG回收
	public void destroy(){
		thread=null;
	}
}
