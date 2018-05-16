package com.am.utils;

import com.jfinal.log.Log;

/**
 * Created by ZHAO on 2018/5/14.
 */
public class  ReturnCodeUtil {
	public static String returnCode = "0000";//成功
	public static String returnCode1 = "0001";//登录失败
	public static String returnCode2 = "9999";//请求异常
	public static String returnCode3 = "9998";//数据格式不正确
	public static String returnCode4 = "0002";//当前系统无该用户
	public static String returnCode5 = "0003"; // 机构名称已经存在
	public static String returnCode6 = "0004"; // 删除失败
	static Log log = Log.getLog(ReturnCodeUtil.class);




}
