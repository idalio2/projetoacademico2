package io.reis.projetoacademico.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "disciplina_id"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Matricula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Aluno aluno;

    @ManyToOne(optional = false)
    private Disciplina disciplina;

    @DecimalMin("0.0") @DecimalMax("10.0")
    private Double nota; // pode iniciar null
}
