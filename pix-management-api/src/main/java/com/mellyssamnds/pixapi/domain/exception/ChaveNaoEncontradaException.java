package com.mellyssamnds.pixapi.domain.exception;

public class ChaveNaoEncontradaException extends RuntimeException {
    public ChaveNaoEncontradaException() {
        super("Chave Pix não encontrada!");
    }
}