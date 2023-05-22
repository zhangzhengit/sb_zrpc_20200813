package com.vo.common;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 	线程池
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 * 
 */
public class ZThreadPoolExecutor {
	
	public static void execute(final Runnable runnable) {
		ZE.execute(runnable);
	}

	private static final ThreadPoolExecutor ZE = 
			new ThreadPoolExecutor(
				Runtime.getRuntime().availableProcessors(),
				Runtime.getRuntime().availableProcessors(),
				0L,
				TimeUnit.MILLISECONDS, 
				new LinkedBlockingQueue<>(),
				new ZThreadFactory("zrpc-server-worker-thread-"),
				new ThreadPoolExecutor.CallerRunsPolicy());

	
}
