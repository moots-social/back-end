package com.api.usuario_post.controller;

import com.api.usuario_post.dto.ResetPasswordDTO;
import com.api.usuario_post.dto.UserDTO;
import com.api.usuario_post.event.ColecaoPostEvent;
import com.api.usuario_post.model.User;
import com.api.usuario_post.repository.UserRepository;
import com.api.usuario_post.service.UserService;
import org.neo4j.cypherdsl.core.Use;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/criar")
    public ResponseEntity<User> criar(@RequestBody UserDTO userDTO) {
        User user = userService.criarUsuario(userDTO);

        if (user != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } else {
            return ResponseEntity.badRequest().body(user);
        }
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<User> atualizar(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userService.alterarUsuario(id, userDTO);

        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/seguir")
    public ResponseEntity<User> seguir(@RequestParam Long id1, @RequestParam Long id2) {
        User user = userService.seguirUsuario(id1, id2);

        return ResponseEntity.ok().body(user);
    }

    @PatchMapping("/redefinir-senha/{id}")
    public ResponseEntity<User> redefinirSenha(@PathVariable Long id, @RequestBody ResetPasswordDTO resetPasswordDTO) {
        User user = userService.redefinirSenha(id, resetPasswordDTO);

        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<User> buscarUser (@PathVariable Long id) {
        User user = userService.buscarUsuarioPorId(id);

        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/buscar-seguidores/{id}" )
    public ResponseEntity<List<User>> buscarSeguidores(@PathVariable Long id){
        List<User> seguidores = userService.buscarSeguidoresDoUsuario(id);

        return ResponseEntity.ok().body(seguidores);
    }

    @GetMapping("/buscar-quem-segue/{id}" )
    public ResponseEntity<List<User>> buscarQuemSegue(@PathVariable Long id){
        List<User> seguidores = userService.buscarQuemOUsuarioSegue(id);

        return ResponseEntity.ok().body(seguidores);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> remover(@PathVariable Long id) {
        User user = userService.deletarUsuarioPorId(id);

        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping
    public void deletarGeral() {
        userRepository.deleteAll();
    }

    @KafkaListener(topics = "post-colecao-topic")
    public ResponseEntity<User> savePostColecao(ColecaoPostEvent colecaoPostEvent){
        User user = userService.salvarPostColecao(colecaoPostEvent);
        log.info("post salvo na coleção");
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/colecao-salvos/{userId}")
    public ResponseEntity<List<ColecaoPostEvent>> getColecaoSalvos(@PathVariable Long userId) {
        List<ColecaoPostEvent> colecaoSalvos = userService.getColecaoSalvosByUserId(userId);
        return ResponseEntity.ok(colecaoSalvos);
    }

    @DeleteMapping("/{userId}/post/{postId}")
    public ResponseEntity<String> removerPostColecao(@PathVariable Long userId, @PathVariable Long postId){
        userService.removerPostColecao(userId, postId);
        return ResponseEntity.ok().body("Post removido da coleção com sucesso");
    }
}
