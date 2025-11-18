package com.sehako.streamboard.application;

import com.sehako.streamboard.application.response.PostDetailRetrieveResponse;
import com.sehako.streamboard.application.response.PostRetrieveResponse;
import com.sehako.streamboard.infrastructure.PostRepository;
import com.sehako.streamboard.infrastructure.domain.Post;
import com.sehako.streamboard.presentation.request.PostDetailRetrieveRequest;
import com.sehako.streamboard.presentation.request.PostRetrieveRequest;
import com.sehako.streamboard.presentation.request.PostWriteRequest;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll().block();
    }

    @Test
    @DisplayName("사용자가 게시글을 포스팅하면 생성된 포스팅의 번호가 반환된다.")
    void postSaveIntegerReturnTest() {
        // given
        PostWriteRequest request = new PostWriteRequest(1, "title", "content");
        // when
        Mono<Integer> postNo = postService.createPost(request);
        // then

        StepVerifier.create(postNo)
                .expectNextMatches(n -> n != null && n > 0)
                .verifyComplete();
    }

    @Test
    @DisplayName("사용자가 게시글을 조회하면 포스팅 조회 응답으로 반환된다.")
    void postListRetrieveTest() {
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
        PostRetrieveRequest request = new PostRetrieveRequest(0, 10);
        Flux<PostRetrieveResponse> response = postService.retrievePosts(request);

        // then
        StepVerifier.create(response)
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    @DisplayName("사용자가 게시글을 조회하면 포스팅 조회 응답으로 반환된다.")
    void retrievePostingTest() {
        // given
        Post post = new Post(1, "title1", "content1");
        Post savedPost = postRepository.save(post).block();

        PostDetailRetrieveRequest request = new PostDetailRetrieveRequest(savedPost.getNo());

        // when
        Mono<PostDetailRetrieveResponse> response = postService
                .retrievePostDetail(request);

        // then
        StepVerifier.create(response)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

    }

}