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
		return Db.use(configName).save(tableName,primaryKey,record);
	}

	@Override
	public boolean update(Record record) {
		return false;
	}

	@Override
	public boolean delete(Record record) {
		return Db.use(configName).delete(tableName,primaryKey,record);
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
			String sql = "SELECT DISTINCT au.OP_OPRATORID AS operator_id,au.OP_NAME AS name FROM AU_OPERATOR au , AU_EMPORG ae WHERE au.OP_OPRATORID = ae.OP_OPRATORID AND au.OP_STATUS = '01' AND ae.ORG_ID  = ?";
		return Db.use(configName).find(sql,orgId);
	}
	/**
	 * 查询不属于某机构的用户
	 * @param orgId
	 * @return
	 */
	public  List<Record> findUserNotOrg(String orgId){
		String sql = "SELECT ao.OP_OPRATORID AS operator_id,ao.OP_NAME AS name FROM  AU_OPERATOR ao WHERE NOT  EXISTS (SELECT DISTINCT ae.OP_OPRATORID FROM  AU_EMPORG ae WHERE ae.ORG_ID =? AND ae.OP_OPRATORID = ao.OP_OPRATORID)";
		return Db.use(configName).find(sql,orgId);
	}

	/**
	 * 查询该角色下拥有的用户信息
	 * @param roleId 角色Id
	 * @return
	 */
	public List<Record> findUsersHaveRole(String roleId){
		//String sql = "SELECT s1.*,o.ORG_NAME,o.ORG_CODE FROM(SELECT r.LA_ID,r.OP_OPRATORID,r.RL_ID,r.ORG_ID,p.OP_ACCOUNT,p.OP_NAME FROM AU_EMPORG r,AU_OPERATOR p WHERE r.OP_OPRATORID = p.OP_OPRATORID  AND p.OP_STATUS = '01' AND p.OP_IFOPERATOR = '1' AND r.RL_ID = ? )s1 LEFT JOIN  AU_ORGANIZATION o ON  s1.ORG_ID = o.ORG_ID ";
		String sql = "SELECT r.LA_ID,r.OP_OPRATORID,r.RL_ID,r.ORG_ID,p.OP_ACCOUNT,p.OP_NAME,o.ORG_NAME,o.ORG_CODE FROM AU_EMPORG r,AU_OPERATOR p,AU_ORGANIZATION o WHERE r.OP_OPRATORID = p.OP_OPRATORID AND r.ORG_ID = o.ORG_ID AND p.OP_STATUS = '01' AND p.OP_IFOPERATOR = '1' AND r.RL_ID = ? ";
		return Db.use(configName).find(sql,roleId);
	}


	/**
	 * 查询没有拥有该角色的用户信息
	 * @param roleId 角色Id
	 * @return
	 */
	public List<Record> findUsersNotHaveRole(String roleId){
		//String sql = "SELECT s1.OP_OPRATORID,s1.OP_ACCOUNT,s1.OP_NAME,r1.ORG_ID,r1.ORG_NAME FROM (SELECT p.OP_OPRATORID,p.OP_ACCOUNT,p.OP_NAME FROM AU_OPERATOR p WHERE p.OP_STATUS = '01' AND p.OP_IFOPERATOR = '1' AND NOT EXISTS (SELECT r.OP_OPRATORID,r.ORG_ID FROM AU_EMPORG r WHERE r.OP_OPRATORID = p.OP_OPRATORID AND r.RL_ID = ? ))s1 LEFT JOIN (SELECT l.*,o.ORG_NAME,o.ORG_CODE FROM AU_EMPORG l,AU_ORGANIZATION o WHERE l.ORG_ID = o.ORG_ID )r1 on s1.OP_OPRATORID = r1.OP_OPRATORID ";
		String sql = "SELECT s1.OP_OPRATORID,s1.OP_ACCOUNT,s1.OP_NAME,o.ORG_ID,o.ORG_NAME FROM (SELECT p.OP_OPRATORID,p.OP_ACCOUNT,p.OP_NAME FROM AU_OPERATOR p WHERE p.OP_STATUS = '01' AND p.OP_IFOPERATOR = '1' AND NOT EXISTS (SELECT r.OP_OPRATORID,r.ORG_ID FROM AU_EMPORG r WHERE r.OP_OPRATORID = p.OP_OPRATORID AND r.RL_ID = ? ))s1,AU_EMPORG l,AU_ORGANIZATION o WHERE l.ORG_ID = o.ORG_ID AND s1.OP_OPRATORID = l.OP_OPRATORID ";
		return Db.use(configName).find(sql,roleId);
	}

	/**
	 * 根据机构和用户编号查询
	 * @param orgId
	 * @param operatorId
	 * @return
	 */
	public  List<Record> findUserByOrgOper(String orgId,String operatorId){
		String sql = "SELECT * FROM AU_EMPORG WHERE ORG_ID = ? AND OP_OPRATORID = ?";
		return Db.use(configName).find(sql,orgId,operatorId);
	}

	/**
	 * 根据机构编号删除
	 * @param orgId
	 * @return
	 */
	public  int deleteByOrg(String orgId){
		String sql = "DELETE  FROM AU_EMPORG WHERE ORG_ID = ?";
		return Db.use(configName).update(sql,orgId);
	}

	/**
	 * 查询某角色下所有用户
	 * @param roleId
	 * @return
	 */
	public List<Record> findAllOperatorByRoleId(String roleId){
		String sql = "SELECT DISTINCT op.OP_OPRATORID AS operator_id,OP_NAME AS name FROM AU_EMPORG eo,AU_OPERATOR op WHERE eo.OP_OPRATORID = op.OP_OPRATORID AND op.OP_STATUS = '01' AND eo.RL_ID = ?";
		return Db.use(configName).find(sql,roleId);
	}

	/**
	 * 查询某机构下所有用户
	 * @param orgId
	 * @return
	 */
	public List<Record> findAllOperatorByOrgId(String orgId){
		String sql = "SELECT DISTINCT op.OP_OPRATORID AS operator_id,OP_NAME AS name FROM AU_EMPORG eo,AU_OPERATOR op WHERE eo.OP_OPRATORID = op.OP_OPRATORID AND op.OP_STATUS = '01' AND eo.ORG_ID = ?";
		return Db.use(configName).find(sql,orgId);
	}
}
