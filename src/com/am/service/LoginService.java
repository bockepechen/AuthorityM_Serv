package com.am.service;

import com.am.dao.AuOperatorDao;
import com.am.utils.DatabaseUtil;
import com.am.utils.ReturnCodeUtil;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZHAO on 2018/5/15.
 */
public class LoginService {
	public static final LoginService service = new LoginService();
	private Log log = Log.getLog(LoginService.class);

	/**
	 * 用户登录业务逻辑
	 * @param accountId
	 * @param pwd
	 * @param ip
	 * @return
	 */
	public  Map loginBiz(String accountId,String pwd,String ip){
		Map map = new HashMap();
		Record auopRecord = AuOperatorDao.dao.queryByaccountIdPwd(accountId, pwd);
		if (null != auopRecord) {//登录成功
			String operatorId = auopRecord.getStr("OP_OPRATORID");
			Date lastLogin = auopRecord.getDate("OP_LOGINTIME");
			String lastip = auopRecord.getStr("OP_LOGINIP");
			List<Record> orgList = AuOperatorDao.dao.queryOrgByOperatorId(operatorId);
			//更新表登录时间以及IP
			updateOperator(operatorId, ip);
			String returnCode = ReturnCodeUtil.returnCode;
			map.put("operatorId",operatorId);
			map.put("lastLogin",lastLogin);
			map.put("lastIp",lastip);
			map.put("orgList",orgList);
			map.put("returnCode",returnCode);
		}else {
			String returnCode = ReturnCodeUtil.returnCode1;
			map.put("returnCode",returnCode);
		}
		return map;
	}

	/**
	 * 更新登录时间及IP
	 */
	public void updateOperator(String operatorId,String ip){
		Record operaRecord = new Record();
		operaRecord.set("OP_OPRATORID",operatorId);
		operaRecord.set("OP_LOGINTIME", DatabaseUtil.getSqlDatetime());
		operaRecord.set("OP_LOGINIP",ip);
		AuOperatorDao.dao.update(operaRecord);
	}

}
