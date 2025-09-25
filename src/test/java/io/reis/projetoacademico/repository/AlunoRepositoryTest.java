package io.reis.projetoacademico.repository;

import io.reis.projetoacademico.domain.Aluno;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AlunoRepositoryTest {

    @Autowired AlunoRepository repo;

    @Test
    void findByCpf_e_findByEmail() {
        var a = repo.save(Aluno.builder()
                .nome("A").cpf("11111111111").email("a@e.com").telefone("t").endereco("e").build());

        assertThat(repo.findByCpf("11111111111")).isPresent();
        assertThat(repo.findByEmail("a@e.com")).isPresent();
    }
}
