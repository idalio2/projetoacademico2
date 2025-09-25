package io.reis.projetoacademico.api.dto;

import jakarta.validation.constraints.*;

public record NotaDTO(
        @NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double nota
) {}
