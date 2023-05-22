package com.vo.admin;

import java.util.List;

import com.vo.client.balance.ZRPCCTX;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 
 * @author zhangzhen
 * @date 2021-12-24 0:21:12
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContextDTO {
	
	private String serviceName;

	private List<ContextDetailDTO> cdList; 
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ContextDetailDTO{
		private String methodName;
		private ZRPCCTX zrpcctx;
		
	}
}
