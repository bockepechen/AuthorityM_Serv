package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuOrganizationDao;
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
 * Created by Administrator on 2018/5/15.
 * 机构管理
 */
public class OrgController extends Controller {
	static Log log = Log.getLog(OrgController.class);

	String returnCode = "";// 返回码
	String returnMessage = "";// 返回信息
	String reqNo = "";// 请求单号
	String operatorId = "";// 用户编号
	String accountId = "";// 登录账号
	String orgId = ""; // 机构ID

	List<Record> orgList = null;// 查询到的所有机构
	JSONArray orgArray = new JSONArray(); // 返回机构listJson
	JSONObject jyau_orgData = new JSONObject();
	JSONObject jb = new JSONObject();
	JSONArray jsonArray = new JSONArray();

	JSONArray dictList = CacheKit.get("dataCache", "s_dict_returncode");



	/**
	 * 机构查询
	 */
	public void index(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"  \"jyau_content\": {\n" +
				"    \"jyau_reqData\": [\n" +
				"      {\n" +
				"        \"req_no\": \"CL048201802051125231351\"\n" +
				"      }\n" +
				"    ],\n" +
				"    \"jyau_pubData\": {\n" +
				"      \"operator_id\": \"1\",\n" +
				"      \"ip_address\": \"10.2.0.116\",\n" +
				"      \"account_id\": \"systemman\",\n" +
				"      \"system_id\": \"10909\"\n" +
				"    }\n" +
				"  }\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				orgList = AuOrganizationDao.dao.findAll();
				if(null == orgList || orgList.size() == 0){

				}else {
					for (int i = 0;i < orgList.size();i++){
						String orgId = orgList.get(i).getStr("org_id");
						String orgCode = orgList.get(i).getStr("org_code");
						String orgName =  orgList.get(i).getStr("org_name");

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("org_id",orgId);
						jsonObject.put("org_code",orgCode);
						jsonObject.put("org_name",orgName);
						orgArray.add(i,jsonObject);
					}
				}
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnJson();
		}catch (Exception e){
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU002",json,jb.toString());
		}

	}


	/**
	 * 保存机构信息（根据org_id 是否为空判断添加、修改）
	 */
	public void saveOrg(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"  \"jyau_content\": {\n" +
				"    \"jyau_reqData\": [\n" +
				"      {\n" +
				"        \"req_no\": \"CL048201802051125231351\",\n" +
				"        \"org_id\": \"1\",\n" +
				"        \"org_code\": \"0001\",\n" +
				"        \"org_name\": \"天津嘉业智德分公司\"\n" +
				"      }\n" +
				"    ],\n" +
				"    \"jyau_pubData\": {\n" +
				"      \"operator_id\": \"1\",\n" +
				"      \"ip_address\": \"10.2.0.116\",\n" +
				"      \"account_id\": \"systemman\",\n" +
				"      \"system_id\": \"10909\"\n" +
				"    }\n" +
				"  }\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try {
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			orgId = map.get("org_id").toString();

			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				String orgCode = map.get("org_code").toString();
				String orgName = map.get("org_name").toString();
				Record orgRecord = new Record();
				// orgId 为空进行的是添加机构
				if(EmptyUtils.isEmpty(orgId)){
					int orgNameCnt = AuOrganizationDao.dao.findByName(orgName,"");
					if(orgNameCnt > 0){
						returnCode = ReturnCodeUtil.returnCode5;
					}else{
						orgRecord.set("ORG_ID", DatabaseUtil.getEntityPrimaryKey("OG"));
						orgRecord.set("ORG_CODE",orgCode);
						orgRecord.set("ORG_NAME",orgName);
						orgRecord.set("ORG_LEVEL",1); // 一级别
						orgRecord.set("ORG_TYPE","02"); // 分公司
						orgRecord.set("ORG_STATUS","01"); // 正常
						AuOrganizationDao.dao.save(orgRecord);
						returnCode = ReturnCodeUtil.returnCode;
					}

				}else {
					int orgNameCnt = AuOrganizationDao.dao.findByName(orgName,orgId);
					if(orgNameCnt > 0) {
						returnCode = ReturnCodeUtil.returnCode5;
					}else{
						orgRecord.set("ORG_ID",orgId);
						orgRecord.set("ORG_CODE",orgCode);
						orgRecord.set("ORG_NAME",orgName);
						orgRecord.set("UPDATE_TIME",DatabaseUtil.getSqlDatetime());
						AuOrganizationDao.dao.update(orgRecord);
						returnCode = ReturnCodeUtil.returnCode;
					}
				}
			}
			returnOperJson();
		}catch (Exception e){
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnOperJson();
		}finally {
			if(EmptyUtils.isEmpty(orgId)){
				PubModelUtil.apiRecordBean(map,"AU00301",json,jb.toString());
			}else{
				PubModelUtil.apiRecordBean(map,"AU00302",json,jb.toString());
			}

		}

	}

	/**
	 * 删除机构
	 */
	public void delOrg(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"  \"jyau_content\": {\n" +
				"    \"jyau_reqData\": [\n" +
				"      {\n" +
				"        \"req_no\": \"AU004201802051125231351\",\n" +
				"        \"org_id\": \"3\"\n" +
				"      }\n" +
				"    ],\n" +
				"    \"jyau_pubData\": {\n" +
				"      \"operator_id\": \"1\",\n" +
				"      \"ip_address\": \"10.2.0.116\",\n" +
				"      \"account_id\": \"systemman\",\n" +
				"      \"system_id\": \"10909\"\n" +
				"    }\n" +
				"  }\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try {
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			orgId = map.get("org_id").toString();

			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(orgId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				Record delOrg = new Record();
				delOrg.set("ORG_ID",orgId);
				boolean delRecord = AuOrganizationDao.dao.delete(delOrg);
				if(delRecord){
					returnCode = ReturnCodeUtil.returnCode;
				}else {
					returnCode = ReturnCodeUtil.returnCode6;
				}
			}
			returnOperJson();
		}catch (Exception e){
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnOperJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU004",json,jb.toString());
		}

	}

	// /**
	//  * 添加机构人员
	//  */
	// public void addOrgUser(){
	// 	//获取请求数据
	// 	String json = HttpKit.readData(getRequest());
	//
	//
	// }

	// 机构显示返回的json
	public void returnJson() {
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_orgData = new JSONObject();
		//拼装json
		jyau_orgData.put("req_no", reqNo);
		jyau_orgData.put("org_data", orgArray);
		jsonArray.add(jyau_orgData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	// 机构操作返回json
	public void returnOperJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_orgData = new JSONObject();
		//拼装json
		jyau_orgData.put("req_no", reqNo);
		jsonArray.add(jyau_orgData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}
}
