package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.service.LoginService;
import com.am.utils.*;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ZHAO on 2018/5/4.
 */
public class LoginController extends Controller {
	static Log log = Log.getLog(LoginController.class);

	String operatorId = "";//用户编号
	String accountId = "";//登录账号
	String pwd = "";//账户密码
	String name = "";//用户姓名
	String ip = "";//当前IP
	String lastIp = "";//上次登录Ip
	String lastLogin = "";//上次登录时间
	String reqNo = "";//请求单号
	String returnCode = "";//返回码
	String returnMessage = "";//返回信息
	List<Record> orgList = null;//用户所属机构
	JSONArray dictList = CacheKit.get("dataCache", "s_dict_returncode");
	JSONObject jb = new JSONObject();

	JSONArray jsonArray = new JSONArray();
	JSONObject jyau_loginData = new JSONObject();

	public void index() {

		//获取请求数据
		String json = HttpKit.readData(getRequest());

		/*String json = "{\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\"jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \" AU001201810231521335687\",\n" +
				"\t\t\t\"account_pwd\": \"111111\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\t\t\t\"operator_id\": \"\",\n" +
				"\t\t\t\"account_id\": \"zhangguobang\",\n" +
				"\t\t\t\"ip_address\": \"10.2.0.116\",\n" +
				"\t\t\t\"system_id\": \"10909\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try {
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			accountId = map.get("account_id").toString();
			pwd = map.get("account_pwd").toString();
			ip = map.get("ip_address").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(pwd)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				//调用登录业务逻辑
				Map loginMap = LoginService.service.loginBiz(accountId,pwd,ip);
				returnCode = loginMap.get("returnCode").toString();
				if(returnCode.equals("0000")) {
					operatorId = loginMap.get("operatorId").toString();
					setSessionAttr("userId",operatorId);
					name = loginMap.get("name").toString();
					if (null != loginMap.get("lastIp")) {
						lastIp = loginMap.get("lastIp").toString();
					}
					if (null != loginMap.get("lastLogin")) {
						lastLogin = loginMap.get("lastLogin").toString();
					}
					orgList = (List<Record>) loginMap.get("orgList");
				}
			}
			returnJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnJson();
		} finally {
			PubModelUtil.apiRecordBean(map,"AU001",json,jb.toString());
		}
	}

	public void returnJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_loginData.put("req_no", reqNo);
		jyau_loginData.put("operator_id", operatorId);
		jyau_loginData.put("name", name);
		jyau_loginData.put("account_id", accountId);
		jyau_loginData.put("last_ip", lastIp);
		jyau_loginData.put("last_login", lastLogin);
		jyau_loginData.put("org_list", orgList);
		jsonArray.add(jyau_loginData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}

	public void wskts(){
		render("/login.jsp");
	}

	public void wskt(){
		render("/testLogin.jsp");
	}

	public void login(){
		setSessionAttr("userId",getPara("userName"));
		// render("/sendMsg.jsp");
		render("/sendMsgTest.jsp");
	}
}