package com.hangzhou.nn.role;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSON;
import com.hangzhou.nn.bill.Bill;
import com.hangzhou.nn.card.Card;
import com.hangzhou.nn.common.Log;
import com.hangzhou.nn.common.Response;
import com.hangzhou.nn.common.Template;
import com.hangzhou.nn.game.GameResult;
import com.hangzhou.nn.game.GamerBlance;
import com.hangzhou.nn.room.Room;
import com.hangzhou.nn.timer.Timer;
import com.hangzhou.nn.timer.TimerCallback;

public class Dealer {
	//荷官状态码
	private final int UNPREPARED=0,//未准备的
		READY_WILL=100,//开始准备
		READY_END=101,//准备结束
		SET_WAGER_WILL=200,//(准备后)开始抢庄
		SET_WAGER_END=201,//抢庄结束
		OPEN_CARD_WILL=300,//开始翻牌
		OPEN_CARD_END=301,//翻拍结束
		SHOWDOWN_WILL=400,//开始摊牌
		SHOWDOWN_END=401,//摊牌结束
		ONECE_GAME_OVER=500,//一局游戏结束
		GAME_OVER=600;//所有游戏结束
		
	//限制牌局场数
	public int gameCount=10;
	public int currentGameCount=0;
	//帐单
	public List<Bill> bills=new ArrayList<Bill>();
	//流水线名称
	final int SUSPEND=-2,//暂停游戏（给予用户考虑或者查看当前牌局） 
			WAITING=-1,//等待 
			INITGAMER=0,//初始化游戏
			READING=100,//准备
			SETTINGAGER=200,//下注
			SENDCARD=300,//发牌
			SENDCARD_VISIBLE_YES=301,
			SENDCARD_VISIBLE_NO=302,
			SHOWDOWN=400,//翻牌
			CREATEBILL=500,//生成账单
			STATISTICSBILLS=600,//清算
			CLOSEROOM=700,//关闭房间
			OPENCARD=800,//翻牌		
			CREATEBLANCELIST=900//创建玩家最终余额列表
			;//翻暗牌
		
	
	//流水线
	public int[] beltline=new int[]{READING,INITGAMER,SETTINGAGER,SENDCARD,OPENCARD,SHOWDOWN,ONECE_GAME_OVER};
//	public int[] beltline=new int[]{INITGAMER,READING,SETTINGAGER};
	//默认超时时间
	public int defaultTimout=8;
	//默认下注倍数
	public int defaultSetWagerMult=1;
	public int maxSetWagerMult=10;
	public int minSetWagerMult=1;
	//默认基础倍数
	public int defaultBaseMult=1;
	//所在房间
	public Room room;
	//状态
	int status;
	//最小开局人数
	int minStartNum=2;
	//房间最多容纳人数
	int maxGamerNum=9;
	//牌盒
	public List<Card> cardsBox;	
	//当前抢庄最大庄家列表
	public List<Gamer> bankers;
	//抢庄成功的庄家
	public Gamer banker;
	//当前已下注最大倍数
	public int currentMaxMult=1;
	//消息格式化对象
	public Response resp=new Response();
	//消息模板
	Template tmpl=new Template();
	//锁
	 public Lock lock = new ReentrantLock();
	//开始准备条件锁
	 public Condition readyingConditionLock = lock.newCondition();
	 //结束准备条件锁
	 public Condition readiedConditionLock = lock.newCondition();
	 //结束下注条件锁
	 public Condition endSettingWagerConditionLock= lock.newCondition();
	 //结束翻牌条件锁
	 public Condition endOpenCardConditionLock= lock.newCondition();
	 //结束摊牌条件
	 public Condition endShowdownConditionLock= lock.newCondition();
	 
	 

	//构造器
	public Dealer(Room room){
		log("荷官被激活");
		this.room=room;
		//荷官一经创建便开始游戏
		this.start();
	}
	
