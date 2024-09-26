package com.moots.api_busca.kafka;

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
            NewTopic postSalvo = new NewTopic("post-salvo-topic", 1, (short) 1);
            NewTopic postDeletado = new NewTopic("post-deletado-topic", 1, (short) 1);
            NewTopic userCriado = new NewTopic("user-criado-topic", 1, (short) 1);
            NewTopic userAlterado = new NewTopic("user-alterado-topic", 1, (short) 1);
            NewTopic userDeletado = new NewTopic("user-deletado-topic", 1, (short) 1);
            adminClient.createTopics(Arrays.asList(postSalvo, postDeletado, userCriado, userAlterado, userDeletado)).all().get();

            System.out.println("Tópicos criado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
