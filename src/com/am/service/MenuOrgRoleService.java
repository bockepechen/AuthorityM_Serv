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
	public void menuAuth(String menuId,String roleId,String orgId){
		//先查询是否存在
		Record record = AuMenuOrgDao.dao.findMenuOrg(menuId,roleId,orgId);
		if(null == record){
			insertMenuOrg(menuId,roleId,orgId);
		}
	}

	//取消授权业务逻辑
	public void cancleMenuAuth(String menuId,String roleId,String orgId){
		deleteMenuOrg(menuId,roleId,orgId);

	}

	public void insertMenuOrg(String menuId,String roleId,String orgId){
		Record record = new Record();
		record.set("MO_ID", DatabaseUtil.getEntityPrimaryKey("MO"));
		record.set("MU_ID",menuId);
		record.set("RL_ID",roleId);
		record.set("ORG_ID",orgId);
		AuMenuOrgDao.dao.save(record);

	}
	public void deleteMenuOrg(String menuId,String roleId,String orgId){
		Record record = AuMenuOrgDao.dao.findMenuOrg(menuId,roleId,orgId);
		if(null != record){
			record.set("MU_ID",menuId);
			record.set("RL_ID",roleId);
			record.set("ORG_ID",orgId);
			AuMenuOrgDao.dao.delete(record);
		}

	}
}
