package com.moots.api_post.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService<T> {

    @Autowired
    private final KafkaTemplate<String, T> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String,  T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendMessage(String topic,  T event) {
        kafkaTemplate.send(topic, event);
    }

}
