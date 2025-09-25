package io.reis.projetoacademico.repository;

import io.reis.projetoacademico.domain.Disciplina;
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
class DisciplinaRepositoryTest {

    @Autowired DisciplinaRepository repo;

    @Test
    void findByCodigo() {
        repo.save(Disciplina.builder().codigo("POO202").nome("POO II").build());
        assertThat(repo.findByCodigo("POO202")).isPresent();
    }
}
