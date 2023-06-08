package com.vo.netty;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vo.client.balance.ZProcuderBalanceEnum;
import com.vo.client.balance.ZRPCCTX;
import com.vo.common.ZIDGenerator;
import com.vo.common.ZRCRequestCache;
import com.vo.common.ZRPCContext;
import com.vo.common.ZRPCProtocol;
import com.vo.common.ZRPCSender;
import com.vo.common.ZThreadPoolExecutor;
import com.vo.core.ZLog2;
import com.vo.enums.ZRPETEnum;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 *
 */
@Sharable
@Component
public class ZRPCServerHandlerAdapter extends ChannelInboundHandlerAdapter {

	public static final ZLog2 log = ZLog2.getInstance();

	@Autowired
	private ZRPCContext zrcctxc;
	@Autowired
	private ZRPCSender zrpcSender;

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		log.info("channelActive,ctx={}", ctx);
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		ZRPCServerHandlerAdapter.log.info("channelRead读取消息,msg={}", msg);
		if (msg instanceof ZRPCProtocol) {
			final ZRPCProtocol zrpe = (ZRPCProtocol) msg;
			ZRPCServerHandlerAdapter.log.info("读取到ZRPE消息, ZRPE={}", zrpe);
			this.h(zrpe, ctx);
		} else {
		}
	}

	private void h(final ZRPCProtocol zrpe, final ChannelHandlerContext ctx) {
		ZThreadPoolExecutor.execute(new ZRR( zrpe, ctx));
	}

	public class ZRR implements Runnable {

		public final ZLog2 log = ZLog2.getInstance();

		private final ZRPCProtocol zrpe;
		/**
		 * ctx具体是producer还是consumer，消息体的type
		 */
		private final ChannelHandlerContext ctx;

		public ZRR( final ZRPCProtocol zrpe, final ChannelHandlerContext ctx) {
			this.zrpe = zrpe;
			this.ctx = ctx;
		}

		@Override
		public void run() {
			this.log.info("处理消息,zrpe={}", this.zrpe);
			final ZRPETEnum e = ZRPETEnum.valueOfType(this.zrpe.getType());
			if (e == null) {
				this.log.error("处理消息,e is null,zpre.type={},zrpe={}", this.zrpe.getType(), this.zrpe);
				return;
			}

			switch (e) {

			case ROLLBACK:
				this.log.info("处理ROLLBACK消息,zrpe={}", this.zrpe);
				final ChannelHandlerContext consumerCTX_ROLLBACK = ZRCRequestCache.getCTX(this.zrpe.getId());
				ZRPCServerHandlerAdapter.this.zrpcSender.send(this.zrpe, consumerCTX_ROLLBACK);
				break;

			case COMMIT:
				this.log.info("处理COMMIT消息,zrpe={}", this.zrpe);
//				final ZRPCCTX commitZRPCCTX = ZRPCServerHandlerAdapter.this.zrcctxc.getZRPCCTX(this.zrpe);
				final ChannelHandlerContext consumerCTX_COMMIT = ZRCRequestCache.getCTX(this.zrpe.getId());
				ZRPCServerHandlerAdapter.this.zrpcSender.send(this.zrpe, consumerCTX_COMMIT);

				break;

			case INVOEK:
				this.log.info("处理INVOEK消息,zrpe={}", this.zrpe);
				final ZRPCCTX producerZRPCCTX = ZRPCServerHandlerAdapter.this.zrcctxc.getZRPCCTX(this.zrpe);

				if (producerZRPCCTX == null) {
					this.log.warn("处理PRODUCER_NOT_FOUND消息,serviceName={},methodName={},zrpe={}",
							this.zrpe.getServiceName(),	this.zrpe.getName(), this.zrpe);
					final ZRPCProtocol result = new ZRPCProtocol();
					result.setId(this.zrpe.getId());
					result.setName(this.zrpe.getName());
					result.setServiceName(this.zrpe.getServiceName());
					result.setType(ZRPETEnum.PRODUCER_NOT_FOUND.getType());
					ZRPCServerHandlerAdapter.this.zrpcSender.send(result, this.ctx);
					return;
				}

				ZRCRequestCache.put(this.zrpe.getId(), this.ctx);
				this.log.info("处理INVOEK消息,发送到producer,zrpe={}", this.zrpe);
				ZRPCServerHandlerAdapter.this.zrpcSender.send(this.zrpe, producerZRPCCTX.getCtx());

				break;

			case INVOEK_EXCEPTION:
				this.log.warn("处理INVOEK_EXCEPTION消息,id={},message={}", this.zrpe.getId(), this.zrpe.getRv());
				final ChannelHandlerContext consumerCTX_E = ZRCRequestCache.getCTX(this.zrpe.getId());
				ZRPCServerHandlerAdapter.this.zrpcSender.send(this.zrpe, consumerCTX_E);
				break;

			case RESULT:

				final ZRPCCTX producerZRPCCTX2 = ZRPCServerHandlerAdapter.this.zrcctxc.getZRPCCTX(this.zrpe);
				if (Objects.nonNull(producerZRPCCTX2)) {
					producerZRPCCTX2.postHandle(this.zrpe);
				}

				this.log.info("处理RESULT消息,发送到consumer,id={},zrpe={}", this.zrpe.getId(), this.zrpe);
				while (true) {
					final ChannelHandlerContext consumerCTX_R = ZRCRequestCache.getCTX(this.zrpe.getId());
					if (consumerCTX_R == null) {
						continue;
					}

					ZRPCServerHandlerAdapter.this.zrpcSender.send(this.zrpe, consumerCTX_R);
					break;
				}
				break;

			case INIT:
				this.log.info("处理INIT消息,id={},serviceName={},methodName={}",
						this.zrpe.getId(), this.zrpe.getServiceName(), this.zrpe.getName());
				final ChannelHandlerContext producerCTX_INIT = this.ctx;
				ZRPCServerHandlerAdapter.this.zrcctxc.put(this.zrpe, producerCTX_INIT);

				final ZRPCProtocol initOK = ZRPCProtocol.builder()
							.id(ZIDGenerator.generateId())
							.type(ZRPETEnum.INIT_SUCCESS.getType())
							.build();

				ZRPCServerHandlerAdapter.this.zrpcSender.send(initOK, producerCTX_INIT);

				break;

			case SHUTDOWN:
				this.log.info("处理SHUTDOWN消息,开始清除ctx,ctx={}",this.ctx);
				ZRPCServerHandlerAdapter.this.zrcctxc.removeClosedCTX(this.ctx);
				this.log.info("处理SHUTDOWN消息,清除ctx结束,ctx={}",this.ctx);

				break;

			default:
				break;
			}

		}

	}

	public static final Function4<ZLog2, ZRPCProtocol, ZRPCSender, ZRPCCTX, Object> invokeFunction
			= (log, zrpe, zrpcSender, zrpcctx) -> {
		log.warn("处理PRODUCER_NOT_FOUND消息,serviceName={},methodName={},zrpe={}", zrpe.getServiceName(), zrpe.getName(),
				zrpe);
		final ZRPCProtocol result = new ZRPCProtocol();
		result.setId(zrpe.getId());
		result.setName(zrpe.getName());
		result.setServiceName(zrpe.getServiceName());
		result.setType(ZRPETEnum.PRODUCER_NOT_FOUND.getType());
		zrpcSender.send(result, zrpcctx.getCtx());
		return null;
	};

}
