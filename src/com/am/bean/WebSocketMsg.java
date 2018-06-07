package com.am.bean;

import com.sun.xml.internal.ws.developer.Serialization;

import java.util.Date;
import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/30.
 */
public class WebSocketMsg  implements Serializable{
	public String msgId; //消息ID
	public String msgContent; // 消息内容
	public String msgReceiveDate; // 消息接收日期
	public String msgSendDate; // 消息发送日期
	public String msgSender; // 消息发送者
	public String msgReceiver; // 消息接收者
	public String msgType; // 消息类型

	public WebSocketMsg() {
		super();
	}

	public String toString() {
		return "Messagepojo [msgReceiver=" + msgReceiver + ", msgType=" + msgType
				+ ", msgContent=" + msgContent + ", msgId=" + msgId
	               + ", msgSender=" + msgSender +  "]";
	}


	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgReceiver() {
		return msgReceiver;
	}

	public void setMsgReceiver(String msgReceiver) {
		this.msgReceiver = msgReceiver;
	}

	public String getMsgSender() {
		return msgSender;
	}

	public void setMsgSender(String msgSender) {
		this.msgSender = msgSender;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getMsgReceiveDate() {
		return msgReceiveDate;
	}

	public void setMsgReceiveDate(String msgReceiveDate) {
		this.msgReceiveDate = msgReceiveDate;
	}

	public String getMsgSendDate() {
		return msgSendDate;
	}

	public void setMsgSendDate(String msgSendDate) {
		this.msgSendDate = msgSendDate;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}


}
