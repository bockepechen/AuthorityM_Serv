package com.am.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by ZHAO on 2018/5/14.
 */
public class AuApiRecordDao {
	public static final AuApiRecordDao dao = new AuApiRecordDao();
	private String configName = "sqlserver_auth";
	private String tableName = "AU_API_RECORD";
	private String primaryKey = "AP_ID";


	public boolean save(Record record) {
		return Db.use(configName).save(tableName,primaryKey,record);
	}
}
