package com.sehako.streamboard.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.sehako.streamboard.application.PostService;
import com.sehako.streamboard.application.response.PostRetrieveResponse;
import com.sehako.streamboard.common.response.JsonResponse;
import com.sehako.streamboard.presentation.request.PostRetrieveRequest;
import com.sehako.streamboard.presentation.request.PostWriteRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(PostController.class)
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PostService postService;

    @Test
    @DisplayName("사용자가 요청한 포스팅 생성이 성공하면 201 응답이 반환된다.")
    void userPostingWriteRequestTest() {
        // given
        PostWriteRequest request = new PostWriteRequest(1, "title", "content");

        // when
        Mockito.when(postService.createPost(request)).thenReturn(Mono.just(1));

        // then
        webTestClient.post().uri("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/post/1");
    }

    @Test
    @DisplayName("사용자가 포스팅 조회를 요청하면 포스팅 리스트가 반환된다.")
    void retrievePostingListTest() {
        // given
        PostRetrieveRequest request = new PostRetrieveRequest(0, 10);

        // when
        LocalDateTime now = LocalDateTime.now();
        Mockito.when(postService.retrievePosts(request)).thenReturn(
                Flux.just(
                        new PostRetrieveResponse(1, "title1", now),
                        new PostRetrieveResponse(1, "title2", now),
                        new PostRetrieveResponse(1, "title3", now),
                        new PostRetrieveResponse(1, "title4", now),
                        new PostRetrieveResponse(1, "title5", now)
                )
        );

        // then
        webTestClient.get().uri("/post")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<JsonResponse<List<PostRetrieveResponse>>>() {
                })
                .consumeWith(response -> {
                    JsonResponse<List<PostRetrieveResponse>> body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.result()).hasSize(5);
                });

    }
}