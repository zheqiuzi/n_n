package com.hangzhou.nn.room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hangzhou.nn.room.Room;
import com.hangzhou.nn.common.Response;
import com.hangzhou.nn.role.Gamer;


public  class RoomMgr {
	Response resp=new Response();
	List<Room> rooms;
	public static RoomMgr roomMgr;
	public RoomMgr(){
		if(rooms==null){
			rooms=new ArrayList<Room>();
		}
	}
	public static RoomMgr getIntance(){
		if(roomMgr==null){
			roomMgr=new RoomMgr();
		}
		return roomMgr;
	}
	//创建房间
	public String craterRoom(){
		Room room=new Room();
		rooms.add(room);
		return room.getRoomid();
	}
	//进入房间
	public Room joinRoom(Gamer gamer,String roomid){
		for(int i=0;i<rooms.size();i++){
			if(roomid.equals(rooms.get(i).getRoomid())){
				rooms.get(i).join(gamer);
				return rooms.get(i);
			}
		}
		return null;
	}
	//获取房间
	public Room getRoom(String roomid){
		return null;
	}
	public String searchRoom(String roomid){
		String str;
		Room room=getRoom(roomid);
		if(room!=null){
			str=resp.normal(room.getRoomid());
		}else{
			str=resp.error("房间已关闭或不存在");
		}
		return "";
	}
}
