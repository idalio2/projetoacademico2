package io.reis.projetoacademico;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.datasource.url=jdbc:h2:mem:academico_ctx_${random.uuid};DB_CLOSE_DELAY=-1;MODE=LEGACY",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.h2.console.enabled=false"
})
class ProjetoAcademicoApplicationTests {

    @Test
    void contextLoads() {
        // apenas valida subir o contexto
    }
}
