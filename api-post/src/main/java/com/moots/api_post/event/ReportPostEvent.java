package com.moots.api_post.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    private Long postId;

    @NonNull
    private String denuncia;

    private Integer contadorDenuncia;
}