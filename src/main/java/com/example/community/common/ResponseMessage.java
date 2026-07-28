package com.example.community.common;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    LOGIN_PAGE_LOAD("login_page_load"),
    LOGIN_SUCCESS("login_success"),
    LOGOUT_SUCCESS("logout_success"),
    ACCESS_TOKEN_REFRESHED("access_token_refreshed"),
    WRITE_COMMENT_SUCCESS("write_comment_success"),
    COMMENT_EDIT_SUCCESS("comment_edit_success"),
    COMMENT_DELETE_SUCCESS("comment_delete_success"),
    AUTHORIZED("authorized"),
    ALREADY_AUTHORIZED("already_authorized"),
    UNAUTHORIZED("unauthorized"),
    POSTS_PAGE_LOAD("posts_page_load"),
    POST_PAGE_LOAD("post_page_load"),
    NEW_PAGE_LOAD("new_page_load"),
    POST_DETAILS_PAGE_LOAD("post_details_page_load"),
    POST_EDIT_SUCCESS("post_edit_success"),
    POST_DELETE_SUCCESS("post_delete_success"),
    WRITE_POST_SUCCESS("write_post_success"),
    POST_EDIT_PAGE_LOAD("post_edit_page_load"),
    LIKE_UPDATE_SUCCESS("like_update_success"),
    JOIN_PAGE_LOAD("join_page_load"),
    JOIN_SUCCESS("join_success"),
    USER_PROFILE_EDIT_PAGE_LOAD("user_profile_edit_page_load"),
    PROFILE_UPDATE_SUCCESS("profile_update_success"),
    USER_PASSWORD_EDIT_PAGE_LOAD("user_password_edit_page_load"),
    PASSWORD_UPDATE_SUCCESS("password_update_success"),
    USER_DELETE_SUCCESS("user_delete_success"),
    TEMP_POST_CREATE_SUCCESS("temp_post_create_success"),
    GET_COMMENTS_SUCCESS("get_comments_success"),
    REPORT_POST_SUCCESS("report_post_success"),
    ADMIN_PAGE_LOAD("admin_page_load"),
    REPORTED_POSTS_PAGE_LOAD("reported_posts_page_load"),
    POST_REPORT_DETAILS_PAGE_LOAD("post_report_details_page_load"),
    POST_BLIND_SUCCESS("post_blind_success"),
    REJECT_REPORTS_SUCCESS("reject_reports_success");


    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

}