	//开局
	public void start(){
		
		Thread t=new Thread(new Runnable(){

			public void run() {
				log("游戏线程开启");
				//第一次进入游戏 需要先初始化游戏
				initGamer();
				while(currentGameCount<gameCount){
					log("进入游戏流水线");					
					//进入流水线
					log("\n\n 第"+(currentGameCount+1)+"局游戏 开始-------------------------\n\n ");
					executeBeltline(beltline);
					log("\n\n 第"+(currentGameCount+1)+"局游戏 结束-------------------------\n\n ");		
					currentGameCount++;
				}	
				//发送账单用于客户端显示
				executeModule(STATISTICSBILLS);
				executeModule(CLOSEROOM);
			}
			
		});
		
		t.start();
		
		
		
	}

	
	//初始化游戏
	public void initGamer(){
		
		log(">>>执行初始化游戏模块");
		
		//初始化庄家id
		bankers=new ArrayList();
		banker=null;
		
		log("初始化在线用户信息");
		
		//初始化房间所有在线用户游戏状态为未准备
		List<Gamer> gameList=room.getGamerList();
		for(int i=0;i<gameList.size();i++){
			Gamer gamer=gameList.get(i);
			gamer.setGameResult(null);
			gamer.setCards(new ArrayList<Card>());
//			gamer.setStatus(Gamer.UNPREPARED);
			gamer.setRole(Gamer.PLAYER);
			gamer.init(this.defaultBaseMult);
			//如果是离线用户设置为未准备状态
			if(!gamer.isOnline()){
				gamer.setStatus(Gamer.UNPREPARED);
			}
			
		}
		//初始化牌盒子
		log("初始化牌盒子信息");
		cardsBox=new ArrayList<Card>();
		for(int i=1;i<=13;i++){
			cardsBox.add(new Card(i,Card.HEITAO));
			cardsBox.add(new Card(i,Card.HONGTAO));
			cardsBox.add(new Card(i,Card.MEIHUA));
			cardsBox.add(new Card(i,Card.FANGKUAI));
		}
		//初始化当前倍数
		currentMaxMult=1;
		//初始化荷官游戏状态
		
	}
	
	/******************玩家准备模块*********************/
	
	Timer readyTimer;
	public void readyModule(){

		lock.lock();
		try {
			
			while(room.getOnlineGamers().size()<minStartNum){
				log("人数不足，无法准备!");
				readyingConditionLock.await();
			}			
			log("开始准备");
			willReady();
			
			
			int peple=room.getOnlineGamers().size();
			//如果房间所有人都准备了 就准备结束	
			while(room.getStateGamers(Gamer.READY_END).size()<peple){
				log("等待其它人准备");
				readiedConditionLock.await();
				peple=room.getOnlineGamers().size();
				Log.d("------当前房间人数："+peple);
			}			
			log("已经全部准备");
			endReadied();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		
		
		
	}
	//开始准备
	public void willReady(){
//		lock.lock();
		log(">>>执行用户准备模块");
		changeAllStatus(Gamer.READY_WILL);
		//设置荷官为准备状态
		this.setStatus(this.READY_WILL);
		//开始准备 给房间所有在线用户发送准备指令 并开始倒计时	readierAble
		this.sendMsgToOnline(resp.normal(tmpl.readierAble()));
		if(true){
			return;
		}
		readyTimer=new Timer();
		readyTimer.startTimer(defaultTimout, new TimerCallback(){

			public void start() {
				// TODO Auto-generated method stub
				String msg=resp.normal(tmpl.timerStart("readying",defaultTimout));
				sendMsgToOnline(msg);
				
			}

			public void heartbeat(int i) {
				// TODO Auto-generated method stub 每隔一秒钟给游戏中的用户发一个时间心跳
				log("剩余时间："+(i));
				String msg=resp.normal(tmpl.timerHeartbeat("readying",i));
				sendMsgToOnline(msg);//倒计时秒数 房间所有在线用户都可以看到
			}

			public void timeOut() {
				// TODO Auto-generated method stub
				
				String msg=resp.normal(tmpl.timerTimeout("readying"));
				sendMsgToOnline(msg);//倒计时秒数 房间所有在线用户都可以看到
				setReadied();
			}

			public void interrupt() {
				// TODO Auto-generated method stub
			}

			public void destroy() {
				// TODO Auto-generated method stub
				
			}
			
			//把所有未准备的在线玩家都默认为准备
			public void setReadied(){
				//初始化房间所有在线用户属性 除余额为累计属性外，其它的都初始化为
				List<Gamer> gameList=room.getGamerList();
				for(int i=0;i<gameList.size();i++){
					if(gameList.get(i).getStatus()!=Gamer.READY_END){
						doReadying(gameList.get(i));
					}
				}
			}
			
		});
		
	}
	//有玩家准备
	public void doReadying(Gamer gamer){
		log("玩家准备");
		//某个玩家开始准备
		if(this.status!=this.READY_WILL){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("游戏已经开始")));
			return ;
		}
		
