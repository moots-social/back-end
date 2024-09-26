package com.moots.api_busca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.moots.api_busca.repository")
public class ApiBuscaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiBuscaApplication.class, args);
	}

}
