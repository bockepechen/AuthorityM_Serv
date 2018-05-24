package com.am.service;

import com.am.dao.AuEmpOrgDao;
import com.am.utils.DatabaseUtil;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/21.
 */
public class OrgEmpRoleService {
	public static final OrgEmpRoleService service = new OrgEmpRoleService();
	private Log log = Log.getLog(OrgEmpRoleService.class);

	/**
	 * 机构添加人员业务逻辑
	 *
	 * @param orgId
	 * @param jsonArrayUser
	 * @return
	 */
	public void insertOrgUser(String orgId, org.json.JSONArray jsonArrayUser) {
		for (int i = 0; i < jsonArrayUser.length(); i++) {
			String operId = jsonArrayUser.get(i).toString();
			Record addRecord = new Record();
			addRecord.set("LA_ID", DatabaseUtil.getEntityPrimaryKey("LA"));
			addRecord.set("ORG_ID", orgId);
			addRecord.set("OP_OPRATORID", operId);
			AuEmpOrgDao.dao.save(addRecord);
		}
	}

	/**
	 * 机构删除人员业务逻辑
	 *
	 * @param orgId
	 * @param jsonArrayUser
	 * @return
	 */
	public void deleteOrgUser(String orgId, org.json.JSONArray jsonArrayUser) {
		for (int i = 0; i < jsonArrayUser.length(); i++) {
			String operatorId = jsonArrayUser.get(i).toString();
			List<Record> empOrgRecordList = AuEmpOrgDao.dao.findUserByOrgOper(orgId,operatorId);
			for(Record record :empOrgRecordList){
				record.set("OP_OPRATORID", operatorId);
				record.set("ORG_ID", orgId);
				AuEmpOrgDao.dao.delete(record);
			}
		}
	}
}
