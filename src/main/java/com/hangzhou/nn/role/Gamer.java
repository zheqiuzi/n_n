package com.hangzhou.nn.role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

import com.hangzhou.nn.room.Room;
import com.hangzhou.nn.room.RoomMgr;
import com.alibaba.fastjson.JSON;
import com.hangzhou.nn.card.Card;
import com.hangzhou.nn.common.Log;
import com.hangzhou.nn.common.Response;
import com.hangzhou.nn.common.Template;
import com.hangzhou.nn.game.GameResult;

public class Gamer {
	//游戏状态码
	public static final int UNPREPARED=0,//未准备的
		READY_WILL=100,//开始准备
		READY_END=101,//准备结束
		SET_WAGER_WILL=200,//开始下注
		SET_WAGER_END=201,//下注结束
		OPEN_CARD_WILL=300,//开始翻拍
		OPEN_CARD_END=301,//翻拍结束
		SHOWDOWN_WILL=400,//开始摊牌
		SHOWDOWN_END=401;//摊牌结束

	
	public static final int PLAYER=1,//闲家
			BANKER=2;//庄家
	//id
	private String id;
	//昵称
	private String nickName;
	//头像
	private String avatarUrl="../../static/images/avatar_default.png";	
	//在线状态 离线 在线
	private boolean isOnline;
	//游戏状态   准备中 准备完成 准备翻牌 已翻牌 准备摊牌 已摊牌 准备下注 下注完成
	private int status;
	//是否已经在游戏中
	private boolean isInCurrentGame=false;
	//牌列表
	private List<Card> cards=new ArrayList<Card>();
	//牌型
	private GameResult gameResult;
	//倍率
	private int allMultiple;
	//所在房间	
	private Room room;
	//积分
	private int balance=0;
	//用户session
	private Session session;
	//消息格式化对象
	public Response resp=new Response();
	//消息模板
	Template tmpl=new Template();
	//消息计数器
	private int msgCount=0;
	//角色
	private int role=PLAYER;
	//是否参与过一局以上的游戏
	private boolean isJoined=false;
	
	//无参构造函数
	public Gamer(){
		this.status=this.UNPREPARED;//用户默认为没有准备的
	}
	
	public void copyDataFromGamer(Gamer gamer){
		
		//如果用户离开房间后再次进入昵称改变了自动备注，避免换马甲进入混淆视听
		
		String newName=gamer.getNickName();
		String oldName=this.nickName;
//		if(!newName.equals(oldName)){
//			this.nickName=this.nickName+"("+gamer.getNickName()+")";
//		}
		this.cards=gamer.getCards();
		this.gameResult=gamer.getGameResult();
		this.allMultiple=gamer.getAllMultiple();
		this.balance=gamer.getBalance();
		this.role=gamer.getRole();
		this.status=gamer.getStatus();
		this.isJoined=gamer.isJoined();
		
	}
	//构造函数
//	public Gamer(String id,String nickName,String avatarUrl){
//		this.id=id;
//		this.nickName=nickName;
//		this.isOnline=true;
//		this.setAvatarUrl("avatar_default.png");
//	}

	//进入房间
	public void joinRoom(Gamer gamer,String roomid){
		Room room=RoomMgr.getIntance().joinRoom(gamer, roomid);
		if(room!=null){
			setRoom(room);
			sendMsgToSelf(resp.normal(tmpl.joinRoomSuccess(room.getRoomid())));		
		}else{
			sendMsgToSelf(resp.error("房间不存在或已经关闭！"));
		}
	}
	//离开房间
	public void leaveRoom(){
		setOnline(false);
		Log.d(this.getNickName()+"玩家离场");
		//告诉荷官有玩家离场
	}

	//初始化
	public void init(int mult){
		//this.status=this.UNPREPARED;//用户默认为没有准备的
		cards=new ArrayList<Card>();
		gameResult=null;
		allMultiple=mult;
	}

	//准备
	public void doReadying(){
		room.getDealer().doReadying(this);
	}

	//下注
	public void settingWager(int mult){
		room.getDealer().doSettingWager(this,mult);
	}
	

	//接收一张牌 
	public void receive(Card card){
		this.cards.add(card);
		this.sendMsgToSelf(resp.normal(tmpl.readCards(this,readSelfVisibleCards())));
	}
	
