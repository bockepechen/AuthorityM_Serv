package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuMenuDao;
import com.am.bean.MenuBean;
import com.am.dao.AuMenuOrgDao;
import com.am.dao.AuOrganizationDao;
import com.am.dao.AuRoleDao;
import com.am.service.MenuOrgRoleService;
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
	String orgdata = ""; // 操作数据（机构）
	List<MenuBean> resultList=new ArrayList<>();//多级菜单列表
	List<Record> roleList = null;//菜单对应的角色列表
	List<Record> orgList = null;//角色对应的机构列表
	List<Record> menuByRoleList = null;//角色对应的菜单列表
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

	JSONArray menuOrgArray = new JSONArray(); // 返回拥有菜单的机构listJson
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
				multiLevelMenu();
				returnCode = ReturnCodeUtil.returnCode;
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


	//查询菜单对应的角色及机构
	public void queryRoleByMenu(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"CL048201802051125231351\",\n" +
				"\t\t\t\"menu_id\": \"MU201805291413135112\"\n" +
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
				getDetailInfo(menuId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			reuturnMenuRole();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			reuturnMenuRole();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU026",json,jb.toString());
		}
	}



	//查询角色对应的菜单及机构
	public void queryMenuOrgByRole(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"CL048201802051125231351\",\n" +
				"\t\t\t\"role_id\": \"RL201805241043060626\"\n" +
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
			roleId = map.get("role_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(roleId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				getDetailInfoByRole(roleId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			reuturnRoleMenu();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			reuturnRoleMenu();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU032",json,jb.toString());
		}
	}

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
/*				String json = "{\n" +
						"\t\"jyau_content\": {\n" +
						"\t\t\"jyau_reqData\": [{\n" +
						"\t\t\t\"req_no\": \"CL048201802051125231351\",\n" +
						"\t\t\t\"menu_id\": \"MU201805231107261367\",\n" +
						"\t\t\t\"role_id\": \"1\",\n" +
						*//*"\t\t\t\"org_ids\": [\"OG201805171438586409\", \"OG201805171726129979\"]\n" +*//*
						"\t\t\t\"org_ids\": []\n" +
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
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			operatorId = map.get("operator_id").toString();
			accountId = map.get("account_id").toString();
			menuId = map.get("menu_id").toString();
			roleId = map.get("role_id").toString();
			// orgId = map.get("org_id").toString();
			orgdata = map.get("org_ids").toString();
			//String roleData = map.get("role_data").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(roleId)
					||EmptyUtils.isEmpty(menuId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				//org.json.JSONArray jsonArrayRole = new org.json.JSONArray(roleData);
				boolean suc = Db.tx(new IAtom() {
					@Override
					public boolean run() throws SQLException {
						try {
							//调用业务授权逻辑
							MenuOrgRoleService.service.menuAuth(menuId,orgId,orgdata);
							/*// 循环插入
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
							}*/

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

	//角色-菜单-机构
	public void reuturnRoleMenu(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("menu_data",resultList);
		jyau_menuData.put("org_data",allOrg);
		jyau_menuData.put("menuOrg_data",menuOrgArray);
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
			JSONArray ja = queryOrgList(menuId,roleId);
			roleJsonObject.put("rg_data",ja);
			userRoleArray.add(roleJsonObject);
		}
	}

	// 根据角色ID查询对应的菜单机构
	private void  getDetailInfoByRole(String roleId){
		allOrg = AuOrganizationDao.dao.findAll();//所有机构
		multiLevelMenu();//所有菜单列表，封装在resultList
		menuByRoleList = AuMenuOrgDao.dao.findMenuByRole(roleId);
		for (Record menu : menuByRoleList){
			String menuId = menu.getStr("menu_id");
			String menuName = menu.getStr("menu_name");
			JSONObject menuJsonObject = new JSONObject();
			menuJsonObject.put("menu_id", menuId);
			menuJsonObject.put("menu_name", menuName);
			JSONArray ja = queryOrgList(menuId,roleId);
			menuJsonObject.put("mg_data",ja);
			menuOrgArray.add(menuJsonObject);
		}
	}
	//角色菜单对应的机构列表
	public JSONArray  queryOrgList(String menuId,String roleId){
		List<Record> orgList = null; // 菜单或者角色对应的orgList
		orgList = AuMenuOrgDao.dao.findOrgByRole(roleId,menuId);
		JSONArray orgArray = new JSONArray(); // 返回角色或者菜单拥有的机构listJson
		for (Record org : orgList){
			JSONObject orgJsonObject = new JSONObject();
			orgJsonObject.put("org_id", org.getStr("org_id"));
			orgJsonObject.put("org_name", org.getStr("org_name"));
			orgArray.add(orgJsonObject);
		}
		return orgArray;
	}
	//多级菜单的查询
	public void multiLevelMenu( ){
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
