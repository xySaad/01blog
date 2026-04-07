package com.z01.blog.exception;

import org.springframework.http.HttpStatus;

public enum AppError {
        INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

        // Auth / JWT
        MISSING_JWT_TOKEN(HttpStatus.UNAUTHORIZED),
        INVALID_JWT(HttpStatus.UNAUTHORIZED),
        INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED),

        // Verification
        INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST),
        VERIFICATION_CODE_EXPIRED(HttpStatus.GONE),
        ACCOUNT_NOT_VERIFIED(HttpStatus.FORBIDDEN),

        // Account / User
        USER_NOT_FOUND(HttpStatus.FORBIDDEN),
        ACCOUNT_IS_BANNED(HttpStatus.FORBIDDEN),
        EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT),
        USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT),
        USER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND),

        // Authorization
        PERMISSION_DENIED(HttpStatus.FORBIDDEN),
        ACCESS_DENIED(HttpStatus.FORBIDDEN),

        // Reports
        REPORT_NOT_FOUND(HttpStatus.NOT_FOUND),
        REPORT_ALREADY_RESOLVED(HttpStatus.CONFLICT),
        INVALID_REPORT_TYPE(HttpStatus.BAD_REQUEST),
        MATERIAL_NOT_DELETEABLE(HttpStatus.BAD_REQUEST),
        MATERIAL_NOT_HIDEABLE(HttpStatus.BAD_REQUEST),
        REPORT_NOT_USER_RELATED(HttpStatus.BAD_REQUEST),
        MATERIAL_NOT_FOUND(HttpStatus.NOT_FOUND),
        // Posts
        PARENT_POST_NOT_FOUND(HttpStatus.NOT_FOUND),

        // Entities
        ENTITY_DELETED(HttpStatus.GONE),
        ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND),

        // Files / Media
        FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
        FAILED_TO_FETCH_MEDIA(HttpStatus.INTERNAL_SERVER_ERROR);

        private final HttpStatus status;

        AppError(HttpStatus status) {
                this.status = status;
        }

        public HttpStatus status() {
                return status;
        }

        public AppException asException() {
                return new AppException(this);
        }
}