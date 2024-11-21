package com.github.netty;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

@Component
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final PagamentoHandler pagamentoHandler;

    public NettyServerInitializer(ApplicationContext context) {
        this.pagamentoHandler = context.getBean(PagamentoHandler.class);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(pagamentoHandler);
    }
}
