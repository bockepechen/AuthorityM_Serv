package com.am.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/9.
 */
public class AuOperatorDao implements IBaseDao{
	public static final AuOperatorDao dao = new AuOperatorDao();
	private String configName = "sqlserver_auth";
	private String tableName = "AU_OPERATOR";
	private String primaryKey = "OP_OPRATORID";

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
		String sql = "SELECT * FROM AU_OPERATOR";
		return Db.use(configName).find(sql);
	}

	/**
	 * 根据登录账号和密码查询
	 * @param accountId
	 * @param pwd
	 * @return
	 */
	public Record queryByaccountIdPwd(String accountId, String pwd){
		String sql = "SELECT * FROM AU_OPERATOR WHERE OP_ACCOUNT = ? AND OP_PWD = ? AND OP_STATUS = '01'";
		return Db.use(configName).findFirst(sql,accountId,pwd);
	}

	/**
	 * 查询用户所属哪些机构
	 * @param operatorId
	 * @return
	 */
	public List<Record> queryOrgByOperatorId(String operatorId){
		String sql  = "SELECT ORG_ID,ORG_NAME FROM AU_ORGANIZATION o LEFT JOIN AU_EMPORG eo ON o.ORG_ID = eo.ORG_ID WHERE eo.OP_OPRATORID = ?";
		return Db.use(configName).find(sql,operatorId);
	}
}
