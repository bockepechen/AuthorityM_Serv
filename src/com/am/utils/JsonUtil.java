package com.am.utils;

import com.jfinal.log.Log;
import net.sf.json.JSONObject;
import org.json.JSONArray;

import java.util.*;

/**
 * Created by Administrator on 2017/10/24.
 */
public class JsonUtil {
	static Log log = Log.getLog(JsonUtil.class);

	/**
	 * json 解析成map(一层解析)
	 * @param json
	 * @return map
	 * @throws Exception
	 */
	public static Map<String, String> splitJson(String json) throws Exception {

		Map<String, String> mapJson = new Hashtable<String, String>();

		String strJson = json.trim().substring(1, json.length() - 1).trim();
		strJson = strJson.replace("\",\"", "\"#\"");
		strJson = strJson.replace("\":\"", "\"=\"");
		String[] arrayStr = strJson.split("#");

		for (String str : arrayStr) {
			String[] arrayValue = str.replace("\"", "").split("=");
			if (arrayValue.length > 1) {
				System.out.println(arrayValue[0].trim()+":"+arrayValue[1].trim());
				mapJson.put(arrayValue[0].trim(), arrayValue[1].trim());
			} else {
				mapJson.put(arrayValue[0], "");
			}
		}
		return mapJson;
	}

	/**
	 * 解析规定格式的三层的json 放在map中
	 * @param json
	 * @return map
	 * @throws Exception
	 */
	public static Map<String, Object> analyzejson(String json)throws Exception{

		JSONObject object = JSONObject.fromObject(json);
		Map<String, Object> map = new Hashtable<String, Object>();
		for (Object k : object.keySet()) {
			Object v = object.get(k);
			map.put(k.toString(), v);
		}
		Map<String, Object> map2 = new Hashtable<String, Object>();
		JSONObject AddDataJson = JSONObject.fromObject(map.get("jyau_content").toString());
		for (Object k : AddDataJson.keySet()) {
			Object v = AddDataJson.get(k);
			if(k.toString() == "jyau_sign" || k.toString().equals("jyau_sign")){
				map.put(k.toString(), v);
			}
			map2.put(k.toString(), v);
		}

		//第二层解析 第二层可能是 也可能不是
		for(Map.Entry<String, Object> entry:map2.entrySet()){
			try {
				JSONArray array = new JSONArray(entry.getValue().toString());  //判断是否是json数组
				//是json数组
				for (int i = 0; i < array.length(); i++) {
					org.json.JSONObject object2 = array.getJSONObject(i);//json数组对象
					JSONObject object3 = JSONObject.fromObject(object2.toString());  //json对象
					for (Object k : object3.keySet()) {
						Object v = object3.get(k);
						map.put(k.toString(), v);
					}
				}
				//不是json 串数组

			} catch (Exception e) {  //不是json串数组
				map.put(entry.getKey(), entry.getValue());
			}
		}

		JSONObject pubDataJson = JSONObject.fromObject(map.get("jyau_pubData").toString());
		map.put("system_id", pubDataJson.getString("system_id"));
		map.put("ip_address", pubDataJson.getString("ip_address"));
		map.put("account_id", pubDataJson.getString("account_id"));
		if(pubDataJson.containsKey("operator_id")){
			map.put("operator_id", pubDataJson.getString("operator_id"));
		}

		return map;
	}

	/**
	 * 拼装JSON
	 * @param resData
	 * @param returnCode
	 * @param returnMsg
	 * @return
	 */
	public static com.alibaba.fastjson.JSONObject returnJson(com.alibaba.fastjson.JSONArray resData,
													  String returnCode,String returnMsg){
		//存放【jyau_resData】，【jyau_resHead】
		com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
		//存放【jyau_content】
		com.alibaba.fastjson.JSONObject jyau_content = new com.alibaba.fastjson.JSONObject();
		com.alibaba.fastjson.JSONObject jyau_resHead = new com.alibaba.fastjson.JSONObject();
		//组装jyau_resHead
		jyau_resHead.put("return_code",returnCode);
		jyau_resHead.put("return_msg",returnMsg);
		jsonObject.put("jyau_resData", resData);
		jsonObject.put("jyau_resHead", jyau_resHead);
		jyau_content.put("jyau_content", jsonObject);
		return jyau_content;

	}


	/**
	 * 获取字典名称
	 * @param dictList 字典列表
	 * @param dictId 字典ID
	 * @return 字典名称
	 */
	public static String getDictName(com.alibaba.fastjson.JSONArray dictList, String dictId){
		String dictName="";
		for(int i = 0; i < dictList.size(); i++){
			JSONObject jo = JSONObject.fromObject(dictList.get(i));
 			if(jo.get(dictId)!=null){
				dictName=jo.get(dictId).toString();
				break;
			}
		}
		return dictName;
	}

