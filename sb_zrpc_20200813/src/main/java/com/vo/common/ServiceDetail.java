package com.vo.common;

import java.util.List;

import com.google.common.collect.Lists;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *	用于ZContextService
 *
 * @author zhangzhen
 * @date 2022年1月5日
 *
 */
@Data
@AllArgsConstructor
public class ServiceDetail {

	private String serviceName;

	private List<ServiceMethodDetail> methodList;

	// FIXME 2022年1月5日 上午12:39:00 zhanghen: 加一个子弹ctxlist
	private List<ChannelHandlerContext> ctxList;

	public ServiceDetail() {
		this.methodList = Lists.newArrayList();
		this.ctxList = Lists.newArrayList();
	}

	@Data
	@AllArgsConstructor
	public static class ServiceMethodDetail{

		private String name;

		private String ip;

		private Integer port;

		private Long invokeCount;

		private Long failedCount;

		private Long successedCount;

		public ServiceMethodDetail() {
			this.invokeCount = 1L;
			this.failedCount = 1L;
			this.successedCount = 1L;
		}
	}

}
