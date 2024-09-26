package com.moots.api_notificacao.repository;

import com.moots.api_notificacao.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByMyUserId(String myUserId);
}