	public static Map<String, Object> parseJSON2Map(String jsonStr) {
		Map map = new HashMap();
		// 最外层解析
		com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(jsonStr);
		for (Object k : json.keySet()) {
			Object v = json.get(k);
			// 如果内层还是数组的话，继续解析
			if (v instanceof net.sf.json.JSONArray) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Iterator<com.alibaba.fastjson.JSONObject> it = ((net.sf.json.JSONArray) v).iterator();
				while (it.hasNext()) {
					com.alibaba.fastjson.JSONObject json2 = it.next();
					list.add(parseJSON2Map(json2.toString()));
				}
				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), v);
			}
		}
		return map;
	}




	/**
	 * 测试方法
	 * @param args
	 */
	public static void main(final String[] args){
		String jsonStr = "{\"appKey\":\"17220171023034046311\",\"phone\":\"13622051880\",\"resultCode\":\"0000\",\"resultMessage\":\"成功\",\"userId\":\"172\"}";
		String jsonStr1 = "{\"acqRes\":\"\", \"bankCode\":\"30050000\", \"batchNo\":\"120007\", \"channel\":\"000002\", \"instCode\":\"01380001\", \"notifyURL\":\"http://wx2.jiayezhide.com/All-backend-service/jyResponse/batchLendPayData\", \"retNotifyURL\":\"http://wx2.jiayezhide.com/All-backend-service/jyResponse/batchLendPayBusiness\", \"seqNo\":\"100496\", \"sign\":\"YSGcuGK9Nf5z33HH4f5pmRyWirz/i9FyfggCBZI4GaT44rPqX/veES0OpkQEQ56cPwnQ/zhsgRvcqkvuXWrgPHjdJJl675nJY+WiDiHZfm+YppFIvKyXNIXYx10p1PC7+mMnAcbP+i/uAesRakOJjXZ3rVDQLI0g/0Wt8xeRGhtk1oVg7GjeCAi0PX7H9v4vlg3jKn1si3p8lbrx2/EGgZOKJZh60CQJtUuDtC9v7dRt59H5LgMXrl/f8wQAXfTQmPDx5y82WT7bKay85yoQgen0fx8Xad2Vpl0S9TzjP0EUKHuVQ4nMnH3+7jVx1xoNqFeaGmX5rE2I5blz2TK52A==\", \"subPacks\":\"[{\\\"accountId\\\":\\\"6212462420000350039\\\", \\\"authCode\\\":\\\"20161020143420670956\\\", \\\"bidFee\\\":\\\"0\\\", \\\"debtFee\\\":\\\"0\\\", \\\"forAccountId\\\":\\\"6212462420000200010\\\", \\\"orderId\\\":\\\"123450987\\\", \\\"productId\\\":\\\"112233\\\", \\\"txAmount\\\":\\\"10\\\"}, {\\\"accountId\\\":\\\"6212462420000050027\\\", \\\"authCode\\\":\\\"20161020145823671075\\\", \\\"bidFee\\\":\\\"0\\\", \\\"debtFee\\\":\\\"0\\\", \\\"forAccountId\\\":\\\"6212462420000200010\\\", \\\"orderId\\\":\\\"1004560\\\", \\\"productId\\\":\\\"112233\\\", \\\"txAmount\\\":\\\"1000\\\"}]\", \"txAmount\":\"1010\", \"txCode\":\"batchLendPay\", \"txCounts\":\"2\", \"txDate\":\"20170911\", \"txTime\":\"151713\", \"version\":\"10\"}";
		String jsonStr2 = "{" +
				"\"jycl_content\":{" +
				"\"jycl_reqData\":[{" +
				"\"req_no\":\"ZC001201710231521335687\"," +
				"\"tel_phone\":\"13752133755\"," +
				"\"img_code\":\"B240\"," +
				"\"pwd\":\"B240009\"," +
				"\"verification_code\":\"234560\"," +
				"\"referral_code\":\"13752133899\"" +
				"}]," +
				"\"jycl_pubData\":{" +
				"\"jcb_id\":\"\"," +
				"\"phone_num\":\"13820514879\"," +
				"\"system_id\":\"11123\"," +
				"\"network_type\":\"网络无线\"," +
				"\"ip\":\"10.2.0.103\"," +
				"\"app_key\":\"eeeew3232323\"" +
				"}," +"\"sign\":\"11123\""+
				"}" +
				"}";
		JsonUtil utis = new JsonUtil();
		try{
//			utis.splitJson(jsonStr2);
			utis.analyzejson(jsonStr2);
		}catch (Exception e){
			e.printStackTrace();
		}


	}


}
