package com.hangzhou.nn.wx;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@Controller
public class UserInfo {
	@RequestMapping(value="/getUserInfo", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getUserInfo(String code){
		String appid="wxf575e46d80c02c89";
		String secret="c9f1e6072b0de97cd9187644c7a94b30";
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
		//String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
		String resstr;
		String accessToken;
		String openid;
		
		GetUrlData getUrlData=new GetUrlData();
		
		//获取访问token和openid
		resstr=getUrlData.get(url);
		
		AuthorizationBean authorizationBean=JSONObject.parseObject(resstr, AuthorizationBean.class);//JSON字符串转对象
		if(authorizationBean.getErrcode()!=null){
			return "{code:'400',msg:'error'}";
		}
		
		accessToken=authorizationBean.getAccess_token();
		openid=authorizationBean.getOpenid();
		if(accessToken==null||openid==null){
			return "{code:'401',msg:'error'}";
		}
		
		String getUserInfoUrl="https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken+"&openid="+openid+"&lang=zh_CN";
		
		return getUrlData.get(getUserInfoUrl);
		

	}

}
