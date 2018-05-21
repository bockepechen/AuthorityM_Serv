package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuMenuDao;
import com.am.utils.JsonUtil;
import com.jfinal.core.Controller;
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
	String orgId = "";//机构编号
	String reqNo = "";//请求单号
	String roleId = "";//角色编号
	String roleName = "";//角色名称
	List<Record> menuList = null;//可操作的菜单
	String returnCode = "";//返回码
	String returnMessage = "";//返回信息
	JSONArray dictList = CacheKit.get("dataCache","s_dict_returncode");
	JSONObject jb = new JSONObject();

	JSONArray jsonArray = new JSONArray();
	JSONObject jyau_menuData = new JSONObject();

	public void index(){

		//获取请求数据
		//String json = HttpKit.readData(getRequest());
		String json = "{\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\" jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \"CL048201802051125231351\",\n" +
				"\t\t\t\"org_id\": \"123\",\n" +
				"\t\t\t\"role_id\": \"4\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\n" +
				"\t\t\t\"operator_id\": \"O201801301417012263\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";
		//解析Json
		Map map = new HashMap();
		try{
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			operatorId = map.get("operator_id").toString();
			orgId = map.get("org_id").toString();
			roleId = map.get("role_id").toString();
			//查询用户角色
			//Record roleRecord = AuRoleDao.dao.queryRole(operatorId,orgId);
			//if(null == roleRecord){
				////无角色
				//returnCode = "0002";
			//}else{
				//查询可操作的菜单
				List<Record> menuList = AuMenuDao.dao.queryMenu(roleId,orgId);
				returnCode = "0000";
			//}
			returnJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = "9999";
			returnJson();
		}finally {

		}
	}
	public void returnJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("role_id",roleId);
		jyau_menuData.put("role_name",roleName);
		jyau_menuData.put("menu_list",menuList);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}
}
