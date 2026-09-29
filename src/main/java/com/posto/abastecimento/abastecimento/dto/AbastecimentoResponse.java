package com.posto.abastecimento.abastecimento.dto;

import com.posto.abastecimento.abastecimento.Abastecimento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AbastecimentoResponse(
        Long id,
        Long bombaId,
        String bombaNome,
        String combustivelNome,
        LocalDateTime data,
        BigDecimal litros,
        BigDecimal precoLitro,
        BigDecimal valorTotal,
        LocalDateTime criadoEm
) {
    public static AbastecimentoResponse from(Abastecimento abastecimento) {
        return new AbastecimentoResponse(
                abastecimento.getId(),
                abastecimento.getBomba().getId(),
                abastecimento.getBomba().getNome(),
                abastecimento.getBomba().getCombustivel().getNome(),
                abastecimento.getData(),
                abastecimento.getLitros(),
                abastecimento.getPrecoLitro(),
                abastecimento.getValorTotal(),
                abastecimento.getCriadoEm());
    }
}
