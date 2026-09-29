package com.posto.abastecimento.combustivel.dto;

import com.posto.abastecimento.combustivel.Combustivel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CombustivelResponse(
        Long id,
        String nome,
        BigDecimal precoLitro,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static CombustivelResponse from(Combustivel combustivel) {
        return new CombustivelResponse(
                combustivel.getId(),
                combustivel.getNome(),
                combustivel.getPrecoLitro(),
                combustivel.getCriadoEm(),
                combustivel.getAtualizadoEm());
    }
}
