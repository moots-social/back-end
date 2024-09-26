package com.moots.api_post.service;

import com.moots.api_post.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserEventService {

    private static UserEvent userEvent;

    @Autowired
    private UserService userService;

    public static void setUserEvent(UserEvent event) {
        userEvent = event;
    }

    public UserEvent getUserEvent() {
        return userEvent;
    }

    @KafkaListener(topics = "user-logado-topic")
    public void saveUser(UserEvent userEvent) {
        log.info("Evento recebido: " + userEvent);
        userService.saveUser(userEvent);
        log.info("User salvo com sucesso");
    }

}



