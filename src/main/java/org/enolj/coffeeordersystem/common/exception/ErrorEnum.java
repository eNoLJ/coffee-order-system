package org.enolj.coffeeordersystem.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.enolj.coffeeordersystem.common.exception.Constants.*;


@Getter
public enum ErrorEnum {
    // region 회원 관련
    ERR_NOT_FOUND_USER(HttpStatus.NOT_FOUND, MSG_NOT_FOUND_USER),
    // endregion

    // region 포인트 관련
    ERR_NOT_FOUND_POINT(HttpStatus.NOT_FOUND, MSG_NOT_FOUND_POINT),
    ERR_INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, MSG_INVALID_CHARGE_AMOUNT),
    // endregion

    // region 주문, 결제 관련
    ERR_NOT_FOUND_MENU(HttpStatus.NOT_FOUND, MSG_NOT_FOUND_MENU),
    ERR_MENU_NOT_ON_SALE(HttpStatus.BAD_REQUEST, MSG_MENU_NOT_ON_SALE),
    ERR_INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, MSG_INSUFFICIENT_POINT),
    //endregion

    // region 레디스 관련
    ERR_LOCK_ACQUIRE_FAILED(HttpStatus.CONFLICT, MSG_LOCK_ACQUIRE_FAILED);
    // endregion

    private final HttpStatus status;
    private final String message;

    ErrorEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