		if(gamer.getStatus()==Gamer.READY_END){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("您已经准备了")));
			return ;
		}
		
		if(gamer.getStatus()!=Gamer.READY_WILL){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("设置准备状态失败")));
			return ;
		}
		//初始化玩家信息设置玩家状态
//		gamer.init(this.defaultBaseMult);
		gamer.setStatus(Gamer.READY_END);
		gamer.setJoined(true);
		gamer.setInCurrentGame(true);
		
		//广播玩家准备事件
		sendMsgToOnline(resp.normal(tmpl.gamerReadied(gamer)));
		Log.d(gamer.getNickName()+"已经准备完成");
		
	
		
		lock.lock();
		readiedConditionLock.signalAll();
		lock.unlock();
	
		
	}	
	//结束准备
	public void endReadied(){
//		readyTimer.interrupt();
		//设置荷官为准备状态
		this.setStatus(this.READY_END);
		//广播结束准备事件
		sendMsgToOnline(resp.normal(tmpl.readierEd()));
		log("结束准备");
	}
		
	/******************玩家抢庄模块*********************/
	
	Timer settingWagerTimer;
	public synchronized void settingWager(){
		lock.lock();
		//告诉已经准备的用户可以开始抢庄
		log("-----------------------------------------------------------------------xiaz");
		willSettingWager();
		try {
			//如果房间SET_WAGER状态的用户个数为零说明全部都下注了	
			log("单签房间人数："+room.getStateGamers(Gamer.SET_WAGER_WILL).size());
			while(room.getStateGamers(Gamer.SET_WAGER_WILL).size()>0){
				log("等待其它人下注");
				System.out.println("下注锁被执行");
				endSettingWagerConditionLock.await();
			}	
			endSetWager();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	//玩家下注
	public void willSettingWager(){
		log(">>>执行用户下注模块");		
		//设置除去未准备的用户为开始下注状态
		changeExcludeStatus(Gamer.UNPREPARED,Gamer.SET_WAGER_WILL);		
		//给房间所有在线用户发送下注指令 并开始倒计时
		sendMsgToGamer(resp.normal(tmpl.setWagerAble()));
		//设置荷官为下注状态
		this.setStatus(this.SET_WAGER_WILL);
		//开始摊牌 给房间所有在线用户发送摊牌指令 并开始倒计时		
		settingWagerTimer=new Timer();
		settingWagerTimer.startTimer(defaultTimout, new TimerCallback(){

			public void start() {
				// TODO Auto-generated method stub				
				String msg=resp.normal(tmpl.timerStart("settingWager",defaultTimout));
				sendMsgToOnline(msg);
			}

			public void heartbeat(int i) {
				// TODO Auto-generated method stub 每隔一秒钟给游戏中的用户发一个时间心跳
				String msg=resp.normal(tmpl.timerHeartbeat("settingWager",i));
				sendMsgToOnline(msg);//倒计时秒数 房间所有在线用户都可以看到
			}

			public void timeOut() {
				// TODO Auto-generated method stub
				String msg=resp.normal(tmpl.timerTimeout("settingWager"));
				sendMsgToOnline(msg);//倒计时秒数 房间所有在线用户都可以看到
				doAllSettingWager();
			}

			public void interrupt() {
				// TODO Auto-generated method stub

			}

			public void destroy() {
				// TODO Auto-generated method stub
				
			}
			
			//把所有未准备的在线玩家都默认为准备
			public void doAllSettingWager(){
				//初始化房间所有在线用户属性 除余额为累计属性外，其它的都初始化为
				List<Gamer> gameList=room.getGamerList();
				for(int i=0;i<gameList.size();i++){
					if(gameList.get(i).getStatus()==Gamer.SET_WAGER_WILL){
						doSettingWager(gameList.get(i),defaultSetWagerMult);
					}
				}
			}
			
		});
		
		
	}
	//有用户下注
	public boolean doSettingWager(Gamer gamer,int mult){
		
		//判断倍数是否在限定范围
		if(mult<this.minSetWagerMult||mult>this.maxSetWagerMult){
			gamer.sendMsgToSelf(resp.error("下注超出限定范围"));
			return false;
		}
		//判断是否可以下注
		if(this.status!=this.SET_WAGER_WILL){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("未到下注时间")));
			return false;
		}
		//判断是否已经下注成功
		if(gamer.getStatus()==Gamer.SET_WAGER_END){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("您已经下注了")));
			return false;
		}
		//异常状态下注
		if(gamer.getStatus()!=Gamer.SET_WAGER_WILL){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("您不在可下注状态，设置下注失败")));
			return false;
		}
		
		//设置下注
		gamer.setAllMultiple(gamer.getAllMultiple()*mult);
			
		//某个玩家下注
		gamer.setStatus(Gamer.SET_WAGER_END);
		
		log(gamer.getNickName()+"已经下注");
		sendMsgToOnline(resp.normal(tmpl.gamerSetWager(gamer)));
		
		//在此确定谁是庄家如果当前下注玩家与最大下注庄家倍数相等，就加入到列表下注结束后随机抽选庄家，如果大于之前所有庄家倍数则清空之前庄家，加入当前用户为新庄家
		if(mult==currentMaxMult){
			bankers.add(gamer);
		}else if(mult>currentMaxMult){
			bankers.clear();
			bankers.add(gamer);
			currentMaxMult=mult;
		}
		
		System.out.println("用户下注----------------："+mult+"-----"+currentMaxMult);
				
		lock.lock();
		endSettingWagerConditionLock.signalAll();
		lock.unlock();
		
		return true;

	}
	//结束下注 在此确定庄家 如果有相同倍数抢庄，随机产生一位庄家
	public void endSetWager(){
		settingWagerTimer.interrupt();
		log("结束下注");
		this.setStatus(this.SET_WAGER_END);
		//所有游戏中玩家都默认下注倍数
		sendMsgToOnline(resp.normal(tmpl.setWagerEd()));
		//确定庄家
		setBanker();
		
	}
	
	
	/******************确定庄家模块*********************/
	public void setBanker(){
		int _r=-1;
		if(bankers==null||bankers.size()<=0){
			System.out.println("异常");
			this.sendMsgToOnline(resp.error("游戏发生异常,无法继续，请退出游戏！"));
			return;
		}
		if(bankers.size()>1){
			System.out.println("随机数");
			_r=(int)(Math.random()*bankers.size());
			
		}else{
			System.out.println("无随机数");
			_r=0;
		}
		
		System.out.println("随机数："+_r);
		
		banker=bankers.get(_r);
		
		//设置庄家状态
		bankers.get(0).setRole(Gamer.BANKER);
		
		log("确定当前庄家为："+banker.getNickName());
		sendMsgToOnline(resp.normal(tmpl.appointBanker(banker)));
		bankers.clear();
	}
	
	/******************玩家翻牌模块*********************/
	
	Timer openCardTimer;
	public synchronized void openCardModule(){
		lock.lock();
		//告诉已经准备的用户可以开始下注
		log("-----------------------------------------------------------------------xiaz");
		willOpenCard();
		try {
			//如果房间SET_WAGER状态的用户个数为零说明全部都下注了	
			log("单签房间人数："+room.getStateGamers(Gamer.OPEN_CARD_WILL).size());
			while(room.getStateGamers(Gamer.OPEN_CARD_WILL).size()>0){
				log("等待其它人翻牌");
				endOpenCardConditionLock.await();
			}	
			endOpenCard();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	//玩家下注
	public void willOpenCard(){
		log(">>>执行用户下注翻牌模块");		
		//设置除去未准备的用户为开始下注状态
		changeExcludeStatus(Gamer.UNPREPARED,Gamer.OPEN_CARD_WILL);		
		//给房间所有在线用户发送下注指令 并开始倒计时
		sendMsgToGamer(resp.normal(tmpl.openCardAble()));
		//设置荷官为下注翻牌状态
		this.setStatus(this.OPEN_CARD_WILL);
		//开始摊牌 给房间所有在线用户发送翻牌指令 并开始倒计时		
		openCardTimer=new Timer();
		openCardTimer.startTimer(defaultTimout, new TimerCallback(){

			public void start() {
				// TODO Auto-generated method stub				
				String msg=resp.normal(tmpl.timerStart("openCard",defaultTimout));
				sendMsgToOnline(msg);
			}

			public void heartbeat(int i) {
				// TODO Auto-generated method stub 每隔一秒钟给游戏中的用户发一个时间心跳
				String msg=resp.normal(tmpl.timerHeartbeat("openCard",i));
				sendMsgToOnline(msg);//倒计时秒数 房间所有在线用户都可以看到
			}

			public void timeOut() {
				// TODO Auto-generated method stub
				String msg=resp.normal(tmpl.timerTimeout("openCard"));
				sendMsgToOnline(msg);//倒计时秒数 房间所有在线用户都可以看到
				doAllOpenCard();
			}

			public void interrupt() {
				// TODO Auto-generated method stub

			}

			public void destroy() {
				// TODO Auto-generated method stub
				
			}
			
			//把所有未准备的在线玩家都默认为准备
			public void doAllOpenCard(){
				//初始化房间所有在线用户属性 除余额为累计属性外，其它的都初始化为
				List<Gamer> gameList=room.getGamerList();
				for(int i=0;i<gameList.size();i++){
					if(gameList.get(i).getStatus()==Gamer.OPEN_CARD_WILL){
						doOpenCard(gameList.get(i),defaultSetWagerMult);
					}
				}
			}
			
		});
		
		
	}
	//有用户翻牌
	public boolean doOpenCard(Gamer gamer,int mult){
		
		//判断倍数是否在限定范围
		if(mult<this.minSetWagerMult||mult>this.maxSetWagerMult){
			gamer.sendMsgToSelf(resp.error("翻牌下注超出限定范围"));
			return false;
		}
		//判断是否可以翻牌
		if(this.status!=this.OPEN_CARD_WILL){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("未到翻牌时间")));
			return false;
		}
		//判断是否已经下注成功
		if(gamer.getStatus()==Gamer.OPEN_CARD_END){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("您已经翻牌了")));
			return false;
		}
		//异常状态翻牌
		if(gamer.getStatus()!=Gamer.OPEN_CARD_WILL){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("您不在可翻牌状态，设置翻牌失败")));
			return false;
		}
		
		//设置翻牌倍数
		gamer.setAllMultiple(gamer.getAllMultiple()*mult);
		
		//翻开自己的暗牌
		List<Card> cards=gamer.getCards();
		for(int i=0;i<cards.size();i++){
			if(!cards.get(i).isVisible()){
				cards.get(i).setVisible(true);
			}
		}
		//给自己发送所有的名牌
		//getMyCards
		gamer.sendMsgToSelf(resp.normal(tmpl.getMyCards(gamer)));
		//某个玩家下注翻牌
		gamer.setStatus(Gamer.OPEN_CARD_END);
		
		sendMsgToOnline(resp.normal(tmpl.gamerOpened(gamer)));

		lock.lock();
		endOpenCardConditionLock.signalAll();
		lock.unlock();
		
		return true;

	}
	//结束翻牌
	public void endOpenCard(){
		openCardTimer.interrupt();
		log("结束翻牌");
		this.setStatus(this.OPEN_CARD_END);
		//所有游戏中未翻牌玩家都默认下注倍数
		sendMsgToOnline(resp.normal(tmpl.openCardEd()));
	}
	
	
	
	public void sendCard(){
		
		sendCard(true);
		sendCard(true);
		sendCard(true);
		
		sendCard(false);
		sendCard(false);
		
	}
	//开始发牌
	public void sendCard(boolean isVisible){
		log("开始发牌");
//		this.sendMsgToOnline("发牌");
		
		//获取游戏中玩家列表
		List<Gamer> list=this.getExcludeOfStatus(Gamer.UNPREPARED);
		
		
		//给房间所有游戏中玩家发牌
		
		for(int j=0;j<list.size();j++){
			int i=(int)(Math.random()*(cardsBox.size()-j));
			Card card=cardsBox.get(i);
			card.setVisible(isVisible);
			cardsBox.remove(i);
			list.get(j).receive(card);
		}
		
		
	}	


	
	/******************玩家摊牌模块*********************/
	Timer showdownTimer;
	public synchronized void showdown(){
		lock.lock();
		willShowdown();		
		
		try {
			while(room.getStateGamers(Gamer.SHOWDOWN_WILL).size()>0){
				log("等待其它人摊牌");
				endShowdownConditionLock.await();
			}
			endShowdown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			lock.unlock();
		}

	}
	
	//开始摊牌
	public void willShowdown(){
		changeExcludeStatus(Gamer.UNPREPARED,Gamer.SHOWDOWN_WILL);
		this.setStatus(this.SHOWDOWN_WILL);
		log("玩家摊牌");
		sendMsgToOnline(resp.normal(tmpl.showdownAble()));
		showdownTimer=new Timer();
		showdownTimer.startTimer(defaultTimout, new TimerCallback(){

			public void start() {
//				sendMsgToOnline("倒计时开始");
				String msg=resp.normal(tmpl.timerStart("showdown",defaultTimout));
				sendMsgToOnline(msg);
			}

			public void heartbeat(int i) {
				log("剩余时间："+(i));
				String msg=resp.normal(tmpl.timerHeartbeat("showdown",i));
				sendMsgToOnline(msg);//倒计时秒数 房间所有在线用户都可以看到
			}

			public void timeOut() {
				String msg=resp.normal(tmpl.timerTimeout("showdown"));
				sendMsgToOnline(msg);//倒计时秒数 房间所有在线用户都可以看到
				doAllShowdown();
			}

			public void interrupt() {
				// TODO Auto-generated method stub
				doAllShowdown();
			}

			public void destroy() {
				// TODO Auto-generated method stub
				
			}
			
			//把所有未准备的在线玩家都默认为准备
			public void doAllShowdown(){
				//初始化房间所有在线用户属性 除余额为累计属性外，其它的都初始化为
				List<Gamer> gameList=room.getGamerList();
				for(int i=0;i<gameList.size();i++){
					if(gameList.get(i).getStatus()==Gamer.SHOWDOWN_WILL){
						doShowdown(gameList.get(i));
					}
				}
			}
			
		});
		
	}	
	//玩家摊牌
	public void doShowdown(Gamer gamer){		

		if(this.status!=this.SHOWDOWN_WILL){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("未到摊牌时间")));
			return ;
		}
		
		if(gamer.getStatus()==Gamer.SHOWDOWN_END){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("您已经摊牌了")));
			return ;
		}
		
		if(gamer.getStatus()!=Gamer.SHOWDOWN_WILL){
			gamer.sendMsgToSelf(resp.normal(tmpl.readierError("设置摊牌失败")));
			return ;
		}
		
		log(gamer.getNickName()+"已经摊牌");
		gamer.setStatus(Gamer.SHOWDOWN_END);
		gamer.setAllCardsOtherVisible(true);
		//摊牌后计算玩家牌型
		gamer.setGameResult(new GameResult(gamer.getCards()));		
		sendMsgToOnline(resp.normal(tmpl.gamerShowdownEd(gamer)));	
		
		lock.lock();
		endShowdownConditionLock.signalAll();
		lock.unlock();
		//某个玩家摊牌
		//设置玩家摊牌状态
	}	
	//全部摊牌
	public void endShowdown(){
		log("全部摊牌");
		showdownTimer.interrupt();
		//所有游戏中玩家都默认摊牌		
		sendMsgToOnline(resp.normal(tmpl.showdownEd()));
		//摊牌结束比牌
		bipai();
	}
	
	//比牌
	
	public void bipai(){
		//获取房间中所有摊牌后的玩家
		List<Gamer> gamers=room.getStateGamers(Gamer.SHOWDOWN_END);
		
		GameResult bankerGameResult=banker.getGameResult();
		//遍历已经翻牌玩家与庄家比牌
		for(int i=0;i<gamers.size();i++){
			
			if(gamers.get(i)==banker){
				continue;
			}
			
			//玩家
			Gamer gamer=gamers.get(i);
			GameResult gamerGameResult=gamer.getGameResult();
			
			//庄家是否大于玩家牌型
			boolean isgt=bankerGameResult.gt(gamerGameResult);
			
			if(isgt){
				//如果庄家胜利 倍数为：庄家牌型倍率X闲家下注倍率
				int multiple=bankerGameResult.getCurrentCardTypeMult()*gamer.getAllMultiple();
				//庄家余额增加
				banker.addBalance(multiple);
				//闲家余额减少
				gamer.subductionBalance(multiple);
			}else{
				//如果闲家胜利 倍数为：闲家牌型倍率X闲家下注倍率
				int multiple=gamerGameResult.getCurrentCardTypeMult()*gamer.getAllMultiple();
				//庄家余额减少
				banker.subductionBalance(multiple);
				//闲家余额增加
				gamer.addBalance(multiple);
				
			}
			
			
		}
	}
	
	//等待
	public void waiting(){
		
	}
	
	

	//生成单局帐单 暂时不需要生成单局账单
	public void createBill(){
		Bill bill=new Bill();
		bills.add(bill);
		log("生成单局帐单");
	}
	
	public void oneceGameOver(){
		List<Gamer> list=room.getGamerList();
		for(int i=0;i<list.size();i++){
			list.get(i).setInCurrentGame(false);
		}
	}
	
	//统计总帐单
	public String getBills(){
		List<Gamer> gamers=room.getGamerList();
		List<Bill> bills=new ArrayList();
		for(int i=0;i<gamers.size();i++){
			Gamer gamer=gamers.get(i);
			if(gamer.isJoined()){
				Bill bill=new Bill();
				bill.setBalance(gamer.getBalance());
				bill.setId(gamer.getId());
				bill.setName(gamer.getNickName());
				bills.add(bill);
			}
			
		}
		String billsStr=JSON.toJSONString(bills);
		log("统计总帐单:"+billsStr);
		return billsStr;
	}
	
	//关闭房间
	public void closeRoom(){
		log("关闭房间");
	}
	public void statisticsBills(){
		this.status=this.GAME_OVER;
		//给在线玩家发送游戏结束通知
		sendMsgToOnline(resp.normal(tmpl.endGameBills()));
	}
	
	
	//执行流水线
	public void executeBeltline(int[] beltline){
		for(int i=0;i<beltline.length;i++){
			executeModule(beltline[i]);
		}
		
	}
	
	//执行某个流程块
	public void executeModule(int module){
		switch(module){
			case WAITING:waiting();break;
			case INITGAMER:initGamer();break;
			case READING:readyModule();break;
			case SETTINGAGER:settingWager();break;
			case SENDCARD:sendCard();break;
			case SENDCARD_VISIBLE_YES:sendCard(true);break;
			case SENDCARD_VISIBLE_NO:sendCard(false);break;
			case OPENCARD:openCardModule();break;
			case SHOWDOWN:showdown();break;
//			case CREATEBILL:createBill();break;			
			case STATISTICSBILLS:statisticsBills();break;
			case ONECE_GAME_OVER:oneceGameOver();break;
			case CLOSEROOM:closeRoom();break;
		}
	}
	
	//有新玩家进入
	public void newGamerJoined(Gamer gamer){		
		
		
		//有玩家入场 广播玩家入场信息
		this.sendMsgToOnline(resp.normal(tmpl.newOtherPlayerJoinRoom(gamer)));
		//有新玩家进入
		if(this.status==this.READY_WILL){
			gamer.setStatus(Gamer.READY_WILL);
		}
		lock.lock();
		readyingConditionLock.signalAll();
		lock.unlock();
				
		//判断当前状态 如果在准备期间判断房间人数是否符合游戏开始条件如果合适就开始游戏
		
		//如果在非准备期间 给该用户拉取当前游戏消息 不做其它操作
	}
	//玩家再次入场
	public void gamerRejoined(Gamer gamer){
		//检查庄家列表中是否有之前的对象引用
		for(int i=0;i<bankers.size();i++){
			if(bankers.get(i).getId().equals(gamer.getId())){
				bankers.remove(i);
				bankers.add(gamer);
			}
		}
		newGamerJoined(gamer);
	}
	//有玩家离场
	public void gamerLeaveRoom(Gamer gamer){
		//如果是在准备期间离场触发移除条件锁 并设置用户为未准备
		if(this.getStatus()==this.READY_WILL){
			gamer.setStatus(Gamer.UNPREPARED);
			readiedConditionLock.signalAll();
			
		}
	}
	//给房间在线玩家发送消息
	public void sendMsgToOnline(String msg){
		List<Gamer> list=room.getOnlineGamers();
		for(int i=0;i<list.size();i++){
			list.get(i).sendMsgToSelf(msg);
		}
	}
	//给游戏中玩家发送消息
	public void sendMsgToGamer(String msg){
		List<Gamer> list=room.getOnlineGamers();
		for(int i=0;i<list.size();i++){
			if(list.get(i).getStatus()!=Gamer.UNPREPARED){
				list.get(i).sendMsgToSelf(msg);
			}
			
		}
	}
	//给游戏中某一状态所有玩家发送信息
	public void sendMsgToOneStatus(String msg){
		
	}
	//给某个ID用户发送消息
	public void sendMsgToId(String id){
		
	}
	public void log(String log){
		System.out.println(log);
	}
	
	//把一种状态的用户改为另一种状态
	public void changeAllStatus(int oldStatus,int newStatus){
		List<Gamer> list=room.getStateGamers(Gamer.SET_WAGER_WILL);
		for(int i=0;i<list.size();i++){
			list.get(i).setStatus(newStatus);
		}
	}
	//把所有用户改为某种状态
	public void changeAllStatus(int newStatus){
		List<Gamer> list=room.getGamerList();
		for(int i=0;i<list.size();i++){
			list.get(i).setStatus(newStatus);
		}
	}
	
	//把排除某种状态以外其它都改为另一种状态	
	public void changeExcludeStatus(int excludeStatus,int newStatus){
		List<Gamer> list=room.getGamerList();
		for(int i=0;i<list.size();i++){
			if(list.get(i).getStatus()!=excludeStatus){
				list.get(i).setStatus(newStatus);
			}			
		}
	}
	
	//获取某一状态的玩家
