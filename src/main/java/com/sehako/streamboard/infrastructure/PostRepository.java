package com.sehako.streamboard.infrastructure;

import com.sehako.streamboard.infrastructure.domain.Post;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository extends ReactiveCrudRepository<Post, Integer> {
    @Query("SELECT * FROM post WHERE no > :cursor LIMIT :size")
    Flux<Post> findByCursor(Integer cursor, Integer size);

    Mono<Post> findByNo(Integer no);
}
