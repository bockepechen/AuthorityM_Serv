package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuMenuDao;
import com.am.bean.MenuBean;
import com.am.dao.AuMenuOrgDao;
import com.am.dao.AuOrganizationDao;
import com.am.dao.AuRoleDao;
import com.am.service.MenuOrgRoleService;
import com.am.service.OrgEmpRoleService;
import com.am.utils.*;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ZHAO on 2018/5/22.
 */
public class MenuAuthController extends Controller{
	static Log log = Log.getLog(MenuAuthController.class);

	String operatorId = "";//用户编号
	String accountId = "";//登录账号
	String reqNo = "";//请求单号
	String menuId = "";//菜单编号
	String roleId = "";//角色编号
	String orgId = "";//机构编号
	List<MenuBean> resultList=new ArrayList<>();//多级菜单列表
	List<Record> roleList = null;//菜单对应的角色列表
	List<Record> orgList = null;//角色对应的机构列表
	JSONArray  menuList =  new JSONArray();//可操作菜单列表
	String returnCode = "";//返回码
	String returnMessage = "";//返回信息
	JSONArray dictList = CacheKit.get("dataCache","s_dict_returncode");
	JSONArray actionList = CacheKit.get("dataCache","s_dict_sysparams");
	JSONObject jb = new JSONObject();
	JSONArray jsonArray = new JSONArray();
	JSONObject jyau_menuData = new JSONObject();
	List<Record> allRole = null;// 所有的角色的列表信息
	List<Record> allOrg = null; // 所有的机构的列表信息


	JSONArray userRoleArray = new JSONArray(); // 返回拥有角色的用户listJson






