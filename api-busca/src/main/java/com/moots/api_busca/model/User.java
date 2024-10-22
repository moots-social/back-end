package com.moots.api_busca.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "user")
public class User {

    @Id
    private String id;

    private Long postId;

    private String userId;

    private String nomeCompleto;

    private String tag;

    private String fotoPerfil;

    private String curso;
}
