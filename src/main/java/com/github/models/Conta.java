package com.github.models;

import com.github.exceptions.SaldoInsuficienteException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroConta;

    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "contaOrigem", cascade = CascadeType.ALL)
    private List<Pagamento> pagamentosOrigem;

    @OneToMany(mappedBy = "contaDestino", cascade = CascadeType.ALL)
    private List<Pagamento> pagamentosDestino;

    public Conta() {}

    public Conta(String numeroConta, BigDecimal saldo, Cliente cliente) {
        this.numeroConta = numeroConta;
        this.saldo = saldo;
        this.cliente = cliente;
    }

    public void receber(BigDecimal valor) {
        saldo = saldo.add(valor);
    }

    public void pagar(BigDecimal valor) {
        if (valor.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo disponível: R$ " + saldo);
        }
        saldo = saldo.subtract(valor);
    }

    public Long getId() {
        return id;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Pagamento> getPagamentosOrigem() {
        return pagamentosOrigem;
    }

    public List<Pagamento> getPagamentosDestino() {
        return pagamentosDestino;
    }
}
