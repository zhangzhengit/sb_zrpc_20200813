package com.vo.common;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.management.MBeanServerDelegateMBean;

import org.checkerframework.checker.units.qual.s;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.vo.common.ServiceDetail.ServiceMethodDetail;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * context 相关
 *
 * @author zhangzhen
 * @date 2022年1月5日
 *
 */
@org.springframework.stereotype.Service
public class ZContextService {

	/**
	 * <服务名,ServiceDetail>
	 */
	private final ConcurrentMap<String, ServiceDetail> dmap = Maps.newConcurrentMap();

	public void add(final String serviceName,final ZRPCProtocol zrpcProtocol, final ChannelHandlerContext ctx) {

		final ServiceDetail sd = this.dmap.get(serviceName);
		if (Objects.nonNull(sd)) {
			sd.getCtxList().add(ctx);
		} else {
			final ServiceDetail sdN = new ServiceDetail();
			sdN.getCtxList().add(ctx);
			this.dmap.put(serviceName, sdN);
		}

	}

	public List<String> getAllServiceName() {
		final Set<String> keySet = this.dmap.keySet();
		return Lists.newArrayList(Sets.newHashSet(keySet));
	}

	/**
	 * 方法调用次数+1
	 *
	 * @param serviceName
	 * @param methodName
	 * @param success
	 *
	 * @author zhangzhen
	 * @date 2022年1月5日
	 */
	public void addCount(final String serviceName, final String methodName,final boolean success) {
		final ServiceDetail sd = this.getByServiceName(serviceName);
		if (Objects.isNull(sd)) {
			final ServiceDetail sdN = new ServiceDetail();

			if (success) {

			}
			this.dmap.put(serviceName, sdN);
		} else {
			final List<ServiceMethodDetail> mlist = sd.getMethodList();
			final Optional<ServiceMethodDetail> mm = mlist.stream().filter(m -> m.getName().equals(methodName)).findFirst();
			if (mm.isPresent()) {
				final ServiceMethodDetail smd = mm.get();
				final Long ic = smd.getInvokeCount();
				if (Objects.isNull(ic)) {
					smd.setInvokeCount(1L);
				}
				if (success) {
					// FIXME 2022年1月5日 上午12:48:13 zhanghen: 写这里，先不写了，等理清楚了再写
				}
			}
		}

	}

	public ServiceDetail getByServiceName(final String serviceName) {

		if (StrUtil.isEmpty(serviceName)) {
			return null;
		}

		final ServiceDetail sd = this.dmap.get(serviceName);

		return sd;
	}

}
