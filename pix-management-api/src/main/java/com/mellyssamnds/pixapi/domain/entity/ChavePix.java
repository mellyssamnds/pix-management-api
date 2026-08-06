package com.mellyssamnds.pixapi.domain.entity;

import com.mellyssamnds.pixapi.domain.exception.ChavePixInvalidaException;
import com.mellyssamnds.pixapi.domain.enums.TipoChavePix;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChavePix {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern CPF_PATTERN =
        Pattern.compile("^\\d{11}$");
        // O padrão para celular foi ajustado para aceitar números no formato brasileiro, 
        // que inclui o DDD (2 dígitos) seguido do número do celular (9 dígitos), totalizando 11 dígitos.
    private static final Pattern CELULAR_PATTERN =
        Pattern.compile("^\\d{2}9\\d{8}$");

    private final UUID id;
    private final TipoChavePix tipo;
    private final String valor;
    private final UUID usuarioId;
    private final LocalDateTime criadaEm;

    public ChavePix(TipoChavePix tipo, String valor, UUID usuarioId) {
        this(tipo, valor, usuarioId, Clock.systemDefaultZone());
    }

    public ChavePix(TipoChavePix tipo, String valor, UUID usuarioId, Clock clock) {
        validar(tipo, valor);

        this.id = UUID.randomUUID();
        this.tipo = tipo;
        this.valor = valor;
        this.usuarioId = usuarioId;
        this.criadaEm = LocalDateTime.now(clock);
    }

    private void validar(TipoChavePix tipo, String valor) {

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

    private void validarEmail(String email) {

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ChavePixInvalidaException(
                "Chave do tipo EMAIL inválida: formato de e-mail incorreto"
            );
        }
    }

    private void validarCpf(String cpf) {

        if (!CPF_PATTERN.matcher(cpf).matches()) {
            throw new ChavePixInvalidaException(
                "Chave do tipo CPF inválida: deve conter exatamente 11 dígitos numéricos"
            );
        }
    }

    private void validarCelular(String celular) {

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

}