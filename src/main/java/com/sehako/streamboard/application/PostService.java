package com.sehako.streamboard.application;

import com.sehako.streamboard.application.response.PostDetailRetrieveResponse;
import com.sehako.streamboard.application.response.PostRetrieveResponse;
import com.sehako.streamboard.infrastructure.PostRepository;
import com.sehako.streamboard.infrastructure.domain.Post;
import com.sehako.streamboard.presentation.request.PostDetailRetrieveRequest;
import com.sehako.streamboard.presentation.request.PostPatchRequest;
import com.sehako.streamboard.presentation.request.PostRetrieveRequest;
import com.sehako.streamboard.presentation.request.PostWriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final static Integer DEFAULT_MAX_CURSOR = Integer.MAX_VALUE;

    private final PostRepository postRepository;

    public Mono<Integer> createPost(PostWriteRequest request) {
        return postRepository.save(request.toEntity())
                .map(Post::getNo);
    }

    @Transactional(readOnly = true)
    public Flux<PostRetrieveResponse> retrievePosts(PostRetrieveRequest request) {
        Integer cursor = request.cursor();
        if (cursor == 0) {
            cursor = DEFAULT_MAX_CURSOR;
        }
        Integer size = request.size();

        return postRepository.findByCursor(cursor, size)
                .map(PostRetrieveResponse::from);
    }

    @Transactional(readOnly = true)
    public Mono<PostDetailRetrieveResponse> retrievePostDetail(PostDetailRetrieveRequest request) {
        Integer no = request.no();
        return postRepository.findByNo(no)
                .map(PostDetailRetrieveResponse::from);
    }

    public Mono<PostDetailRetrieveResponse> patchPostDetail(Integer no, PostPatchRequest request) {
        return postRepository.updatePost(no, request.title(), request.content())
                .then(postRepository.findByNo(no))
                .map(PostDetailRetrieveResponse::from);
    }

    public Mono<Integer> deletePost(Integer no) {
        return postRepository.deleteByNo(no);
    }
}
