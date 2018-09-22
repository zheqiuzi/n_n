package com.hangzhou.nn.role;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.hangzhou.nn.room.RoomMgr;



@ServerEndpoint("/roomAdmin")
public class RoomAdmin {
	private static CopyOnWriteArraySet<RoomAdmin> sokectManager = new CopyOnWriteArraySet<RoomAdmin>();
	/*客户端连接入事件*/
    @OnOpen
	public void onOpen(Session session,EndpointConfig config) throws IOException{
    	System.out.println("有管理员登陆");
    }
    /*接收到客户端消息*/
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		System.out.println(message+"--");
		if(message.equals("create")){
			String roomid=RoomMgr.getIntance().craterRoom();
			session.getBasicRemote().sendText("roomid:"+roomid);
		}
	}
	
	//客户端需要关闭才不会报错
	  @OnClose
	  public void onClose(){    
	      System.out.println("有一连接关闭！");
	  }
	  //报错
	  @OnError
	  public void onError(Throwable t){
	  	System.out.println(t);
	  }
}
