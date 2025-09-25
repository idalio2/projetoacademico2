package io.reis.projetoacademico.service;

import io.reis.projetoacademico.api.dto.MatriculaResponse;
import io.reis.projetoacademico.domain.Matricula;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.exception.RecursoNaoEncontradoException;
import io.reis.projetoacademico.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class MatriculaService {
    private final MatriculaRepository matriculaRepository;
    private final AlunoService alunoService;
    private final DisciplinaService disciplinaService;

    @Transactional
    public Long matricular(Long alunoId, Long disciplinaId) {
        matriculaRepository.findByAlunoIdAndDisciplinaId(alunoId, disciplinaId)
                .ifPresent(m -> { throw new NegocioException("Aluno já matriculado nesta disciplina"); });

        var aluno = alunoService.buscarEntidade(alunoId);
        var disc = disciplinaService.buscarEntidade(disciplinaId);

        var mat = matriculaRepository.save(Matricula.builder()
                .aluno(aluno)
                .disciplina(disc)
                .nota(null)
                .build());
        return mat.getId();
    }

    @Transactional
    public void atribuirNota(Long alunoId, Long disciplinaId, Double nota) {
        var mat = matriculaRepository.findByAlunoIdAndDisciplinaId(alunoId, disciplinaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matrícula não encontrada"));
        mat.setNota(nota);
        matriculaRepository.save(mat);
    }

    @Transactional(readOnly = true)
    public List<io.reis.projetoacademico.domain.Aluno> listarAprovados(Long disciplinaId) {
        return matriculaRepository.findAprovados(disciplinaId);
    }

    @Transactional(readOnly = true)
    public List<io.reis.projetoacademico.domain.Aluno> listarReprovados(Long disciplinaId) {
        return matriculaRepository.findReprovados(disciplinaId);
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarPorDisciplina(Long disciplinaId) {
        return matriculaRepository.findByDisciplinaId(disciplinaId).stream()
                .map(m -> new MatriculaResponse(m.getId(), m.getAluno().getId(), m.getDisciplina().getId(), m.getNota()))
                .toList();
    }
}
