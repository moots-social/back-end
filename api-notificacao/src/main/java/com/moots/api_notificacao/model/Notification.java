package com.moots.api_notificacao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private String myUserId;
}
