package com.vo.client.balance;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import com.vo.common.ZRPCProtocol;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ctx对象，用于负载均衡策略
 *
 * @author zhangzhen
 * @date 2021-12-22 3:40:22
 *
 */
@Data
@NoArgsConstructor
public class ZRPCCTX {

	private ChannelHandlerContext ctx;

	private ZProcuderBalanceEnum balanceEnum;

	/**
	 * 调用平均耗时，默认为0
	 */
	private double avgInvokeMS = 0;

	/**
	 * 已调用次数
	 */
	private AtomicLong callCount = new AtomicLong();

	/**
	 * producer端口号
	 */
	private int port;

	public final void postHandle(final ZRPCProtocol zrpcProtocol) {
		this.balanceEnum.postHandle(zrpcProtocol, this);
	}


	/**
	 * 所有调用总耗时
	 */
	private long ms;
	/**
	 * 每次调用前的时间点
	 */
	private long start;
	/**
	 * 每次调用后，返回结果时的时间点
	 */
	private long end;
	/**
	 * 增加一次调用次数
	 */
	public void addCallCount() {
		if (Objects.isNull(this.getCallCount())) {
			this.setCallCount(new AtomicLong(1));
			return;
		}

		final AtomicLong cc = this.getCallCount();
		cc.incrementAndGet();
	}
}
