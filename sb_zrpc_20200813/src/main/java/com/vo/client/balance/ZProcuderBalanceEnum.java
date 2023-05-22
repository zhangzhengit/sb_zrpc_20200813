package com.vo.client.balance;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.vo.common.ZRPCProtocol;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对于服务提供者的负载均衡策略
 *
 * @author zhangzhen
 * @date 2021-12-14 21:33:16
 *
 */
@Getter
@AllArgsConstructor
public enum ZProcuderBalanceEnum {

	ROUND("轮询") {

		@Override
		ZRPCCTX preHandle(final ZRPCProtocol zrpcProtocol, final List<ZRPCCTX> ctxList) {
			final int ctxSize = ctxList.size();
			final Object index = ZProcuderBalanceEnum.map.get(ZProcuderBalanceEnum.ROUND);
			if (Objects.isNull(index)) {
				return ctxList.get(0);
			}

			final int ci = (int) index;

			if (ci + 1 >= ctxSize) {
				ZProcuderBalanceEnum.map.put(ZProcuderBalanceEnum.ROUND, 0);
				return ctxList.get(0);
			}

			ZProcuderBalanceEnum.map.put(ZProcuderBalanceEnum.ROUND, ci + 1);
			return ctxList.get(ci + 1);
		}

		@Override
		ZRPCCTX postHandle(final ZRPCProtocol zrpcProtocol, final ZRPCCTX ctx) {
			ctx.addCallCount();
			return ctx;
		}
	},

	WEIGHTED("权重") {

		@Override
		ZRPCCTX preHandle(final ZRPCProtocol zrpcProtocol, final List<ZRPCCTX> ctxList) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		ZRPCCTX postHandle(final ZRPCProtocol zrpcProtocol, final ZRPCCTX ctx) {
			ctx.addCallCount();
			return ctx;
		}
	},

	HASH("HASH") {

		@Override
		ZRPCCTX preHandle(final ZRPCProtocol zrpcProtocol, final List<ZRPCCTX> ctxList) {
			final String remoteMethodName = zrpcProtocol.getName();
			final int hashCode = remoteMethodName.hashCode();
			final int ctxSize = ctxList.size();
			final int remainder = hashCode % ctxSize;
			return ctxList.get(remainder);
		}

		@Override
		ZRPCCTX postHandle(final ZRPCProtocol zrpcProtocol, final ZRPCCTX ctx) {
			ctx.addCallCount();
			return ctx;
		}
	},

	CONSISTENCY_HASH("一致性HASH") {
		@Override
		ZRPCCTX preHandle(final ZRPCProtocol zrpcProtocol, final List<ZRPCCTX> ctxList) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		ZRPCCTX postHandle(final ZRPCProtocol zrpcProtocol, final ZRPCCTX ctx) {
			ctx.addCallCount();
			return ctx;
		}

	},

	RANDOM("随机") {
		@Override
		ZRPCCTX preHandle(final ZRPCProtocol zrpcProtocol, final List<ZRPCCTX> ctxList) {
			final int nextIndex = ZProcuderBalanceEnum.SECURE_RANDOM.nextInt(ctxList.size());
			final ZRPCCTX ctx = ctxList.get(nextIndex);
			return ctx;
		}

		@Override
		ZRPCCTX postHandle(final ZRPCProtocol zrpcProtocol, final ZRPCCTX ctx) {
			ctx.addCallCount();
			return ctx;
		}
	},

	LEAST_CONNECTION("最小连接数") {
		@Override
		ZRPCCTX preHandle(final ZRPCProtocol zrpcProtocol, final List<ZRPCCTX> ctxList) {
			ZRPCCTX minCallCTX = ctxList.get(0);
			for (int i = 1; i < ctxList.size(); i++) {

				final ZRPCCTX c2 = ctxList.get(i);
				if (c2.getCallCount().get() < minCallCTX.getCallCount().get()) {
					minCallCTX = c2;
				}
			}

			return minCallCTX;
		}

		@Override
		ZRPCCTX postHandle(final ZRPCProtocol zrpcProtocol, final ZRPCCTX ctx) {
			ctx.addCallCount();
			return ctx;
		}
	},

	LEAST_RESPONSE_TIME("最短响应时间") {
		@Override
		ZRPCCTX preHandle(final ZRPCProtocol zrpcProtocol, final List<ZRPCCTX> ctxList) {
			// TODO 做这个
			return ctxList.get(0);
		}


		@Override
		ZRPCCTX postHandle(final ZRPCProtocol zrpcProtocol, final ZRPCCTX zrpcctx) {
			return zrpcctx;
		}
	};

	public ZRPCCTX handle(final ZRPCProtocol zrpcProtocol, final List<ZRPCCTX> zctxList) {
		if (CollUtil.isEmpty(zctxList)) {
			return null;
		}

		final ZRPCCTX ctx1 = this.preHandle(zrpcProtocol, zctxList);

		return ctx1;
	}

	abstract ZRPCCTX preHandle(ZRPCProtocol zrpcProtocol, List<ZRPCCTX> ctxList);

	abstract ZRPCCTX postHandle(ZRPCProtocol zrpcProtocol, ZRPCCTX ctx);

	/**
	 * 关于此方式的描述
	 */
	private String desc;

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final ConcurrentMap<ZProcuderBalanceEnum, Object> map = Maps.newConcurrentMap();

}
