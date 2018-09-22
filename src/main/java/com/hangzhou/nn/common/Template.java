package com.hangzhou.nn.common;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.hangzhou.nn.game.Game;
import com.hangzhou.nn.role.Dealer;
import com.hangzhou.nn.role.Gamer;
import com.hangzhou.nn.room.Room;


public class Template {
	
	
	
	
	/***********进场离场消息块 开始************/
	
	public String joinRoomSuccess(String roomid){
		String str="{msgType:'joinedRoom',msg:'success',remarks:'进入房间成功！',roomid:'"+roomid+"'}";
		return str;
	}
	public String joinRoomError(String reason){
		String str="{msgType:'joinedRoom',msg:'error',remarks:'进入房间失败！',reason:'"+reason+"'}";
		return str;
	}
	//告诉所有房间人员，有新玩家入场
	public String newOtherPlayerJoinRoom(Gamer gamer){
		String str="{msgType:'otherJoinedRoom',msg:{id:'"+gamer.getId()+"',nickName:'"+gamer.getNickName()+"',avatarUrl:'"+gamer.getAvatarUrl()+"',blance:'"+gamer.getBalance()+"'}}";
		return str;
	}
	//告诉所有房间人员，有玩家离场
	public String OtherPlayerLeaveRoom(Gamer gamer){
		String str="{msgType:'otherLeaveRoom',msg:{id:'"+gamer.getId()+"',nickName:'"+gamer.getNickName()+"',avatarUrl:'"+gamer.getAvatarUrl()+"',blance:'"+gamer.getBalance()+"'}}";
		return str;
	}
	//获取当局游戏信息
	public String getCurrentRoomInfo(String roomInfo){
	
//		String str="";
//		if(room==null){
//			str="{msgType:'currentGameInfo',msg:null}";
//			return str;
//		}
//		Dealer dealer=room.getDealer();
//		//Game game=dealer.getCurrentGame();
//		
//		List<String> gamersInfo=new ArrayList<String>();
//		for(int i=0;i<room.getOnlineGamers().size();i++){
//			Gamer gamer=room.getOnlineGamers().get(i);
//			if(gamer.isOnline()==true){
//				gamersInfo.add(gamer.getInfoToOther());
//			}
//			
//		}
//		
//		String playersJson=JSON.toJSONString(gamersInfo);

		//str="{msgType:'currentGameInfo',msg:{game:{gameCount:"+dealer.getGameCount()+",gameCurrentCount:"+dealer.getGameCurrentCount()+",maxPlayerNum:"+game.getMaxPlayerNum()+"},gamers:"+playersJson+"}}";
		return roomInfo;
	}
	
	/***********进场离场消息块 结束************/
	
	
	
	
	
	
	/***********准备消息块 开始************/
	
	//广播可以开始准备
	public String readierAble(){
		String str="{msgType:'readier',msg:'able',remarks:'游戏就绪，开始准备'}";
		return str;
	}
	//玩家结束准备回函/所有玩家设置为准备
	public String readierEd(){
		String str="{msgType:'readier',msg:'ed',remarks:'已准备'}";
		return str;
	}
	//广播某玩家已准备信息
	public String gamerReadied(Gamer gamer){
		String str="{msgType:'readier',msg:'gamerReadied',gamerid:'"+gamer.getId()+"',remarks:'已准备'}";
		return str;
	}
	
	public String readierError(String reson){
		String str="{msgType:'readier',msg:'error',reason:'"+reson+"'}";
		return str;
	}
	
	/***********时钟消息块 结束************/
	
	
	
	
	
	
	
	/***********下注消息块 开始************/
	
	//通知可以下注
	public String setWagerAble(){
		String str="{msgType:'setWager',msg:'able',remarks:'可以下注'}";
		return str;
	}
	//结束已经
	public String setWagerEd(){
		String str="{msgType:'setWager',msg:'ed',remarks:'下注完成'}";
		return str;
	}
	//某玩家已经下注
	public String gamerSetWager(Gamer gamer){
		String str="{msgType:'setWager',msg:'gamerSetWager',gamerid:'"+gamer.getId()+"',remarks:'已下注'}";
		return str;
	}
	
	//下注失败
	public String setWagerError(String reason){
		String str="{msgType:'setWager',msg:'error',reason:'"+reason+"'}";
		return str;
	}
		
//	public String grabBankerAble(){
//		String str="{msgType:'grabBanker',msg:'able',remarks:'开始抢庄'}";
//		return str;
//	}
//	
//	public String grabBankerEd(){
//		String str="{msgType:'grabBanker',msg:'ed',remarks:'抢庄成功'}";
//		return str;
//	}
//	
//	
//	public String gamerGrabBanker(Gamer gamer){
//		String str="{msgType:'grabBanker',msg:'gamerGrabBanker',gamerid:'"+gamer.getId()+"',remarks:'已准备'}";
//		return str;
//	}
//	
//	public String grabBankerError(String reson){
//		String str="{msgType:'grabbedBanker',msg:'error',reason:'"+reson+"'}";
//		return str;
//	}
	
