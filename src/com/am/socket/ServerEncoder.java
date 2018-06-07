package com.am.socket;

import com.am.bean.WebSocketMsg;
import com.am.utils.JsonUtil;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by Administrator on 2018/6/5.
 */
public class ServerEncoder implements Encoder.Text<WebSocketMsg> {
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(EndpointConfig arg0) {
		// TODO Auto-generated method stub
	}

	public String encode(WebSocketMsg webSocketMsg) throws EncodeException {
		try {
			return JsonUtil.Object2Json(webSocketMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
