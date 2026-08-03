package com.example.community.common;

public final class ValidationMessage {
    public static final String EMAIL_REQUIRED = "email_required";
    public static final String INVALID_EMAIL_FORMAT = "invalid_email_format";

    public static final String NICKNAME_REQUIRED = "nickname_required";
    public static final String INVALID_NICKNAME_LENGTH = "invalid_nickname_length";
    public static final String INVALID_NICKNAME_BLANK = "invalid_nickname_blank";

    public static final String PASSWORD_REQUIRED = "password_required";
    public static final String INVALID_PASSWORD_LENGTH = "invalid_password_length";
    public static final String INVALID_PASSWORD_FORMAT = "invalid_password_format";

    public static final String PASSWORD_CHECK_REQUIRED = "password_check_required";
    public static final String INVALID_PASSWORD_CHECK_LENGTH = "invalid_password_check_length";
    public static final String INVALID_PASSWORD_CHECK_FORMAT = "invalid_password_check_format";

    public static final String USER_IMAGE_REQUIRED = "user_image_required";

    public static final String POST_TITLE_REQUIRED = "post_title_required";
    public static final String INVALID_POST_TITLE_LENGTH = "invalid_post_title_length";
    public static final String POST_CONTENT_REQUIRED = "post_content_required";
    public static final String INVALID_POST_CONTENT_LENGTH = "invalid_post_content_length";

    public static final String MUSIC_TITLE_REQUIRED = "music_title_required";
    public static final String COVER_IMAGE_REQUIRED = "cover_image_required";
    public static final String ARTIST_NAMES_REQUIRED = "artist_names_required";
    public static final String ARTIST_NAME_BLANK = "artist_name_blank";

    public static final String COMMENT_CONTENT_REQUIRED = "comment_content_required";

    public static final String REPORT_REASON_REQUIRED = "report_reason_required";

    public static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S+$";
    public static final String NICKNAME_PATTERN = "^\\S+$";

    private ValidationMessage() {
    }
}
