package com.am.service;

import com.am.dao.AuMenuOrgDao;
import com.am.utils.DatabaseUtil;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by ZHAO on 2018/5/24.
 */
public class MenuOrgRoleService{
	public static final MenuOrgRoleService service = new MenuOrgRoleService();
	private Log log = Log.getLog(MenuOrgRoleService.class);

	//菜单授权业务逻辑
	public void menuAuth(String menuId,String roleId,String orgdata){
		// 删除menuId,roleId 对应的配置
		AuMenuOrgDao.dao.deleteByMenuId(menuId,roleId);
		//传进来的机构数组为空，循环时候都不进入循环
		org.json.JSONArray jsonArrayOrg = new org.json.JSONArray(orgdata);
		for (int j = 0 ; j < jsonArrayOrg.length(); j++){
			String orgId = jsonArrayOrg.get(j).toString();
			insertMenuOrg(menuId,roleId,orgId);
		}
	}

	public void insertMenuOrg(String menuId,String roleId,String orgId){
		Record record = new Record();
		record.set("MO_ID", DatabaseUtil.getEntityPrimaryKey("MO"));
		record.set("MU_ID",menuId);
		record.set("RL_ID",roleId);
		record.set("ORG_ID",orgId);
		AuMenuOrgDao.dao.save(record);

	}

}
