package com.moots.api_busca.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElasticEvent implements Serializable {

    private String userId;

    private Long postId;

    private String nomeCompleto;

    private String tag;

    private String fotoPerfil;

    private String texto;

    private List<String> listImagens;

    private Integer contadorLike;

    private Integer contadorDeslike;

    private String curso;

    private List<String> likeUsers = new ArrayList<>();

}