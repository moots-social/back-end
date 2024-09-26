package com.moots.api_notificacao.controller;

import com.moots.api_notificacao.event.NotificationEvent;
import com.moots.api_notificacao.model.Notification;
import com.moots.api_notificacao.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/notification")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "notification-topic")
    public void save(NotificationEvent notificationEvent){
        System.out.println("Mensagem recebida " + notificationEvent);
        notificationService.saveNotification(notificationEvent);
        log.info("Mensagem salva com sucesso");
    }

    @DeleteMapping("/deletar-notificacao/{id}")
    public ResponseEntity<Optional<Notification>> deletarNotification(@PathVariable String id){
        var likeNotification = notificationService.deleteById(id);
        return ResponseEntity.ok().body(likeNotification);
    }

    @GetMapping("/{myUserId}")
    public ResponseEntity<List<Notification>> acharNotificacaoPorUserId(@PathVariable String myUserId){
        var notifications = notificationService.findByMyUserId(myUserId);
        return ResponseEntity.ok().body(notifications);
    }

}
