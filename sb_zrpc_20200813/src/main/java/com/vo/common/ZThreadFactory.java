package com.vo.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 	自定义线程工厂
 * 
 * @author zhangzhen
 * @date 2021-12-22 1:49:47
 * 
 */
public class ZThreadFactory implements ThreadFactory {

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private String namePrefix;

	public ZThreadFactory(final String namePrefix) {
		super();
		this.namePrefix = namePrefix;
		final SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = namePrefix;
	}

	@Override
	public Thread newThread(final Runnable r) {
		final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
