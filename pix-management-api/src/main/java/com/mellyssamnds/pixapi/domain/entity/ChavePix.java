package com.mellyssamnds.pixapi.domain.entity;

import com.mellyssamnds.pixapi.domain.exception.ChavePixInvalidaException;
import com.mellyssamnds.pixapi.domain.enums.TipoChavePix;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChavePix {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.+-]+@([\\w-]+\\.)+[a-zA-Z]{2,}$");
    private static final Pattern CPF_PATTERN =
        Pattern.compile("^\\d{11}$");
    // O padrão para celular foi ajustado para aceitar números no formato brasileiro,
    // que inclui o DDD (2 dígitos) seguido do número do celular (9 dígitos), totalizando 11 dígitos.
    private static final Pattern CELULAR_PATTERN =
        Pattern.compile("^[1-9]\\d9\\d{8}$");

    private static final int MODULO_CPF = 11;
    private static final int TAMANHO_CPF_SEM_DIGITOS = 9;
    // O segundo DV é calculado sobre os 9 dígitos base + o primeiro DV já validado
    private static final int QTD_DIGITOS_SEGUNDO_DV = TAMANHO_CPF_SEM_DIGITOS + 1;
    private static final int PESO_PRIMEIRO_DIGITO = 10;
    private static final int PESO_SEGUNDO_DIGITO = 11;
    private static final int INDICE_PRIMEIRO_DV = TAMANHO_CPF_SEM_DIGITOS;
    private static final int INDICE_SEGUNDO_DV = QTD_DIGITOS_SEGUNDO_DV;

    private final UUID id;
    private final TipoChavePix tipo;
    private final String valor;
    private final UUID usuarioId;
    private final LocalDateTime criadaEm;

    private ChavePix(UUID id, TipoChavePix tipo, String valor, UUID usuarioId, LocalDateTime criadaEm) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.usuarioId = usuarioId;
        this.criadaEm = criadaEm;
    }

    /**
     * Cria uma nova chave PIX, validando as regras de formato do tipo informado.
     * Use este método para o fluxo de cadastro (novo registro).
     */
    public static ChavePix criar(TipoChavePix tipo, String valor, UUID usuarioId) {
        return criar(tipo, valor, usuarioId, Clock.systemDefaultZone());
    }

    public static ChavePix criar(TipoChavePix tipo, String valor, UUID usuarioId, Clock clock) {
        String valorFormatado = normalizar(valor);
        validar(tipo, valorFormatado);

        return new ChavePix(
            UUID.randomUUID(),
            tipo,
            valorFormatado,
            usuarioId,
            LocalDateTime.now(clock)
        );
    }

    /**
     * Reconstitui uma chave PIX já existente (ex.: ao carregar do banco de dados).
     * Não reaplica as regras de formato (regex, dígito verificador) — assume que os
     * dados já foram validados no momento da criação original — mas ainda normaliza
     * e garante que nenhum campo obrigatório chegue nulo ou em branco, para que um
     * registro corrompido no banco não vire silenciosamente uma ChavePix inconsistente.
     */
    public static ChavePix reconstituir(
        UUID id,
        TipoChavePix tipo,
        String valor,
        UUID usuarioId,
        LocalDateTime criadaEm
    ) {
        String valorFormatado = normalizar(valor);

        if (id == null || tipo == null || valorFormatado == null || valorFormatado.isBlank()
            || usuarioId == null || criadaEm == null) {
            throw new ChavePixInvalidaException(
                "Dados obrigatórios ausentes ou inválidos na reconstituição da chave PIX"
            );
        }

        return new ChavePix(id, tipo, valorFormatado, usuarioId, criadaEm);
    }

    private static String normalizar(String valor) {
        return (valor != null) ? valor.trim() : null;
    }

    private static void validar(TipoChavePix tipo, String valor) {

        if (tipo == null) {
            throw new ChavePixInvalidaException(
                "Tipo da chave não pode ser nulo"
            );
        }

        if (valor == null || valor.isBlank()) {
            throw new ChavePixInvalidaException(
                "Valor da chave não pode ser nulo ou vazio"
            );
        }

        switch (tipo) {
            case EMAIL -> validarEmail(valor);
            case CPF -> validarCpf(valor);
            case CELULAR -> validarCelular(valor);
            default -> throw new ChavePixInvalidaException(
                "Tipo de chave não suportado: " + tipo
            );
        }
    }

    private static void validarEmail(String email) {

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ChavePixInvalidaException(
                "Chave do tipo EMAIL inválida: formato de e-mail incorreto"
            );
        }
    }

    private static void validarCpf(String cpf) {

        if (!CPF_PATTERN.matcher(cpf).matches()) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CPF inválida: deve conter 11 dígitos numéricos"
            );
        }

        if (todosDigitosIguais(cpf)) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CPF inválida: não pode conter todos os dígitos iguais"
            );
        }

        if (!cpfValido(cpf)) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CPF inválida: dígitos verificadores incorretos"
            );
        }
    }

    // Checagem por laço simples: evita o overhead de boxing/stream do chars().distinct()
    // para uma verificação que só precisa comparar cada caractere contra o primeiro.
    private static boolean todosDigitosIguais(String cpf) {
        char primeiro = cpf.charAt(0);

        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != primeiro) {
                return false;
            }
        }

        return true;
    }

    // Método para validar o CPF utilizando o algoritmo de validação de CPF
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

    private static void validarCelular(String celular) {

        if (!CELULAR_PATTERN.matcher(celular).matches()) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CELULAR inválida: deve conter 11 dígitos numéricos (DDD + número)"
            );
        }
    }

    public UUID getId() {
        return id;
    }

    public TipoChavePix getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChavePix outra)) return false;
        return Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        // "valor" é deliberadamente omitido: é o dado sensível da chave (CPF, e-mail
        // ou celular) e não deve vazar para logs através de um toString() padrão.
        return "ChavePix{" +
            "id=" + id +
            ", tipo=" + tipo +
            ", usuarioId=" + usuarioId +
            ", criadaEm=" + criadaEm +
            '}';
    }

}