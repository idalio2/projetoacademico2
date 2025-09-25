package io.reis.projetoacademico.api.controller;

import io.reis.projetoacademico.api.dto.*;
import io.reis.projetoacademico.domain.Aluno;
import io.reis.projetoacademico.service.DisciplinaService;
import io.reis.projetoacademico.service.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/disciplinas")
@RequiredArgsConstructor
public class DisciplinaController {
    private final DisciplinaService disciplinaService;
    private final MatriculaService matriculaService;

    @PostMapping
    public ResponseEntity<DisciplinaResponse> criar(@RequestBody @Validated DisciplinaRequest req) {
        var salvo = disciplinaService.criar(req);
        return ResponseEntity.created(URI.create("/disciplinas/" + salvo.id())).body(salvo);
    }

    @GetMapping
    public List<DisciplinaResponse> listar() { return disciplinaService.listar(); }

    @GetMapping("/{id}")
    public DisciplinaResponse buscar(@PathVariable Long id) { return disciplinaService.buscar(id); }

    @PutMapping("/{id}")
    public DisciplinaResponse atualizar(@PathVariable Long id, @RequestBody @Validated DisciplinaRequest req) {
        return disciplinaService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        disciplinaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // Matrículas e Notas
    @PostMapping("/{disciplinaId}/matriculas")
    public ResponseEntity<Void> matricular(@PathVariable Long disciplinaId, @RequestParam Long alunoId) {
        var id = matriculaService.matricular(alunoId, disciplinaId);
        return ResponseEntity.created(URI.create("/matriculas/" + id)).build();
    }

    @PutMapping("/{disciplinaId}/notas/{alunoId}")
    public ResponseEntity<Void> atribuirNota(@PathVariable Long disciplinaId, @PathVariable Long alunoId,
                                             @RequestBody @Validated NotaDTO dto) {
        matriculaService.atribuirNota(alunoId, disciplinaId, dto.nota());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{disciplinaId}/aprovados")
    public List<Aluno> aprovados(@PathVariable Long disciplinaId) {
        return matriculaService.listarAprovados(disciplinaId);
    }

    @GetMapping("/{disciplinaId}/reprovados")
    public List<Aluno> reprovados(@PathVariable Long disciplinaId) {
        return matriculaService.listarReprovados(disciplinaId);
    }

    @GetMapping("/{disciplinaId}/matriculas")
    public List<MatriculaResponse> matriculas(@PathVariable Long disciplinaId) {
        return matriculaService.listarPorDisciplina(disciplinaId);
    }
}
