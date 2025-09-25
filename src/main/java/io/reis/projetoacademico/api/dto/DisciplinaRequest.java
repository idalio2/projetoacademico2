package io.reis.projetoacademico.api.dto;

import jakarta.validation.constraints.*;

public record DisciplinaRequest(
        @NotBlank String codigo,
        @NotBlank String nome
) {}
