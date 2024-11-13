package com.moots.api_busca.service;

import com.moots.api_busca.event.ElasticEvent;
import com.moots.api_busca.model.User;
import com.moots.api_busca.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "user-criado-topic")
    public void salvarUserElastic(ElasticEvent elasticEvent){
        log.info("O evento de salvar usuario foi recebido " + elasticEvent);
        User user = new User();
        user.setTag(elasticEvent.getTag());
        user.setNomeCompleto(elasticEvent.getNomeCompleto());
        user.setFotoPerfil(elasticEvent.getFotoPerfil());
        user.setUserId(elasticEvent.getUserId());
        user.setCurso(elasticEvent.getCurso());

        userRepository.save(user);
        log.info("Usuario salvo com sucesso" + user);
    }

    @KafkaListener(topics = "user-deletado-topic")
    public void deletarUser(ElasticEvent elasticEvent){
        log.info("O evento de deletar usuario foi recebido " + elasticEvent.getUserId());
        User user = userRepository.findByUserId(elasticEvent.getUserId());
        userRepository.delete(user);
        log.info("Usuario deletado com sucesso" + user);
    }

    @KafkaListener(topics = "user-alterado-topic")
    public void alterarUser(ElasticEvent elasticEvent){
        log.info("O evento de alterar usuario foi recebido " + elasticEvent);
        User user = userRepository.findByUserId(elasticEvent.getUserId());

        user.setTag(elasticEvent.getTag());
        user.setNomeCompleto(elasticEvent.getNomeCompleto());
        user.setFotoPerfil(elasticEvent.getFotoPerfil());
        user.setUserId(elasticEvent.getUserId());
        user.setCurso(elasticEvent.getCurso());
        user.setPostId(elasticEvent.getPostId());

        userRepository.save(user);
        log.info("Usuario alterado com sucesso" + user);
    }


    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    public List<User> findByTagOrNomeCompleto(String query){
        List<User> result = userRepository.findByTagOrNomeCompleto(query, query);
        return result;
    }

    public List<User> findByCurso(String curso){
        List<User> users = userRepository.findByCurso(curso);
        return users;
    }

    public List<User> findByCursoAndTagOrNomeCompleto(String curso, String query){
        List<User> result = userRepository.findCursoAndTagOrNomeCompleto(curso, query, query);
        return result;
    }
}
