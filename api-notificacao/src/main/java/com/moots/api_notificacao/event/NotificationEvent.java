package com.moots.api_notificacao.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long postId;

    private Long userId;

    private String userTag;

    private String evento;

    private String myUserId;
}
