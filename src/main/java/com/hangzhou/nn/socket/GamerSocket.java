package com.hangzhou.nn.socket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EndpointConfig;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;

import com.hangzhou.nn.common.Log;
import com.hangzhou.nn.message.Message;
import com.hangzhou.nn.role.Gamer;
import com.hangzhou.nn.wx.UserInfo;
import com.hangzhou.nn.wx.WXInfoBean;

@ServerEndpoint("/login")
public class GamerSocket extends Gamer{
	String build="dev";
	//并不需要管理大厅中（只连接没有进入房间）的用户,只需要管理进入房间里的用户
	//private static CopyOnWriteArraySet<GamerSocket> sokectManager = new CopyOnWriteArraySet<GamerSocket>();

	 /**
	  * 客户端连接入口
	  * @param session
	  * @param config
	  * @throws IOException
	  */
    @OnOpen
	public void onOpen(Session session,EndpointConfig config) throws IOException{
    	Log.d("创建了一个新得玩家连接");
    	//获取参数
		Map<String, List<String>> parameterMap=session.getRequestParameterMap();
		String nickName,openid,roomid,avatarUrl;
		
		
		System.out.println("获取到code");;
		String code=parameterMap.get("code").get(0);
		UserInfo userinfo=new UserInfo();
		
		
		
		WXInfoBean info;		
		
		if(build.equals("dev")){
			//模拟微信信息
			String userid=parameterMap.get("userid").get(0);
			info=new WXInfoBean();
			info.setOpenid(userid);
			info.setNickName("test_"+(int)(Math.random()*10000));
			info.setHeadimgurl("../../static/images/avatar_default.png");
			
		}else{
			//获取微信信息
			String userWXInfo=userinfo.getUserInfo(code);
			info=JSON.parseObject(userWXInfo, WXInfoBean.class);	
		}
		
		nickName=info.getNickName();
		openid=info.getOpenid();
		avatarUrl=info.getHeadimgurl();
		
		System.out.println("获取到 nickName:"+nickName+"  openid:"+openid+"   avatarUrl:"+avatarUrl);;
		
		
		this.setNickName(nickName);
		this.setId(openid);
		this.setAvatarUrl(avatarUrl);
		this.setOnline(true);
		this.setSession(session);

		
		

		//加入在线队列
		//sokectManager.add(this); 
		//分配房间
		//System.out.println(this.getNickName()+"（识别码："+this.getSession().hashCode()+" id:"+id+"）") ;
	}
    
    
    /**
     * 接收到客户端发送消息
     * @param message
     * @param session
     */
    @OnMessage
	public void onMessage(String message, Session session) {
    	Log.d("接收到客户端消息："+message);
    	//格式化客户端消息并处理消息所对应的事件
    	handleMsg(message);
    }
    
    /**
     * 发生错误（用户断开了连接）
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
    	super.leaveRoom();
//    	System.out.println(error);
    	//如果该用户已经进入房间 通知荷官该玩家离场
    	if(this.getRoom()!=null){
    		this.getRoom().getDealer().gamerLeaveRoom(this);
    	}
    	
    }
    
    //处理客户端消息
    public void handleMsg(String message){
    	if(message==null){
    		sendMsgToSelf(resp.error("请求内容不能为空"));
    		return;
    	}
    	
    	Message msg=JSON.parseObject(message,Message.class);
    	
    	try{
    		
    		if(msg.action.equals("join")){
    			super.joinRoom(this,msg.getContent());
    		}
    		
			if(msg.action.equals("readying")){
				super.doReadying();
			}

			
			if(msg.action.equals("setWager")){
				int multiple=Integer.parseInt(msg.getContent());
				super.settingWager(multiple);
			}
			
			if(msg.action.equals("openCard")){
				int multiple=Integer.parseInt(msg.getContent());
				super.openCard(multiple);
			}
			
			if(msg.action.equals("setShowdown")){
				super.showdown();
			}
			
			if(msg.action.equals("getCurrentGameInfo")){
				super.getCurrentGameInfo();
			}
			
			if(msg.action.equals("getCurrentRoomInfo")){
				super.getCurrentRoomInfo();
			}
    		

    	}catch(Exception e){
    		e.printStackTrace();
    		sendMsgToSelf(resp.error("意外的请求内容"));
    	}
    }
    
    

}
