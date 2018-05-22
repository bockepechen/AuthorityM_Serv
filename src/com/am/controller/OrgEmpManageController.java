package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuEmpOrgDao;
import com.am.dao.AuOperatorDao;
import com.am.dao.AuOrganizationDao;
import com.am.service.OrgEmpRoleService;
import com.am.utils.EmptyUtils;
import com.am.utils.JsonUtil;
import com.am.utils.PubModelUtil;
import com.am.utils.ReturnCodeUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZHAO on 2018/5/15.
 */
public class OrgEmpManageController extends Controller{
	static Log log = Log.getLog(OrgEmpManageController.class);

	String reqNo = "";//请求单号
	String accountId = "";//登录账号
	String orgId = "";//机构Id
	String operatorId = "";//操作员编号
	String returnCode = "";//返回码
	String returnMessage = "";//返回信息
	List<Record> notOrgList = null;//不属于该机构的用户人员

	String roleId = ""; // 角色Id
	List<Record> userList = null;// 拥有角色的用户列表信息
	List<Record> userNList = null;// 不含有该角色的用户信息
	JSONArray userRoleArray = new JSONArray(); // 返回拥有角色的用户listJson
	String operdata = ""; // 操作数据（用于删除或添加用户信息）

	JSONArray dictList = CacheKit.get("dataCache", "s_dict_returncode");
	JSONObject jb = new JSONObject();
	JSONArray jsonArray = new JSONArray();
	JSONObject jyau_oporgData = new JSONObject();
	JSONArray joo = new JSONArray();
	/**
	 * 1.查询机构用户列表2.查询无机构人员（查询员工表机构为空的）
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
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				//查询机构
				List<Record> orgList = AuOrganizationDao.dao.findAll();
				//机构下用户
				for(Record org : orgList){
					String orgId = org.getStr("org_id");
					String orgName = org.getStr("org_name");
					List<Record>  empOrgList= AuEmpOrgDao.dao.findUserInOrg(orgId);
					JSONObject jo = new JSONObject();
					jo.put("org_id",orgId);
					jo.put("org_name",orgName);
					jo.put("emp_list",empOrgList);
					joo.add(jo);
				}
				//查询无机构员工
				List<Record> noOrgEmpList = AuOperatorDao.dao.findEmpNoOrg();
				JSONObject jo = new JSONObject();
				jo.put("org_name","无机构的人员");
				jo.put("emp_list",noOrgEmpList);
				joo.add(jo);
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnJson();
		} finally {
			PubModelUtil.apiRecordBean(map,"AU010",json,jb.toString());
		}
	}

	/**
	 * 查询不属于该机构的人员
	 */
	public void notOrgUser(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\"jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \" AU001201810231521335687\",\n" +
				"\t\t\t\"org_id\": \"OG201805171726129979\"\n" +
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
			orgId = map.get("org_id").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(orgId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				//查询不属于该机构的人员
				notOrgList  = AuEmpOrgDao.dao.findUserNotOrg(orgId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnNotOrgJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnNotOrgJson();
		} finally {
			PubModelUtil.apiRecordBean(map,"AU015",json,jb.toString());
		}
	}

	/**
	 * 机构添加人员
	 */
	public void addOperator(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\"jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \" AU001201810231521335687\",\n" +
				"\t\t\t\"org_id\": \"OG201805171726129979\"\n" +
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
			orgId = map.get("org_id").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(orgId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				//调用机构添加人员业务逻辑
				OrgEmpRoleService.service.insertOrgUser(orgId,operatorId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnOrgJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnOrgJson();
		} finally {
			PubModelUtil.apiRecordBean(map,"AU016",json,jb.toString());
		}
	}

	//展示某个角色已经拥有的用户列表信息
	public void showRoleUser(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		//解析Json
		Map map = new HashMap();
		try {
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			roleId = map.get("org_id").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(roleId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				userList = AuEmpOrgDao.dao.findUsersHaveRole(roleId);
				if (null == userList || userList.size() == 0) {

				} else {
					for (int i = 0; i < userList.size(); i++) {
						String lation_id = userList.get(i).getStr("LA_ID");
						String oper_id = userList.get(i).getStr("OP_OPRATORID");
						String role_id = userList.get(i).getStr("RL_ID");
						String org_id = userList.get(i).getStr("ORG_ID");
						String account_id = userList.get(i).getStr("OP_ACCOUNT");
						String account_name = userList.get(i).getStr("OP_NAME");
						String org_name = userList.get(i).getStr("ORG_NAME");
						String org_code = userList.get(i).getStr("ORG_CODE");

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("role_id", role_id);
						jsonObject.put("oper_id", oper_id);
						jsonObject.put("org_id", org_id);
						jsonObject.put("lation_id", lation_id);
						jsonObject.put("account_id", account_id);
						jsonObject.put("account_name", account_name);
						jsonObject.put("org_name", org_name);
						jsonObject.put("org_code", org_code);
						userRoleArray.add(i, jsonObject);
					}
				}
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnUserRoleJson();
		}catch (Exception e){
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnUserRoleJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU021",json,jb.toString());
		}

	}

	// 查询没有拥有指定角色的用户信息
	public void showNoRoleUser(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		//解析Json
		Map map = new HashMap();
		try {
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			roleId = map.get("org_id").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(roleId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				userNList = AuEmpOrgDao.dao.findUsersNotHaveRole(roleId);
				if (null == userNList || userNList.size() == 0) {

				} else {
					for (int i = 0; i < userNList.size(); i++) {
						String oper_id = userNList.get(i).getStr("OP_OPRATORID");
						String account_id = userNList.get(i).getStr("OP_ACCOUNT");
						String account_name = userNList.get(i).getStr("OP_NAME");
						String org_id = userNList.get(i).getStr("ORG_ID");
						String org_name = userNList.get(i).getStr("ORG_NAME");

						JSONObject jsonObject = new JSONObject();
						jsonObject.put("oper_id", oper_id);
						jsonObject.put("org_id", org_id);
						jsonObject.put("account_id", account_id);
						jsonObject.put("account_name", account_name);
						jsonObject.put("org_name", org_name);

						userRoleArray.add(i, jsonObject);
					}
				}
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnUserRoleJson();
		}catch (Exception e){
			log.error(e.getMessage(), e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnUserRoleJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU022",json,jb.toString());
		}
	}

	// 删除用户拥有的角色
	public void delUserRole(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		//解析Json
		Map map = new HashMap();
		try {
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			operdata = map.get("oper_data").toString(); // 操作的数据

			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(operdata)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				org.json.JSONArray jsonArrayUser = new org.json.JSONArray(operdata);
				for (int i = 0; i < jsonArrayUser.length(); i++) {
					String lation_id = jsonArrayUser.get(i).toString();


				}
			}

		}catch (Exception e){

		}finally {

		}

	}

	// 添加用户到指定角色
	public void addUserRole(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		//解析Json
		Map map = new HashMap();
		try {
			map = JsonUtil.analyzejson(json);

		}catch (Exception e){

		}finally {

		}
	}

	public void returnJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_oporgData.put("req_no", reqNo);
		jyau_oporgData.put("operator_id", operatorId);
		jyau_oporgData.put("orgemp_list", joo);
		jsonArray.add(jyau_oporgData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}
	public void returnNotOrgJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_oporgData.put("req_no", reqNo);
		jyau_oporgData.put("notorg_list", notOrgList);
		jsonArray.add(jyau_oporgData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}
	public void returnOrgJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_oporgData.put("req_no", reqNo);
		jsonArray.add(jyau_oporgData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}

	//用户角色信息返回的json
	public void returnUserRoleJson(){
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_oporgData.put("req_no", reqNo);
		jyau_oporgData.put("users_data", userRoleArray);
		jsonArray.add(jyau_oporgData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}
}
