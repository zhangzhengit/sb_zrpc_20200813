package com.vo.netty;

/**
 * 带有四个参数的Function
 *
 * @author zhangzhen
 * @date 2022年1月18日
 *
 */
@FunctionalInterface
public interface Function4<P1, P2, P3, P4, R> {

	R apply(P1 p1, P2 p2, P3 p3, P4 p4);

}
