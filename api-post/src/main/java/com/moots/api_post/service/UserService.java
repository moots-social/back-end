package com.moots.api_post.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moots.api_post.event.UserEvent;
import com.moots.api_post.model.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
@Slf4j
public class UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveUser(UserEvent userEvent) {
        try {
            User user = new User();
            user.setUserId(userEvent.getUserId().toString());
            user.setNomeCompleto(userEvent.getNomeCompleto());
            user.setTag(userEvent.getTag());
            user.setFotoPerfil(userEvent.getFotoPerfil());

            String userKey = "user:" + user.getUserId();
            String userJson = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(userKey, userJson);
            log.info("Usuário salvo no Redis com sucesso: " + user);
        } catch (Exception e) {
            log.error("Erro ao salvar usuário no Redis", e);
        }
    }

    public User getUserRedis(String userId) throws Exception {
        String userKey = "user:" + userId;
        String userJson = redisTemplate.opsForValue().get(userKey);

        if (userJson != null) {
            return objectMapper.readValue(userJson, User.class);
        } else {
            throw new NoSuchElementException("Usuário não encontrado no Redis com o ID: " + userId);
        }
    }



}
