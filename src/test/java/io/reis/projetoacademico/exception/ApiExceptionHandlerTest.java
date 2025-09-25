package io.reis.projetoacademico.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleNotFound_retorna404() {
        var pd = handler.handleNotFound(new RecursoNaoEncontradoException("Aluno não encontrado"));
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd.getDetail()).contains("Aluno não encontrado");
    }

    @Test
    void handleBusiness_retorna400() {
        var pd = handler.handleBusiness(new NegocioException("CPF já cadastrado"));
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getDetail()).contains("CPF já cadastrado");
    }

    @Test
    void handleConflict_retorna409() {
        var pd = handler.handleConflict(new DataIntegrityViolationException("unique"));
        assertThat(pd.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(pd.getDetail()).contains("integridade de dados");
    }

    @Test
    void handleValidation_retorna400_comDetalhesDosCampos() throws NoSuchMethodException {
        // cria um BindingResult com erros simulados
        var target = new Object();
        var binding = new BeanPropertyBindingResult(target, "alunoRequest");
        binding.addError(new FieldError("alunoRequest", "cpf", "size must be 11"));
        binding.addError(new FieldError("alunoRequest", "email", "must be a well-formed email address"));

        // precisa de um MethodParameter qualquer (dummy) para construir a exceção
        Method m = this.getClass().getDeclaredMethod("dummy", String.class);
        MethodParameter mp = new MethodParameter(m, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mp, binding);

        ProblemDetail pd = handler.handleValidation(ex);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getDetail()).contains("cpf").contains("email");
    }

    // usado apenas para gerar um MethodParameter dummy
    @SuppressWarnings("unused")
    private void dummy(String s) {}
}
