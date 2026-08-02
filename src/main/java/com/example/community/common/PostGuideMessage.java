package com.example.community.common;

import lombok.Getter;

@Getter
public enum PostGuideMessage {
    NEW_TEMP_POST_EXISTS("작성 중인 임시 저장 글이 있습니다. 불러오시겠습니까?"),
    EDIT_TEMP_POST_EXISTS("수정 중인 임시 저장 글이 있습니다. 불러오시겠습니까?");

    private final String message;

    PostGuideMessage(String message) {
        this.message = message;
    }
}
