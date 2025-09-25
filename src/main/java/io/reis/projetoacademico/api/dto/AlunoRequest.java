package io.reis.projetoacademico.api.dto;

import jakarta.validation.constraints.*;

public record AlunoRequest(
        @NotBlank String nome,
        @NotBlank @Size(min = 11, max = 11) String cpf,
        @NotBlank @Email String email,
        @NotBlank String telefone,
        @NotBlank String endereco
) {}
