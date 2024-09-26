package com.api.usuario_post.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColecaoPostEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NonNull
    private Long userId;

    @NonNull
    private Long postId;

    @NonNull
    private String texto;

    @NonNull
    private List<String> listImagens;
}