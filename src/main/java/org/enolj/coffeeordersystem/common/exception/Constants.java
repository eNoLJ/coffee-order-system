package org.enolj.coffeeordersystem.common.exception;

public class Constants {

    // region 서버 관련 메세지
    public static final String MSG_NOT_VALID_VALUE = "유효하지 않은 값이 입력되었습니다";
    public static final String MSG_DATA_INSERT_FAIL = "데이터 등록에 실패하였습니다";
    public static final String MSG_SERVER_ERROR_OCCUR = "서버 오류가 발생하였습니다, 잠시 후 다시 시도 바랍니다";
    // endregion

    // region 유저 관련 메세지
    public static final String MSG_NOT_FOUND_USER = "해당 유저를 찾을 수 없습니다";
    // endregion

    // region 포인트 관련 메세지
    public static final String MSG_NOT_FOUND_POINT = "해당 포인트를 찾을 수 없습니다";
    public static final String MSG_INVALID_CHARGE_AMOUNT = "충전 금액은 1 이상이어야 합니다";
    // endregion
}
