package com.am.service;

import com.am.dao.AuMenuDao;
import com.am.utils.DatabaseUtil;
import com.am.utils.EmptyUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by ZHAO on 2018/5/21.
 */
public class MenuService {
	public static final MenuService service = new MenuService();
	private Log log = Log.getLog(MenuService.class);

	/**
	 * 删除菜单
	 */
	public void deleteMenu(String menuId){
		Record menuRecord = AuMenuDao.dao.queryById(menuId);
		if(null != menuRecord){
			String ifleft = menuRecord.getStr("MU_IFLEAF");//null或者0是父级菜单还要删除他所有的子级菜单
			if(null == ifleft || ifleft.equals("0")){
				//1.删除父级菜单2.以及其子菜单
				AuMenuDao.dao.deleteById(menuId);
				AuMenuDao.dao.deleteMenuIdByPmenuId(menuId);
			}else{//子级菜单直接删除即可
				AuMenuDao.dao.deleteById(menuId);
			}
		}

	}
	/**
	 * 新增菜单--父级菜单
	 */
	public void InsertMenu(String menuName,String menuCode,String ifLeaf,String displayOrder,String parentId){
		Record record = new Record();
		record.set("MU_ID", DatabaseUtil.getEntityPrimaryKey("MU"));
		if(EmptyUtils.isNotEmpty(parentId)){//新增子菜单
			record.set("MU_PARENTID",parentId);
		}
		record.set("MU_NAME",menuName);
		record.set("MU_CODE",menuCode);
		record.set("MU_IFLEAF",ifLeaf);
		record.set("MU_DISPLAYORDER",displayOrder);
		AuMenuDao.dao.save(record);
	}
	/**
	 * 修改菜单--父级菜单
	 */
	public void UpdateMenu(String menuName,String menuCode,String ifLeaf,String displayOrder,String menuId){
		Record record = new Record();
		record.set("MU_ID", menuId);
		record.set("MU_NAME",menuName);
		record.set("MU_CODE",menuCode);
		record.set("MU_IFLEAF",ifLeaf);
		record.set("MU_DISPLAYORDER",displayOrder);
		AuMenuDao.dao.update(record);
	}
}
