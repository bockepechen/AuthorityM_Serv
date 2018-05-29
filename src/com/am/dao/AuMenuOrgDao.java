package com.am.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/24.
 */
public class AuMenuOrgDao implements IBaseDao{
	public static final AuMenuOrgDao dao = new AuMenuOrgDao();
	private String configName = "sqlserver_auth";
	private String tableName = "AU_MENUORG";
	private String primaryKey = "MO_ID";

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
	 * 查询菜单对应的角色
	 * @param menuId
	 * @return
	 */
	public List<Record> findRoleByMenu(String menuId){
		String sql = "SELECT DISTINCT mo.RL_ID AS role_id,ar.RL_NAME AS role_name FROM AU_MENUORG mo LEFT JOIN AU_ROLE ar ON ar.RL_ID = mo.RL_ID WHERE MU_ID = ?";
		return Db.use(configName).find(sql,menuId);
	}

	/**
	 * 查询角色对应的机构
	 * @param roleId
	 * @return
	 */
	public List<Record> findOrgByRole(String roleId,String menuId){
		String sql = "SELECT DISTINCT mo.ORG_ID AS org_id,ao.ORG_NAME AS org_name FROM AU_MENUORG mo LEFT JOIN AU_ORGANIZATION ao ON ao.ORG_ID = mo.ORG_ID WHERE RL_ID = ? AND MU_ID = ?";
		return Db.use(configName).find(sql,roleId,menuId);
	}
	/**
	 * 查询菜单角色机构表
	 * @param roleId
	 * @return
	 */
	public Record findMenuOrg(String menuId,String roleId,String orgId){
		String sql = "SELECT  * FROM AU_MENUORG WHERE  MU_ID = ? AND RL_ID = ?  AND ORG_ID = ?";
		return Db.use(configName).findFirst(sql,menuId,roleId,orgId);
	}
	/**
	 * 查询可操作的菜单
	 * @param operatorId
	 * @param orgId
	 * @return
	 */
	public List<Record> authMenu(String operatorId,String orgId){
		String sql = "SELECT  am.MU_NAME,am.MU_ACTION  FROM AU_MENUORG  amo LEFT JOIN AU_MENU am ON  am.MU_ID = amo.MU_ID WHERE amo.ORG_ID = ? AND amo.RL_ID IN(SELECT RL_ID FROM AU_EMPORG WHERE OP_OPRATORID  = ? AND ORG_ID = ? AND RL_ID IS NOT NULL )";
		return Db.use(configName).find(sql,orgId,operatorId,orgId);
	}

	/**
	 * 根据菜单ID删除菜单-角色-机构删除关系
	 * @param menuId 菜单ID
	 * @return
	 */
	public int deleteByMenuId(String menuId) {
		String sql = "DELETE  FROM AU_MENUORG WHERE MU_ID = ?";
		return Db.use(configName).update(sql,menuId);
	}


	/**
	 * 根据机构编号删除
	 * @param orgId
	 * @return
	 */
	public int deleteByOrg(String orgId){
		String sql = "DELETE  FROM AU_MENUORG WHERE ORG_ID = ?";
		return Db.use(configName).update(sql,orgId);
	}
}
