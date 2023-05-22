package com.vo.common;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vo.admin.ContextDTO;
import com.vo.admin.ContextDTO.ContextDetailDTO;
import com.vo.client.balance.ZProcuderBalanceEnum;
import com.vo.client.balance.ZRPCCTX;
import com.vo.conf.BalanceStrategyComponent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 *
 */
@Component
public class ZRPCContext {

	private static final String D = "@";

	@Autowired
	private ZContextService contextService;
	@Autowired
	private BalanceStrategyComponent balanceStrategyComponent;

	private final ConcurrentMap<String, List<ZRPCCTX>> cm = Maps.newConcurrentMap();

	public ContextDTO getAllCTXByMethodNameList1(final String serviceName, final String methodName) {
		if (StrUtil.isEmpty(methodName)) {
			return null;
		}

		final List<String> methodNameList = this.getAllMethodByServiceName(serviceName);
		if (CollUtil.isEmpty(methodNameList)) {
			return null;
		}

		if(!methodNameList.contains(methodName)) {
			return null;
		}

		final Set<Entry<String, List<ZRPCCTX>>> es = this.cm.entrySet();
		final List<ZRPCCTX> zctxList = es.stream()
				.filter(ee -> Objects.equals(ee.getKey(), methodName))
				.flatMap(ee -> ee.getValue().stream())
				.distinct()
				.collect(Collectors.toList());

		if (CollUtil.isEmpty(zctxList)) {
			return null;
		}

		final ContextDTO contextDTO = new ContextDTO();
		contextDTO.setServiceName(serviceName);

		final List<ContextDetailDTO> cdList = zctxList.stream()
				.map(zctx -> {
					final ContextDetailDTO detailDTO = new ContextDetailDTO();
					detailDTO.setMethodName(methodName);
					detailDTO.setZrpcctx(zctx);
					return detailDTO;
				}).distinct()
				.collect(Collectors.toList());
		contextDTO.setCdList(cdList);

		return contextDTO;
	}

	public List<Object> getAllCTXByMethodNameList(final List<String> methodNameList) {
		if(CollUtil.isEmpty(methodNameList)) {
			return Collections.emptyList();
		}
		if (CollUtil.isEmpty(this.cm)) {
			return Collections.emptyList();
		}

		final Set<Entry<String, List<ZRPCCTX>>> es = this.cm.entrySet();
		es.stream()
		  .filter(ee -> methodNameList.contains(ee.getKey()))
		;

		return null;
	}

	/**
	 * 根据服务名称获取所有的方法
	 *
	 * @param serviceName
	 * @return
	 */
	public List<String> getAllMethodByServiceName(final String serviceName) {
		if (CollUtil.isEmpty(this.cm)) {
			return Collections.emptyList();
		}

		final Set<String> k = this.cm.keySet();
		final List<String> methodNameList = k.stream()
			.filter(s -> s.startsWith(serviceName))
			.collect(Collectors.toList());

		return methodNameList;
	}

	/**
	 * 获取在线的服务名称
	 *
	 * @return
	 */
	public List<String> getAllServiceName() {
		if (CollUtil.isEmpty(this.cm)) {
			return Collections.emptyList();
		}

		final Set<String> k = this.cm.keySet();
		final List<String> serviceNameList = k.stream()
			.map(s -> {
				final int i = s.indexOf(ZRPCContext.D);
				if(i > -1) {
					return s.substring(0,i);
				}
				return "";
			}).distinct()
			.collect(Collectors.toList())
		;

		return serviceNameList;
	}

	public List<Entry<String, List<ZRPCCTX>>> all() {
		final Set<Entry<String, List<ZRPCCTX>>> es = this.cm.entrySet();
		return Lists.newArrayList(es);
	}

	public void remove(final String serviceName, final String methodName) {

	}

	public void put(final ZRPCProtocol zrpcProtocol, final ChannelHandlerContext ctx) {

		this.contextService.add(zrpcProtocol.getServiceName(), zrpcProtocol, ctx);

		final List<ZRPCCTX> v = this.cm.get(generateKeyByServiceNameAndName(zrpcProtocol));
		// FIXME 2022年1月18日 下午4:29:06 zhanghen: 下面构造入 均衡策略
		final ZRPCCTX zrpcctx = new ZRPCCTX();
		zrpcctx.setBalanceEnum(this.balanceStrategyComponent.getBalanceStrategyEnum());
		zrpcctx.setCtx(ctx);
		if (CollUtil.isEmpty(v)) {
			this.cm.put(generateKeyByServiceNameAndName(zrpcProtocol), Lists.newArrayList(zrpcctx));
		} else {
			v.add(zrpcctx);
		}

	}

	public void removeClosedCTX(final ChannelHandlerContext ctx) {
		if (Objects.isNull(ctx)) {
			return;
		}

		final Set<Entry<String, List<ZRPCCTX>>> entrySet = this.cm.entrySet();
		if (CollUtil.isEmpty(entrySet)) {
			return;
		}

		for (final Entry<String, List<ZRPCCTX>> entry : entrySet) {
			final List<ZRPCCTX> v = entry.getValue();
			if (CollUtil.isEmpty(v)) {
				continue;
			}

			final List<ZRPCCTX> r2 = Lists.newArrayList();
			for (final ZRPCCTX ctx1 : v) {
				if (ctx1.getCtx() == ctx) {
					r2.add(ctx1);
				}
			}
			v.removeAll(r2);
		}

	}

	public ZRPCCTX getZRPCCTX(final ZRPCProtocol zrpcProtocol) {
		final List<ZRPCCTX> v = this.cm.get(generateKeyByServiceNameAndName(zrpcProtocol));
		if (CollUtil.isEmpty(v)) {
			return null;
		}

		final ZProcuderBalanceEnum balanceEnum = this.balanceStrategyComponent.getBalanceStrategyEnum();
		final ZRPCCTX zrpcctx = balanceEnum.handle(zrpcProtocol, v);
		return zrpcctx;
	}

	public static String generateKeyByServiceNameAndName(final ZRPCProtocol zrpcProtocol) {
		return zrpcProtocol.getServiceName() + ZRPCContext.D + zrpcProtocol.getName();
	}

}
