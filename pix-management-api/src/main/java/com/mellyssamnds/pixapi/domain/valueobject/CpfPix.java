package com.mellyssamnds.pixapi.domain.valueobject;

import com.mellyssamnds.pixapi.domain.exception.ChavePixInvalidaException;

import java.util.regex.Pattern;

public record CpfPix(String valor) implements ChavePixValor {

    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");

    private static final int MODULO_CPF = 11;
    private static final int TAMANHO_CPF_SEM_DIGITOS = 9;
    // O segundo DV é calculado sobre os 9 dígitos base + o primeiro DV já validado
    private static final int QTD_DIGITOS_SEGUNDO_DV = TAMANHO_CPF_SEM_DIGITOS + 1;
    private static final int PESO_PRIMEIRO_DIGITO = 10;
    private static final int PESO_SEGUNDO_DIGITO = 11;
    private static final int INDICE_PRIMEIRO_DV = TAMANHO_CPF_SEM_DIGITOS;
    private static final int INDICE_SEGUNDO_DV = QTD_DIGITOS_SEGUNDO_DV;

    public CpfPix {
        valor = (valor != null) ? valor.trim() : null;

        if (valor == null || valor.isBlank()) {
            throw new ChavePixInvalidaException("Valor da chave não pode ser nulo ou vazio");
        }

        if (!CPF_PATTERN.matcher(valor).matches()) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CPF inválida: deve conter 11 dígitos numéricos"
            );
        }

        if (todosDigitosIguais(valor)) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CPF inválida: não pode conter todos os dígitos iguais"
            );
        }

        if (!cpfValido(valor)) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CPF inválida: dígitos verificadores incorretos"
            );
        }
    }

    private static boolean todosDigitosIguais(String cpf) {
        char primeiro = cpf.charAt(0);

        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != primeiro) {
                return false;
            }
        }

        return true;
    }

    private static boolean cpfValido(String cpf) {
        int dv1 = calcularDigitoVerificador(cpf, TAMANHO_CPF_SEM_DIGITOS, PESO_PRIMEIRO_DIGITO);

        if (dv1 != Character.getNumericValue(cpf.charAt(INDICE_PRIMEIRO_DV))) {
            return false;
        }

        int dv2 = calcularDigitoVerificador(cpf, QTD_DIGITOS_SEGUNDO_DV, PESO_SEGUNDO_DIGITO);

        return dv2 == Character.getNumericValue(cpf.charAt(INDICE_SEGUNDO_DV));
    }

    private static int calcularDigitoVerificador(String cpf, int qtdDigitos, int pesoInicial) {
        int soma = 0;

        for (int i = 0; i < qtdDigitos; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (pesoInicial - i);
        }

        int resto = soma % MODULO_CPF;
        return (resto < 2) ? 0 : MODULO_CPF - resto;
    }
}