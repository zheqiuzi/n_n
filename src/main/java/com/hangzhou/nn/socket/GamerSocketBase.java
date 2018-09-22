package com.hangzhou.nn.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import com.hangzhou.nn.card.Card;
import com.hangzhou.nn.common.Log;
import com.hangzhou.nn.common.Response;
import com.hangzhou.nn.common.Template;
import com.hangzhou.nn.game.GameResult;
import com.hangzhou.nn.role.Gamer;
import com.hangzhou.nn.room.Room;
import com.hangzhou.nn.room.RoomMgr;

public class GamerSocketBase {
	
	//id
	private String id;
	//昵称
	private String nickName;
	//头像
	private String avatarUrl;	
	//在线状态 离线 在线
	private boolean isOnline;
	//游戏状态  游戏中 准备中 准备完成 准备翻牌 已翻牌 准备摊牌 已摊牌 准备下注 下注完成
	private int status;
	//消息处理器
	protected MsgHandler msgHandler; 
	//用户session
	private Session session;
	//消息格式化对象
	public Response resp=new Response();
	//消息模板
	Template tmpl=new Template();
	
	//无参构造函数
	public GamerSocketBase(){
		
	}


	//进入房间
	public void joinRoom(GamerSocket gamerSoket,String roomid){
		Room room=RoomMgr.getIntance().joinRoom(gamerSoket, roomid);
		if(room!=null){
			//setRoom(room);
			sendMsgToSelf(resp.normal(tmpl.joinRoomSuccess(room.getRoomid())));		
		}else{
			sendMsgToSelf(resp.error("房间不存在或已经关闭！"));
		}
	}
	//离开房间
	public void leaveRoom(){
		setOnline(false);
		Log.d(this.getNickName()+"玩家离场");
	}

	

	
	
	/**
	 * 以下是get set 函数
	 */
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return this.session;
	}
	
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	public MsgHandler getMsgHandler() {
		return msgHandler;
	}
	public void setMsgHandler(MsgHandler msgHandler) {
		this.msgHandler = msgHandler;
	}
	

	
	//发送信息
  	public synchronized void sendMsgToSelf(String msg){
  		//判断用户是否在线，如果在线就发送如果没有就不发送
  		boolean isline=this.isOnline();
  		if(!this.isOnline()){
  			return;
  		}
  		//Log.d(msg);
  		try {
  			if(!this.getSession().isOpen()){
  				Log.d(this.getNickName()+"已经断开连接");
  				return;
  			};
  			this.getSession().getBasicRemote().sendText(msg+"        "+Math.random());
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  	}

	
	
}
