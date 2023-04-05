package kr.co.nicevan.nvcat.retrofit.error;

public enum ErrorCode {
    /** 공통 */
    RETROFIT_NETWORK_FAILED(503, "NET01", "네트워크 상태가 원활하지 않습니다. 관리자에게 문의 바랍니다."), //retrofit2 네트워크 연결 실패.

    /**sms전송실패**/
    RETROFIT_SMS_FAILED(503, "SMS01", "문자/링크전송에 실패하엿습니다."),
    /**주문내역 실패 데이터 저장 실패**/
    RETROFIT_ORDER_REFUND_FAILED(503, "ORDER02", "주문내역 취소를 실패하였습니다."),

    ;
    private int status;
    private final String message;
    private final String title;


    ErrorCode(int status, String title, String message) {
        this.status = status;
        this.title = title;
        this.message = message;
    }

    public String getCode() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

}
