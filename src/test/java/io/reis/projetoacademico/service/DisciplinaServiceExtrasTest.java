package io.reis.projetoacademico.service;

import io.reis.projetoacademico.api.dto.DisciplinaRequest;
import io.reis.projetoacademico.domain.Disciplina;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.exception.RecursoNaoEncontradoException;
import io.reis.projetoacademico.repository.DisciplinaRepository;
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
class DisciplinaServiceExtrasTest {

    @Mock DisciplinaRepository repo;
    @InjectMocks DisciplinaService service;

    @Test
    void listar_ok() {
        when(repo.findAll()).thenReturn(List.of(Disciplina.builder().id(1L).codigo("POO202").nome("POO").build()));
        var out = service.listar();
        assertThat(out).hasSize(1);
    }

    @Test
    void buscarEntidade_404() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarEntidade(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void atualizar_ok() {
        var atual = Disciplina.builder().id(1L).codigo("POO202").nome("POO").build();
        when(repo.findById(1L)).thenReturn(Optional.of(atual));
        when(repo.findByCodigo("POO202")).thenReturn(Optional.of(atual));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var res = service.atualizar(1L, new DisciplinaRequest("POO202","POO II"));
        assertThat(res.nome()).isEqualTo("POO II");
    }

    @Test
    void atualizar_duplicado_codigo() {
        var atual = Disciplina.builder().id(1L).codigo("POO202").nome("POO").build();
        when(repo.findById(1L)).thenReturn(Optional.of(atual));
        // outra disciplina com o mesmo código
        when(repo.findByCodigo("POO202")).thenReturn(Optional.of(Disciplina.builder().id(2L).build()));

        assertThatThrownBy(() -> service.atualizar(1L, new DisciplinaRequest("POO202","X")))
                .isInstanceOf(NegocioException.class);
    }

    @Test
    void excluir_ok() {
        when(repo.findById(1L)).thenReturn(Optional.of(Disciplina.builder().id(1L).build()));
        service.excluir(1L);
        verify(repo).delete(any(Disciplina.class));
    }
}
