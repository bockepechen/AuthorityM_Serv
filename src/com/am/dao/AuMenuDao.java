package com.am.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/9.
 */
public class AuMenuDao implements  IBaseDao {
	public static final AuMenuDao dao = new AuMenuDao();

	private String configName = "sqlserver_auth";
	private String tableName = "AU_MENU";
	private String primaryKey = "MU_ID";

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
		String sql = "SELECT * FROM AU_MENU";
		return Db.use(configName).find(sql);
	}

	/**
	 * 查询角色机构可以操作的菜单
	 * @param roleId
	 * @param orgId
	 * @return
	 */
	public List<Record> queryMenu(String roleId, String orgId){
		String sql = "SELECT m.MU_ID,m.MU_NAME  FROM AU_MENU m LEFT JOIN AU_MENUORG mo ON mo.MU_ID = m.MU_ID  WHERE mo.ORG_ID = ? AND mo.RL_ID =?";
		return Db.use(configName).find(sql,roleId,orgId);
	}
}
