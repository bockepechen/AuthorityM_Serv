package com.am.service;

import com.am.dao.AuEmpOrgDao;
import com.am.dao.AuMenuOrgDao;
import com.am.dao.AuOrganizationDao;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by ZHAO on 2018/5/28.
 */
public class OrgService {
	public static final OrgService service = new OrgService();
	private Log log = Log.getLog(OrgService.class);

	/**
	 * 删除机构业务逻辑
	 * @param orgId
	 */
	public void delOrg(String orgId) {
		//1.删除机构表2,。删除机构员工角色关系表3.删除菜单机构角色关系表
		Record delOrg = new Record();
		delOrg.set("ORG_ID",orgId);
		AuOrganizationDao.dao.delete(delOrg);
		AuEmpOrgDao.dao.deleteByOrg(orgId);
		AuMenuOrgDao.dao.deleteByOrg(orgId);
	}

}
