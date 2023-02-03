package com.kaminski.webflux.repository;

import com.kaminski.webflux.entity.User;
import com.kaminski.webflux.mapper.UserMapper;
import com.kaminski.webflux.model.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final ReactiveMongoTemplate mongoTemplate;
    private final UserMapper mapper;

    public Mono<User> save(final User user){
        return mongoTemplate.save(user);
    }

    public Mono<User> findById(String id) {
        return mongoTemplate.findById(id, User.class);
    }

    public Flux<User> findAll(){
        return mongoTemplate.findAll(User.class);
    }

    public Mono<User> findEndRemove(String id) {
        var criteria = Criteria.where("id").is(id);
        var query = new Query().addCriteria(criteria);
        return mongoTemplate.findAndRemove(query, User.class);

    }
}