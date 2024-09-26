package com.moots.api_busca.repository;

import com.moots.api_busca.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends ElasticsearchRepository<User, String> {

    User findByUserId(String userId);

    List<User> findByTagOrNomeCompleto(String tag, String nomeCompleto, Pageable pageable); // 5 users
}
