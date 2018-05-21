package com.am.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/9.
 */
public class AuRoleDao implements IBaseDao{
	public static final AuRoleDao dao = new AuRoleDao();
	private String configName = "sqlserver_auth";
	private String tableName = "AU_ROLE";
	private String primaryKey = "RL_ID";
	@Override
	public boolean save(Record record) {
		return Db.use(configName).save(tableName,primaryKey,record);
	}

	@Override
	public boolean update(Record record) {
		return Db.use(configName).update(tableName,primaryKey,record);
	}

	@Override
	public boolean delete(Record record) {
		return Db.use(configName).delete(tableName,primaryKey,record);
	}

	@Override
	public List<Record> findAll() {
		String sql = "SELECT * FROM AU_ROLE";
		return Db.use(configName).find(sql);
	}

	/**
	 * 根据用户所选机构查询用户角色
	 * @param operatorId
	 * @param orgId
	 * @return
	 */
	/*public Record queryRole(String operatorId, String orgId){
		String sql = "SELECT r.RL_ID,r.RL_NAME FROM AU_ROLE r LEFT JOIN  AU_EMPORG eo ON eo.RL_ID = r.RL_ID WHERE eo.OP_OPRATORID = ?  AND eo.ORG_ID = ?";
		return Db.use(configName).findFirst(sql,operatorId,orgId);
	}*/
}
