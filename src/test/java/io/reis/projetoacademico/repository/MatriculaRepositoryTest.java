package io.reis.projetoacademico.repository;

import io.reis.projetoacademico.domain.Aluno;
import io.reis.projetoacademico.domain.Disciplina;
import io.reis.projetoacademico.domain.Matricula;
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
class MatriculaRepositoryTest {

    @Autowired AlunoRepository alunoRepo;
    @Autowired DisciplinaRepository discRepo;
    @Autowired MatriculaRepository matRepo;

    @Test
    void deveListarAprovadosEReprovados() {
        var a1 = alunoRepo.save(Aluno.builder()
                .nome("A1").cpf("11111111111").email("a1@e.com").telefone("t").endereco("e").build());
        var a2 = alunoRepo.save(Aluno.builder()
                .nome("A2").cpf("22222222222").email("a2@e.com").telefone("t").endereco("e").build());

        var d  = discRepo.save(Disciplina.builder().codigo("TST100").nome("Teste").build());

        matRepo.save(Matricula.builder().aluno(a1).disciplina(d).nota(8.0).build()); // aprovado
        matRepo.save(Matricula.builder().aluno(a2).disciplina(d).nota(5.0).build()); // reprovado

        var aprovados  = matRepo.findAprovados(d.getId());
        var reprovados = matRepo.findReprovados(d.getId());

        assertThat(aprovados).hasSize(1);
        assertThat(reprovados).hasSize(1);
    }
}

