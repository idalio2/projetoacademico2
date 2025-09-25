package io.reis.projetoacademico.service;

import io.reis.projetoacademico.api.dto.DisciplinaRequest;
import io.reis.projetoacademico.domain.Disciplina;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.repository.DisciplinaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisciplinaServiceTest {

    @Mock DisciplinaRepository repo;
    @InjectMocks DisciplinaService service;

    @Test
    void criar_ok() {
        when(repo.findByCodigo("POO202")).thenReturn(Optional.empty());
        when(repo.save(any(Disciplina.class))).thenAnswer(inv -> {
            var d = (Disciplina) inv.getArgument(0);
            d.setId(1L);
            return d;
        });

        var res = service.criar(new DisciplinaRequest("POO202","POO II"));
        assertThat(res.id()).isEqualTo(1L);
    }

    @Test
    void criar_duplicado_codigo() {
        when(repo.findByCodigo("POO202")).thenReturn(Optional.of(new Disciplina()));
        assertThatThrownBy(() -> service.criar(new DisciplinaRequest("POO202","x")))
                .isInstanceOf(NegocioException.class);
    }
}
