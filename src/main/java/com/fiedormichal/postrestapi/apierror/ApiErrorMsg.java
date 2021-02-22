package com.fiedormichal.postrestapi.apierror;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiErrorMsg {
    MISMATCH_TYPE("Wrong method parameter in URL."),
    VALIDATION_ERRORS("Occurred some validation errors. Please check if JSON contains correct values." +
            " Check errors list for details."),
    MALFORMED_JSON_REQUEST("Request body is invalid. Check if fields in JSON have correct types" +
            " (integer Id, String title, String body)"),
    UNSUPPORTED_MEDIA_TYPE("Specified request media type (Content type) is not supported. Got to errors list for details."),
    METHOD_NOT_FOUND("Method with this URL not found."),
    POST_NOT_FOUND("Post not found. Check errors list for details."),
    API_CONNECTION_FAILURE("Posts not updated. API connection failure."),
    POSTS_NOT_FOUND("Posts not found."),
    POST_WITH_GIVEN_TITLE_NOT_FOUND("Post not found. Check errors list for details."),
    ERROR_OCCURRED("Some exceptions occurred. Check errors list.");

    private String value;
}
