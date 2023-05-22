package com.vo.netty;

import java.util.List;

import com.vo.common.ZProtobufUtil;
import com.vo.common.ZRPCProtocol;
import com.vo.core.ZLog2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 *     自定义消息解码器,消息体位定长4字节+protobuf序列化的数据
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 *
 */
public class ZRPCMessageDecoder extends ByteToMessageDecoder{

	public static final ZLog2 LOG = ZLog2.getInstance();

	@Override
	protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {

		final int readableBytes = in.readableBytes();
		if (readableBytes < ZProtobufUtil.L_LENGTH) {
			return;
		}

		in.markReaderIndex();

		final byte[] lba = new byte[ZProtobufUtil.L_LENGTH];
		in.readBytes(lba);

		final int vlength = ZProtobufUtil.byteArrayToInt(lba);
		if (vlength <= 0) {
			in.resetReaderIndex();
			return;
		}

		final int readerIndex = in.readerIndex();
		final int writerIndex = in.writerIndex();
		if (readerIndex + vlength > writerIndex) {
			in.resetReaderIndex();
			return;
		}

		final byte[] vba = new byte[vlength];
		in.readBytes(vba);

		final ZRPCProtocol zrpe = ZProtobufUtil.deserialize(vba, ZRPCProtocol.class);
		ZRPCMessageDecoder.LOG.info("decode到消息,zrpe={}", zrpe);
		out.add(zrpe);
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		ZRPCMessageDecoder.LOG.error("exceptionCaught,cause.message={},cause={},ctx={}", cause.getMessage(), cause, ctx);
	}

}
