package com.hangzhou.nn.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hangzhou.nn.card.Card;


public class GameResult {
	//是否有牛
	private boolean haveOX=false;
	//余数
	private int remainder=0;
	//扑克列表
	private List<Card> cardList;
	//牌型默认倍数
	private int defaultCardTypeMult=1;
	//当前牌型倍数 默认为一倍
	private int currentCardTypeMult=defaultCardTypeMult;
	//牌型倍数
	public static final int CARD_TYPE_MULT_NIUNIU=3,
			CARD_TYPE_MULT_NIU9=2,
			CARD_TYPE_MULT_NIU8=1;
	
	public GameResult(List<Card> cardList){
		this.cardList=cardList;
		int[] points=new int[cardList.size()];
		for(int i=0;i<cardList.size();i++){
			points[i]=cardList.get(i).getSize();
		}
		
		analysisCardType(points);
	}
	
	//扑克组合运算 得到牌型
	public GameResult analysisCardType(int[] _math){
			int[] math=new int[_math.length];
			for(int i=0;i<math.length;i++){
				if(_math[i]>10){
					math[i]=10;
				}else{
					math[i]=_math[i];
				}
				
			}
			
			for(int a=0;a<math.length-2;a++){
				for(int b=a+1;b<math.length-1;b++){
					for(int c=b+1;c<math.length;c++){
						//System.out.println(math[a]+" "+math[b]+" "+math[c]+" ");
						if((math[a]+math[b]+math[c])%10==0){
							
							//有牛
							this.setHaveOX(true);
							int m=0;
							for(int x=0;x<math.length;x++){
								if(x!=a&&x!=b&&x!=c){
									m+=math[x];
								}
							}
							//余数
							int remainder=m%10;
							if(remainder==0){
								remainder=10;
							}
							this.setRemainder(remainder);
						}
					}
				}
			}
			
			//判断当前牌型应该为多少倍率
			if(haveOX){
				final int _remainder=remainder;
				switch(_remainder){
					case 0:setCurrentCardTypeMult(CARD_TYPE_MULT_NIUNIU);break;
					case 9:setCurrentCardTypeMult(CARD_TYPE_MULT_NIU9);break;
					case 8:setCurrentCardTypeMult(CARD_TYPE_MULT_NIU8);break;
					default:setCurrentCardTypeMult(defaultCardTypeMult);
				}
			}
			
			
			
			return this;
		}
			
		
	//判断是否大与传入的结果集
	public boolean gt(GameResult gameResult){
		//自己有牛别人没牛
		if(this.haveOX&&!gameResult.haveOX){
			return true;
		}else 
		//自己没牛 别人有牛
		if(!this.haveOX&&gameResult.haveOX){
			return false;
		}else
		//都没牛
		if(!this.haveOX&&!gameResult.haveOX){
			return gtPoint(gameResult);
		}else
		//都有牛
		if(this.haveOX&&gameResult.haveOX){
			return allHaveOX(gameResult);
		}
		return true;
	}
	
	//都有牛的时候判断
	private boolean allHaveOX(GameResult gameResult){
		//大于
		if(this.getRemainder()>gameResult.getRemainder()){
			return true;
		}else 
			//小于
			if(this.getRemainder()<gameResult.getRemainder()){
			return false;
		}else{
			//余数等于时，判断牌的点数最大值
			return this.gtPoint(gameResult);
			//如果点数相等还需要判断花色
		}
	
	}
	//手上的牌排序 降序
	private Card[] desc(Card[] cards){
		
	
		for(int i=0;i<cards.length;i++){
			
			for(int j=i+1;j<cards.length;j++){
				Card stmp=cards[i];
				if(cards[i].getSize()<cards[j].getSize()){
					stmp=cards[i];
					cards[i]=cards[j];
					cards[j]=stmp;
				}else if(cards[i].getSize()==cards[j].getSize()){
					if(cards[i].getColor()<cards[j].getColor()){
						stmp=cards[i];
						cards[i]=cards[j];
						cards[j]=stmp;
					}
				}
			}
		}
		return cards;
	} 
	//手上的牌排序 降序
	private Card[] desc(List<Card> cardList){
		
		Card[] cards=new Card[cardList.size()];
		for(int i=0;i<cardList.size();i++){
			cards[i]=cardList.get(i);
		}
		
		return this.desc(cards);
	} 
	
	//比较点数大小
	private boolean gtPoint(GameResult gameResult){
		
		
		Card[] _cards=desc(gameResult.getCardList());
		Card[] _thiscards=this.desc(this.getCardList());
		
		if(_thiscards[0].getSize()>_cards[0].getSize()){
			return true;
		}else if(_thiscards[0].getSize()<_cards[0].getSize()){
			return false;
		}else{
			if(_thiscards[0].getColor()>_cards[0].getColor()){
				return true;
			}else{
				return false;
			}
		}		
		
	}
		

	
	
	//取得点数列表
	
	//取得牌型
	

	public boolean isHaveOX() {
		return haveOX;
	}

	public void setHaveOX(boolean haveOX) {
		this.haveOX = haveOX;
	}

	public int getRemainder() {
		return remainder;
	}

	public void setRemainder(int remainder) {
		this.remainder = remainder;
	}

	public List<Card> getCardList() {
		return cardList;
	}

	public void setCardList(List<Card> cardList) {
		this.cardList = cardList;
	}

	public int getCurrentCardTypeMult() {
		return currentCardTypeMult;
	}

	public void setCurrentCardTypeMult(int currentCardTypeMult) {
		this.currentCardTypeMult = currentCardTypeMult;
	}
	
	
	
	
}
