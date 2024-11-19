package com.github.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.nio.charset.StandardCharsets;

public class ClienteHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Conectado ao servidor. Enviando mensagem ISO 8583...");

        // Carrega a configuração do ISO 8583
        GenericPackager packager = new GenericPackager("src/main/resources/iso8583.xml");

        // Cria a mensagem ISO 8583 do tipo 0200
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(packager);
        isoMsg.setHeader("ISO1987".getBytes());
        isoMsg.setMTI("0200");
        isoMsg.set( 2 , "5642570404782927" );
        isoMsg.set( 3 , "011000" );
        isoMsg.set( 4 , "78000" );
        isoMsg.set( 7 , "1220145711" );
        isoMsg.set( 11 , "101183" );
        isoMsg.set( 12 , "145711" );
        isoMsg.set( 13 , "1220" );
        isoMsg.set( 14 , "2408" );
        isoMsg.set( 15 , "1220" );
        isoMsg.set( 18 , "6011" );
        isoMsg.set( 22 , "051" );
        isoMsg.set( 25 , "00" );
        isoMsg.set( 26 , "04" );
        isoMsg.set( 28 , "C00000000" );
        isoMsg.set( 30 , "C00000000" );
        isoMsg.set( 32 , "56445700" );
        isoMsg.set( 37 , "567134101183" );
        isoMsg.set( 41 , "N1742" );
        isoMsg.set( 42 , "ATM004" );
        isoMsg.set( 43 , "45 SR LIDERANÇA DUABANAT NOVA ECIJAQ PH" );
        isoMsg.set( 49 , "608" );
        isoMsg.set( 102 , "970630181070041" );
        isoMsg.set( 120 , "BRN015301213230443463" );

        // Empacota a mensagem ISO para bytes
        byte[] messageBytes = isoMsg.pack();

        // Envia a mensagem ao servidor
        ByteBuf buffer = Unpooled.wrappedBuffer(messageBytes);
        ctx.writeAndFlush(buffer);
        System.out.println("Mensagem ISO 8583 enviada: " + new String(messageBytes, StandardCharsets.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        System.out.println("Resposta recebida do servidor: " + new String(bytes, StandardCharsets.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
