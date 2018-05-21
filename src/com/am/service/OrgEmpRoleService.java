package com.am.service;

import com.am.dao.AuEmpOrgDao;
import com.am.utils.DatabaseUtil;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by ZHAO on 2018/5/21.
 */
public class OrgEmpRoleService {
	public static final OrgEmpRoleService service = new OrgEmpRoleService();
	private Log log = Log.getLog(OrgEmpRoleService.class);

	/**
	 * 机构添加人员业务逻辑
	 * @param orgId
	 * @param operatorId
	 * @return
	 */
	public void insertOrgUser(String orgId,String operatorId){
		Record record = new Record();
		record.set("LA_ID", DatabaseUtil.getEntityPrimaryKey("LA"));
		record.set("ORG_ID",orgId);
		record.set("OP_OPRATORID",operatorId);
		AuEmpOrgDao.dao.save(record);

	}
}
