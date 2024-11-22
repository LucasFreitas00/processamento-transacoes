package com.github.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PagamentoRequestConsumer {

    @KafkaListener(
            topics = "${topicos.pagamento.request.topic}",
            groupId = "pagamento-request-consumer-1"
    )
    public void consume(String mensagem) {
        System.out.println("Mensagem recebida: " + mensagem);
    }
}
