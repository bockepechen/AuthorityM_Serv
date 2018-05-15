package com.am.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/10/24.
 */
public class DatabaseUtil {
	/**
	 * 主键生成
	 * @param initialLetter 开头字母
	 * @return
	 */
	public static String getEntityPrimaryKey(String initialLetter){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String tmptime = sdf.format(date);
		java.util.Random random = new java.util.Random();
		int result1 = random.nextInt(10);
		int result2 = random.nextInt(10);
		int result3 = random.nextInt(10);
		int result4 = random.nextInt(10);
		String primaryKeys = "";
		primaryKeys = initialLetter + tmptime + result1 + result2 + result3 + result4;
		return primaryKeys;
	}

	/**
	 * 获得日期指定格式
	 * @param dateTime 日期
	 * @param formate 时间格式
	 * @return
	 */
	public static String getDateStr(Date dateTime,String formate){
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		String tmptime = sdf.format(dateTime);
		return tmptime;
	}

	/**
	 * 将指定的字符串转换成时间格式
	 * @param dateStr
	 * @return
	 */
	public static java.sql.Date getSqlDate(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
		java.sql.Date sqlDate = null;
		try {
			Date date = sdf.parse(dateStr);
			sqlDate = new java.sql.Date(date.getTime());
		}catch (Exception e){
			e.printStackTrace();
		}
		return sqlDate;
	}

	/**
	 * 获取当前系统的java.sql.datetime
	 * @return datetime
	 */
	public static Timestamp getSqlDatetime() {
		Date date = new Date();
		Timestamp datetime = new Timestamp(date.getTime());
		return datetime;
	}

}
