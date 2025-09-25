package io.reis.projetoacademico.service;

import io.reis.projetoacademico.api.dto.AlunoRequest;
import io.reis.projetoacademico.api.dto.AlunoResponse;
import io.reis.projetoacademico.domain.Aluno;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.exception.RecursoNaoEncontradoException;
import io.reis.projetoacademico.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class AlunoService {
    private final AlunoRepository alunoRepository;

    @Transactional
    public AlunoResponse criar(AlunoRequest req) {
        alunoRepository.findByCpf(req.cpf()).ifPresent(a -> { throw new NegocioException("CPF já cadastrado"); });
        alunoRepository.findByEmail(req.email()).ifPresent(a -> { throw new NegocioException("E-mail já cadastrado"); });

        var aluno = alunoRepository.save(Aluno.builder()
                .nome(req.nome())
                .cpf(req.cpf())
                .email(req.email())
                .telefone(req.telefone())
                .endereco(req.endereco())
                .build());
        return toResponse(aluno);
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> listar() {
        return alunoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Aluno buscarEntidade(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado"));
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscar(Long id) { return toResponse(buscarEntidade(id)); }

    @Transactional
    public AlunoResponse atualizar(Long id, AlunoRequest req) {
        var atual = buscarEntidade(id);

        alunoRepository.findByCpf(req.cpf()).filter(a -> !a.getId().equals(id))
                .ifPresent(a -> { throw new NegocioException("CPF já cadastrado"); });
        alunoRepository.findByEmail(req.email()).filter(a -> !a.getId().equals(id))
                .ifPresent(a -> { throw new NegocioException("E-mail já cadastrado"); });

        atual.setNome(req.nome());
        atual.setCpf(req.cpf());
        atual.setEmail(req.email());
        atual.setTelefone(req.telefone());
        atual.setEndereco(req.endereco());

        return toResponse(alunoRepository.save(atual));
    }

    @Transactional
    public void excluir(Long id) {
        var atual = buscarEntidade(id);
        alunoRepository.delete(atual);
    }

    private AlunoResponse toResponse(Aluno a) {
        return new AlunoResponse(a.getId(), a.getNome(), a.getCpf(), a.getEmail(), a.getTelefone(), a.getEndereco());
    }
}