//	public List<Gamer> getAllOfStatus(int status){
//		List<Gamer> list=room.getGamerList();
//		for(int i=0;i<list.size();i++){
//			if(list.get(i).getStatus()!=status){
//				list.remove(i);
//			}
//		}
//		return list;
//	}
	//获取除去某一状态的玩家	
	public List<Gamer> getExcludeOfStatus(int excludeStatus){
		List<Gamer> list=room.getGamerList();
		for(int i=0;i<list.size();i++){
			if(list.get(i).getStatus()==excludeStatus){
				list.remove(i);
			}
		}
		return list;
	}
	
	//获取当局游戏信息(当前在线和已经在游戏中的玩家)
	public String getCurrentRoomInfo(Gamer gamer){
		String billsStr=null;
		String roomInfo="";
		//获取在线玩家信息
		List<String> otherGamersInfo=new ArrayList<String>();
		for(int i=0;i<room.getOnlineOrPlayingGamer().size();i++){
			Gamer _gamer=room.getOnlineOrPlayingGamer().get(i);
//			if(_gamer.isOnline()==true && _gamer!=gamer){
//				if(_gamer==gamer){
//					otherGamersInfo.add(_gamer.getInfoToSelf());
//				}else{
//					otherGamersInfo.add(_gamer.getInfoToOther());
//				}
//				
//			}
			
			if(_gamer!=gamer){
				otherGamersInfo.add(_gamer.getInfoToOther());			
			}				
			
		}
		//获取参与过的游戏玩家信息
		if(this.status==this.GAME_OVER){
			billsStr=getBills();
		}
		
		String otherGamers=JSON.toJSONString(otherGamersInfo);
		roomInfo="{msgType:'currentGameInfo',msg:{game:{gameCount:"+this.getGameCount()+",gameCurrentCount:"+this.currentGameCount+",maxPlayerNum:"+this.maxGamerNum+",roomid:"+room.getRoomid()+"},bills:"+billsStr+",otherGamers:"+otherGamers+",my:"+gamer.getInfoToSelf()+"}}";
		return roomInfo;
	}
	
	
	/**
	 * setter getter
	 * **/
	
	public int getGameCount() {
		return gameCount;
	}

	public void setGameCount(int gameCount) {
		this.gameCount = gameCount;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
	
}
