package com.posto.abastecimento.bomba.dto;

import com.posto.abastecimento.bomba.Bomba;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BombaResponse(
        Long id,
        String nome,
        Long combustivelId,
        String combustivelNome,
        BigDecimal precoLitro,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static BombaResponse from(Bomba bomba) {
        return new BombaResponse(
                bomba.getId(),
                bomba.getNome(),
                bomba.getCombustivel().getId(),
                bomba.getCombustivel().getNome(),
                bomba.getCombustivel().getPrecoLitro(),
                bomba.getCriadoEm(),
                bomba.getAtualizadoEm());
    }
}
