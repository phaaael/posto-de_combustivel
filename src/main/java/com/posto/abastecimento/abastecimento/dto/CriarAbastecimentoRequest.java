package com.posto.abastecimento.abastecimento.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CriarAbastecimentoRequest(
        @NotNull Long bombaId,
        @NotNull LocalDateTime data,
        @NotNull @DecimalMin("0.001") BigDecimal litros
) {
}
