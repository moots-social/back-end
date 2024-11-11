package com.moots.api_post.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deslike {

    private Long userId;
    private Long postId;
    private Integer contadorDeslike;
    private boolean deslike;
}
