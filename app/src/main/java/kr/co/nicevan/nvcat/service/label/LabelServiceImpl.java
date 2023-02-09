package kr.co.nicevan.nvcat.service.label;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import kr.co.nicevan.nvcat.retrofit.RetrofitResponseAPI;
import kr.co.nicevan.nvcat.retrofit.RevealStringListCallbacks;
import kr.co.nicevan.nvcat.retrofit.error.ErrorCode;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;
import kr.co.nicevan.nvcat.retrofit.error.ErrorUtils;
import kr.co.nicevan.nvcat.service.common.CommonService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LabelServiceImpl implements LabelService{

    /**  2022-01-30 작성자 : 염에녹
     * refillcycle.com/kiosk/order/label 통신.
     * 기능 : [라벨 데이터 추출 API] 호출.
     * request : approvalNo
     * respone : shoName, liquidName, odExAmount, liquidDateOfManufacture,
     *           odUseDate, liquidDateOfUse, liquidComment, liquidIngredients,
     *           liquidCaution, liquidManufacturer, liquidResponsibleSalesBusiness
     * return : String */
    @Override
    public void printLabelByOrder(String approvalNo, @NonNull RevealLabelRespCallbacks callbacks) {
        RetrofitClient
                .getDefaultResponseAPI()
                .postLabel(approvalNo)
                .enqueue(new Callback<List<LabelDTO.LabelResp>>() {
                    @Override
                    public void onResponse(Call<List<LabelDTO.LabelResp>> call, Response<List<LabelDTO.LabelResp>> response) {
                        // status code >= 200 || code < 300
                        if(response.isSuccessful()) callbacks.onSuccess(response.body());
                        // status code < 200 || code >= 300
                        else callbacks.onError(ErrorUtils.parseError(response));
                    }
                    @Override
                    public void onFailure(Call<List<LabelDTO.LabelResp>> call, Throwable t) {
                        callbacks.onError(ErrorResponse.of(ErrorCode.RETROFIT_NETWORK_FAILED));
                    }
                });

    }
}
