package com.mellyssamnds.pixapi.domain.valueobject;

import com.mellyssamnds.pixapi.domain.exception.ChavePixInvalidaException;

import java.util.regex.Pattern;

public record EmailPix(String valor) implements ChavePixValor {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.+-]+@([\\w-]+\\.)+[a-zA-Z]{2,}$");

    public EmailPix {
        valor = (valor != null) ? valor.trim() : null;

        if (valor == null || valor.isBlank()) {
            throw new ChavePixInvalidaException("Valor da chave não pode ser nulo ou vazio");
        }

        if (!EMAIL_PATTERN.matcher(valor).matches()) {
            throw new ChavePixInvalidaException(
                "Chave do tipo EMAIL inválida: formato de e-mail incorreto"
            );
        }
    }
}