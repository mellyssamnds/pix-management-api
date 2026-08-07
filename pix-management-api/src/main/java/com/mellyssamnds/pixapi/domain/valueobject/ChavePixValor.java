package com.mellyssamnds.pixapi.domain.valueobject;

import com.mellyssamnds.pixapi.domain.enums.TipoChavePix;
import com.mellyssamnds.pixapi.domain.exception.ChavePixInvalidaException;

/**
 * Representa o valor validado de uma chave PIX, independente do tipo.
 * Cada implementação concreta sabe validar seu próprio formato — ChavePix
 * (a entidade) não conhece regex nem algoritmo de dígito verificador,
 * apenas delega a construção pro tipo certo.
 */
public sealed interface ChavePixValor permits CpfPix, EmailPix, CelularPix {

    String valor();

    /**
     * Fábrica central: recebe o tipo (vindo da API/comando) e o texto bruto,
     * e constrói o Value Object correto. Toda a validação de formato acontece
     * dentro do compact constructor de cada record — se o dado for inválido,
     * a exceção já estoura aqui, antes de qualquer ChavePix existir.
     */
    static ChavePixValor criar(TipoChavePix tipo, String valorTexto) {
        return switch (tipo) {
            case null -> throw new ChavePixInvalidaException("Tipo da chave não pode ser nulo");
            case CPF -> new CpfPix(valorTexto);
            case EMAIL -> new EmailPix(valorTexto);
            case CELULAR -> new CelularPix(valorTexto);
        };
    }

    /**
     * Deriva o TipoChavePix a partir da classe concreta do VO — evita guardar
     * o tipo como um campo redundante em ChavePix. O switch é exaustivo por
     * causa do "sealed": se um dia surgir um quarto tipo de chave, o compilador
     * força você a tratar o novo caso aqui.
     */
    default TipoChavePix tipo() {
        return switch (this) {
            case CpfPix ignored -> TipoChavePix.CPF;
            case EmailPix ignored -> TipoChavePix.EMAIL;
            case CelularPix ignored -> TipoChavePix.CELULAR;
        };
    }
}