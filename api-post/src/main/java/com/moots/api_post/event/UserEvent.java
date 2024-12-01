package com.moots.api_post.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent implements Serializable {

    private String type;

    private Long userId;

    private String nomeCompleto;

    private String tag;

    private String fotoPerfil;
}

