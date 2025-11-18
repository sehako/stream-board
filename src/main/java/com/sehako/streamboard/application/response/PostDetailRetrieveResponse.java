package com.sehako.streamboard.application.response;

import com.sehako.streamboard.infrastructure.domain.Post;
import java.time.LocalDateTime;

public record PostDetailRetrieveResponse(
        Integer no,
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static PostDetailRetrieveResponse from(Post post) {
        return new PostDetailRetrieveResponse(
                post.getNo(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt()
        );
    }
}
