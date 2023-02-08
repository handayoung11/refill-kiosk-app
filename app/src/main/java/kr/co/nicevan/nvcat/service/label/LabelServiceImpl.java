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
import kr.co.nicevan.nvcat.service.common.CommonService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelServiceImpl implements LabelService{

    private RetrofitClient retrofitClient;
    private RetrofitResponseAPI callAPI;
    private final CommonService commonService;

    public LabelServiceImpl(CommonService commonService) {
        this.commonService = commonService;
    }

    /**  2022-01-30 작성자 : 염에녹
     * refillcycle.com/kiosk/order/label 통신.
     * 기능 : [라벨 데이터 추출 API] 호출.
     * request : approvalNo
     * respone : shoName, liquidName, odExAmount, liquidDateOfManufacture,
     *           odUseDate, liquidDateOfUse, liquidComment, liquidIngredients,
     *           liquidCaution, liquidManufacturer, liquidResponsibleSalesBusiness
     * return : String */
    @Override
    public void printLabelByOrder(String approvalNo, @NonNull RevealStringListCallbacks callbacks) {
        retrofitClient = RetrofitClient.getInstance();
        callAPI = RetrofitClient.getRetrofitInterface();
        callAPI.postLabel(approvalNo).enqueue(new Callback<List<LabelDTO.LabelResp>>() {
            @Override
            public void onResponse(Call<List<LabelDTO.LabelResp>> call, Response<List<LabelDTO.LabelResp>> response) {
                Log.d(this.getClass().getSimpleName(), "Data fetch success");

                //통신성공
                if(response.isSuccessful()) {
                    List<LabelDTO.LabelResp> resps = response.body();
                    if(resps.size() > 0){
                        List<String> res = new ArrayList<>();
                        for(LabelDTO.LabelResp label : resps){
                            res.add(outStringForLabel(label));
                        }
                        callbacks.onSuccess(res);
                    }
                }
                else {
                    Log.d(this.getClass().getSimpleName(), "[오류발생] : "+response.code());
                }
            }

            @Override
            public void onFailure(Call<List<LabelDTO.LabelResp>> call, Throwable t) {
                if (callbacks != null) callbacks.onError(t);
                else Log.d(this.getClass().getSimpleName(), "[오류발생] : " + t.toString());
            }
        });

    }
    /**  2022-01-30 작성자 : 염에녹
     * 기능 : 라벨 formatter.
     */
    @Override
    public String outStringForLabel(LabelDTO.LabelResp response) {
        String strData = "";
            strData += "[상품명] "+response.getLiquidName()+"\t\t\t"+"[용량] "+ response.getOdExAmount()+"\n";
            strData += "[제조일] "+ response.getLiquidDateOfManufacture()+"\t\t\t"+"[사용기한] "+ response.getLiquidDateOfUse()+"까지\n";
            strData += "--------------------------------------------------------\n";
            strData += "[전성분]\n";
            strData += commonService.formatterByRightEnter(response.getLiquidIngredients(),40)+"\n";
            strData += "\n\n";
            strData += "[사용시 주의사항]\n";
            strData += commonService.formatterByRightEnter(response.getLiquidCaution(),40)+"\n";
            strData += "\n\n";
            strData += "[제조업체]\n";
            strData += response.getLiquidDateOfManufacture()+"\n";
            strData += "\n\n";
            strData += "[책임판매업체]\n";
            strData += response.getLiquidResponsibleSalesBusiness()+"\n";
            strData += "\n\n";
            strData += "[맞춤판매업체]\n";
            strData += response.getLiquidSellerName()+"\n";
            strData += "--------------------------------------------------------\n";
        return strData;
    }
}
