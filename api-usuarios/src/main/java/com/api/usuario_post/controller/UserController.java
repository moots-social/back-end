package com.api.usuario_post.controller;

import com.api.usuario_post.client.imagestorage.ImageStorageClient;
import com.api.usuario_post.dto.ResetPasswordDTO;
import com.api.usuario_post.dto.UserDTO;
import com.api.usuario_post.dto.UsuarioDiferenteDTO;
import com.api.usuario_post.event.ElasticEvent;
import com.api.usuario_post.event.PostEvent;
import com.api.usuario_post.model.Post;
import com.api.usuario_post.model.User;
import com.api.usuario_post.repository.UserRepository;
import com.api.usuario_post.service.UserService;
import com.api.usuario_post.utils.Result;
import com.api.usuario_post.utils.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageStorageClient imageStorageClient;

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
    public ResponseEntity<User> seguir(@RequestParam Long id1, @RequestParam Long id2, @RequestParam(defaultValue = "true") boolean follow) {
        User user = userService.seguirUsuario(id1, id2, follow);

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

    @GetMapping("buscarEmail")
    public ResponseEntity<User> buscarUserEmail(@RequestParam String email) {
        User user = userService.buscarUsuarioEmail(email);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/buscar/perfil/{id}")
    public ResponseEntity<UsuarioDiferenteDTO> buscarUserSemToken(@PathVariable Long id){
        UsuarioDiferenteDTO user = userService.buscarUsuarioPorIdSemToken(id);
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


    @GetMapping("/colecao-salvos/{userId}")
    public ResponseEntity<List<PostEvent>> getColecaoSalvos(@PathVariable Long userId) {
        List<PostEvent> colecaoSalvos = userService.getColecaoSalvosByUserId(userId);
        return ResponseEntity.ok(colecaoSalvos);
    }

    @DeleteMapping("/{userId}/post/{postId}")
    public ResponseEntity<String> removerPostColecao(@PathVariable Long userId, @PathVariable Long postId){
        userService.removerPostColecao(userId, postId);
        return ResponseEntity.ok().body("Post removido da coleção com sucesso");
    }

    @PostMapping("/images")
    public Result uparImgemBlob(@RequestParam (defaultValue = "artifact-image-container") String containerName, @RequestParam MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()){
            String imageUrl = this.imageStorageClient.uploadImage(containerName, file.getOriginalFilename(),inputStream, file.getSize());
            String blobName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            return new Result(true, StatusCode.SUCCESS, "Imagem upada com sucesso", imageUrl, blobName);
        }
    }

    @DeleteMapping("/deletar-blob")
    public String deletarImagemBlob(@RequestParam String containerName, @RequestParam String blobName) {
        imageStorageClient.deleteBlob(blobName, containerName);
        return "Blob deletado com sucesso " + blobName;
    }

    @GetMapping("/seguidores/post/{userId}")
    public ResponseEntity<List<Post>> findPostAndCommentByFollowers(@PathVariable Long userId){
        List<Post> posts = userService.findPostAndCommentByFollowers(userId);
        log.info("Post adicionado na lista de usuarios com sucesso");
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/post/{userId}")
    public ResponseEntity<List<Post>> findPostByUserId(@PathVariable Long userId){
        List<Post> posts = userService.findPostByUserId(userId);
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/colecao-salvos-postId/{userId}")
    public ResponseEntity<List<Long>> findPostIdByColecoes(@PathVariable Long userId){
        List<Long> postIds = userService.findPostIdByColecoes(userId);
        return ResponseEntity.ok().body(postIds);
    }
}