	//显示菜单层次列表
	public void index(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"CL048201802051125231351\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
				"\t\t\t\"ip_address\": \"10.2.0.116\",\n" +
				"\t\t\t\"system_id\": \"10909\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			operatorId = map.get("operator_id").toString();
			accountId = map.get("account_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(accountId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				List<Record> menuList = AuMenuDao.dao.queryMenu();
				List<MenuBean> mbList = new ArrayList<>();
				for(int i=0;i<menuList.size();i++){
					MenuBean mb = new MenuBean();
					mb.setMenu_id(menuList.get(i).getStr("menu_id"));
					mb.setName(menuList.get(i).getStr("menu_name"));
					mb.setParent_id(menuList.get(i).getStr("parent_id"));

					mbList.add(mb);
				}

				//获取顶层元素集合
				for (MenuBean entity : mbList) {
					String parentId=entity.getParent_id();
					//if(parentId==null||topId.equals(parentId)){
					if(parentId==null){
						resultList.add(entity);
					}
				}
				//获取每个顶层元素的子数据集合
				for (MenuBean entity : resultList) {
					entity.setChild_list(getChild(entity.getMenu_id(),mbList));
				}
			}
			returnMenuJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnMenuJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU025",json,jb.toString());
		}
	}


	//查询菜单对应的角色
	public void queryRoleByMenu(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"CL048201802051125231351\",\n" +
				"\t\t\t\"menu_id\": \"1\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
				"\t\t\t\"ip_address\": \"10.2.0.116\",\n" +
				"\t\t\t\"system_id\": \"10909\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			operatorId = map.get("operator_id").toString();
			accountId = map.get("account_id").toString();
			menuId = map.get("menu_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(menuId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				roleList = AuMenuOrgDao.dao.findRoleByMenu(menuId);
				getDetailInfo(menuId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			// returnRoleJson();
			reuturnMenuRole();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			// returnRoleJson();
			reuturnMenuRole();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU026",json,jb.toString());
		}
	}


	// 根据菜单ID查询对应的角色机构
	private void  getDetailInfo(String menuId){
		allOrg = AuOrganizationDao.dao.findAll();
		allRole = AuRoleDao.dao.findAllRole();
		roleList = AuMenuOrgDao.dao.findRoleByMenu(menuId);
		for (Record role : roleList){
			String roleId = role.getStr("role_id");
			String roleName = role.getStr("role_name");
			JSONObject roleJsonObject = new JSONObject();
			roleJsonObject.put("role_id", roleId);
			roleJsonObject.put("role_Name", roleName);

			List<Record> roleOrgList = null; // 角色对应的orgList
			roleOrgList = AuMenuOrgDao.dao.findOrgByRole(roleId);
			JSONArray roleOrgArray = new JSONArray(); // 返回角色拥有的机构listJson
			for (Record org : roleOrgList){
				JSONObject orgJsonObject = new JSONObject();
				orgJsonObject.put("org_id", org.getStr("org_id"));
				orgJsonObject.put("org_name", org.getStr("org_name"));
				roleOrgArray.add(orgJsonObject);
			}
			roleJsonObject.put("rg_data",roleOrgArray);
			userRoleArray.add(roleJsonObject);
		}
	}

	//查询角色对应的机构
	/*public void queryOrgByRole(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"CL048201802051125231351\",\n" +
				"\t\t\t\"role_id\": \"1\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
				"\t\t\t\"ip_address\": \"10.2.0.116\",\n" +
				"\t\t\t\"system_id\": \"10909\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";
		//解析Json
		Map map = new HashMap();
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			operatorId = map.get("operator_id").toString();
			accountId = map.get("account_id").toString();
			roleId = map.get("role_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(roleId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				orgList = AuMenuOrgDao.dao.findOrgByRole(roleId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnOrgJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnOrgJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU027",json,jb.toString());
		}
	}*/

	//菜单-角色-机构授权
	public void operatorMenuAuth(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"  \"jyau_content\": {\n" +
				"    \" jyau_reqData\": [\n" +
				"      {\n" +
				"        \"req_no\": \"AU2018048201802051125231351\",\n" +
				"        \"menu_id\": \"MU201805231036299192\",\n" +
				"        \"role_data\": [\n" +
				"          {\n" +
				"            \"role_name\": \"角色1\",\n" +
				"            \"role_id\": \"RL201805230932410066\",\n" +
				"            \"rg_data\": [\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171438586409\",\n" +
				"                \"org_name\": \"天津嘉业智德分公司\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171726129979\",\n" +
				"                \"org_name\": \"机构0005\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805240947521098\",\n" +
				"                \"org_name\": \"机构0006\"\n" +
				"              }\n" +
				"            ]\n" +
				"          },\n" +
				"          {\n" +
				"            \"role_name\": \"角色2\",\n" +
				"            \"role_id\": \"RL201805241043060626\",\n" +
				"            \"rg_data\": [\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171438586409\",\n" +
				"                \"org_name\": \"天津嘉业智德分公司\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171726129979\",\n" +
				"                \"org_name\": \"机构0005\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805240947521098\",\n" +
				"                \"org_name\": \"机构0006\"\n" +
				"              }\n" +
				"            ]\n" +
				"          },\n" +
				"          {\n" +
				"            \"role_name\": \"角色3\",\n" +
				"            \"role_id\": \"RL201805241043124038\",\n" +
				"            \"rg_data\": [\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171438586409\",\n" +
				"                \"org_name\": \"天津嘉业智德分公司\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171726129979\",\n" +
				"                \"org_name\": \"机构0005\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805240947521098\",\n" +
				"                \"org_name\": \"机构0006\"\n" +
				"              }\n" +
				"            ]\n" +
				"          },\n" +
				"          {\n" +
				"            \"role_name\": \"角色4\",\n" +
				"            \"role_id\": \"RL201805241043192919\",\n" +
				"            \"rg_data\": [\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171438586409\",\n" +
				"                \"org_name\": \"天津嘉业智德分公司\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171726129979\",\n" +
				"                \"org_name\": \"机构0005\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805240947521098\",\n" +
				"                \"org_name\": \"机构0006\"\n" +
				"              }\n" +
				"            ]\n" +
				"          },\n" +
				"          {\n" +
				"            \"role_name\": \"角色5\",\n" +
				"            \"role_id\": \"RL201805241043252102\",\n" +
				"            \"rg_data\": [\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171438586409\",\n" +
				"                \"org_name\": \"天津嘉业智德分公司\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805171726129979\",\n" +
				"                \"org_name\": \"机构0005\"\n" +
				"              },\n" +
				"              {\n" +
				"                \"org_id\": \"OG201805240947521098\",\n" +
				"                \"org_name\": \"机构0006\"\n" +
				"              }\n" +
				"            ]\n" +
				"          }\n" +
				"        ]\n" +
				"      }\n" +
				"    ],\n" +
				"    \"jyau_pubData\": {\n" +
				"      \"operator_id\": \"O201801301417012263\",\n" +
				"      \"account_id\": \"systemman\",\n" +
				"      \"ip_address\": \"10.2.0.116\",\n" +
				"      \"system_id\": \"10909\"\n" +
				"    }\n" +
				"  }\n" +
				"}";*/
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"CL048201802051125231351\",\n" +
				"\t\t\t\"menu_id\": \"1\",\n" +
				"\t\t\t\"role_id\": \"1\",\n" +
				"\t\t\t\"org_id\": \"1\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
				"\t\t\t\"ip_address\": \"10.2.0.116\",\n" +
				"\t\t\t\"system_id\": \"10909\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			operatorId = map.get("operator_id").toString();
			accountId = map.get("account_id").toString();
			menuId = map.get("menu_id").toString();
			// roleId = map.get("role_id").toString();
			// orgId = map.get("org_id").toString();
			String roleData = map.get("role_data").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(roleData)
					||EmptyUtils.isEmpty(menuId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				org.json.JSONArray jsonArrayRole = new org.json.JSONArray(roleData);
				boolean suc = Db.tx(new IAtom() {
					@Override
					public boolean run() throws SQLException {
						try {
							// 删除menuId 对应的配置
							AuMenuOrgDao.dao.deleteByMenuId(menuId);
							// 循环插入
							for (int i = 0; i < jsonArrayRole.length(); i++) {
								org.json.JSONObject jsonRoleObject = (org.json.JSONObject) jsonArrayRole.get(i);
								String orgData = jsonRoleObject.optString("rg_data");
								String role_id = jsonRoleObject.optString("role_id");
								org.json.JSONArray jsonArrayOrg = new org.json.JSONArray(orgData);
								for (int j = 0 ; j < jsonArrayOrg.length(); j++){
									org.json.JSONObject jsonOrgObject = (org.json.JSONObject) jsonArrayOrg.get(j);
									String orgId = jsonOrgObject.getString("org_id");
									Record lationRecord = new Record();
									lationRecord.set("MO_ID", DatabaseUtil.getEntityPrimaryKey("MO"));
									lationRecord.set("MU_ID", menuId);
									lationRecord.set("RL_ID", role_id);
									lationRecord.set("ORG_ID", orgId);
									AuMenuOrgDao.dao.save(lationRecord);
								}
							}

						} catch (Exception e) {
							log.error(e.getMessage(), e);
							log.error("菜单授权失败：" + e.getMessage());
							returnJson();
							return false;
						}
						return true;
					}

				});
				if (suc) returnCode = ReturnCodeUtil.returnCode;
				else returnCode = ReturnCodeUtil.returnCode12;
			}

			returnJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU028",json,jb.toString());
		}
	}

	//取消授权
	/*public void cancleOperatorMenuAuth(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"CL048201802051125231351\",\n" +
				"\t\t\t\"menu_id\": \"1\",\n" +
				"\t\t\t\"role_id\": \"1\",\n" +
				"\t\t\t\"org_id\": \"1\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
				"\t\t\t\"ip_address\": \"10.2.0.116\",\n" +
				"\t\t\t\"system_id\": \"10909\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";
		//解析Json
		Map map = new HashMap();
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			operatorId = map.get("operator_id").toString();
			accountId = map.get("account_id").toString();
			menuId = map.get("menu_id").toString();
			roleId = map.get("role_id").toString();
			orgId = map.get("org_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(roleId)
					||EmptyUtils.isEmpty(menuId) || EmptyUtils.isEmpty(orgId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				MenuOrgRoleService.service.cancleMenuAuth(menuId,roleId,orgId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU029",json,jb.toString());
		}
	}*/

	//查询可操作的菜单
	public void queryOperatorMenu(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"AU048201802051125231351\",\n" +
				"\t\t\t\"org_id\": \"OG201805171726129979\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
				"\t\t\t\"ip_address\": \"10.2.0.116\",\n" +
				"\t\t\t\"system_id\": \"10909\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			operatorId = map.get("operator_id").toString();
			accountId = map.get("account_id").toString();
			orgId = map.get("org_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(orgId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				List<Record> muList = AuMenuOrgDao.dao.authMenu(operatorId,orgId);
				for(Record  record :muList){
					String menuAction = "";
					String mAction = record.getStr("MU_ACTION");
					if(EmptyUtils.isNotEmpty(mAction)){
						String preFix = JsonUtil.getDictName(actionList,"url");
						menuAction = preFix + mAction;
					}
					JSONObject jo = new JSONObject();
					jo.put("menu_action",menuAction);
					jo.put("menu_name",record.getStr("MU_NAME"));
					menuList.add(jo);
				}
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnOperatorMenuJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnOperatorMenuJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU030",json,jb.toString());
		}
	}

	public void returnRoleJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("role_list",roleList);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	//菜单-角色-机构
	public void reuturnMenuRole(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("role_data",allRole);
		jyau_menuData.put("org_data",allOrg);
		jyau_menuData.put("roleOrg_data",userRoleArray);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	public void returnOperatorMenuJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("menu_list",menuList);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	public void returnOrgJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("org_list",orgList);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	public void returnJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	public void returnMenuJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("multi_menuList",resultList);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}

	//查询子菜单
	private List<MenuBean> getChild(String id, List<MenuBean> rootMenu) {
		List<MenuBean> childList=new ArrayList<>();
		String parentId;
		//子集的直接子对象
		for (MenuBean entity : rootMenu) {
			parentId=entity.getParent_id();
			if(id.equals(parentId)){
				childList.add(entity);
			}
		}
		//子集的间接子对象
		for (MenuBean entity : childList) {
			entity.setChild_list(getChild(entity.getMenu_id(), rootMenu));
		}
		//递归退出条件
		if(childList.size()==0){
			return null;
		}
		return childList;
	}

}
