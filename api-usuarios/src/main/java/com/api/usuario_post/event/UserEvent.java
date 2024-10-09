package com.api.usuario_post.event;

import com.api.usuario_post.model.Curso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent implements Serializable {

    @NonNull
    private String type;

    @NonNull
    private Long userId;

    @NonNull
    private String nomeCompleto;

    @NonNull
    private String tag;

    private String fotoPerfil;


}
