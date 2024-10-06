package com.api.usuario_post.dto;

import java.util.List;

public record PostDTO(Long postId, String texto, List<String> listImagens, Integer contadorLike, Integer contadorDeslike) {
}
