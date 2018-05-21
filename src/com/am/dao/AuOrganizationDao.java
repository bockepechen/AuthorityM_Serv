package com.am.dao;

import com.am.utils.EmptyUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/9.
 */
public class AuOrganizationDao implements IBaseDao {
	public static final AuOrganizationDao dao = new AuOrganizationDao();
	private String configName = "sqlserver_auth";
	private String tableName = "AU_ORGANIZATION";
	private String primaryKey = "ORG_ID";

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
		String sql = "SELECT o.ORG_ID AS org_id,o.ORG_CODE AS org_code,o.ORG_NAME AS org_name FROM AU_ORGANIZATION o WHERE o.ORG_STATUS = '01' ";
		return Db.use(configName).find(sql);
	}

	/**
	 * 判断机构名称是否存在
	 * @param orgName 机构名称
	 * @param orgId 机构Id
	 * @return
	 */
	public int findByName(String orgName,String orgId){
		String sql = "";
		if(EmptyUtils.isEmpty(orgId)){
			sql = "SELECT count(*) as cnt FROM AU_ORGANIZATION WHERE ORG_NAME = ? ";
			return Db.use(configName).findFirst(sql,orgName).get("cnt");
		}else {
			sql = "SELECT count(*) as cnt FROM AU_ORGANIZATION WHERE ORG_NAME = ? AND ORG_ID != ? ";
			return Db.use(configName).findFirst(sql,orgName,orgId).get("cnt");
		}
	}

	/**
	 * 判断机构编码是否存在
	 * @param orgCode 机构编码
	 * @param orgId 机构Id
	 * @return
	 */
	public int findByCode(String orgCode,String orgId){
		String sql = "";
		if(EmptyUtils.isEmpty(orgId)){
			sql = "SELECT count(*) as cnt FROM AU_ORGANIZATION WHERE ORG_CODE = ? ";
			return Db.use(configName).findFirst(sql,orgCode).get("cnt");
		}else {
			sql = "SELECT count(*) as cnt FROM AU_ORGANIZATION WHERE ORG_CODE = ? AND ORG_ID != ? ";
			return Db.use(configName).findFirst(sql,orgCode,orgId).get("cnt");
		}
	}
}
