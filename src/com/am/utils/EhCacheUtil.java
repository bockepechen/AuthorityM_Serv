package com.am.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.List;

/**
 * Created by ztk on 2018/1/10.
 */
public class EhCacheUtil {

    /**
     * 加载缓存数据
     */
    public static void loadCache() {
        // 缓存加载动态SQL数据
        loadDynamicData();
    }
    /**
     *	缓存加载动态SQL数据
     */
    private static void loadDynamicData() {
        // 加载Json数据文件，转换为Properties
        Prop p = PropKit.use("dynamicData.txt");
        // 根据数据文件中的Key名，进行遍历
        for (String keyName : p.getProperties().stringPropertyNames()) {
            // 取得SQL语句
            String dynamicSQL = p.get(keyName);
            if (dynamicSQL != null && !"".equals(dynamicSQL)) {
                // 根据SQL查询字典数据
                List<Record> dynamicData = Db.find(dynamicSQL);
                // 将查询结果<Record>转换为JSONArray
                JSONArray final_jarray = recordToJson(dynamicData, "SIMPLE");
                JSONArray final_jarray_kv = recordToJson(dynamicData, "KV");
                // 将字典结果存入缓存中对应DICTTYPEID的键值
                CacheKit.put("dataCache", keyName, final_jarray);
                CacheKit.put("dataCache", keyName+"_KV", final_jarray_kv);
            }
        }
        // 从缓存中清除数据文件
        PropKit.useless("dynamicData.txt");
    }

    /**
     * 将List(Record)数据转换成JSONArray类型
     * @param records, KVFormat(JSONArray 转换格式)
     * @return JSONArray
     */
    private static JSONArray recordToJson(List<Record> records, String KVFormat) {
        // 将查List<Record>转换为JSONArray
        String records_Str = JSON.toJSONString(records);
        JSONArray ori_jarray = JSONArray.parseArray(records_Str);	// 原始数据JSONArray
        JSONArray final_jarray = new JSONArray();	// 结果数据JSONArray
        // 使用【原始数据JSONArray】构造新的【结果数据JSONArray】
        for (Object loopObj : ori_jarray) {
            JSONObject ori_jsObj = (JSONObject)((JSONObject)loopObj).get("columns");
            JSONObject final_jsObj = new JSONObject();
            if (KVFormat == null || KVFormat == "" || "SIMPLE".equals(KVFormat)) {
                // 格式：[{id:name}, ...]
                final_jsObj.put((String)ori_jsObj.get("DICTID"), ori_jsObj.get("DICTNAME"));
            } else if ("KV".equals(KVFormat)) {
                // 格式：[{'key':val, 'value':val}, {'key':val, 'value':val}, ...]
                final_jsObj.put("key", ori_jsObj.get("DICTID"));
                final_jsObj.put("value", ori_jsObj.get("DICTNAME"));
            }
            final_jarray.add(final_jsObj);
        }
        return final_jarray;
    }

}
