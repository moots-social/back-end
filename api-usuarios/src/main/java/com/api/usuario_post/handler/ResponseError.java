package com.api.usuario_post.handler;
import java.util.Date;

// ESTA CLASSE É COMO SE FOSSE A MODEL DE UMA EXCEÇÃO. Utilizamos no GlobalExceptionHandler.
public class ResponseError {
    // Armazena o timestamp (data e hora) em que o erro ocorreu.
    private Date timestamp = new Date();

    // Armazena o status da resposta, que geralmente será "error".
    private String status = "error";

    // Armazena o código de status HTTP. O padrão é 400 (Bad Request).
    private int statusCode = 400;

    // Armazena a mensagem de erro.
    private String error;

    // Métodos getters e setters para acessar e modificar as propriedades acima.
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}