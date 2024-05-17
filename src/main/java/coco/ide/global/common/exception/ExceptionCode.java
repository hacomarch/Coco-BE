package coco.ide.global.common.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    MEMBER_EXISTS("Member already exists."),
    NO_SUCH_ALGORITHM("No such algorithm found."),
    AUTH_CODE_INVALID("The provided authentication code is invalid."),
    MEMBER_NOT_FOUND("Member not found."),
    EMAIL_SENDING_ERROR("Error occurred while sending email."),
    UNABLE_TO_SEND_EMAIL("Unable to send email."),
    UNAUTHORIZED("Unauthorized access.");

    private final String description;

    ExceptionCode(String description) {
        this.description = description;
    }

}
