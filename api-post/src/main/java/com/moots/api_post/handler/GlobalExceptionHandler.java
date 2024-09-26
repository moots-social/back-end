package com.moots.api_post.handler;

import jakarta.annotation.Resource;
import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// @RestControllerAdvice é usada para lidar com exceções de maneira global na aplicação, interceptando as exceções
// lançadas por controladores e retornando respostas adequadas.
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Injeta o MessageSource, que é usado para buscar mensagens de erro internacionalizadas (i18n).
    @Resource
    private MessageSource messageSource;

    // Metodo que cria e retorna cabeçalhos HTTP, configurando o tipo de mídia para JSON.
    private HttpHeaders headers(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Metodo que cria e retorna um objeto ResponseError, encapsulando informações de erro,
    // como a mensagem e o código de status HTTP.
    private ResponseError responseError(String message, HttpStatus statusCode){
        ResponseError responseError = new ResponseError();
        responseError.setStatus("error");
        responseError.setError(message);
        responseError.setStatusCode(statusCode.value());
        return responseError;
    }

    // Trata exceções gerais (Exception.class). O metodo verifica se a exceção é uma instância de UndeclaredThrowableException,
    // e, se for, trata como uma BusinessException. Caso contrário, retorna uma mensagem de erro genérica.
    @ExceptionHandler(Exception.class)
    private ResponseEntity<Object> handleGeneral(Exception e, WebRequest request) {
        if (e instanceof UndeclaredThrowableException) {
            Throwable cause = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
            if (cause instanceof BusinessException) {
                return handleBusinessException((BusinessException) cause, request);
            }
        }
        String message = messageSource.getMessage("error.server", new Object[]{e.getMessage()}, null);
        ResponseError error = responseError(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return handleExceptionInternal(e, error, headers(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // Trata exceções do tipo BusinessException, retornando uma resposta HTTP com status de conflito (409).
    @ExceptionHandler(BusinessException.class)
    private ResponseEntity<Object> handleBusinessException(BusinessException e, WebRequest request) {
        ResponseError error = responseError(e.getMessage(), HttpStatus.CONFLICT);
        return handleExceptionInternal(e, error, headers(), HttpStatus.CONFLICT, request);
    }

}
