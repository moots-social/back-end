package com.api.usuario_post.service;

import com.api.usuario_post.dto.ResetPasswordDTO;
import com.api.usuario_post.dto.UserDTO;
import com.api.usuario_post.event.ColecaoPostEvent;
import com.api.usuario_post.event.ElasticEvent;
import com.api.usuario_post.event.NotificationEvent;
import com.api.usuario_post.event.UserEvent;
import com.api.usuario_post.handler.BusinessException;
import com.api.usuario_post.model.User;
import com.api.usuario_post.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public User criarUsuario(@Valid UserDTO userDTO){
        // Verificar unicidade de email e tag
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new BusinessException("Email já está em uso.");
        }
        if (userRepository.findByTag(userDTO.getTag()) != null) {
            throw new BusinessException("Tag já está em uso.");
        }

        // Validar campos obrigatórios
        if (userDTO.getNomeCompleto() == null || userDTO.getNomeCompleto().trim().isEmpty()) {
            throw new BusinessException("Nome completo é obrigatório.");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email é obrigatório.");
        }
        if (userDTO.getSenha() == null || userDTO.getSenha().trim().isEmpty()) {
            throw new BusinessException("Senha é obrigatória.");
        }
        if (userDTO.getTag() == null || userDTO.getTag().trim().isEmpty()) {
            throw new BusinessException("Tag é obrigatória.");
        }
        if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
            throw new BusinessException("Roles é obrigatório.");
        }

        User user = new User();
        user.setNomeCompleto(userDTO.getNomeCompleto());
        user.setEmail(userDTO.getEmail());

        String pass = userDTO.getSenha();
        user.setSenha(encoder.encode(pass));

        user.setNumeroTelefone(userDTO.getNumeroTelefone());
        user.setTag(userDTO.getTag());
        user.setCurso(userDTO.getCurso());
        user.setDescricao(userDTO.getDescricao());
        user.setFotoPerfil(userDTO.getFotoPerfil());
        user.setFotoCapa(userDTO.getFotoCapa());
        user.setRoles(userDTO.getRoles());

        User savedUser = userRepository.save(user);
        savedUser.setUserId(savedUser.getId());

        kafkaProducerService.sendMessage("user-criado-topic", new ElasticEvent(user.getUserId().toString(), null, user.getNomeCompleto(), user.getTag(), user.getFotoPerfil(), null, null, null, null));
        return userRepository.save(savedUser);
    }

    @CacheEvict(cacheNames = "user", key = "#id")
    @Transactional
    public User alterarUsuario(Long id, UserDTO userDTO) throws RuntimeException {
        Optional<User> user = userRepository.findByUserId(id);

        if (user.isPresent()) {

            if (userDTO.getNomeCompleto() != null) {
                user.get().setNomeCompleto(userDTO.getNomeCompleto());
            }
            if (userDTO.getNumeroTelefone() != null) {
                user.get().setNumeroTelefone(userDTO.getNumeroTelefone());
            }
            if (userDTO.getTag() != null){
                user.get().setTag(userDTO.getTag());
            }
            if (userDTO.getCurso() != null) {
                user.get().setCurso(userDTO.getCurso());
            }
            if (userDTO.getDescricao() != null) {
                user.get().setDescricao(userDTO.getDescricao());
            }
            if (userDTO.getFotoPerfil() != null) {
                user.get().setFotoPerfil(userDTO.getFotoPerfil());
            }
            if (userDTO.getFotoCapa() != null) {
                user.get().setFotoCapa(userDTO.getFotoCapa());
            }

            userRepository.save(user.get());
            kafkaProducerService.sendMessage("user-alterado-topic", new ElasticEvent(user.get().getUserId().toString(), null, user.get().getNomeCompleto(), user.get().getTag(), user.get().getFotoPerfil(), null, null, null, null));
            return userRepository.findOnlyUser(user.get().getUserId());
        } else {
            // Lançar uma exceção apropriada se o usuário não for encontrado
            throw new BusinessException("Usuário com ID " + id + " não encontrado.");
        }
    }

    public User redefinirSenha(Long id, @Valid ResetPasswordDTO resetPasswordDTO) {
        Optional<User> userOpt = userRepository.findByUserId(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Verifique se a senha antiga e a nova foram fornecidas
            if (resetPasswordDTO.getSenhaAntiga() == null || resetPasswordDTO.getSenhaNova() == null) {
                throw new BusinessException("Senha antiga e nova senha são obrigatórias.");
            }

            // Verifique a senha antiga
            if (!encoder.matches(resetPasswordDTO.getSenhaAntiga(), user.getSenha())) {
                throw new BusinessException("Senha antiga está incorreta.");
            }

            // Atualize a senha com a nova senha criptografada
            user.setSenha(encoder.encode(resetPasswordDTO.getSenhaNova()));
            userRepository.save(user);

            // Retorne o usuário atualizado
            return userRepository.findOnlyUser(user.getUserId());
        } else {
            throw new BusinessException("Usuário com ID " + id + " não encontrado.");
        }
    }

    @CacheEvict(value = {"seguidores", "seguindos"}, key = "#userId")
    public User seguirUsuario(Long userId, Long userId2) throws RuntimeException{
        Optional<User> user1 = userRepository.findByUserId(userId);
        Optional<User> user2 = userRepository.findByUserId(userId2);
        String evento = "Seguiu";

        if (user1.isPresent() && user2.isPresent()) {
            user1.get().follow(user2.get());

            userRepository.save(user1.get());

            kafkaProducerService.sendMessage("notification-topic", new NotificationEvent(null,user1.get().getUserId(), user1.get().getTag(), evento, new Date(),user2.get().getUserId().toString()));
            log.info("Evento enviado com sucesso");
            return userRepository.findOnlyUser(user1.get().getUserId());
        } else {
            throw new BusinessException("Usuario(s) não encontrado(s)");
        }
    }

    @Cacheable(cacheNames = "user", key = "#id")
    public User buscarUsuarioPorId(Long id) throws RuntimeException {
        User user = userRepository.findOnlyUser(id);

        if (user != null) {
            return user;
        } else {
            throw new BusinessException("User não encontrado");
        }
    }

    @Cacheable(cacheNames = "seguidores", key = "#userId")
    public List<User> buscarSeguidoresDoUsuario(Long userId) throws RuntimeException {
        List<User> seguidores = userRepository.findFollowersByUserId(userId);

        if (!seguidores.isEmpty()) {
            return seguidores;
        } else {
            throw new BusinessException("Seguidor com id" + userId + "não encontrado");
        }
    }

    @Cacheable(cacheNames = "seguindos", key = "#userId")
    public List<User> buscarQuemOUsuarioSegue(Long userId) throws RuntimeException {
        List<User> seguidores = userRepository.findFollowingByUserId(userId);

        if (!seguidores.isEmpty()) {
            return seguidores;
        } else {
            throw new BusinessException("User " + userId + "que você segue não encontrado");
        }
    }

    @CacheEvict(cacheNames = "user", key = "#id")
    public User deletarUsuarioPorId(Long id) throws RuntimeException{
        Optional<User> user = userRepository.findByUserId(id);
        if (user.isPresent()) {
            User user1 = userRepository.findOnlyUser(user.get().getUserId());
            userRepository.deleteById(id);
            kafkaProducerService.sendMessage("user-deletado-topic", new ElasticEvent(user1.getUserId().toString(), null, null, null, null, null, null, null, null));
            return user1;
        } else {
            throw new BusinessException("Erro ao excluir usuario");
        }
    }

    public String enviarEvento(String topico, UserEvent userEvent){
        kafkaProducerService.sendMessage(topico, userEvent);
        return "Evento enviado com sucesso";
    }

    @CacheEvict(value = "colecaoPost", key = "#colecaoPostEvent.userId")
    public User salvarPostColecao(ColecaoPostEvent colecaoPostEvent) {
        Optional<User> optionalUser = userRepository.findByUserId(colecaoPostEvent.getUserId());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + colecaoPostEvent.getUserId());
        }

        User user = optionalUser.get();

        var colecao = user.getColecaoSalvos();

        var userId = colecaoPostEvent.getUserId();
        var postId = colecaoPostEvent.getPostId();
        var conteudo = colecaoPostEvent.getTexto();
        var imagens = colecaoPostEvent.getListImagens();
        var postSalvo = new ColecaoPostEvent(colecaoPostEvent.getId(), userId, postId, conteudo, imagens);

        colecao.add(postSalvo);

        log.info("Colecao salvo com sucesso");
        return userRepository.save(user);
    }

    @CacheEvict(value = "colecaoPost", key = "#userId")
    public User removerPostColecao(Long userId, Long postId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + userId);
        }

        User user = optionalUser.get();
        var colecao = user.getColecaoSalvos();
        boolean removerPost = colecao.removeIf(c -> c.getPostId().equals(postId));

        log.info("Post removido da coleção com sucesso");
        return userRepository.save(user);
    }

    @Cacheable(cacheNames = "colecaoPost", key = "#userId")
    public List<ColecaoPostEvent> getColecaoSalvosByUserId(Long userId) {
        Optional<User> user = userRepository.findByUserId(userId);

        if (user.isPresent()) {
            return user.get().getColecaoSalvos();
        } else {
            throw new NoSuchElementException("Usuário não encontrado para o ID: " + userId);
        }
    }

}
