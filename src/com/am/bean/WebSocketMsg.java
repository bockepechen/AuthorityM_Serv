package com.am.bean;

import com.sun.xml.internal.ws.developer.Serialization;

import java.util.Date;
import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/30.
 */
public class WebSocketMsg  implements Serializable{
	public String msgId;
	public String msgContent;
	public Date msgReceiveDate;
	public Date msgSendDate;

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public Date getMsgReceiveDate() {
		return msgReceiveDate;
	}

	public void setMsgReceiveDate(Date msgReceiveDate) {
		this.msgReceiveDate = msgReceiveDate;
	}

	public Date getMsgSendDate() {
		return msgSendDate;
	}

	public void setMsgSendDate(Date msgSendDate) {
		this.msgSendDate = msgSendDate;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}


}
