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

    @NonNull
    private Long postId;

    @NonNull
    private String userId;

    @NonNull
    private String nomeCompleto;

    @NonNull
    private String tag;

    @NonNull
    private String fotoPerfil;
}
