package com.am.dao;

import com.am.utils.EmptyUtils;
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
	public Record queryRole(String operatorId, String orgId){
		String sql = "SELECT r.RL_ID,r.RL_NAME FROM AU_ROLE r LEFT JOIN  AU_EMPORG eo ON eo.RL_ID = r.RL_ID WHERE eo.OP_OPRATORID = ?  AND eo.ORG_ID = ?";
		return Db.use(configName).findFirst(sql,operatorId,orgId);
	}

	/**
	 * 判断角色名称是否存在
	 * @param roleName 角色名称
	 * @param roleId 角色Id
	 * @return
	 */
	public int findByName(String roleName,String roleId){
		String sql = "";
		if(EmptyUtils.isEmpty(roleId)){
			sql = "SELECT count(*) as cnt FROM AU_ROLE WHERE RL_NAME = ? ";
			return Db.use(configName).findFirst(sql,roleName).get("cnt");
		}else {
			sql = "SELECT count(*) as cnt FROM AU_ROLE WHERE RL_NAME = ?  AND RL_ID != ? ";
			return Db.use(configName).findFirst(sql,roleName,roleId).get("cnt");
		}
	}

	/**
	 * 判断角色编号是否存在
	 * @param roleCode 角色编号
	 * @param roleId 角色Id
	 * @return
	 */
	public int findByCode(String roleCode,String roleId){
		String sql = "";
		if(EmptyUtils.isEmpty(roleId)){
			sql = "SELECT count(*) as cnt FROM AU_ROLE WHERE OR RL_CODE = ? ";
			return Db.use(configName).findFirst(sql,roleCode).get("cnt");
		}else {
			sql = "SELECT count(*) as cnt FROM AU_ROLE WHERE RL_CODE = ? AND RL_ID != ? ";
			return Db.use(configName).findFirst(sql,roleCode,roleId).get("cnt");
		}
	}
}
