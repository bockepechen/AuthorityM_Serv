package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuMenuDao;
import com.am.bean.MenuBean;
import com.am.utils.*;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

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
	List<MenuBean> resultList=new ArrayList<>();//多级菜单列表
	String returnCode = "";//返回码
	String returnMessage = "";//返回信息
	JSONArray dictList = CacheKit.get("dataCache","s_dict_returncode");
	JSONObject jb = new JSONObject();
	JSONArray jsonArray = new JSONArray();
	JSONObject jyau_menuData = new JSONObject();

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
			if(EmptyUtils.isEmpty(reqNo) ){
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
			returnJson();
		}catch (Exception e){
			log.error(e.getMessage(),e);
			returnCode = ReturnCodeUtil.returnCode2;
			returnJson();
		}finally {
			PubModelUtil.apiRecordBean(map,"AU025",json,jb.toString());
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

	public void returnJson(){
		returnMessage = JsonUtil.getDictName(dictList,returnCode);
		jyau_menuData.put("req_no",reqNo);
		jyau_menuData.put("operator_id",operatorId);
		jyau_menuData.put("multi_menuList",resultList);
		jsonArray.add(jyau_menuData);
		jb = JsonUtil.returnJson(jsonArray,returnCode,returnMessage);
		renderJson(jb);
	}
}
