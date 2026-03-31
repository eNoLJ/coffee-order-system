package org.enolj.coffeeordersystem.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.enolj.coffeeordersystem.common.exception.Constants.*;


@Getter
public enum ErrorEnum {
    // region 회원 관련
    ERR_NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, MSG_NOT_FOUND_USER);
    // endregion

    private final HttpStatus status;
    private final String message;

    ErrorEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
