package com.github.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipoTransacao;

    @Enumerated(EnumType.STRING)
    private StatusTransacao status;

    private BigDecimal valor;

    private LocalDateTime dataTransacao;

    public Transacao() {
    }

    public Transacao(Long id, TipoTransacao tipoTransacao, StatusTransacao status, BigDecimal valor, LocalDateTime dataTransacao) {
        this.id = id;
        this.tipoTransacao = tipoTransacao;
        this.status = status;
        this.valor = valor;
        this.dataTransacao = dataTransacao;
    }

    public Long getId() {
        return id;
    }

    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    public void setStatus(StatusTransacao status) {
        this.status = status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}
