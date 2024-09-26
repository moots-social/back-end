package com.api.usuario_post.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class ResetPasswordDTO {
    @NotBlank(message = "Senha antiga obrigatório")
    private String senhaAntiga;

    @NotBlank(message = "Senha nova obrigatório")
    private String senhaNova;

    public ResetPasswordDTO(String senhaAntiga, String senhaNova) {
        this.senhaAntiga = senhaAntiga;
        this.senhaNova = senhaNova;
    }
}
