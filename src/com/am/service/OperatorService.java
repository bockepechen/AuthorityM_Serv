package com.am.service;

import com.am.dao.AuEmployeeDao;
import com.am.dao.AuOperatorDao;
import com.am.utils.DatabaseUtil;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;


/**
 * Created by ZHAO on 2018/5/15.
 */
public class OperatorService {
	public static final OperatorService service = new OperatorService();
	private Log log = Log.getLog(OperatorService.class);

	/**
	 * 修改用户信息业务逻辑
	 * @param account
	 * @param name
	 * @param pwd
	 * @param status
	 * @param ifAdmin
	 */
	public String operatorInterestBiz(String account,String  name,String pwd,String status,String ifAdmin){
		//1.新增用户表2.新增员工表
		String operatorId = DatabaseUtil.getEntityPrimaryKey("OP");
		interestOperator(operatorId,account,name,pwd,status,ifAdmin);
		interestEmployee(operatorId,ifAdmin,name,status);
		return operatorId;

	}

	/**
	 * 修改用户信息业务逻辑
	 * @param operatorId
	 * @param account
	 * @param name
	 * @param pwd
	 * @param status
	 * @param ifAdmin
	 */
	public void operatorModifyBiz(String operatorId,String account,String  name,String pwd,String status,String ifAdmin){
		//1.更新用户表2.更新员工表
		updateOperator(operatorId,account,name,pwd,status,ifAdmin);
		updateEmployee(operatorId,name,status,ifAdmin);


	}

	/**
	 * 删除用户信息业务逻辑
	 * @param operatorId
	 */
	public void operatorDeleteBiz(String operatorId){
		//1.删除用户信息表，2.删除员工表
		AuOperatorDao.dao.deleteById(operatorId);
		Record empRecord = AuEmployeeDao.dao.findByOperatorId(operatorId);
		AuEmployeeDao.dao.delete(empRecord);
	}

	public void  updateOperator(String operatorId,String account,String name,String pwd,String status,String ifAdmin){
		Record operaRecord = new Record();
		operaRecord.set("OP_OPRATORID",operatorId);
		operaRecord.set("OP_ACCOUNT",account);
		operaRecord.set("OP_NAME",name);
		operaRecord.set("OP_PWD",pwd);
		operaRecord.set("OP_STATUS",status);
		operaRecord.set("OP_IFOPERATOR",ifAdmin);
		AuOperatorDao.dao.update(operaRecord);

	}

	public void  updateEmployee(String operatorId,String name,String status,String ifAdmin){
		Record empRecord = AuEmployeeDao.dao.findByOperatorId(operatorId);
		empRecord.set("EMP_NAME",name);
		empRecord.set("EMP_STATUS",status);
		empRecord.set("EMP_IFOPERATOR",ifAdmin);
		AuEmployeeDao.dao.update(empRecord);

	}

	public void  interestOperator(String operatorId,String account,String name,String pwd,String status,String ifAdmin){
		Record operaRecord = new Record();
		operaRecord.set("OP_OPRATORID",operatorId);
		operaRecord.set("OP_ACCOUNT",account);
		operaRecord.set("OP_NAME",name);
		operaRecord.set("OP_PWD",pwd);
		operaRecord.set("OP_STATUS",status);
		operaRecord.set("OP_IFOPERATOR",ifAdmin);
		AuOperatorDao.dao.save(operaRecord);
	}

	public void  interestEmployee(String operatorId,String ifAdmin,String name,String status){
		Record empRecord = new Record();
		String empId = DatabaseUtil.getEntityPrimaryKey("EM");
		empRecord.set("EMP_ID",empId);
		empRecord.set("OP_OPRATORID",operatorId);
		empRecord.set("EMP_NAME",name);
		empRecord.set("EMP_STATUS",status);
		empRecord.set("EMP_IFOPERATOR",ifAdmin);
		AuEmployeeDao.dao.save(empRecord);

	}

}
