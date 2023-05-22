package com.vo.common;

import java.nio.channels.ClosedChannelException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vo.core.ZLog2;
import com.vo.enums.ZRPETEnum;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 *
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 *
 */
@Component
public final class ZRPCSender {

	public static final ZLog2 LOG = ZLog2.getInstance();

	@Autowired
	private ZRPCContext zrcctxc;

	/**
	 * 发送到执行的context
	 *
	 * @param zrpe
	 * @param ctx
	 */
	public void send(final ZRPCProtocol zrpe, final ChannelHandlerContext ctx) {
		if (ctx == null) {
			ZRPCSender.LOG.error("ctx null,zrpe={}", zrpe);
			return;
		}

		final byte[] w = ZProtobufUtil.wanzhangbytearray(ZProtobufUtil.serialize(zrpe));
		final ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(w));


		future.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(final Future<? super Void> future) throws Exception {
				if (future.isSuccess()) {
					ZRPCSender.LOG.info("send成功,zrpe={}", zrpe);
					return;
				}

				final Throwable throwable = future.cause();
				if (throwable instanceof ClosedChannelException) {
					final String message = future.cause().getMessage();
					ZRPCSender.LOG.error("send失败,producerCTX closed,id={},cause.message={},cause={}, zrpe={}",
							zrpe.getId(), message, future.cause(), zrpe);
					ZRPCSender.this.zrcctxc.removeClosedCTX(ctx);
					final ZRPCProtocol r = new ZRPCProtocol();
					r.setId(zrpe.getId());
					r.setType(ZRPETEnum.PRODUCER_CTX_CLOSED.getType());
					final ChannelHandlerContext consumerCTX = ZRCRequestCache.getCTX(zrpe.getId());
					// FIXME 2022年1月4日 下午9:04:46 zhanghen: c发送N次到p，还没处理完关闭c，p会一直重复知道到此？解决掉
					ZRPCSender.LOG.warn("send失败,producerCTX closed,通知consumer,id={},r={}", zrpe.getId(), r);

					// FIXME 2023年5月23日 上午12:35:33 zhanghen: 暂时处理为消费者关闭则不再发送，注释掉下面一行
//					ZRPCSender.this.send(r, consumerCTX);
				}

			}
		});
	}
}
