package com.am.controller;

import com.am.bean.WebSocketMsg;
import com.am.dao.AuOperatorDao;
import com.am.socket.HttpSessionConfigurator;
import com.am.socket.ServerEncoder;
import com.am.utils.DatabaseUtil;
import com.am.utils.JsonUtil;
import com.jfinal.log.Log;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.activerecord.Record;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by Administrator on 2018/6/4.
 */

/**
 * @ServerEndpoint
 * 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端，
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端，
 * 以{}为标志的字段，是用于接收http请求所携带的参数
 */
@ServerEndpoint(value = "/websocket/{userId}/{receiveId}/{msgType}", configurator = HttpSessionConfigurator.class,encoders = ServerEncoder.class)
public class WebSocketController {
	static Log log = Log.getLog(WebSocketController.class);
	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount;

	// concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象
	private static ConcurrentHashMap<String, WebSocketController> webSocketMap = new ConcurrentHashMap<String, WebSocketController>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 * @param session 为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 * @param userId 当前登录用户ID，用于Map的key
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("userId") String userId,@PathParam("msgType") String msgType,EndpointConfig config) {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		if(null == httpSession) {

		}else {
			String operateId = (String) httpSession.getAttribute("userId");
			log.info("***************" + operateId);
			this.session = session;
			// 判断websocket 是否已经含有同一个userId
			if(webSocketMap.get(userId) != null){

			}else{
				webSocketMap.put(userId, this); // 放入map中
				addOnlineCount(); // 在线数加
				log.info("有新连接加入！当前在线人数为" + getOnlineCount() + "\nID分别为：");
				// 获得这个用户的离线信息并发送出去
				getLeaveMsgSend(userId);
			}


		}
	}

	// 获得这个用户的离线信息并发送出去
	private void getLeaveMsgSend(String userId){
		try {
			for (String key : webSocketMap.keySet()) {
				System.out.print(key + "\t");
				List<WebSocketMsg> listMsg = this.getMsgList(key);

				if (null != listMsg && listMsg.size() > 0) {
					synchronized (listMsg) {
						for (int i = 0; i < listMsg.size(); i++) {
							WebSocketMsg msg = listMsg.get(i);
							//存在离线的消息
							boolean falg = sendLeaveMsg(msg.getMsgContent(), "1", msg.getMsgSender(),key,msg.getMsgSendDate());
							if (falg) {
								listMsg.remove(msg);
							}
						}
						if (null != listMsg && listMsg.size() != 0) {
							CacheKit.remove("dataSocketCache", userId);
							CacheKit.put("dataSocketCache", userId, listMsg);
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("创建连接异常");
		} finally {

		}
	}

	// 从缓存总获取消息
	private List<WebSocketMsg> getMsgList(String userId){
		List<WebSocketMsg> list = CacheKit.get("dataSocketCache", userId);
		return list;
	}

	/**
	 * 连接关闭调用的方法
	 * @param userId
	 */
	@OnClose
	public void onClose(@PathParam("userId") String userId) {
		webSocketMap.remove(userId); // 从map中移除
		subOnlineCount(); // 在线数减
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @param session
	 * @param userId 发送者ID
	 * @param receiveId 接收者ID
	 */
	@OnMessage
	public void onMessage(String message, Session session, @PathParam("userId") String userId, @PathParam("receiveId") String receiveId,@PathParam("msgType") String msgType) {
		System.out.println("来自客户端的消息:" + message);

		/**
		 * 如果userId，receiveId同时为0，则为系统群发消息
		 * 如果userId，receiveId均不为0，则为消息发送者
		 * 如果userId不为0，receiveId为0，则为消息接收者
		 */
		try {
			if(!"0".equals(userId) && !"0".equals(receiveId)) { // 单个用户推送
				sendMsgById(message, userId, receiveId,msgType);
			} else if("0".equals(userId) && "0".equals(receiveId)) {  // 群发消息
				sendMsgToOnLine(message,msgType,userId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发生错误时调用
	 */
	@OnError
	public void onError( Session session, Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/**
	 * 向指定用户发送消息
	 * @throws IOException
	 */
	public void sendMsgById(String message, String userId, String receiveId) throws IOException {
		if(webSocketMap.containsKey(receiveId)){
			webSocketMap.get(receiveId).sendMessage(message);
			webSocketMap.get(userId).sendMessage("消息发送成功");
		} else {
			webSocketMap.get(userId).sendMessage("该用户不存在或已离线");
		}
	}

	/**
	 * 发送信息
	 * @param message
	 * @param userId 发送者
	 * @param receiveId 接收者
	 * @param msgType 信息类型
	 * @throws IOException
	 */
	public void sendMsgById(String message, String userId, String receiveId,String msgType) throws IOException{

		String dateStr = DatabaseUtil.getDateStr(new Date(),"yyyy-mm-dd hh:MM:ss");
		if(webSocketMap.containsKey(receiveId)){
			webSocketMap.get(receiveId).sendMessage(message,msgType,userId,receiveId,dateStr);
			// webSocketMap.get(userId).sendMessage("消息发送成功");
		} else {
			webSocketMap.get(userId).sendMessage("用户:" + receiveId + "不存在或已离线");
			//必须发送的信息
			if(msgType == "1" || msgType.equals("1")){
				// 添加到缓存
				addLeaveMsg(receiveId,message,userId,dateStr,msgType);
			}
			// webSocketMap.get(userId).sendMessage("该离线用户信息已经保存");

		}
	}

	/**
	 * 发送离线消息
	 * @param message
	 * @param userId
	 * @param receiveId
	 * @return
	 * @throws IOException
	 */
	public boolean sendLeaveMsg(String message, String msgType, String userId, String receiveId,String sendDate) throws  IOException{
		if(webSocketMap.containsKey(receiveId)){
			// webSocketMap.get(receiveId).sendMessage(message);
			// webSocketMap.get(userId).sendMessage("消息发送成功");
			webSocketMap.get(receiveId).sendMessage(message,msgType,userId,receiveId,sendDate);
			return true;
		} else {
			// webSocketMap.get(userId).sendMessage("该用户不存在或已离线");
			return false;
		}
	}

	/**
	 * 系统群发
	 * @throws IOException
	 */
	public void sendMsgToAll(String message) throws IOException {
		for(String key : webSocketMap.keySet()) {
			webSocketMap.get(key).sendMessage(message);
		}
	}


	/**
	 * 给在线的人发送短信
	 * @param message
	 * @param messageType 消息类型
	 * @param sender 发送者
	 * @throws IOException
	 */
	public void sendMsgToOnLine(String message,String messageType,String sender) throws IOException{
		String dateStr = DatabaseUtil.getDateStr(new Date(),"yyyy-mm-dd hh:MM:ss");
		List<Record> userList = AuOperatorDao.dao.findAllId();
		for(String key : webSocketMap.keySet()) {
			webSocketMap.get(key).sendMessage(message,messageType,sender,key,dateStr);
			// 必须发送的消息
			if(messageType.equals("1")){
				if(userList.contains(key)){

				}else {
					// 添加到缓存
					addLeaveMsg(key,message,sender,dateStr,messageType);
				}
			}

		}

	}


	/**
	 * 将没有收到信息的放到该用户的缓存中
	 * @param userId 接收者
	 * @param message 内容
	 * @param sender 发送者
	 * @param sendDate 发送日期
	 */
	private void addLeaveMsg(String userId,String message,String sender,String sendDate,String msgType){
		// 添加到缓存
		List<WebSocketMsg> list = CacheKit.get("dataSocketCache", userId);
		int size = 0;
		if(null == list || list.size() == 0){
			size = 0;
			list = new ArrayList<WebSocketMsg>();
		}else{
			size = size + 1;
		}

		WebSocketMsg socketMsg = new WebSocketMsg();
		socketMsg.setMsgContent(message);
		socketMsg.setMsgId(size + "");
		socketMsg.setMsgSendDate(sendDate);
		socketMsg.setMsgType(msgType);
		socketMsg.setMsgReceiver(userId);
		socketMsg.setMsgSender(sender);
		list.add(socketMsg);
		CacheKit.remove("dataSocketCache",userId);
		CacheKit.put("dataSocketCache",userId,list);
	}


	/**
	 * 消息发送
	 * @param message
	 * @param msgType
	 * @param msgSender
	 * @param msgReceiver
	 * @param msgSendDate
	 * @throws IOException
	 */
	public void sendMessage(String message,String msgType,String msgSender,String msgReceiver,String msgSendDate) throws IOException{
		WebSocketMsg bean = new WebSocketMsg();
		String dateStr = DatabaseUtil.getDateStr(new Date(),"yyyy-mm-dd hh:MM:ss");
		bean.setMsgContent(message);
		bean.setMsgSendDate(msgSendDate);
		bean.setMsgSender(msgSender);
		bean.setMsgType(msgType);
		bean.setMsgReceiver(msgReceiver);
		bean.setMsgReceiveDate(dateStr);
		String json = JsonUtil.Object2Json(bean);

		this.session.getBasicRemote().sendText(json);
	}

	/**
	 * 消息发送
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);  // 同步
		// this.session.getAsyncRemote().sendText(message); // 异步
	}

	// 获取当前登录人数
	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	// 用户上线
	public static synchronized void addOnlineCount() {
		WebSocketController.onlineCount++;
	}

	// 用户离线
	public static synchronized void subOnlineCount() {
		WebSocketController.onlineCount--;
	}

}
