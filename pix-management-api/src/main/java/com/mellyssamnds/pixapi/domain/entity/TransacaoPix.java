package com.mellyssamnds.pixapi.domain.entity;

import com.mellyssamnds.pixapi.domain.exception.TransacaoPixInvalidaException;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransacaoPix {

    private UUID id;
    private String chavePixOrigem;
    private String chavePixDestino;
    private double valor;
    private LocalDateTime realizadaEm;

    public TransacaoPix(String chavePixOrigem, String chavePixDestino, double valor) {
        if (chavePixOrigem == null || chavePixOrigem.isEmpty()) {
            throw new TransacaoPixInvalidaException("A chave Pix de origem não pode ser nula ou vazia");
        }

        if (chavePixDestino == null || chavePixDestino.isEmpty()) {
            throw new TransacaoPixInvalidaException("A chave Pix de destino não pode ser nula ou vazia");
        }

        if (chavePixOrigem.equals(chavePixDestino)) {
            throw new TransacaoPixInvalidaException("A chave Pix de origem e destino não podem ser iguais");
        }
        
        if (valor <= 0) {
            throw new TransacaoPixInvalidaException("O valor da transação deve ser maior que zero");
        }

        this.id = UUID.randomUUID();
        this.chavePixOrigem = chavePixOrigem;
        this.chavePixDestino = chavePixDestino;
        this.valor = valor;
        this.realizadaEm = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getChavePixOrigem() {
        return chavePixOrigem;
    }

    public String getChavePixDestino() {
        return chavePixDestino;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return realizadaEm;
    }
}