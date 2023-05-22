package com.vo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * 
 * @author zhangzhen
 * @date 2021-12-14 19:03:28
 * 
 */
@Getter
@AllArgsConstructor
public enum ZRPCExceptionEnum {

	PRODUCER_NOT_FOUND(10001,"PRODUCER_NOT_FOUND"),
	
	;
	
	private Integer code;
	private String message;
	
}
