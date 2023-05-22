package com.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 
 * @author zhangzhen
 * @date 2021-12-14 19:02:44
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZRPCException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private Integer code;
	private String message;

}
