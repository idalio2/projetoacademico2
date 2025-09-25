package io.reis.projetoacademico.api;

import io.reis.projetoacademico.api.controller.DisciplinaController;
import io.reis.projetoacademico.api.dto.DisciplinaRequest;
import io.reis.projetoacademico.api.dto.DisciplinaResponse;
import io.reis.projetoacademico.api.dto.MatriculaResponse;
import io.reis.projetoacademico.config.SecurityConfig;
import io.reis.projetoacademico.domain.Aluno;
import io.reis.projetoacademico.service.DisciplinaService;
import io.reis.projetoacademico.service.MatriculaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DisciplinaController.class)
@Import(SecurityConfig.class)
class DisciplinaControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean DisciplinaService discService;
    @MockBean MatriculaService matService;

    @Test
    void criar_disciplina() throws Exception {
        when(discService.criar(any(DisciplinaRequest.class)))
                .thenReturn(new DisciplinaResponse(1L,"POO202","POO II"));

        mockMvc.perform(post("/disciplinas")
                        .with(httpBasic("professor","senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"POO202\",\"nome\":\"POO II\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location","/disciplinas/1"));
    }

    @Test
    void aprovados_reprovados_matriculas() throws Exception {
        when(matService.listarAprovados(1L)).thenReturn(List.of(new Aluno()));
        when(matService.listarReprovados(1L)).thenReturn(List.of());
        when(matService.listarPorDisciplina(1L)).thenReturn(List.of(
                new MatriculaResponse(10L,2L,1L,8.5)
        ));

        mockMvc.perform(get("/disciplinas/1/aprovados").with(httpBasic("professor","senha123")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/disciplinas/1/reprovados").with(httpBasic("professor","senha123")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/disciplinas/1/matriculas").with(httpBasic("professor","senha123")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("10")));
    }
}
