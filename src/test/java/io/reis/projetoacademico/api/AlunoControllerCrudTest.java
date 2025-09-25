package io.reis.projetoacademico.api;

import io.reis.projetoacademico.api.controller.AlunoController;
import io.reis.projetoacademico.api.dto.AlunoRequest;
import io.reis.projetoacademico.api.dto.AlunoResponse;
import io.reis.projetoacademico.config.SecurityConfig;
import io.reis.projetoacademico.exception.ApiExceptionHandler;
import io.reis.projetoacademico.service.AlunoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AlunoController.class)
@Import({SecurityConfig.class, ApiExceptionHandler.class})
class AlunoControllerCrudTest {

    @Autowired MockMvc mockMvc;
    @MockBean AlunoService alunoService;

    // GET /alunos (listar)
    @Test
    void listar_alunos_ok() throws Exception {
        when(alunoService.listar()).thenReturn(List.of(
                new AlunoResponse(1L,"Marina","11111111111","m@e.com","t","e"),
                new AlunoResponse(2L,"Rafael","22222222222","r@e.com","t2","e2")
        ));

        mockMvc.perform(get("/alunos").with(httpBasic("professor","senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    // GET /alunos/{id}
    @Test
    void buscar_aluno_por_id_ok() throws Exception {
        when(alunoService.buscar(10L))
                .thenReturn(new AlunoResponse(10L,"Marina","11111111111","m@e.com","t","e"));

        mockMvc.perform(get("/alunos/10").with(httpBasic("professor","senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("Marina"));
    }

    // PUT /alunos/{id}
    @Test
    void atualizar_aluno_ok() throws Exception {
        when(alunoService.atualizar(eq(5L), any(AlunoRequest.class)))
                .thenReturn(new AlunoResponse(5L,"Novo Nome","11111111111","n@e.com","t2","e2"));

        mockMvc.perform(put("/alunos/5").with(httpBasic("professor","senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
          {
            "nome":"Novo Nome",
            "cpf":"11111111111",
            "email":"n@e.com",
            "telefone":"t2",
            "endereco":"e2"
          }
        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"));
    }

    // DELETE /alunos/{id}
    @Test
    void deletar_aluno_ok() throws Exception {
        doNothing().when(alunoService).excluir(7L);

        mockMvc.perform(delete("/alunos/7").with(httpBasic("professor","senha123")))
                .andExpect(status().isNoContent());

        verify(alunoService).excluir(7L);
    }

    // VALIDAÇÃO: POST /alunos com payload inválido -> 400 + detalhes
    @Test
    void criar_aluno_validacao_campos_invalidos() throws Exception {
        // nome em branco, cpf curto, email inválido, telefone/endereco em branco
        mockMvc.perform(post("/alunos").with(httpBasic("professor","senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
          {
            "nome":"",
            "cpf":"123",
            "email":"nao-e-email",
            "telefone":"",
            "endereco":""
          }
        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("cpf")))
                .andExpect(content().string(containsString("email")))
                .andExpect(content().string(containsString("nome")))
                .andExpect(content().string(containsString("telefone")))
                .andExpect(content().string(containsString("endereco")));
    }
}
