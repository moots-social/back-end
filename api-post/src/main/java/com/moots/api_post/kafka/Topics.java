package com.moots.api_post.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class Topics {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");

        try (AdminClient adminClient = AdminClient.create(properties)) {
            NewTopic postColecao = new NewTopic("post-colecao-topic", 1, (short) 1);
            adminClient.createTopics(Collections.singletonList( postColecao )).all().get();

            System.out.println("Tópicos criado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
