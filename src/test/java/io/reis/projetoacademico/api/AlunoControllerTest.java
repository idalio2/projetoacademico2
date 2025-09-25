package io.reis.projetoacademico.api;

import io.reis.projetoacademico.api.controller.AlunoController;
import io.reis.projetoacademico.api.dto.AlunoRequest;
import io.reis.projetoacademico.api.dto.AlunoResponse;
import io.reis.projetoacademico.config.SecurityConfig;
import io.reis.projetoacademico.service.AlunoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AlunoController.class)
@Import(SecurityConfig.class)
class AlunoControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean AlunoService alunoService;

    @Test
    void deveCriarAlunoComAutenticacao() throws Exception {
        when(alunoService.criar(any(AlunoRequest.class)))
                .thenReturn(new AlunoResponse(1L,"Nome","12345678901","n@e.com","t","e"));

        mockMvc.perform(post("/alunos")
                        .with(httpBasic("professor","senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Nome\",\"cpf\":\"12345678901\",\"email\":\"n@e.com\",\"telefone\":\"t\",\"endereco\":\"e\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/alunos/1"));
    }
}