	/**
	 * 读取自身可见全部牌
	 */	
	public String readSelfVisibleCards(){
		List<Card> list=new ArrayList<Card>();
		for(int i=0;i<this.getCards().size();i++){
			Card card=this.getCards().get(i);
			//sendMsg(rs.normal(tmpl.receiveCard(card.getSize(), card.getColor(),card.isVisible(),i)));
			if(this.getCards().get(i).isVisible()){
				list.add(this.getCards().get(i));
			}else{
				//自己不可视的牌用一张空牌代替
				list.add(new Card(0,0,false,false));
			}
			
		}
		String json=JSON.toJSONString(list); 
		return json;
	}

	//换牌 需校验
	public void x(){}

	//翻牌(翻暗牌)（实际上为 下注+翻牌,下注成功会返回true）
	public void openCard(int mult){
		room.getDealer().doOpenCard(this,mult);
		
	}
	
	//摊牌
	public void showdown() {
		room.getDealer().doShowdown(this);
	}
	
	//获取个人信息(获取可视的信息)
	public void getCurrentGameInfo(){
		
	}
	//获取房间内的对局信息
	public void getCurrentRoomInfo(){
		Dealer dealer=room.getDealer();
		String roomInfo=dealer.getCurrentRoomInfo(this);
		sendMsgToSelf(resp.normal(tmpl.getCurrentRoomInfo(roomInfo)));
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
			this.getSession().getBasicRemote().sendText(msg+"#9879874654564987984654#"+msgCount());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 获取用户信息
	 * @param list
	 * @return
	 */
	public String getInfo(List<Card> list) {
		String cardsJson=JSON.toJSONString(list);
		String gameResultJson=JSON.toJSONString(gameResult);
		return "{isOnline:" + isOnline + ", id:'" + id + "', nickName:'" + nickName + "', avatarUrl:'" + avatarUrl
				+ "', cards:" + cardsJson + ", status:'" + status + "', allMultiple:'"
				+ allMultiple + "', gameResult:" + gameResultJson + ", balance:" + balance + ",role:"+role+"}";
	}
	public String getInfoToSelf(){
		return getInfo(getVisibleCards("self"));
	}
	//用于展示给其它用户可看的信息
	public String getInfoToOther(){
		return getInfo(getVisibleCards("other"));
	}
	
	/**
	 * 获取自己可见的牌
	 * @param visibleType self:代表自己可见，other:代表别人可见
	 * @return
	 */
	
	public List<Card> getVisibleCards(String visibleType){
		List<Card> list=new ArrayList<Card>();
		for(int i=0;i<this.getCards().size();i++){
			
			if(visibleType.equals("self")&&this.getCards().get(i).isVisible()){
				//自己可见
				list.add(this.getCards().get(i));
			}else if(visibleType.equals("other")){	
				if(this.getCards().get(i).isOtherVisible()){
					//别人可见
					list.add(this.getCards().get(i));
				}else if(this.getCards().get(i).isVisible()){
					//自己可见且别人不可见 插入一张空牌
					Card card=new Card(0,0,false,false);
					list.add(card);
				}
				
				
				
			}
			
		}
		return list;
	}

	public void setAllCardsOtherVisible(boolean visible){
		for(int i=0;i<this.getCards().size();i++){					
			this.getCards().get(i).setOtherVisible(visible);			
		}
	}

	
	/**
	 * 余额加减处理
	 */
	
	public void addBalance(int m){
		this.balance=balance+m;
	}
	
	public void subductionBalance(int m){
		this.balance=balance-m;
	}
	
	/**
	 * 消息计数器
	 */
	
	private int msgCount(){
		return this.msgCount++;
		
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

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public GameResult getGameResult() {
		return gameResult;
	}

	public void setGameResult(GameResult gameResult) {
		this.gameResult = gameResult;
	}

	public int getAllMultiple() {
		return allMultiple;
	}

	public void setAllMultiple(int allMultiple) {
		this.allMultiple = allMultiple;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public boolean isJoined() {
		return isJoined;
	}

	public void setJoined(boolean isJoined) {
		this.isJoined = isJoined;
	}

	public boolean isInCurrentGame() {
		return isInCurrentGame;
	}

	public void setInCurrentGame(boolean isInCurrentGame) {
		this.isInCurrentGame = isInCurrentGame;
	}
	
	
	
	
//	public void initCards() {
//		this.cards = new ArrayList<Card>();
//	}
	
	
	
	
	
	

}
