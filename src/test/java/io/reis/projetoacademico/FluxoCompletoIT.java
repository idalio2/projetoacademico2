package io.reis.projetoacademico;

import io.reis.projetoacademico.api.dto.AlunoRequest;
import io.reis.projetoacademico.api.dto.DisciplinaRequest;
import io.reis.projetoacademico.api.dto.NotaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",              // evita rodar seu data.sql aqui
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FluxoCompletoIT {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;

    private HttpHeaders authJson(){
        HttpHeaders h = new HttpHeaders();
        h.setBasicAuth("professor","senha123");
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    @Test
    void fluxo_completo() {
        var base = "http://localhost:" + port;

        var a1 = rest.exchange(base+"/alunos", HttpMethod.POST,
                new HttpEntity<>(new AlunoRequest("Marina","12345678901","m@e.com","t","e"), authJson()), String.class);
        assertThat(a1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var d1 = rest.exchange(base+"/disciplinas", HttpMethod.POST,
                new HttpEntity<>(new DisciplinaRequest("POO202","POO II"), authJson()), String.class);
        assertThat(d1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var mat = rest.exchange(base+"/disciplinas/1/matriculas?alunoId=1", HttpMethod.POST,
                new HttpEntity<>(authJson()), String.class);
        assertThat(mat.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var nota = rest.exchange(base+"/disciplinas/1/notas/1", HttpMethod.PUT,
                new HttpEntity<>(new NotaDTO(8.0), authJson()), String.class);
        assertThat(nota.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
