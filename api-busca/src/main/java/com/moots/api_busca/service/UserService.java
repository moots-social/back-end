package com.moots.api_busca.service;

import com.moots.api_busca.event.ElasticEvent;
import com.moots.api_busca.model.Post;
import com.moots.api_busca.model.User;
import com.moots.api_busca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User salvarUserElastic(ElasticEvent elasticEvent){
        var user = new User();
        user.setTag(elasticEvent.getTag());
        user.setNomeCompleto(elasticEvent.getNomeCompleto());
        user.setFotoPerfil(elasticEvent.getFotoPerfil());
        user.setUserId(elasticEvent.getUserId());

        return userRepository.save(user);
    }

    public User deletarUser(String userId){
        User user = userRepository.findByUserId(userId);
        userRepository.deleteById(user.getId());
        return user;
    }

    public User alterarUser(String userId, ElasticEvent elasticEvent){
        User user = userRepository.findByUserId(userId);

        user.setTag(elasticEvent.getTag());
        user.setNomeCompleto(elasticEvent.getNomeCompleto());
        user.setFotoPerfil(elasticEvent.getFotoPerfil());
        user.setUserId(elasticEvent.getUserId());

        return userRepository.save(user);
    }


    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    public List<User> findByTagOrNomeCompleto(String query, int page){
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        var result = userRepository.findByTagOrNomeCompleto(query, query, pageRequest);
        return result;
    }
}
