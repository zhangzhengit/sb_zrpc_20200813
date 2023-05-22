package com.vo.common;

import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 * 
 */
public class ZRCRequestCache {

	private final static ConcurrentMap<String, ChannelHandlerContext> rm = Maps.newConcurrentMap();

	public static ChannelHandlerContext getCTX(final String requestId) {
		return rm.get(requestId);
	}

	public static void put(final String requestId, final ChannelHandlerContext ctx) {
		rm.put(requestId, ctx);
	}
}
