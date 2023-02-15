package kr.co.nicevan.nvcat.retrofit;

import java.util.List;

import kr.co.nicevan.nvcat.dto.KioskOrderDTO;
import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

//통신할 URL을 미리 정의하는 interface
public interface RetrofitResponseAPI {

    @POST("/kiosk/order/receipt/{id}")
    Call<ReceiptDTO.ReceiptResp> postReceipt(@Path(value = "id") String approvalNo);
    @POST("/kiosk/order/label/{id}")
    Call<List<LabelDTO.LabelResp>> postLabel(@Path(value = "id") String approvalNo);
    @POST("/login")
    Call<ResponseBody> login(@Query("username") String id, @Query("password") String pw);
    @POST("/kiosk/order")
    Call<ResponseBody> saveKioskOrders(@Body KioskOrderDTO.SaveOrders saveOrders);
}
