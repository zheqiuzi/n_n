package com.hangzhou.nn.room;

import java.util.ArrayList;
import java.util.List;

import com.hangzhou.nn.common.Log;
import com.hangzhou.nn.role.Dealer;
import com.hangzhou.nn.role.Gamer;


public class Room {
	//房间id
	private String roomid;
	//房间人员列表（该人员不一定都是玩家）
	private List<Gamer> gamerList=new ArrayList<Gamer>();
	//荷官
	private Dealer dealer;
	
	//构造房间
	public Room(){
		dealer=new Dealer(this);
		roomid=(int)(Math.random()*10000000)+"";
		System.out.println("创建房间成功，房间ID:"+roomid);
	}
	
	/**
	 * 用户进入房间
	 * @param player
	 */
	public void join(Gamer gamer){
		//后期优化的时候这一步应该放到荷官处理
		//根据id判断该用户是否是再次进入如果是就把现有的用户属性赋值过去赋值
		for(int i=0;i<gamerList.size();i++){
			String id=gamerList.get(i).getId();
			String _id=gamer.getId();
			if(id.equals(_id)){
				Log.d("ok");
			}
			if(gamerList.get(i).getId().equals(gamer.getId())){
				Gamer oldGamer=gamerList.get(i);
				
				//gamer.setOnline(true);
				Log.d(oldGamer.getNickName()+"再次入场");
				//把当前会话设置到原来对象上				
				oldGamer.setSession(gamer.getSession());
				oldGamer.setOnline(true);
//				oldGamer=gamer;
				//gamerList.set(i, gamer);asdfsadf
				//拷贝之前的数据到当前新对象
				gamer.copyDataFromGamer(oldGamer);
				//移除原来的对象添加新对象
				gamerList.remove(i);
				gamerList.add(gamer);
				
			
				//告诉荷官新人入场
				this.dealer.gamerRejoined(gamer);		
				return;
			}
		}
		gamerList.add(gamer);		
		//告诉荷官新人入场
		this.dealer.newGamerJoined(gamer);
		
		System.out.println("欢迎"+gamer.getNickName()+"进入房间"+this.getRoomid()+" 当前房间人数："+gamerList.size());
	}
	
	/**
	 * 用户离开房间
	 * @return
	 */
	public void leaveRoom(Gamer gamer){
		for(int i=0;i<gamerList.size();i++){
			if(gamer==gamerList.get(i)){
				gamerList.get(i).setOnline(false);
				System.out.println(gamerList.get(i).getNickName()+"（"+gamerList.get(i).getSession().hashCode()+"） : 离开了房间");
			}
		}
	}
	
	/**
	 * 获取所有在线玩家 考虑废弃
	 * @return
	 */
	public List<Gamer> getOnlineGamers(){
		List<Gamer> list=new ArrayList<Gamer>();
		for(int i=0;i<this.gamerList.size();i++){
			if(gamerList.get(i).isOnline()){
				list.add(gamerList.get(i));
			};
		}
		return list;
	}
	
	/**
	 * 获取当前在线和已经在游戏中的玩家
	 * @return
	 */
	public List<Gamer> getOnlineOrPlayingGamer(){
		List<Gamer> list=new ArrayList<Gamer>();
		for(int i=0;i<this.gamerList.size();i++){
			if(gamerList.get(i).isOnline()||gamerList.get(i).isInCurrentGame()){
				list.add(gamerList.get(i));
			};
		}
		return list;
		
	}
	/**
	 * 获取房间某一状态的所有玩家
	 * @param state
	 * @return
	 */
	public List<Gamer> getStateGamers(int status){
		List<Gamer> list=new ArrayList<Gamer>();
		for(int i=0;i<this.gamerList.size();i++){
			if(gamerList.get(i).getStatus()==status){
				list.add(gamerList.get(i));
			};
		}
		return list;
		
	}
	/**以下是setter getter**/

	public String getRoomid() {
		return roomid;
	}

	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}

	public List<Gamer> getGamerList() {
		return gamerList;
	}

	public  void setGamerList(List<Gamer> gamerList) {
		this.gamerList = gamerList;
	}

	public Dealer getDealer() {
		return dealer;
	}

	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}

	
	
	
}

