package com.am.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/9.
 */
public class AuEmployeeDao implements  IBaseDao{
	public static final AuEmployeeDao dao = new AuEmployeeDao();
	private String configName = "sqlserver_auth";
	private String tableName = "AU_EMPLOYEE";
	private String primaryKey = "EMP_ID";

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
		String sql = "SELECT * FROM AU_EMPLOYEE";
		return Db.use(configName).find(sql);
	}

	/**
	 * 根据操作编号查询员工信息
	 * @param operatorId
	 * @return
	 */
	public  Record findByOperatorId(String operatorId){
		String sql = "SELECT * FROM  AU_EMPLOYEE  WHERE OP_OPRATORID = ? ";
		return Db.use(configName).findFirst(sql,operatorId);
	}

	/**
	 * 查询无机构的员工
	 * @return
	 */
	public List<Record> findEmpNoOrg(){
		String sql= "SELECT OP_OPRATORID AS operator_id,EMP_NAME AS name FROM AU_EMPLOYEE  WHERE ORG_ID IS NULL";
		return Db.use(configName).find(sql);
	}

}
