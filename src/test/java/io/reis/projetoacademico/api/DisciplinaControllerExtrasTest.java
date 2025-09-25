package io.reis.projetoacademico.api;

import io.reis.projetoacademico.api.controller.DisciplinaController;
import io.reis.projetoacademico.api.dto.DisciplinaRequest;
import io.reis.projetoacademico.config.SecurityConfig;
import io.reis.projetoacademico.exception.ApiExceptionHandler;
import io.reis.projetoacademico.exception.NegocioException;
import io.reis.projetoacademico.exception.RecursoNaoEncontradoException;
import io.reis.projetoacademico.service.DisciplinaService;
import io.reis.projetoacademico.service.MatriculaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DisciplinaController.class)
@Import({SecurityConfig.class, ApiExceptionHandler.class})
class DisciplinaControllerExtrasTest {

    @Autowired MockMvc mockMvc;
    @MockBean DisciplinaService discService;
    @MockBean MatriculaService matService;

    @Test
    void buscar_por_id_404() throws Exception {
        when(discService.buscar(99L)).thenThrow(new RecursoNaoEncontradoException("Disciplina não encontrada"));

        mockMvc.perform(get("/disciplinas/99").with(httpBasic("professor","senha123")))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("Disciplina não encontrada"));
    }

    @Test
    void atualizar_duplicado_codigo_400() throws Exception {
        when(discService.atualizar(eq(1L), any(DisciplinaRequest.class)))
                .thenThrow(new NegocioException("Código já cadastrado"));

        mockMvc.perform(put("/disciplinas/1").with(httpBasic("professor","senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"POO202\",\"nome\":\"POO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("Código já cadastrado"));
    }

    @Test
    void matricular_ok_e_duplicado_400() throws Exception {
        // sucesso
        when(matService.matricular(2L,1L)).thenReturn(10L);
        mockMvc.perform(post("/disciplinas/1/matriculas?alunoId=2").with(httpBasic("professor","senha123")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location","/matriculas/10"));

        // duplicado
        doThrow(new NegocioException("Aluno já matriculado nesta disciplina"))
                .when(matService).matricular(2L,1L);

        mockMvc.perform(post("/disciplinas/1/matriculas?alunoId=2").with(httpBasic("professor","senha123")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail", containsString("Aluno já matriculado")));
    }

    @Test
    void atribuir_nota_404_quando_matricula_inexistente() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Matrícula não encontrada"))
                .when(matService).atribuirNota(2L,1L,5.0);

        mockMvc.perform(put("/disciplinas/1/notas/2").with(httpBasic("professor","senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\":5.0}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").value("Matrícula não encontrada"));
    }

    @Test
    void validar_payload_invalido_disciplina() throws Exception {
        mockMvc.perform(post("/disciplinas").with(httpBasic("professor","senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"\",\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail", containsString("codigo")))
                .andExpect(jsonPath("$.detail", containsString("nome")));
    }

    @Test
    void validar_payload_invalido_nota() throws Exception {
        mockMvc.perform(put("/disciplinas/1/notas/2").with(httpBasic("professor","senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\": 20.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"));
    }

    @Test
    void sem_autenticacao_401() throws Exception {
        mockMvc.perform(get("/disciplinas"))
                .andExpect(status().isUnauthorized());
    }
}
