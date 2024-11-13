package com.moots.api_busca.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "post")
public class Post {

    @Id
    private String id;

    private Long postId;

    private String userId;

    private String nomeCompleto;

    private String tag;

    private String fotoPerfil;

    private String texto;

    private List<String> listImagens;

    private Integer contadorLike;

    private Integer contadorDeslike;

    private List<String> likeUsers = new ArrayList<>();
}