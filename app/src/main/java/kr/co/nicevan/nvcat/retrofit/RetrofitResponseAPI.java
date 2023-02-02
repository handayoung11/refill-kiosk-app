package kr.co.nicevan.nvcat.retrofit;

import kr.co.nicevan.nvcat.dto.RequestDTO;
import kr.co.nicevan.nvcat.dto.ResponseDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

//통신할 URL을 미리 정의하는 interface
public interface RetrofitResponseAPI {

    @POST("/kiosk/order/receipt")
    Call<ResponseDTO.ReceiptDTO> postReceipt(@Body RequestDTO.ReceiptDTO request);

}
