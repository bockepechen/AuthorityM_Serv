package com.am.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/16.
 */
public class AuEmpOrgDao implements IBaseDao {
	public static final AuEmpOrgDao dao = new AuEmpOrgDao();
	private String configName = "sqlserver_auth";
	private String tableName = "AU_EMPORG";
	private String primaryKey = "LA_ID";


	@Override
	public boolean save(Record record) {
		return false;
	}

	@Override
	public boolean update(Record record) {
		return false;
	}

	@Override
	public boolean delete(Record record) {
		return false;
	}

	@Override
	public List<Record> findAll() {
		return null;
	}

	/**
	 * 查询机构下用户
	 * @param orgId
	 * @return
	 */
	public  List<Record> findUserInOrg(String orgId){
		String sql = "SELECT au.OP_OPRATORID AS operator_id,au.OP_NAME AS name FROM AU_OPERATOR au LEFT JOIN  AU_EMPORG ae ON au.OP_OPRATORID = ae.OP_OPRATORID WHERE ae.ORG_ID  = ?";
		return Db.use(configName).find(sql,orgId);
	}
}
