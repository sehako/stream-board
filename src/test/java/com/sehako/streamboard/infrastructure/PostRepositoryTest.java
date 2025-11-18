package com.sehako.streamboard.infrastructure;

import com.sehako.streamboard.common.R2dbcAuditingConfiguration;
import com.sehako.streamboard.infrastructure.domain.Post;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@DataR2dbcTest
@Import(R2dbcAuditingConfiguration.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll()
                .block();
    }

    @Test
    @DisplayName("사용자가 게시판에 글을 작성하면 데이터베이스에 저장된다.")
    void postSaveTest() {
        // given
        Post post = new Post(1, "title", "content");

        // when
        Mono<Post> save = postRepository.save(post);
        // then
        StepVerifier.create(save)
                .expectNextMatches(p ->
                        p.getNo() != null && p.getCreatedAt() != null
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("사용자가 게시판 조회 기능을 요청하면 게시판 리스트를 조회한다.")
    void findPostsTest() {
        // given
        List<Post> posts = List.of(
                new Post(1, "title1", "content1"),
                new Post(1, "title2", "content2"),
                new Post(1, "title3", "content3"),
                new Post(1, "title4", "content4"),
                new Post(1, "title5", "content5")
        );

        postRepository.saveAll(posts).subscribe();
        // when
        Flux<Post> searchedPosts = postRepository.findByCursor(0, 10);
        // then
        StepVerifier.create(searchedPosts)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    @DisplayName("사용자가 게시판 조회를 위해 게시판 번호를 전달하면 게시판 상세 정보를 조회한다.")
    void retrievePostDetailTest() {
        // given
        Post post = new Post(1, "title", "content");
        Post savedPost = postRepository.save(post).block();

        // when
        Mono<Post> retrievedPost = postRepository.findByNo(savedPost.getNo());

        // then
        StepVerifier.create(retrievedPost)
                .expectNextMatches(p ->
                        p.getNo() != null && p.getTitle().equals("title") && p.getContent().equals("content")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("사용자가 수정할 게시글에 대한 새로운 제목과 내용을 전달하면 게시글이 수정된다.")
    void patchPostTest() {
        // given
        Post post = new Post(1, "title", "content");
        Post savedPost = postRepository.save(post).block();

        // when
        postRepository.updatePost(savedPost.getNo(), "newTitle", "newContent").block();

        Mono<Post> updatedPost = postRepository.findByNo(savedPost.getNo());

        // then
        StepVerifier.create(updatedPost)
                .expectNextMatches(
                        entity ->
                                entity.getTitle().equals("newTitle")
                                        && entity.getContent().equals("newContent")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("사용자가 삭제할 게시글 번호를 전달하면 게시글이 삭제된다.")
    void deletePostTest() {
        // given
        Post post = new Post(1, "title", "content");
        Post savedPost = postRepository.save(post).block();

        // when

        // then
        postRepository.deleteByNo(savedPost.getNo())
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();

    }
}
