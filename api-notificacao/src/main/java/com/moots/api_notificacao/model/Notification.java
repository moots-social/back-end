package com.moots.api_notificacao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notification")
public class Notification {

    @Id
    private String notificationId;

    private Long postId;

    private Long userId;

    private String userTag;

    private String evento;

    private LocalDateTime dataCriacao;

    private String myUserId;

    private String fotoPerfil;
}
