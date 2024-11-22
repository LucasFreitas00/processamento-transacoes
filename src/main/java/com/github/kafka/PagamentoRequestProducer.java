package com.github.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dto.PagamentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PagamentoRequestProducer {

    @Value("${topicos.pagamento.request.topic}")
    private String pagamentoTransacaoRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public String sendMessage(PagamentoDTO pagamentoDTO) throws JsonProcessingException {
        String pagamentoStr = objectMapper.writeValueAsString(pagamentoDTO);
        kafkaTemplate.send(pagamentoTransacaoRequest, pagamentoStr);
        return "Pagamento enviado para processamento";
    }
}
