package com.github.service;

import com.github.exceptions.ContaNaoEncontradaException;
import com.github.models.Conta;
import com.github.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public void atualizarSaldosDeContas(Conta contaOrigem, Conta contaDestino, BigDecimal valorPagamento) {
        validarConta(contaOrigem, contaDestino, valorPagamento);

        contaOrigem.pagar(valorPagamento);
        contaDestino.receber(valorPagamento);

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);
    }

    private void validarConta(Conta contaOrigem, Conta contaDestino, BigDecimal valor) {
        if (contaOrigem == null) {
            throw new ContaNaoEncontradaException("Conta de origem não encontrada.");
        }
        if (contaDestino == null) {
            throw new ContaNaoEncontradaException("Conta de destino não encontrada.");
        }
    }
}
