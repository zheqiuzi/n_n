package com.hangzhou.nn.game;

public class GamerBlance {
	String nickName;
	String blance;
	
	
	public GamerBlance() {
	}
	public GamerBlance(String nickName, String blance) {
		this.nickName = nickName;
		this.blance = blance;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getBlance() {
		return blance;
	}
	public void setBlance(String blance) {
		this.blance = blance;
	}
	
}
