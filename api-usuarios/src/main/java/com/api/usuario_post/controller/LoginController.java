package com.api.usuario_post.controller;

import com.api.usuario_post.dto.Login;
import com.api.usuario_post.dto.Sessao;
import com.api.usuario_post.event.UserEvent;
import com.api.usuario_post.handler.BusinessException;
import com.api.usuario_post.model.User;
import com.api.usuario_post.repository.UserRepository;
import com.api.usuario_post.security.JWTCreator;
import com.api.usuario_post.security.JWTObject;
import com.api.usuario_post.security.SecurityConfig;
import com.api.usuario_post.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private UserRepository repository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/login")
    public Sessao logar(@RequestBody Login login){
        if (login.getSenha() == null || login.getSenha().isEmpty()) {
            throw new BusinessException("Senha não pode ser nula ou vazia");
        }

        User user = repository.findByEmail(login.getEmail());

        if(user!=null) {
            boolean passwordOk = encoder.matches(login.getSenha(), user.getSenha());
            if (!passwordOk) {
                throw new BusinessException("Senha inválida para o login: " + login.getSenha());
            }
            //Estamos enviando um objeto Sessão para retornar mais informações do usuário
            Sessao sessao = new Sessao();
            sessao.setLogin(user.getEmail());

            JWTObject jwtObject = new JWTObject();
            jwtObject.setUserId(user.getUserId());
            jwtObject.setIssuedAt(new Date(System.currentTimeMillis()));
            jwtObject.setExpiration((new Date(System.currentTimeMillis() + SecurityConfig.EXPIRATION)));
            jwtObject.setRoles(user.getRoles());
            sessao.setToken(JWTCreator.create(SecurityConfig.PREFIX, SecurityConfig.KEY, jwtObject));
            kafkaProducerService.sendMessage("user-logado-topic", new UserEvent( "UserEvent",user.getUserId(), user.getNomeCompleto(), user.getTag(), user.getFotoPerfil()));
            log.info("Evento enviado");
            return sessao;
        }else {
            throw new BusinessException("Erro ao tentar fazer login");
        }
    }
}