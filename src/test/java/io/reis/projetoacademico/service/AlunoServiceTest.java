package io.reis.projetoacademico.service;

import io.reis.projetoacademico.api.dto.AlunoRequest;
import io.reis.projetoacademico.domain.Aluno;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.exception.RecursoNaoEncontradoException;
import io.reis.projetoacademico.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock AlunoRepository repo;
    @InjectMocks AlunoService service;

    private AlunoRequest req() {
        return new AlunoRequest("Marina","12345678901","m@e.com","t","e");
    }

    @Test
    void criar_ok() {
        when(repo.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(repo.findByEmail("m@e.com")).thenReturn(Optional.empty());
        when(repo.save(any(Aluno.class))).thenAnswer(inv -> {
            Aluno a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        var res = service.criar(req());
        assertThat(res.id()).isEqualTo(1L);
        verify(repo).save(any(Aluno.class));
    }

    @Test
    void criar_duplicado_cpf() {
        when(repo.findByCpf("12345678901")).thenReturn(Optional.of(new Aluno()));
        assertThatThrownBy(() -> service.criar(req()))
                .isInstanceOf(NegocioException.class).hasMessageContaining("CPF");
    }

    @Test
    void criar_duplicado_email() {
        when(repo.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(repo.findByEmail("m@e.com")).thenReturn(Optional.of(new Aluno()));
        assertThatThrownBy(() -> service.criar(req()))
                .isInstanceOf(NegocioException.class).hasMessageContaining("E-mail");
    }

    @Test
    void listar_ok() {
        when(repo.findAll()).thenReturn(List.of(
                Aluno.builder().id(1L).nome("A").cpf("11111111111").email("a@e.com").telefone("t").endereco("e").build()
        ));
        var lista = service.listar();
        assertThat(lista).hasSize(1);
    }

    @Test
    void buscarEntidade_nao_encontrado() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarEntidade(9L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void atualizar_ok() {
        var req = new AlunoRequest("Novo","12345678901","m@e.com","t2","e2");
        var atual = Aluno.builder().id(1L).nome("Old").cpf("12345678901").email("m@e.com").telefone("t").endereco("e").build();
        when(repo.findById(1L)).thenReturn(Optional.of(atual));
        when(repo.findByCpf("12345678901")).thenReturn(Optional.of(atual));
        when(repo.findByEmail("m@e.com")).thenReturn(Optional.of(atual));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var res = service.atualizar(1L, req);
        assertThat(res.nome()).isEqualTo("Novo");
        verify(repo).save(any());
    }

    @Test
    void excluir_ok() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Aluno()));
        service.excluir(1L);
        verify(repo).delete(any(Aluno.class));
    }
}
