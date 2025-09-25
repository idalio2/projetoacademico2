package io.reis.projetoacademico.service;

import io.reis.projetoacademico.domain.Aluno;
import io.reis.projetoacademico.domain.Disciplina;
import io.reis.projetoacademico.domain.Matricula;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.exception.RecursoNaoEncontradoException;
import io.reis.projetoacademico.repository.MatriculaRepository;
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
class MatriculaServiceTest {

    @Mock MatriculaRepository repo;
    @Mock AlunoService alunoService;
    @Mock DisciplinaService discService;
    @InjectMocks MatriculaService service;

    @Test
    void matricular_ok() {
        when(repo.findByAlunoIdAndDisciplinaId(2L,1L)).thenReturn(Optional.empty());
        when(alunoService.buscarEntidade(2L)).thenReturn(Aluno.builder().id(2L).build());
        when(discService.buscarEntidade(1L)).thenReturn(Disciplina.builder().id(1L).build());
        when(repo.save(any(Matricula.class))).thenAnswer(inv -> {
            var m = (Matricula) inv.getArgument(0);
            m.setId(10L);
            return m;
        });

        var id = service.matricular(2L,1L);
        assertThat(id).isEqualTo(10L);
    }

    @Test
    void matricular_duplicado() {
        when(repo.findByAlunoIdAndDisciplinaId(2L,1L)).thenReturn(Optional.of(new Matricula()));
        assertThatThrownBy(() -> service.matricular(2L,1L))
                .isInstanceOf(NegocioException.class);
    }

    @Test
    void atribuirNota_ok() {
        Matricula m = Matricula.builder().id(5L).aluno(Aluno.builder().id(2L).build())
                .disciplina(Disciplina.builder().id(1L).build()).nota(null).build();
        when(repo.findByAlunoIdAndDisciplinaId(2L,1L)).thenReturn(Optional.of(m));
        when(repo.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        service.atribuirNota(2L,1L,8.7);
        assertThat(m.getNota()).isEqualTo(8.7);
    }

    @Test
    void atribuirNota_matriculaNaoEncontrada() {
        when(repo.findByAlunoIdAndDisciplinaId(2L,1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.atribuirNota(2L,1L,6.0))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void listarAprovados_e_Reprovados() {
        when(repo.findAprovados(1L)).thenReturn(List.of(Aluno.builder().id(2L).build()));
        when(repo.findReprovados(1L)).thenReturn(List.of(Aluno.builder().id(3L).build()));

        assertThat(service.listarAprovados(1L)).hasSize(1);
        assertThat(service.listarReprovados(1L)).hasSize(1);
    }

    @Test
    void listarPorDisciplina_ok() {
        when(repo.findByDisciplinaId(1L)).thenReturn(List.of(
                Matricula.builder().id(7L).aluno(Aluno.builder().id(2L).build())
                        .disciplina(Disciplina.builder().id(1L).build()).nota(8.0).build()
        ));
        var res = service.listarPorDisciplina(1L);
        assertThat(res).extracting("id").containsExactly(7L);
    }
}
