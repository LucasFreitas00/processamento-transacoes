package com.github.netty;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

@Component
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final TransacaoHandler transacaoHandler;

    public NettyServerInitializer(ApplicationContext context) {
        this.transacaoHandler = context.getBean(TransacaoHandler.class);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(transacaoHandler);
    }
}
