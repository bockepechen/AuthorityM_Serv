package com.am.dao;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/9.
 */
public interface IBaseDao {
	/**
	 * 保存实体
	 * @param record
	 * @return
	 */
	public boolean save(Record record);

	/**
	 * 更新实体
	 * @param record
	 * @return
	 */
	public boolean update(Record record);

	/**
	 * 删除实体
	 * @param record
	 * @return
	 */
	public boolean delete(Record record);

	/**
	 * 查询所有信息
	 * @return
	 */
	public List<Record> findAll();
}
