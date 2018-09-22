package com.hangzhou.nn.card;

import com.alibaba.fastjson.annotation.JSONField;

public class Card {
	
	public static final int HEITAO=4;
	public static final int HONGTAO=3;
	public static final int MEIHUA=2;
	public static final int FANGKUAI=1;
	
	private int size;
	private int color;//黑-4、红-3、梅-2、方-1
	@JSONField(serialize=false)
	private boolean isVisible=true;
	@JSONField(serialize=false)
	private boolean isOtherVisible=false;

	public Card(int size, int color) {
		super();
		this.size = size;
		this.color = color;
	}
	
	
	public Card(int size, int color, boolean isVisible, boolean isOtherVisible) {
		super();
		this.size = size;
		this.color = color;
		this.isVisible = isVisible;
		this.isOtherVisible = isOtherVisible;
	}


	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	
	
	
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public String transfer(int color){
		String _color="未知";
		switch(color){
			case HEITAO:{
				_color= "黑桃";
						};break;
			case HONGTAO:{
				_color= "黑桃";
			};break;
			
			case MEIHUA:{
				_color= "梅花";
			};break;
			
			case FANGKUAI:{
				_color= "方块";
			};break;
		}
		
		return _color;
	}


	public String getJson() {
		return "{size:" + size + ", color:" + color + ", isVisible:" + isVisible + "}";
	}

	public boolean isOtherVisible() {
		return isOtherVisible;
	}

	public void setOtherVisible(boolean isOtherVisible) {
		this.isOtherVisible = isOtherVisible;
	}
	
	
	
	
}
