package io.reis.projetoacademico.api.dto;

public record AlunoResponse(
        Long id, String nome, String cpf, String email, String telefone, String endereco
) {}
