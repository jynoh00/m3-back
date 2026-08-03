package com.example.community.common;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
    // HTTP 계층 관련
    INTERNAL_SERVER_ERROR("internal_server_error"),
    INVALID_INPUT_FORMAT("invalid_input_format"),
    INVALID_REQUEST_BODY("invalid_request_body"),
    NOT_FOUND_URL("not_found_url"),
    INVALID_PAGE("invalid_page"),

    // 인증 / 인가 관련
    USER_EMAIL_NOT_FOUND("user_email_not_found"),
    PASSWORD_FAILED("password_failed"),
    INVALID_REFRESH_TOKEN("invalid_refresh_token"),
    EXPIRED_REFRESH_TOKEN("expired_refresh_token"),

    // 사용자 관련
    USER_NOT_FOUND("user_not_found"),
    DUPLICATED_USER_EMAIL("duplicated_user_email"),
    DUPLICATED_USER_NICKNAME("duplicated_user_nickname"),
    MISMATCH_USER_PASSWORD("mismatch_user_password"),
    NO_USER_UPDATE_CHANGES("no_user_update_changes"),

    // 게시글 관련
    POST_NOT_FOUND("post_not_found"),
    POST_DELETED("post_deleted"),
    POST_CREATE_LIMIT_EXCEEDED("post_create_limit_exceeded"),
    POST_UPDATE_FORBIDDEN("post_update_forbidden"),
    POST_DELETE_FORBIDDEN("post_delete_forbidden"),
    POST_REPORT_ALREADY_EXISTS("post_report_already_exists"),
    ALREADY_BLINDED_POST("already_blinded_post"),
    CANNOT_ACCESS_BLINDED_POST("can't_access_to_blinded_post"),

    // 음악 관련
    MUSIC_NOT_SELECTED("music_not_selected"),
    SEARCH_KEYWORD_REQUIRED("search_keyword_required"),

    // 댓글 관련
    COMMENT_NOT_FOUND("comment_not_found"),
    COMMENT_NOT_IN_POST("comment_not_in_post"),
    COMMENT_EDIT_FORBIDDEN("comment_edit_forbidden"),
    COMMENT_DELETE_FORBIDDEN("comment_delete_forbidden");
    
    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}