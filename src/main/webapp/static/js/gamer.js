// var Vue=require("vue")
// var app = new Vue({
//     el: '#app',
//     data: {
//         nickName:"xxxx",
//         message: '页面加载于 ' + new Date().toLocaleString()
//     }
// })
//
// module.exports={
//
// }

/**
 * Created by Administrator on 2017/4/18 0018.
 */

/***VUE UI相关模块处理**/
import Vue from 'vue';
import leftUser from './leftUser.vue';
import rightUser from './rightUser.vue';
import timer from './timer.vue';
import toolsBtn from './toolsBtn.vue';
import myInfo from "./my.vue"
import $ from "jquery";

var roomGamerData={
    game:{
        gameCount:'--',
        gameCurrentCount:0,
        maxPlayerNum:0
    },
    gamers:[]
}
var defaultUser={
    isOnline:true,
    id:'--',
    nickName:'--',
    avatarUrl:'../avatar_default.png',
    cards:[],
    status:'0',
    allMultiple:'0',
    gameResult:null,
    balance:0
}
var timerData={timerType:'readying',event:'show',remaining:10}
var game=new Vue({
    el: '#app',
    data:{
        msg:"hello world",
        name:"xxxxxxxxxxx",
        game:roomGamerData.game,
        gamers:roomGamerData.gamers,
        timer:timerData,
        btnsStatus:"ed",
        myid:null
    },
    components:{
        leftUser:leftUser,
        rightUser:rightUser,
        timer:timer,
        toolsBtn:toolsBtn,
        myInfo:myInfo
    },
    computed:{
        my:function(){
            var my=defaultUser;
            for(var i=0;i<this.gamers.length;i++){
                if(this.gamers[i].id==userid){
                    my=this.gamers[i]
                }
            }
            return my
        },
        otherGamer:function(){
            var otherGamer=[];
            for(var i in this.gamers){
                if(this.gamers[i].id!=this.myid){
                    otherGamer.push(this.gamers[i]);
                }
            }
            return otherGamer;
        }
    },
    methods:{
        readying:function(){
            console.log("readying");
            readying();
        },
        setwager:function(mult){
            console.log("setwager");
            setWager(mult);
        },
        opencard:function(mult){
            console.log("openCard");
            openCard(mult);
        },
        setshowdown:function(){
            console.log("setshowdown");
            setShowdown();
        }

    }
});

/***客服端消息和事件处理***/




var getQueryString=function(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}


//获取用户请求信息
var userid=getQueryString("userid");
var nickName=getQueryString("nickName");
var roomid=getQueryString("roomid");

if(userid==null||userid=="") userid=parseInt(Math.random()*1000000)+"m"+new Date().getTime();
if(nickName==null||nickName=="")userid="未知用户";
if(roomid==null||roomid==""){
    // return;
};

game.myid=game.my.id=userid;




//处理本地事件



    function send(){
        websocket.send("msg:"+getInput());
    }

    function getInput(){
        var inputText=document.getElementById("input");
        return inputText.value;
    }

//进入房间


    function joinRoom(roomid){
        websocket.send(formatMsg('join',roomid));
    }
//准备
    function readying(){
        websocket.send(formatMsg("readying","-"));
    }

//设置倍数
    function setWager(mult){
        websocket.send(formatMsg("setWager",mult));
    }
//翻牌设置倍数
    function openCard(mult){
        websocket.send(formatMsg("openCard",mult));
    }

//设置倍数
    function setShowdown(){
        websocket.send(formatMsg("setShowdown","-"));
    }

//获取房间及用户信息
    function getCurrentRoomInfo(){
        websocket.send(formatMsg("getCurrentRoomInfo","-"));
    }

    function formatMsg(action,content){
        return "{action:'"+action+"',content:'"+content+"'}";
    }




