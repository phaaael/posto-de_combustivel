package com.posto.abastecimento.bomba.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarBombaRequest(
        @NotBlank String nome,
        @NotNull Long combustivelId
) {
}
