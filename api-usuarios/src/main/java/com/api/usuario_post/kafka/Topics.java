package com.api.usuario_post.kafka;

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
            NewTopic userLogado = new NewTopic("user-logado-topic", 1, (short) 1);
            NewTopic userCriado = new NewTopic("user-criado-topic", 1, (short) 1);
            NewTopic userDeletado = new NewTopic("user-deletado-topic", 1, (short) 1);
            NewTopic userAlterado = new NewTopic("user-alterado-topic", 1, (short) 1);
            NewTopic post = new NewTopic("post-topic", 1, (short) 1);

            adminClient.createTopics(Arrays.asList(userLogado,userCriado, userDeletado, userAlterado, post)).all().get();

            System.out.println("Tópico criado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