//创建sokect连接
var websocket = null;
var lastMsgTimeStamp=0;
//打开页面创建连接
createSocket(nickName,userid);
function createSocket(nick,id){

    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://localhost:8023/nn/login?nickName="+nick+"&id="+id);//+"&roomid="+roomid
    }else{
        return;
    }
    //连接成功建立的回调方法
    websocket.onopen = function () {
        console.log("WebSocket连接成功");
        // 连接成功后进入房间
        joinRoom(roomid)
    }

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        var reArray;
        var timeStamp;
        var resp;
        console.log(event.data);
        //把所有的json串替换成对象格式
        resp=event.data.replace(/\\/g,"").replace(/\"{/g,"{").replace(/}\"/g,"}").replace(/\\"{/g,"{").replace(/}\\"/g,"}");
        reArray=resp.split("#9879874654564987984654#");

        resp=reArray[0];
        timeStamp=reArray[1]

        if(timeStamp<lastMsgTimeStamp){
            return;
        }
        lastMsgTimeStamp=timeStamp;


        console.log(resp);
        //解析json
        resp=eval("("+resp+")");

        if(resp==null)return;
        if(resp.status==200||resp.status=='200'){
            var func= msgHandler[resp.data.msgType];
            if(typeof(func) == "function"){
                func(resp.data);
            }

        }

    }
}


//处理服务器消息


var msgHandler={
    "currentGameInfo":function(data){
        //game.roomInfo=roomInfo;
        game.game=data.msg.game;
        game.gamers=data.msg.gamers.map(function(item,index,array){

            if(typeof(item) =="string"){
                var _gamer= eval("("+item+")");
                if(_gamer.avatarUrl==null||_gamer.avatarUrl==""||_gamer.avatarUrl=="null"){
                    _gamer.avatarUrl="../avatar_default.png"
                }
                return _gamer;
            }
            return item;
        });

        console.log( game.gamers)
        //game.roomGamerData=data.msg;
    },
    otherJoinedRoom:function(){
        //获取房间所有信息
        getCurrentRoomInfo();
    },
    //倒计时
    "timer":function(data){
        game.timer=data.msg;
    },
    //准备模块
    readier:function(data){
        if(data.gamerid==null||data.gamerid==userid){
            game.btnsStatus=data.msg
        }
    },
    //下注模块
    setWager:function(data){
        if(data.gamerid==null||data.gamerid==userid){
            game.btnsStatus=data.msg
        }
    },
    //发牌模块
    cards:function(data){

        var construct=function(size,color,length){
            var cards=[];
            for(var i=0;i<length;i++){
                cards.push({
                    size:size,
                    color:color
                })
            }
            return cards;
        }

        //接收服务器给自己发来的扑克
        for(var index in game.gamers){
            if(game.gamers[index].id==userid){
                game.gamers[index].cards=data.cards;
                game.my.cards=data.cards;
            }else{
                game.gamers[index].cards=construct(0,0,data.cards.length);
            }
        }

    },
    //翻牌模块
    openCard:function(data){
        if(data.gamerid==null||data.gamerid==userid){
            game.btnsStatus=data.msg
        }
    },
    //摊牌模块
    showdown:function(data){
        for(var index in game.gamers){
            if(game.gamers[index].id==data.id){
                game.gamers[index].cards=data.cards;
                game.gamers[index].gameResult=data.gameResult;
            }
        }
    },
    mycards:function(data){
        for(var index in game.gamers){
            if(game.gamers[index].id==userid){
                game.gamers[index].cards=data.msg;
            }
        }
    },

    //游戏结束模块
    endGameBills:function(data){
        for(var index in game.gamers){
            for(var _index in data.msg){
                if(game.gamers[index].id==data.msg[_index].id){
                    game.gamers[index].balance=data.msg[_index].balance;
                }
            }

        }
    },
    //进入房间成功
    joinedRoom:function(){
        //获取房间所有信息
        getCurrentRoomInfo();
    }


}


// {status:200,data:{msgType:'showdown',id:'345553',nickName:'jinjinggg',cards:[{"color":3,"json":"{size:7, color:3, isVisible:true}","size":7},{"color":2,"json":"{size:11, color:2, isVisible:true}","size":11},{"color":2,"json":"{size:5, color:2, isVisible:true}","size":5},{"color":4,"json":"{size:8, color:4, isVisible:false}","size":8},{"color":2,"json":"{size:1, color:2, isVisible:false}","size":1}],gameResult:{"cardList":[{"color":3,"json":"{size:7, color:3, isVisible:true}","size":7},{"color":2,"json":"{size:11, color:2, isVisible:true}","size":11},{"color":2,"json":"{size:5, color:2, isVisible:true}","size":5},{"color":4,"json":"{size:8, color:4, isVisible:false}","size":8},{"color":2,"json":"{size:1, color:2, isVisible:false}","size":1}],"currentCardTypeMult":1,"haveOX":true,"remainder":1}}}





