package com.hangzhou.nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hangzhou.nn.role.Gamer;

public class Test {
	public static void main(String args[]){
		
		int[] a=new int[]{1,8,4,5,4,7,1,6,5};
		Arrays.sort(a);
		System.out.println(Arrays.toString(a));
		System.out.println();
		
		for(int x : a){
			System.out.println(x);
		}
		
		List<String> list=new ArrayList();
		list.add("hello");
		list.add("girl");
		list.set(1, "boy");
		
		for(String str : list){
			System.out.println(str);
		}

		
		System.out.println(new Date().getTime());
		System.out.println(new Date().getTime());
		System.out.println(new Date().getTime());
		System.out.println(new Date().getTime());
		System.out.println(new Date().getTime());
		
		

		System.out.println( System.currentTimeMillis());
		System.out.println( System.currentTimeMillis());
		System.out.println( System.currentTimeMillis());
		System.out.println( System.currentTimeMillis());
		System.out.println( System.currentTimeMillis());
		
		lockTest();
	}
	
	public static void lockTest(){
		//锁
		
		final LockA locka=new LockA();
		
		Thread t=new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
					locka.unlock();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		t.start();
		
		locka.lock();
		
			
	}
	

		
	
}

class LockA{
	 Lock lock = new ReentrantLock();
	 Condition readyingConditionLock = lock.newCondition();
	 
	 public void lock(){
		 lock.lock();	
		 try {
			
			System.out.println("还没解锁1");
			readyingConditionLock.await();
			System.out.println("解锁了");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				lock.unlock();
		
			
		}
	 }
	 
	 public void unlock(){
		 readyingConditionLock.signalAll();
	 }
	 
			
	 
		
		
		
}
