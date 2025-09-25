package io.reis.projetoacademico.service;

import io.reis.projetoacademico.api.dto.DisciplinaRequest;
import io.reis.projetoacademico.api.dto.DisciplinaResponse;
import io.reis.projetoacademico.domain.Disciplina;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.exception.RecursoNaoEncontradoException;
import io.reis.projetoacademico.repository.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class DisciplinaService {
    private final DisciplinaRepository disciplinaRepository;

    @Transactional
    public DisciplinaResponse criar(DisciplinaRequest req) {
        disciplinaRepository.findByCodigo(req.codigo())
                .ifPresent(d -> { throw new NegocioException("Código já cadastrado"); });
        var disc = disciplinaRepository.save(Disciplina.builder()
                .codigo(req.codigo())
                .nome(req.nome())
                .build());
        return toResponse(disc);
    }

    @Transactional(readOnly = true)
    public List<DisciplinaResponse> listar() {
        return disciplinaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Disciplina buscarEntidade(Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada"));
    }

    @Transactional(readOnly = true)
    public DisciplinaResponse buscar(Long id) { return toResponse(buscarEntidade(id)); }

    @Transactional
    public DisciplinaResponse atualizar(Long id, DisciplinaRequest req) {
        var atual = buscarEntidade(id);
        disciplinaRepository.findByCodigo(req.codigo()).filter(d -> !d.getId().equals(id))
                .ifPresent(d -> { throw new NegocioException("Código já cadastrado"); });
        atual.setCodigo(req.codigo());
        atual.setNome(req.nome());
        return toResponse(disciplinaRepository.save(atual));
    }

    @Transactional
    public void excluir(Long id) {
        var atual = buscarEntidade(id);
        disciplinaRepository.delete(atual);
    }

    public DisciplinaResponse toResponse(Disciplina d) {
        return new DisciplinaResponse(d.getId(), d.getCodigo(), d.getNome());
    }
}
