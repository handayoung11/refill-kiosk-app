package kr.co.nicevan.nvcat.retrofit.error;

import com.google.gson.annotations.Expose;

public class ErrorResponse {
    int status;
    String title;
    String msg;

    public int getStatus() {return status;}

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    private ErrorResponse() {
    }

    private ErrorResponse(ErrorCode code) {
        this.status = code.getStatus();
        this.title = code.getCode();
        this.msg = code.getMessage();
    }

    public static ErrorResponse of(){
        return NotNull(new ErrorResponse());
    }

    public static ErrorResponse of(ErrorCode errorCode){
        return new ErrorResponse(errorCode);
    }


    public static ErrorResponse NotNull(ErrorResponse e){
        if(e.msg == null) e.msg = "서버에러가 발생하였습니다. 관리자에게 문의 바랍니다.";
        if(e.status == 0) e.status = 500;
        if(e.title == null) e.title = "";
        return e;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
