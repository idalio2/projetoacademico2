package io.reis.projetoacademico.service;

import io.reis.projetoacademico.api.dto.AlunoRequest;
import io.reis.projetoacademico.domain.Aluno;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceExtrasTest {

    @Mock AlunoRepository repo;
    @InjectMocks AlunoService service;

    private AlunoRequest req() {
        return new AlunoRequest("Nome","12345678901","n@e.com","t","e");
    }

    @Test
    void atualizar_duplicado_cpf() {
        var atual = Aluno.builder().id(1L).cpf("12345678901").email("n@e.com")
                .nome("X").telefone("t").endereco("e").build();

        when(repo.findById(1L)).thenReturn(Optional.of(atual));
        // outro aluno com mesmo CPF -> causa exceção antes de checar e-mail
        when(repo.findByCpf("12345678901")).thenReturn(Optional.of(Aluno.builder().id(2L).build()));
        // NÃO stubar findByEmail aqui (não será chamado)

        assertThatThrownBy(() -> service.atualizar(1L, req()))
                .isInstanceOf(NegocioException.class).hasMessageContaining("CPF");

        verify(repo, never()).save(any());
    }

    @Test
    void atualizar_duplicado_email() {
        var atual = Aluno.builder().id(1L).cpf("12345678901").email("n@e.com")
                .nome("X").telefone("t").endereco("e").build();

        when(repo.findById(1L)).thenReturn(Optional.of(atual));
        // mesmo CPF do próprio registro -> ok
        when(repo.findByCpf("12345678901")).thenReturn(Optional.of(atual));
        // outro aluno usando o e-mail -> causa exceção
        when(repo.findByEmail("n@e.com")).thenReturn(Optional.of(Aluno.builder().id(3L).build()));

        assertThatThrownBy(() -> service.atualizar(1L, req()))
                .isInstanceOf(NegocioException.class).hasMessageContaining("E-mail");

        verify(repo, never()).save(any());
    }
}
