package com.vo.common;

import cn.hutool.core.lang.UUID;

/**
 *  
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 * 
 */
public class ZIDGenerator {

	public static String generateId() {
		// TODO	太长了 
		final String id = UUID.randomUUID().toString();
		return id.replace("-", "");
	}

}
