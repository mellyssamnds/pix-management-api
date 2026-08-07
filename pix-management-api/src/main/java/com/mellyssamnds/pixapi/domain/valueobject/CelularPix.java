package com.mellyssamnds.pixapi.domain.valueobject;

import com.mellyssamnds.pixapi.domain.exception.ChavePixInvalidaException;

import java.util.regex.Pattern;

public record CelularPix(String valor) implements ChavePixValor {

    // DDD (2 dígitos) + 9 (celular) + 8 dígitos = 11 dígitos numéricos no total
    private static final Pattern CELULAR_PATTERN = Pattern.compile("^[1-9]\\d9\\d{8}$");

    public CelularPix {
        valor = (valor != null) ? valor.trim() : null;

        if (valor == null || valor.isBlank()) {
            throw new ChavePixInvalidaException("Valor da chave não pode ser nulo ou vazio");
        }

        if (!CELULAR_PATTERN.matcher(valor).matches()) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CELULAR inválida: deve conter 11 dígitos numéricos (DDD + número)"
            );
        }
    }
}