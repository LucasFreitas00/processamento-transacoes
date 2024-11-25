package com.github.netty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dto.PagamentoDTO;
import com.github.kafka.PagamentoRequestProducer;
import com.github.models.Pagamento;
import com.github.service.PagamentoService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
@Sharable
public class PagamentoHandler extends ChannelInboundHandlerAdapter {

    private GenericPackager packager;

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PagamentoRequestProducer pagamentoRequestProducer;

    public PagamentoHandler() {
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

        ISOMsg isoMsg = desempacotarIsoMessage(receivedBytes);

        if ("0200".equals(isoMsg.getMTI())) {
            String processingCode = isoMsg.getString(3);

            if ("301100".equals(processingCode)) {
                String numeroContaOrigem = isoMsg.getString(102);
                String numeroContaDestino = isoMsg.getString(103);
                BigDecimal valorPagamento = BigDecimal.valueOf(Double.parseDouble(isoMsg.getString(4)));

                Pagamento pagamento = pagamentoService.processarPagamento(numeroContaOrigem, numeroContaDestino, valorPagamento);

                enviarMensagemKafka(pagamento);

                enviarRespostaIso(ctx, byteBuf, processingCode);
            } else {
                System.out.println("Código de processamento inválido.");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private ISOMsg desempacotarIsoMessage(byte[] receivedBytes) throws ISOException {
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(packager);
        isoMsg.setHeader("ISO8583".getBytes());
        isoMsg.unpack(receivedBytes);
        return isoMsg;
    }

    private void enviarMensagemKafka(Pagamento pagamento) {
        PagamentoDTO pagamentoDTO = new PagamentoDTO();
        pagamentoDTO.setNumero(pagamento.getId());
        pagamentoDTO.setValor(pagamento.getValor());
        pagamentoDTO.setDescricao("Pagamento de compra no " + pagamento.getMetodoPagamento());

        try {
            pagamentoRequestProducer.sendMessage(pagamentoDTO);
        } catch (JsonProcessingException e) {
            System.out.println("Erro ao enviar dados do pagamento para o Kafka.");
        }
    }

    private void enviarRespostaIso(ChannelHandlerContext ctx, ByteBuf byteBuf, String processingCode) throws ISOException {
        ISOMsg responseMsg = new ISOMsg();
        responseMsg.setPackager(packager);
        responseMsg.setHeader("ISO8583".getBytes());
        responseMsg.setMTI("0210");
        responseMsg.set(3, processingCode);
        responseMsg.set(39, "00");

        byte[] responseBytes = responseMsg.pack();
        ByteBuf responseBuffer = byteBuf.alloc().buffer(responseBytes.length);
        responseBuffer.writeBytes(responseBytes);
        ctx.writeAndFlush(responseBuffer).addListener(ChannelFutureListener.CLOSE);

        System.out.println("Resposta ISO 8583 enviada: " + new String(responseBytes, StandardCharsets.UTF_8));
    }
}
