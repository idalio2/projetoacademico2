package io.reis.projetoacademico.repository;

import io.reis.projetoacademico.domain.Matricula;
import io.reis.projetoacademico.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    Optional<Matricula> findByAlunoIdAndDisciplinaId(Long alunoId, Long disciplinaId);

    @Query("select m.aluno from Matricula m where m.disciplina.id = :disciplinaId and m.nota >= 7")
    List<Aluno> findAprovados(Long disciplinaId);

    @Query("select m.aluno from Matricula m where m.disciplina.id = :disciplinaId and m.nota < 7")
    List<Aluno> findReprovados(Long disciplinaId);

    List<Matricula> findByDisciplinaId(Long disciplinaId);
    List<Matricula> findByAlunoId(Long alunoId);
}
