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
		String sql = "SELECT am.MU_ID AS mu_id,am.MU_NAME AS mu_name, ad.DICTNAME AS if_leaf FROM AU_MENU AS am, AU_DICT_ENTRY AS ad WHERE DICTTYPEID = 'dict_if'AND DICTID = am.MU_IFLEAF";
		return Db.use(configName).find(sql);
	}

	/**
	 * 根据主键查询
	 * @param id
	 * @return
	 */
	public Record  queryById(String id){
		return Db.use(configName).findById(tableName,primaryKey,id);
	}

	/**
	 * 根据主键删除信息
	 * @param id
	 * @return
	 */
	public boolean deleteById(String id){
		return Db.use(configName).deleteById(tableName,primaryKey,id);
	}

	/**
	 * 删除父级菜单其子菜单
	 */
	public int deleteMenuIdByPmenuId(String menuId){
		String sql  = "DELETE FROM AU_MENU WHERE MU_PARENTID  = ?";
		return Db.use(configName).update(sql,menuId);
	}

	/**
	 * 查询菜单(父菜单为空即为一级菜单)
	 */
	public List<Record> queryMenu() {
		String sql = "SELECT MU_ID AS menu_id ,MU_NAME as menu_name, MU_ACTION AS menu_action,MU_PARENTID AS parent_id FROM AU_MENU";
		return Db.use(configName).find(sql);
	}

	/**
	 * 根据父级菜单查询菜单
	 */
	public Record queryMenuByParentId(String parentId) {
		String sql = "SELECT MU_ID AS menu_id ,MU_NAME as menu_name ,MU_PARENTID AS parent_id FROM AU_MENU WHERE MU_ID = ?";
		return Db.use(configName).findFirst(sql,parentId);
	}

}
