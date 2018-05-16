package com.am.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.am.dao.AuEmpOrgDao;
import com.am.dao.AuEmployeeDao;
import com.am.dao.AuOrganizationDao;
import com.am.utils.EmptyUtils;
import com.am.utils.JsonUtil;
import com.am.utils.PubModelUtil;
import com.am.utils.ReturnCodeUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZHAO on 2018/5/15.
 */
public class OrgEmpManageController extends Controller{
	static Log log = Log.getLog(OrgEmpManageController.class);

	String reqNo = "";//请求单号
	String accountId = "";//登录账号
	String operatorId = "";//操作员编号
	String returnCode = "";//返回码
	String returnMessage = "";//返回信息

	JSONArray dictList = CacheKit.get("dataCache", "s_dict_returncode");
	JSONObject jb = new JSONObject();
	JSONArray jsonArray = new JSONArray();
	JSONObject jyau_oporgData = new JSONObject();
	JSONArray joo = new JSONArray();
	/**
	 * 1.查询机构用户列表2.查询无机构人员（查询员工表机构为空的）
	 */
	public void index(){
		//获取请求数据
		String json = HttpKit.readData(getRequest());
		/*String json = "{\n" +
				"\n" +
				"\t\"jyau_content\": {\n" +
				"\t\t\"jyau_reqData\": [{\n" +
				"\t\t\t\"req_no\": \" AU001201810231521335687\"\n" +
				"\t\t}],\n" +
				"\t\t\"jyau_pubData\": {\n" +
				"\t\t\t\"operator_id\": \"1\",\n" +
				"\t\t\t\"account_id\": \"systemman\",\n" +
				"\t\t\t\"ip_address\": \"10.2.0.116\",\n" +
				"\t\t\t\"system_id\": \"10909\"\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}";*/
		//解析Json
		Map map = new HashMap();
		try {
			map = JsonUtil.analyzejson(json);
			reqNo = map.get("req_no").toString();
			accountId = map.get("account_id").toString();
			operatorId = map.get("operator_id").toString();
			if(EmptyUtils.isEmpty(reqNo) || EmptyUtils.isEmpty(accountId) || EmptyUtils.isEmpty(operatorId)){
				returnCode = ReturnCodeUtil.returnCode3;
			}else {
				//查询机构
				List<Record> orgList = AuOrganizationDao.dao.findAll();
				//机构下用户
				for(Record org : orgList){
					String orgId = org.getStr("org_id");
					String orgName = org.getStr("org_name");
					List<Record>  empOrgList= AuEmpOrgDao.dao.findUserInOrg(orgId);
					JSONObject jo = new JSONObject();
					jo.put("org_id",orgId);
					jo.put("org_name",orgName);
					jo.put("emp_list",empOrgList);
					joo.add(jo);
				}
				//查询无机构员工
				List<Record> noOrgEmpList = AuEmployeeDao.dao.findEmpNoOrg();
				JSONObject jo = new JSONObject();
				jo.put("org_name","无机构的人员");
				jo.put("emp_list",noOrgEmpList);
				joo.add(jo);
				returnCode = ReturnCodeUtil.returnCode;
			}
			returnJson();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			returnCode = "9999";
			returnJson();
		} finally {
			PubModelUtil.apiRecordBean(map,"AU010",json,jb.toString());
		}
	}

	public void returnJson() {
		returnMessage = JsonUtil.getDictName(dictList, returnCode);
		jyau_oporgData.put("req_no", reqNo);
		jyau_oporgData.put("operator_id", operatorId);
		jyau_oporgData.put("orgemp_list", joo);
		jsonArray.add(jyau_oporgData);
		jb = JsonUtil.returnJson(jsonArray, returnCode, returnMessage);
		renderJson(jb);
	}
}
