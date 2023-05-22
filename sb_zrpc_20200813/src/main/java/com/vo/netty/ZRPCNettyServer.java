package com.vo.netty;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.vo.conf.ZrpcServerConfiguration;
import com.vo.core.ZLog2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 *
 *
 * @author zhangzhen
 * @data Aug 13, 2020
 *
 */
@Order(value = 1)
@Component
public class ZRPCNettyServer implements ApplicationRunner, DisposableBean {

	public static final ZLog2 LOG = ZLog2.getInstance();

	@Autowired
	private ZRPCServerHandlerAdapter zrnsh;
	@Autowired
	private ZrpcServerConfiguration zrsp;

	private EventLoopGroup boss;
	private EventLoopGroup worker;

	@Override
	public void run(final ApplicationArguments args) throws Exception {

		ZRPCNettyServer.LOG.info("zrpc-server开始启动,args={}", args);

		final ServerBootstrap b = new ServerBootstrap();
		this.boss = new NioEventLoopGroup();
		this.worker = new NioEventLoopGroup();

		b.group(this.boss, this.worker);
		b.channel(NioServerSocketChannel.class);
		b.option(ChannelOption.SO_BACKLOG, 20000);
		b.option(ChannelOption.TCP_NODELAY, true);
		b.option(ChannelOption.SO_REUSEADDR, true);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.localAddress(new InetSocketAddress(this.zrsp.getPort()));

		b.childHandler(new ChannelInitializer<SocketChannel>(){

			@Override
			protected void initChannel(final SocketChannel ch) throws Exception {
				ZRPCNettyServer.LOG.info("initChannel执行");

				final ChannelPipeline p = ch.pipeline();
				p.addLast(new ZRPCMessageDecoder());
				p.addLast(ZRPCNettyServer.this.zrnsh);
			}

		});

		final ChannelFuture channelFuture = b.bind().sync();
		ZRPCNettyServer.LOG.info("zrpc - bind.prot={}", this.zrsp.getPort());
		channelFuture.channel().closeFuture().sync();
		ZRPCNettyServer.LOG.info("zrpc启动成功,bind.prot={}", this.zrsp.getPort());
	}

	@Override
	public void destroy() throws Exception {
		ZRPCNettyServer.LOG.info("destroy开始");
		this.boss.shutdownGracefully().sync();
		ZRPCNettyServer.LOG.info("boss.shutdownGracefully().sync() 成功");
		this.worker.shutdownGracefully().sync();
		ZRPCNettyServer.LOG.info("worker.shutdownGracefully().sync() 成功");
	}

}
