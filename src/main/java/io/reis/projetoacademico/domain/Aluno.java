package io.reis.projetoacademico.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Aluno {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    @Column(unique = true, length = 11)
    private String cpf;

    @NotBlank @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String telefone;

    @NotBlank
    private String endereco;
}
