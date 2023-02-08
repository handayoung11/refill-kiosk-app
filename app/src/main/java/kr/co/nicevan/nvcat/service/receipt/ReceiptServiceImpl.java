package kr.co.nicevan.nvcat.service.receipt;
import android.util.Log;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import kr.co.nicevan.nvcat.retrofit.RetrofitResponseAPI;
import kr.co.nicevan.nvcat.retrofit.RevealStringCallbacks;
import kr.co.nicevan.nvcat.service.common.CommonService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReceiptServiceImpl implements ReceiptService {

    private RetrofitClient retrofitClient;
    private RetrofitResponseAPI callAPI;
    private final CommonService commonService;

    public ReceiptServiceImpl(CommonService commonService) {
        this.commonService = commonService;
    }

    /**  2022-01-30 작성자 : 염에녹
     * refillcycle.com/kiosk/order/receipt 통신.
     * 기능 : [영수증 데이터 추출 API] 호출.
     * request : approvalNo
     * respone : shopName, companyNo, address, owner, tell, <List>items
     * return : String */
    @Override
    public void printReceiptByOrder(CardDTO card, @NonNull RevealStringCallbacks callbacks) {
        retrofitClient = RetrofitClient.getInstance();
        callAPI = RetrofitClient.getRetrofitInterface();
        callAPI.postReceipt(card.getApprovalNo()).enqueue(new Callback<ReceiptDTO.ReceiptResp>() {
            @Override
            public void onResponse(Call<ReceiptDTO.ReceiptResp> call, Response<ReceiptDTO.ReceiptResp> response) {
                Log.d(this.getClass().getSimpleName(), "Data fetch success");

                //통신성공
                if(response.isSuccessful()) {
                    ReceiptDTO.ReceiptResp receiptDTO = response.body();
                    String s = outStringForReceipt(receiptDTO, card);
                    callbacks.onSuccess(s);
                }
                else {
                    Log.d(this.getClass().getSimpleName(), "[오류발생] : "+response.code());
                }
            }
            @Override
            public void onFailure(Call<ReceiptDTO.ReceiptResp> call, Throwable t) {
                if (callbacks != null) callbacks.onError(t);
                else Log.d(this.getClass().getSimpleName(), "[오류발생] : "+t.toString());
            }
        });
    }


    /**  2022-01-30 작성자 : 염에녹
     * 기능 : 영수증 formatter.
     */
    @Override
    public String outStringForReceipt(ReceiptDTO.ReceiptResp response, CardDTO card) {
        String strData = "영수증\n";
        strData += "[매장명] "+response.getShopName()+"\n";
        strData += "[사업자번호] "+response.getCompanyNo()+"\n";
        strData += "[주소] "+ response.getAddress()+"\n";
        strData += "[대표자] "+ response.getOwner()+"\t\t\t"+"[TEL] "+ response.getTell()+"\n";
        strData += "[매출일] "+ response.getPayDate()+"\n";
        strData += "=======================================\n";
        strData += "\t\t상품명\t\t단가\t\t수량\t\t금액\t\n ";
        strData += "---------------------------------------\n";
        for(ReceiptDTO.ReceiptResp.ItemDTO i : response.getItems()){
            String name = commonService.formatterByLeftSpace(i.getItemName(), 11);
            String unitPrice = commonService.formatterByLeftSpace(String.valueOf((i.getPrice() / i.getQuantity())), 7);
            String quantity = commonService.formatterByLeftSpace(i.getQuantity() + i.getUnit(), 5);
            String price = CommonUtil.convertCommaDecimalFormat(String.valueOf(i.getPrice()));
            strData += name+"\t"+unitPrice+"\t"+quantity+"\t"+price+"\n";
        }
        strData += "---------------------------------------\n";
        strData += "합계금액\t\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "---------------------------------------\n";
        strData += "과세물품가액\t\t\t\t\t\t"+card.getDutiableVal()+"\n";
        strData += "부  가  세\t\t\t\t\t\t"+card.getTax()+"\n";
        strData += "매출합계(카드)\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "=======================================\n";
        strData += "[카드번호]\t\t"+card.getCardBin()+"\n";
        strData += "[할부개월]\t\t\t\t\t\t\t"+card.getInstallment()+"\n";
        strData += "[카드사명]\t\t\t\t\t\t"+card.getCardName()+"\n";
        strData += "[승인번호]\t\t\t\t"+card.getApprovalNo()+"\n";
        strData += "[승인일자]\t\t\t\t\t"+card.getApprovalDate()+"\n";
        strData += "[결제금액]\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "---------------------------------------\n";
        return strData;
    }
}
