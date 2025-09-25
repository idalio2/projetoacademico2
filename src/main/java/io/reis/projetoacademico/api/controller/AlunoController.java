package io.reis.projetoacademico.api.controller;

import io.reis.projetoacademico.api.dto.AlunoRequest;
import io.reis.projetoacademico.api.dto.AlunoResponse;
import io.reis.projetoacademico.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {
    private final AlunoService alunoService;

    @PostMapping
    public ResponseEntity<AlunoResponse> criar(@RequestBody @Validated AlunoRequest req) {
        var salvo = alunoService.criar(req);
        return ResponseEntity.created(URI.create("/alunos/" + salvo.id())).body(salvo);
    }

    @GetMapping
    public List<AlunoResponse> listar() { return alunoService.listar(); }

    @GetMapping("/{id}")
    public AlunoResponse buscar(@PathVariable Long id) { return alunoService.buscar(id); }

    @PutMapping("/{id}")
    public AlunoResponse atualizar(@PathVariable Long id, @RequestBody @Validated AlunoRequest req) {
        return alunoService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