	/***********下注消息块 结束************/
	
	/***********确定庄家 开始************/
	public String appointBanker(Gamer gamer){
		String str="{msgType:'appointBanker',msg:"+gamer.getInfoToOther()+"}";
		return str;
	}
	
		
/***********翻牌消息块 开始************/
	
	//通知可以翻牌
	public String openCardAble(){
		String str="{msgType:'openCard',msg:'able',remarks:'可以翻牌'}";
		return str;
	}
	//翻牌结束已经
	public String openCardEd(){
		String str="{msgType:'openCard',msg:'ed',remarks:'翻牌完成'}";
		return str;
	}
	//某玩家已经翻牌
	public String gamerOpened(Gamer gamer){
		String str="{msgType:'openCard',msg:'gamerOpened',gamerid:'"+gamer.getId()+"',remarks:'已翻牌'}";
		return str;
	}
	
	//翻牌失败
	public String openCardError(String reason){
		String str="{msgType:'openCard',msg:'error',reason:'"+reason+"'}";
		return str;
	}
	
	//翻开暗牌后给用户推送暗牌信息
	public String getMyCards(Gamer gamer){
		String str="{msgType:'mycards',msg:"+JSON.toJSONString(gamer.getCards())+"}";
		return str;
	}
	
	
	
	
	
	
	/***********时钟消息块 开始************/
	
	public String timerStart(String type,int time){
		String str="{msgType:'timer',msg:{timerType:'"+type+"',event:'show',remaining:"+time+"}}";
		return str;
	}
	
	public String timerHeartbeat(String type,int time){
		String str="{msgType:'timer',msg:{timerType:'"+type+"',event:'heartbeat',remaining:"+time+"}}";
		return str;
	}
	
	public String timerTimeout(String type){
		String str="{msgType:'timer',msg:{timerType:'"+type+"',event:'timeOut'}}";
		return str;
	}
	
	public String timerInterrupt(String type){
		String str="{msgType:'timer',msg:{timerType:'"+type+"',event:'interrupt'}}";
		return str;
	}
	
	/***********时钟消息块 结束************/
	
	
	
	//{status:200,data:{msgType:'receiveCard',msg:{size:1, color:1, isVisible:true}}}
	//-1表示一张新牌 获取全部牌的时候会带index
	public String receiveCard(int size,int color,boolean isVisible,int index){
		String str="{msgType:'receiveCard',msg:{size:"+size+", color:"+color+", isVisible:"+isVisible+",index:"+index+"}}";
		return str;
	}
	
	//所有的牌
	public String readCards(Gamer gamer,String cards){
		String str="{msgType:'cards',id:'"+gamer.getId()+"',cards:"+cards+"}";
		return str;
	}
	
	
	
	//通知可以摊牌
	public String showdownAble(){
		String str="{msgType:'showdown',msg:'able',remarks:'可以摊牌'}";
		return str;
	}
	//已经摊牌
	public String showdownEd(){
		String str="{msgType:'showdown',msg:'ed',remarks:'已经摊牌'}";
		return str;
	}
	//摊牌失败
	public String showdownError(String reason){
		String str="{msgType:'showdown',msg:'error',reason:'"+reason+"'}";
		return str;
	}
	
	//通知所有玩家 某玩家已经摊牌
	
	public String onePlayerShowdownEd(String id,String nickName,String cardsJson,String gameResult){
		String str="{msgType:'showdown',id:'"+id+"',nickName:'"+nickName+"',cards:"+cardsJson+",gameResult:"+gameResult+"}";
		return str;
	}
	
	public String gamerShowdownEd(Gamer gamer){
		String cards=JSON.toJSONString(gamer.getCards());
		String gameResult=JSON.toJSONString(gamer.getGameResult());
		String str="{msgType:'showdown',id:'"+gamer.getId()+"',nickName:'"+gamer.getNickName()+"',cards:"+cards+",gameResult:"+gameResult+"}";
		return str;
	}
	
	
	
	//当局游戏结束发送对局结果
	public String currentGamerBills(String bills){
		String str="{msgType:'currentGamerBills',msg:"+bills+"}";
		return str;
		
	}
	//总局数对局完成后发送对局结果
	public String endGameBills(String bills){
		String str="{msgType:'endGameBills',msg:"+bills+"}";
		return str;
		
	}
	
	//总局数对局完成后发送对局结果
	public String endGameBills(){
		String str="{msgType:'endGameBills',msg:''}";
		return str;
		
	}
	
	

	
	
}
