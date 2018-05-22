package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuOperatorDao;
import com.am.service.OperatorService;
import com.am.utils.EmptyUtils;
import com.am.utils.JsonUtil;
import com.am.utils.PubModelUtil;
import com.am.utils.ReturnCodeUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.weixin.sdk.api.ReturnCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZHAO on 2018/5/14.
 */
public class OperatorController extends Controller {
	static Log log = Log.getLog(OperatorController.class);

	String type = "";//01新增、02修改
	String operatorId = "";//用户编号
	String account = "";//登录账号
	String name = "";//用户姓名
	String pwd = "";//登录密码
	String status = "";//用户状态
	String ifAdimn = "0";//是否管理员（0否1是）

	String reqNo = "";//请求单号
	String returnCode = "";//返回码
	String returnMessage = "";//返回信息
	List<Record> operatorList = null;//用户信息
	JSONArray dictList = CacheKit.get("dataCache", "s_dict_returncode");
	JSONArray operatorStatusList = CacheKit.get("dataCache", "s_dict_operatorStatus");
	JSONObject jb = new JSONObject();

	JSONArray jsonArray = new JSONArray();
	JSONObject jyau_operData = new JSONObject();

	/**
	 * 显示列表
	 */
	public void index(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\"jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \" AU001201810231521335687\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
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
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				operatorList = AuOperatorDao.dao.findAll();
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnJson();
		} finally {
			PubModelUtil.apiRecordBean(map,"AU006",json,jb.toString());
		}
	}

	/**
	 *选中某用户显示具体用户信息
	 */
	public void displayOperator(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
/*		String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\"jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \" AU001201810231521335687\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
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
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				Record operRecord = AuOperatorDao.dao.findById(operatorId);
				if(null != operRecord){
					account = operRecord.getStr("OP_ACCOUNT");
					name = operRecord.getStr("OP_NAME");
					pwd = operRecord.getStr("OP_PWD");
					String opStatus = operRecord.getStr("OP_STATUS");
					status = JsonUtil.getDictName(operatorStatusList, opStatus);
					ifAdimn = operRecord.getStr("OP_IFOPERATOR");

					returnCode = ReturnCodeUtil.returnCode;
				}else{
					returnCode = ReturnCodeUtil.returnCode4;
				}
			}
			returnDisplayJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnDisplayJson();
		} finally {
			PubModelUtil.apiRecordBean(map,"AU007",json,jb.toString());
		}
	}

	/**
	 * 修改用户信息---新增用户信息
	 */
	public void modifyOperator(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\"jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \" AU001201810231521335687\",\n" +
				"\t\t\t\"account\": \"systemman\",\n" +
				"\t\t\t\"name\": \"wwwwwwwwwwww\",\n" +
				"\t\t\t\"pwd\": \"lllll98989898\",\n" +
				"\t\t\t\"status\": \"01\",\n" +
				"\t\t\t\"ifAdmin\": \"1\",\n" +
				"\t\t\t\"type\": \"02\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
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
			type = map.get("type").toString();
			operatorId = map.get("operator_id").toString();
			account = map.get("account").toString();
			name = map.get("name").toString();
			pwd = map.get("pwd").toString();
			status = map.get("status").toString();
			ifAdimn = map.get("ifAdmin").toString();
			if(!ifCheck(reqNo,type,operatorId,status,ifAdimn,account,pwd)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				if(type.equals("01")){//新增
					//查询登录账号是否存在
					Record operRecord = AuOperatorDao.dao.queryByaccountId(account);
					if(null != operRecord){
						returnCode = ReturnCodeUtil.returnCode11;
					}else{
						operatorId = OperatorService.service.operatorInterestBiz(account,name,pwd,status,ifAdimn);
						returnCode = ReturnCodeUtil.returnCode;
					}
				}else{//修改
					OperatorService.service.operatorModifyBiz(operatorId,account,name,pwd,status,ifAdimn);
					returnCode = ReturnCodeUtil.returnCode;
				}
			}
			returnModifyJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnModifyJson();
		} finally {
			if(type.equals("01")){
				PubModelUtil.apiRecordBean(map,"AU00801",json,jb.toString());
			}else{
				PubModelUtil.apiRecordBean(map,"AU00802",json,jb.toString());
			}

		}

	}

	/**
	 * 删除用户信息
	 */
	public void deleteOperator(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\"jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \" AU001201810231521335687\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\t\t\t\"operator_id\": \"OP201805151602064884\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
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
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				OperatorService.service.operatorDeleteBiz(operatorId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnDeleteJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnDeleteJson();
		} finally {
			PubModelUtil.apiRecordBean(map,"AU009",json,jb.toString());
		}

	}

	/**
	 * 检验合法性
	 * @return
	 */
	public boolean ifCheck(String reqNo,String type,String operatorId,String status,String ifAdmin,String account,String pwd){
		boolean flag = false;
		if(EmptyUtils.isEmpty(type) || EmptyUtils.isEmpty(reqNo) ||  EmptyUtils.isEmpty(status) || EmptyUtils.isEmpty(ifAdimn) || EmptyUtils.isEmpty(account) || EmptyUtils.isEmpty(name) || EmptyUtils.isEmpty(pwd)){

		}else{
			if(type.equals("02")){//修改
				if(EmptyUtils.isNotEmpty(operatorId)){
					flag = true;
				}
			}else{
				flag = true;
			}
		}
		return flag;

	}

	public void returnDisplayJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_operData.put("req_no", reqNo);
		jyau_operData.put("operator_id", operatorId);
		jyau_operData.put("account", account);
		jyau_operData.put("name", name);
		jyau_operData.put("pwd", pwd);
		jyau_operData.put("status", status);
		jyau_operData.put("ifAdimn", ifAdimn);
		jsonArray.add(jyau_operData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}

	public void returnJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_operData.put("req_no", reqNo);
		jyau_operData.put("operator_id", operatorId);
		jyau_operData.put("oper_list", operatorList);
		jsonArray.add(jyau_operData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}
	public void returnModifyJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_operData.put("req_no", reqNo);
		jyau_operData.put("operator_id", operatorId);
		jsonArray.add(jyau_operData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}
	public void returnDeleteJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_operData.put("req_no", reqNo);
		jsonArray.add(jyau_operData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}

}
