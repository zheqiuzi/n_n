package com.hangzhou.nn;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Testsuo {

	 public Lock lock = new ReentrantLock();
	 public Condition conditionLock = lock.newCondition();
	 
	 int ac=0;
	 
	 public void out(int i){
		 lock.lock();
		 try {
			 
			 while(i>ac){
				 System.out.println("你想取"+i+"元，但余额不足，只有"+ac+"元！");
				 conditionLock.await();				 
			 }
			 
			 ac-=i; 
			 System.out.println("成功取款"+i+"元，余额"+ac+"元！");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				lock.unlock();
			}
		 
		 
		 
		 
	 }
	 
	 public void in(int i){
		 lock.lock();
		 ac+=i;
		 System.out.println("您成功存款"+i+"元！余额"+ac+"元");
		 try{
			 conditionLock.signalAll();
		 }finally{
			 lock.unlock();
		 }
		 
		 
	 }
	 
	public static void main(String args[]){
		final Testsuo test= new Testsuo();
		
		Thread t1=new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				test.out(50);
			}
			
		});
		
//		Thread t2=new Thread(new Runnable(){
//
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					Thread.sleep(3000);
//					test.in(10);
//					Thread.sleep(3000);
//					test.in(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//			
//		});
		
		t1.start();
//		t2.start();
		
		test.in(10);
//		Thread.sleep(3000);
		test.in(100);
		
	}
	
}
