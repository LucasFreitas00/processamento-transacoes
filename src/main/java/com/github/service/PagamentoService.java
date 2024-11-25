package com.github.service;

import com.github.models.Conta;
import com.github.models.MetodoPagamento;
import com.github.models.Pagamento;
import com.github.models.StatusPagamento;
import com.github.repository.ContaRepository;
import com.github.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PagamentoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ContaService contaService;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    public Pagamento processarPagamento(String numeroContaOrigem, String numeroContaDestino, BigDecimal valorPagamento) {
        Conta contaOrigem = contaRepository.findByNumeroConta(numeroContaOrigem);
        Conta contaDestino = contaRepository.findByNumeroConta(numeroContaDestino);

        contaService.atualizarSaldosDeContas(contaOrigem, contaDestino, valorPagamento);

        return salvarPagamento(contaOrigem, contaDestino, valorPagamento);
    }

    private Pagamento salvarPagamento(Conta contaOrigem, Conta contaDestino, BigDecimal valorPagamento) {
        Pagamento pagamento = new Pagamento();
        pagamento.setMetodoPagamento(MetodoPagamento.DEBITO);
        pagamento.setStatus(StatusPagamento.CONCLUIDO);
        pagamento.setValor(valorPagamento);
        pagamento.setData(LocalDateTime.now());
        pagamento.setContaOrigem(contaOrigem);
        pagamento.setContaDestino(contaDestino);

        pagamentoRepository.save(pagamento);
        return pagamento;
    }
}
