package kr.co.nicevan.nvcat.retrofit.error;

public enum ErrorCode {
    /** 공통 */
    RETROFIT_NETWORK_FAILED(503, "NET01", "네트워크 상태가 원활하지 않습니다. 관리자에게 문의 바랍니다."), //retrofit2 네트워크 연결 실패.
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
