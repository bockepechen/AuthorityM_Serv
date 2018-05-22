package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuMenuDao;
import com.am.service.MenuService;
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
 * Created by ZHAO on 2018/5/11.
 */
public class MenuController extends Controller{
	static Log log = Log.getLog(MenuController.class);

	String operatorId = "";//用户编号
	String accountId = "";//登录账号
	String type = "";//01新增--02修改
	String reqNo = "";//请求单号
	String menuId = "";//菜单编号
	String menuName = "";//菜单名称
	String menuCode = "";//菜单代码
	String displayOrder = "";//显示顺序
	String ifLeaf = "";//是否子菜单-0否1是
	String parentName = "";//父级菜单显示名称
	String parentId = "";//父级菜单编号
	//String roleId = "";//角色编号
	//String roleName = "";//角色名称
	List<Record> menuList = null;//显示菜单列表
	String returnCode = "";//返回码
	String returnMessage = "";//返回信息
	JSONArray dictList = CacheKit.get("dataCache","s_dict_returncode");
	JSONObject jb = new JSONObject();

	JSONArray jsonArray = new JSONArray();
	JSONObject jyau_menuData = new JSONObject();
	//显示菜单列表
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
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				menuList = AuMenuDao.dao.findAll();
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU017",json,jb.toString());
		}
	}
	//删除菜单
	public void deleteMenu(){

		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"AU2018048201802051125231351\",\n" +
				"\t\t\t\"menu_id\": \"1\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"O201801301417012263\",\n" +
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
			menuId = map.get("menu_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(menuId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else{
				MenuService.service.deleteMenu(menuId);
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnDelJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnDelJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU016",json,jb.toString());
		}
	}

	/**
	 * 查询菜单详细信息
	 */
	public void displayMenu(){

		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"AU2018048201802051125231351\",\n" +
				"\t\t\t\"menu_id\": \"1\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"O201801301417012263\",\n" +
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
			menuId = map.get("menu_id").toString();
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(menuId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				Record menuRecord = AuMenuDao.dao.queryById(menuId);
				if (null != menuRecord) {
					String parentId = menuRecord.getStr("MU_PARENTID");
					if(EmptyUtils.isNotEmpty(parentId)) {
						Record parentMenuRecord = AuMenuDao.dao.queryById(parentId);
						parentName = parentMenuRecord.getStr("MU_NAME");
					}
					menuName = menuRecord.getStr("MU_NAME");
					menuCode = menuRecord.getStr("MU_CODE");
					ifLeaf = menuRecord.getStr("MU_IFLEAF");
					if(null != menuRecord.getInt("MU_DISPLAYORDER")){
						displayOrder = menuRecord.getInt("MU_DISPLAYORDER").toString();
					}
				}
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnDisplayJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnDisplayJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU019",json,jb.toString());
		}
	}

	//新增菜单--新增父级菜单，新增子菜单
	//修改菜单
	public void modifyMenu(){

		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"AU2018048201802051125231351\",\n" +
				"\t\t\t\"parent_id\": \"1\",\n" +
				"\t\t\t\"menu_id\": \"1\",\n" +
				"\t\t\t\"menu_name\": \"新增子菜单一\",\n" +
				"\t\t\t\"menu_code\": \"10101\",\n" +
				"\t\t\t\"if_leaf\": \"1\",\n" +
				"\t\t\t\"display_order\": \"1\",\n" +
				"\t\t\t\"type\": \"01\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"O201801301417012263\",\n" +
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
			type = map.get("type").toString();
			menuId = map.get("menu_id").toString();
			parentId = map.get("parent_id").toString();
			menuName = map.get("menu_name").toString();
			menuCode = map.get("menu_code").toString();
			ifLeaf = map.get("if_leaf").toString();
			displayOrder = map.get("display_order").toString();
			operatorId = map.get("operator_id").toString();
			if(!ifCheck(reqNo,operatorId,menuName,menuCode,ifLeaf,displayOrder,type,menuId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				if(type.equals("01")) {//新增菜单--增子菜单,需要传parentId
					MenuService.service.InsertMenu(menuName, menuCode, ifLeaf, displayOrder, parentId);
					returnCode = ReturnCodeUtil.returnCode;
				}else{//修改,传menuId
					MenuService.service.UpdateMenu(menuName, menuCode, ifLeaf, displayOrder, menuId);
					returnCode = ReturnCodeUtil.returnCode;
				}
			}
			returnDelJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnDelJson();
		}finally {
			if(type.equals("01")) {
				PubModelUtil.apiRecordBean(map,"AU02001",json,jb.toString());
			}else{
				PubModelUtil.apiRecordBean(map,"AU02002",json,jb.toString());
			}
		}
	}

	public boolean ifCheck(String reqNo,String operatorId,String menuName,String menuCode,String ifLeaf,String displayOrder,String type,String menuId){
		boolean flag = false;
		if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(operatorId) || EmptyUtils.isEmpty(menuName) || EmptyUtils.isEmpty(menuCode)
				|| EmptyUtils.isEmpty(ifLeaf) || EmptyUtils.isEmpty(displayOrder) || EmptyUtils.isEmpty(type)){

		}else{
			if(type.equals("02")){
				if(EmptyUtils.isNotEmpty(menuId)){
					flag = true;
				}
			}else if(type.equals("01")){
				flag = true;
			}
		}
		return flag;
	}

	public void returnDelJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}
	public void returnJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("menu_list",menuList);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}
	public void returnDisplayJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("parent_name",parentName);
		jyau_menuData.put("menu_name",menuName);
		jyau_menuData.put("menu_code",menuCode);
		jyau_menuData.put("if_leaf",ifLeaf);
		jyau_menuData.put("display_order",displayOrder);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}
}
