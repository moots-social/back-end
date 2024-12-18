package com.moots.api_notificacao.service;

import com.moots.api_notificacao.event.NotificationEvent;
import com.moots.api_notificacao.model.Notification;
import com.moots.api_notificacao.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification saveNotification(NotificationEvent notificationEvent){
        var notification = new Notification();

        notification.setUserId(notificationEvent.getUserId());
        notification.setEvento(notificationEvent.getEvento());
        notification.setPostId(notificationEvent.getPostId());
        notification.setUserTag(notificationEvent.getUserTag());
        notification.setMyUserId(notificationEvent.getMyUserId());
        notification.setDataCriacao(LocalDateTime.now());
        notification.setFotoPerfil(notificationEvent.getFotoPerfil());

        Notification notificationSalva = notificationRepository.save(notification);
        atualizarCache(notificationSalva.getMyUserId());
        return notificationSalva;
    }

    @CacheEvict(cacheNames = "notification", key = "#myUserId")
    public Optional<Notification> deleteById(String id){
        var notification = notificationRepository.findById(id);
        invalidarCache(notification.get().getMyUserId());
        notificationRepository.deleteById(id);
        return notification;
    }

    @Cacheable(value = "notification", key = "#myUserId")
    public List<Notification> findByMyUserId(String myUserId){
        return notificationRepository.findByMyUserIdOrderByDataCriacaoDesc(myUserId);
    }

    @CachePut(cacheNames = "notification", key = "#myUserId")
    public void atualizarCache(String myUserId){}

    @CacheEvict(value = "notification", key = "#myUserId")
    public void invalidarCache(String myUserId) {
    }

    //criar um tópico separado para quando o usuario for deletado
    @KafkaListener(topics = "delete-notification-topic")
    public void deleteNotificationByUserId(NotificationEvent notificationEvent){
        String userId = notificationEvent.getMyUserId();

        List<Notification> notifications = notificationRepository.findByMyUserId(userId);

        notifications.forEach((notification -> notificationRepository.deleteByMyUserId(notification.getMyUserId())));
    }
}
