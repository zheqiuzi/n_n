package com.hangzhou.nn.socket;

import com.hangzhou.nn.message.Message;

public interface MsgHandler {
	public abstract void handleMsg(Message msg);
}
