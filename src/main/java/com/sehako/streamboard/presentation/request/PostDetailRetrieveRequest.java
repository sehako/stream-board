package com.sehako.streamboard.presentation.request;

public record PostDetailRetrieveRequest(
        Integer no
) {
    public static PostDetailRetrieveRequest from(Integer no) {
        return new PostDetailRetrieveRequest(no);
    }
}
