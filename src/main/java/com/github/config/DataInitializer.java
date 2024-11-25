package com.github.config;

import com.github.models.Cliente;
import com.github.models.Conta;
import com.github.repository.ClienteRepository;
import com.github.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class DataInitializer {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaRepository contaRepository;

    @PostConstruct
    public void init() {
        Cliente cliente1 = new Cliente("João Silva", "123.456.789-00");
        Cliente cliente2 = new Cliente("Lucas Silva", "987.654.321-00");

        Conta conta1 = new Conta("11111-1", BigDecimal.valueOf(1000), cliente1);
        Conta conta2 = new Conta("22222-2", BigDecimal.valueOf(2000), cliente2);

        cliente1.addConta(conta1);
        cliente2.addConta(conta2);

        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);

        System.out.println("Dados iniciais carregados!");
    }
}
