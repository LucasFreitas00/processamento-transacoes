package com.github.netty;

import com.github.models.StatusTransacao;
import com.github.models.TipoTransacao;
import com.github.models.Transacao;
import com.github.repository.TransacaoRepository;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class TransacaoHandler extends ChannelInboundHandlerAdapter {

    private GenericPackager packager;

    @Autowired
    private TransacaoRepository transacaoRepository;

    public TransacaoHandler() {
        try {
            // Carrega a configuração do ISO 8583
            packager = new GenericPackager("src/main/resources/iso8583.xml");
        } catch (ISOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] receivedBytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(receivedBytes);

        System.out.println("Mensagem ISO 8583 recebida: " + new String(receivedBytes, StandardCharsets.UTF_8));

        // Desempacota a mensagem ISO 8583 recebida
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(packager);
        isoMsg.setHeader("ISO1987".getBytes());
        isoMsg.unpack(receivedBytes);

        // Processa a mensagem (verifica o MTI e o código de processamento, por exemplo)
        String mti = isoMsg.getMTI();
        if ("0200".equals(mti)) {
            // Exemplo de lógica de validação e processamento
            String processingCode = isoMsg.getString(3);
            if ("011000".equals(processingCode)) {
                // Criar uma resposta para a mensagem
                ISOMsg responseMsg = new ISOMsg();
                responseMsg.setPackager(packager);
                responseMsg.setHeader("ISO1987".getBytes());
                responseMsg.setMTI("0210"); // MTI de resposta para transação 0200
                responseMsg.set(3, processingCode); // Código de processamento (echo)
                responseMsg.set(39, "00"); // Código de resposta de sucesso

                // Empacotar e enviar a resposta
                byte[] responseBytes = responseMsg.pack();
                ByteBuf responseBuffer = byteBuf.alloc().buffer(responseBytes.length);
                responseBuffer.writeBytes(responseBytes);
                ctx.writeAndFlush(responseBuffer).addListener(ChannelFutureListener.CLOSE);

                System.out.println("Resposta ISO 8583 enviada: " + new String(responseBytes, StandardCharsets.UTF_8));

                Transacao transacao = new Transacao();
                transacao.setTipoTransacao(TipoTransacao.DEBITO);
                transacao.setStatus(StatusTransacao.CONCLUIDA);
                transacao.setValor(BigDecimal.valueOf(Double.parseDouble(isoMsg.getString(4))));
                transacao.setDataTransacao(LocalDateTime.now());

                transacaoRepository.save(transacao);
            } else {
                System.out.println("Código de processamento inválido.");
            }
        } else {
            System.out.println("MTI inválido ou não suportado.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
