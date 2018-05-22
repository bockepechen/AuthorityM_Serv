package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuRoleDao;
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
 * Created by Administrator on 2018/5/21.
 */
public class RoleController extends Controller{
	static Log log = Log.getLog(RoleController.class);

	String returnCode = "";// 返回码
	String returnMessage = "";// 返回信息
	String reqNo = "";// 请求单号
	String operatorId = "";// 用户编号
	String accountId = "";// 登录账号
	String roleId = ""; // 角色Id

	List<Record> roleList = null;// 查询到的所有角色
	JSONArray roleArray = new JSONArray(); // 返回角色listJson
	JSONObject jb = new JSONObject();
	JSONObject jyau_roleData = new JSONObject();
	JSONArray jsonArray = new JSONArray();

	JSONArray dictList = CacheKit.get("dataCache", "s_dict_returncode");

	public void index(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"  \"jyau_content\": {\n" +
				"    \"jyau_reqData\": [\n" +
				"      {\n" +
				"        \"req_no\": \"AU048201802051125231351\"\n" +
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
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				roleList = AuRoleDao.dao.findAll();
				if(null == roleList || roleList.size() == 0){

				}else {
					for (int i = 0;i < roleList.size();i++){
						String roleId = roleList.get(i).getStr("RL_ID");
						String roleCode = roleList.get(i).getStr("RL_CODE");
						String roleName = roleList.get(i).getStr("RL_NAME");

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("role_id",roleId);
						jsonObject.put("role_code",roleCode);
						jsonObject.put("role_name",roleName);
						roleArray.add(i,jsonObject);
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
			PubModelUtil.apiRecordBean(map,"AU011",json,jb.toString());
		}
	}

	// 保存角色信息（根据role_id 是否为空判断添加、修改）
	public void saveRole(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"  \"jyau_content\": {\n" +
				"    \"jyau_reqData\": [\n" +
				"      {\n" +
				"        \"req_no\": \"AU012201802051125231351\",\n" +
				"        \"role_id\": \"\",\n" +
				"        \"role_code\": \"0001\",\n" +
				"        \"role_name\": \"角色1\"\n" +
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
			roleId = map.get("role_id").toString();

			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				String roleCode = map.get("role_code").toString();
				String roleName = map.get("role_name").toString();
				Record roleRecord = new Record();
				// roleId 为空进行的是添加角色
				if(EmptyUtils.isEmpty(roleId)){
					int roleNameCnt = AuRoleDao.dao.findByName(roleName,"");
					if(roleNameCnt > 0){
						returnCode = ReturnCodeUtil.returnCode8;
					}else{
						int roleCodeCnt = AuRoleDao.dao.findByCode(roleCode,"");
						if(roleCodeCnt > 0){
							returnCode = ReturnCodeUtil.returnCode9;
						}else{
							roleRecord.set("RL_ID", DatabaseUtil.getEntityPrimaryKey("RL"));
							roleRecord.set("RL_CODE",roleCode);
							roleRecord.set("RL_NAME",roleName);
							AuRoleDao.dao.save(roleRecord);
							returnCode = ReturnCodeUtil.returnCode;
						}

					}
				}else {
					int roleNameCnt = AuRoleDao.dao.findByName(roleName,roleId);
					if(roleNameCnt > 0){
						returnCode = ReturnCodeUtil.returnCode8;
					}else{
						int roleCodeCnt = AuRoleDao.dao.findByCode(roleCode,roleId);
						if(roleCodeCnt > 0){
							returnCode = ReturnCodeUtil.returnCode9;
						}else{
							roleRecord.set("RL_ID", roleId);
							roleRecord.set("RL_CODE",roleCode);
							roleRecord.set("RL_NAME",roleName);
							roleRecord.set("UPDATE_TIME",DatabaseUtil.getSqlDatetime());
							AuRoleDao.dao.update(roleRecord);
							returnCode = ReturnCodeUtil.returnCode;
						}
					}
				}
			}
			returnOperJson();
		}catch (Exception e){
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnOperJson();
		}finally {
			if(EmptyUtils.isEmpty(roleId)){
				PubModelUtil.apiRecordBean(map,"AU01201",json,jb.toString());
			}else{
				PubModelUtil.apiRecordBean(map,"AU01202",json,jb.toString());
			}
		}

	}

	// 显示角色详细信息
	public void showRole(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"  \"jyau_content\": {\n" +
				"    \"jyau_reqData\": [\n" +
				"      {\n" +
				"        \"req_no\": \"CL048201802051125231351\",\n" +
				"        \"role_id\": \"1\"\n" +
				"       }\n" +
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
		jyau_roleData = new JSONObject();
		try {
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			roleId = map.get("role_id").toString();

			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(roleId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				Record roleRecord = AuRoleDao.dao.findById(roleId);
				if(null == roleRecord){
					returnCode = ReturnCodeUtil.returnCode10;
				}else{
					jyau_roleData.put("role_id",roleRecord.getStr("RL_ID"));
					jyau_roleData.put("role_code",roleRecord.getStr("RL_CODE"));
					jyau_roleData.put("role_name",roleRecord.getStr("RL_NAME"));
					returnCode = ReturnCodeUtil.returnCode;
				}
			}
			returnDetailJson(jyau_roleData);
		}catch (Exception e){
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnDetailJson(jyau_roleData);
		}finally {
			PubModelUtil.apiRecordBean(map,"AU014",json,jb.toString());
		}
	}

	// 删除角色信息
	public void delRole(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"  \"jyau_content\": {\n" +
				"    \"jyau_reqData\": [\n" +
				"      {\n" +
				"        \"req_no\": \"AU004201802051125231351\",\n" +
				"        \"role_id\": \"RL201805211610561160\"\n" +
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
			roleId = map.get("role_id").toString();

			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(roleId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				Record delRole = new Record();
				delRole.set("RL_ID",roleId);
				boolean delRecord = AuRoleDao.dao.delete(delRole);
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
			PubModelUtil.apiRecordBean(map,"AU013",json,jb.toString());
		}
	}

	// 角色列表显示返回的json
	public void returnJson() {
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_roleData = new JSONObject();
		//拼装json
		jyau_roleData.put("req_no", reqNo);
		jyau_roleData.put("role_data", roleArray);
		jsonArray.add(jyau_roleData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	// 角色操作返回json
	public void returnOperJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_roleData = new JSONObject();
		//拼装json
		jyau_roleData.put("req_no", reqNo);
		jsonArray.add(jyau_roleData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	// 角色详细信息返回json
	public void returnDetailJson(JSONObject jyau_roleData){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		//拼装json
		jyau_roleData.put("req_no", reqNo);
		jsonArray.add(jyau_roleData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}
}
