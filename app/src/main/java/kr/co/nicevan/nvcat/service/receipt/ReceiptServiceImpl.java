package kr.co.nicevan.nvcat.service.receipt;
import android.util.Log;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import kr.co.nicevan.nvcat.retrofit.error.ErrorCode;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;
import kr.co.nicevan.nvcat.retrofit.error.ErrorUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReceiptServiceImpl implements ReceiptService {

    /**  2022-01-30 작성자 : 염에녹
     * refillcycle.com/kiosk/order/receipt 통신.
     * 기능 : [영수증 데이터 추출 API] 호출.
     * request : approvalNo
     * respone : shopName, companyNo, address, owner, tell, <List>items
     * return : String */
    @Override
    public void printReceiptByOrder(CardDTO card, @NonNull RevealReceiptRespCallbacks callbacks) {
        RetrofitClient
                .getDefaultResponseAPI()
                .postReceipt(card.getApprovalNo())
                .enqueue(new Callback<ReceiptDTO.ReceiptResp>() {
                    @Override
                    public void onResponse(Call<ReceiptDTO.ReceiptResp> call, Response<ReceiptDTO.ReceiptResp> response) {
                        // status code >= 200 || code < 300
                        if(response.isSuccessful()) callbacks.onSuccess(response.body());
                        // status code < 200 || code >= 300
                        else callbacks.onError(ErrorUtils.parseError(response));
                    }
                    @Override
                    public void onFailure(Call<ReceiptDTO.ReceiptResp> call, Throwable t) {
                        callbacks.onError(ErrorResponse.of(ErrorCode.RETROFIT_NETWORK_FAILED));
                    }
                });
    }
}
