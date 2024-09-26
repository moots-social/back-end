package com.moots.api_busca.controller;

import com.moots.api_busca.event.ElasticEvent;
import com.moots.api_busca.model.User;
import com.moots.api_busca.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @KafkaListener(topics = "user-criado-topic")
    public void salvarUserElastic(ElasticEvent elasticEvent){
        System.out.println("Mensagem recebida " + elasticEvent);
        userService.salvarUserElastic(elasticEvent);
        log.info("User salvo no Elastic Search");
    }

    @KafkaListener(topics = "user-deletado-topic")
    public void deletarUserElastic(ElasticEvent elasticEvent){
        System.out.println("Mensagem recebida " + elasticEvent);
        userService.deletarUser(elasticEvent.getUserId());
        log.info("User deletado no Elastic Search");
    }

    @KafkaListener(topics = "user-alterado-topic")
    public void alterarUserElastic(ElasticEvent elasticEvent){
        System.out.println("Mensagem recebida " + elasticEvent);
        userService.alterarUser(elasticEvent.getUserId(), elasticEvent);
        log.info("User alterado no Elastic Search");
    }

    @GetMapping
    public ResponseEntity<List<User>> findByTagOrNomeCompleto(@RequestParam String query, @RequestParam (defaultValue = "0", value = "page") int page){
        var result = userService.findByTagOrNomeCompleto(query, page);
        return ResponseEntity.ok().body(result);
    }
}
