package com.hangzhou.nn.game;

import java.util.List;

import com.hangzhou.nn.card.Card;

public class Game {
	//状态
	int status;
	//牌盒
	public List<Card> cardsBox;
	/**
	 * 初始化
	 */
	public void init(){}
	
	//开始准备
	public void readying(){}
	
	//结束准备
	public void readied(){}
	
	//开始发牌
	public void sendCard(){}
	
	//结束发牌
	public void sentCard(){}
	
	//开始下注
	public void settingWager(){} 
	
	//结束下注
	public void setWagerEnd(){}
	
	//清场
	public void liquidation(){}

}
