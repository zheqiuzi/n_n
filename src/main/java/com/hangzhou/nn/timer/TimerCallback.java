package com.hangzhou.nn.timer;

public interface TimerCallback {
	public void start();
	public void heartbeat(int i);
	public void timeOut();
	public void interrupt();
	public void destroy();
}
