package com.mellyssamnds.pixapi.domain.exception;

public class ChavePixDuplicadaException extends DomainException {
    public ChavePixDuplicadaException() {
        super("Chave Pix já cadastrada!");
    }
}