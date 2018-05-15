package com.am.utils;

import com.am.dao.AuApiRecordDao;
import com.jfinal.plugin.activerecord.Record;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/1.
 */
public class PubModelUtil {

	/**
	 * 接口记录实体构件
	 * @param jsonMap 解析json 之后的map
	 * @param apiCode 接口名称
	 * @param reqJson 请求JSON
	 * @param repJson 响应
	 */
	public static void apiRecordBean(Map jsonMap,String apiCode,String reqJson,String repJson){
		String operatorId = "";
		String account = "";
		String reqNo = "";
		String systemId = "";
		String ip = "";
		if(jsonMap.containsKey("operator_id")){
			operatorId = jsonMap.get("operator_id").toString();
		}
		if(jsonMap.containsKey("account_id")){
			account = jsonMap.get("account_id").toString();
		}
		if(jsonMap.containsKey("req_no")){
			reqNo = jsonMap.get("req_no").toString();
		}

		if(jsonMap.containsKey("system_id")){
			systemId = jsonMap.get("system_id").toString();
		}
		if (jsonMap.containsKey("ip")){
			ip = jsonMap.get("ip").toString();
		}
		Record apiRecord = new Record();
		apiRecord.set("AP_ID", DatabaseUtil.getEntityPrimaryKey("AU"));
		apiRecord.set("OP_OPRATORID",operatorId);
		apiRecord.set("OP_ACCOUNT",account);
		apiRecord.set("AP_REQNO",reqNo);
		apiRecord.set("AP_APICODE",apiCode);
		apiRecord.set("AP_REQUESTDATA",reqJson);
		apiRecord.set("AP_RESPONSEDATA",repJson);
		apiRecord.set("AP_SYSTEM",systemId);
		apiRecord.set("AP_IP",ip);
		AuApiRecordDao.dao.save(apiRecord);
	}

}
