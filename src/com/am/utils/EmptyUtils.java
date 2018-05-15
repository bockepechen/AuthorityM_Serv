package com.am.utils;

import java.util.Collection;
import java.util.Map;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: WANGXR
 * \* Date: 2017/11/2
 * \* Time: 10:19
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */

public class EmptyUtils {

	/**
	 * 判断一个对象是否为null或空 <br>
	 * 如果对象为null返回true <br>
	 * 如果对象为空String、空Array、空Map、空Collection返回true <br>
	 * 否则返回false
	 *
	 * @param o
	 * @return
	 */
	public static boolean isEmpty(Object o){
		if (o == null)
			return true;
		else if (o instanceof String){
			if(o.equals("null"))
				return true;
			else return ((String) o).length() == 0;
		}
		else if (o instanceof Collection)
			return ((Collection) o).size() == 0;
		else if (o instanceof Map)
			return ((Map) o).size() == 0;
		else if (o instanceof Object[])
			return ((Object[]) o).length == 0;
		else
			return false;
	}

	/**
	 * 判断对象是否非空
	 *
	 * @param obj 对象
	 * @return {@code true}: 非空<br>{@code false}: 空
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

}