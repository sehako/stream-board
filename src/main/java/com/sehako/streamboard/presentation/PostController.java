package com.sehako.streamboard.presentation;

import com.sehako.streamboard.application.PostService;
import com.sehako.streamboard.application.response.PostDetailRetrieveResponse;
import com.sehako.streamboard.application.response.PostRetrieveResponse;
import com.sehako.streamboard.common.response.JsonResponse;
import com.sehako.streamboard.common.response.message.code.SuccessCode;
import com.sehako.streamboard.presentation.request.PostDetailRetrieveRequest;
import com.sehako.streamboard.presentation.request.PostPatchRequest;
import com.sehako.streamboard.presentation.request.PostRetrieveRequest;
import com.sehako.streamboard.presentation.request.PostWriteRequest;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private static final String POST_RETRIEVE_URI = "/post/%s";

    private final PostService postService;
    private final MessageSource messageSource;

    @PostMapping
    public Mono<ResponseEntity<JsonResponse<Void>>> createPost(
            @RequestBody PostWriteRequest request,
            Locale locale
    ) {
        Mono<Integer> postNo = postService.createPost(request);

        SuccessCode success = SuccessCode.SUCCESS;
        String message = messageSource.getMessage(success.getCode(), null, locale);

        return postNo.map(no -> ResponseEntity
                .created(URI.create(String.format(POST_RETRIEVE_URI, no)))
                .body(JsonResponse.of(SuccessCode.SUCCESS, message))
        );
    }

    @GetMapping
    public Mono<ResponseEntity<JsonResponse<List<PostRetrieveResponse>>>> retrievePosts(
            @RequestParam(value = "cursor", defaultValue = "0") Integer cursor,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            Locale locale
    ) {
        Flux<PostRetrieveResponse> response = postService
                .retrievePosts(PostRetrieveRequest.from(cursor, size));

        SuccessCode success = SuccessCode.SUCCESS;
        String message = messageSource.getMessage(success.getCode(), null, locale);

        return response
                .collectList()
                .map(list -> ResponseEntity
                        .ok(JsonResponse.of(success, message, list)));
    }

    @GetMapping("/{no}")
    public Mono<ResponseEntity<JsonResponse<PostDetailRetrieveResponse>>> retrievePostDetail(
            @PathVariable Integer no,
            Locale locale
    ) {
        Mono<PostDetailRetrieveResponse> response = postService
                .retrievePostDetail(PostDetailRetrieveRequest.from(no));

        SuccessCode success = SuccessCode.SUCCESS;
        String message = messageSource.getMessage(success.getCode(), null, locale);

        return response.map(data -> ResponseEntity
                .ok(JsonResponse.of(success, message, data))
        );
    }

    @PatchMapping("/{no}")
    public Mono<ResponseEntity<JsonResponse<PostDetailRetrieveResponse>>> patchPostDetail(
            @PathVariable Integer no,
            @RequestBody PostPatchRequest request,
            Locale locale
    ) {
        Mono<PostDetailRetrieveResponse> response = postService
                .patchPostDetail(no, request);

        SuccessCode success = SuccessCode.SUCCESS;
        String message = messageSource.getMessage(success.getCode(), null, locale);

        return response.map(data -> ResponseEntity
                .ok(JsonResponse.of(success, message, data))
        );
    }

    @DeleteMapping("/{no}")
    public Mono<ResponseEntity<Void>> deletePost(
            @PathVariable Integer no
    ) {
        return postService.deletePost(no)
                .map(__ -> ResponseEntity.noContent().build());
    }
}
