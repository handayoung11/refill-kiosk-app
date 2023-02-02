package kr.co.nicevan.nvcat.service;
import android.util.Log;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.RequestDTO;
import kr.co.nicevan.nvcat.dto.ResponseDTO;
import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import kr.co.nicevan.nvcat.retrofit.RetrofitResponseAPI;
import kr.co.nicevan.nvcat.retrofit.RevealStringCallbacks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReceiptServiceImpl implements ReceiptService {

    private RetrofitClient retrofitClient;
    private RetrofitResponseAPI callAPI;


    /**  2022-01-30 작성자 : 염에녹
     * refillcycle.com/kiosk/order/receipt 통신.
     * 기능 : [영수증 데이터 추출 API] 호출.
     * request : approvalNo
     * respone : shopName, companyNo, address, owner, tell, <List>items
     * return : String */
    @Override
    public void printReceiptByOrder(RequestDTO.ReceiptDTO request, CardDTO card, @NonNull RevealStringCallbacks callbacks) {
        retrofitClient = RetrofitClient.getInstance();
        callAPI = RetrofitClient.getRetrofitInterface();
        callAPI.postReceipt(request).enqueue(new Callback<ResponseDTO.ReceiptDTO>() {
            @Override
            public void onResponse(Call<ResponseDTO.ReceiptDTO> call, Response<ResponseDTO.ReceiptDTO> response) {
                Log.d(this.getClass().getSimpleName(), "Data fetch success");

                //통신성공
                if(response.isSuccessful()) {
                    ResponseDTO.ReceiptDTO receiptDTO = response.body();
                    String s = outStringForReceipt(receiptDTO, card);
                    callbacks.onSuccess(s);
                }
                else {
                    Log.d(this.getClass().getSimpleName(), "[오류발생] : "+response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseDTO.ReceiptDTO> call, Throwable t) {
                Log.d(this.getClass().getSimpleName(), "[오류발생] : "+t.toString());
                if (callbacks != null)
                    callbacks.onError(t);
            }
        });
    }


    /**  2022-01-30 작성자 : 염에녹
     * 기능 : 영수증 formatter.
     */
    @Override
    public String outStringForReceipt(ResponseDTO.ReceiptDTO response, CardDTO card) {
        String strData = "영수증\n";
        strData += "[매장명] "+response.getShopName()+"\n";
        strData += "[사업자번호] "+response.getCompanyNo()+"\n";
        strData += "[주소] "+ response.getAddress()+"\n";
        strData += "[대표자] "+ response.getOwner()+"\t\t\t"+"[TEL] "+ response.getTell()+"\n";
        strData += "[매출일] "+ response.getPayDate()+"\n";
        strData += "=======================================\n";
        strData += "\t\t상품명\t\t단가\t\t수량\t\t금액\t\n ";
        strData += "---------------------------------------\n";
        for(ResponseDTO.ReceiptDTO.ItemDTO i : response.getItems()){
            String name = formatterByLeftSpace(i.getItemName(), 11);
            String unitPrice = formatterByLeftSpace(String.valueOf((i.getPrice()/i.getQuantity())), 7);
            String quantity = formatterByLeftSpace(i.getQuantity()+i.getUnit(), 5);
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

    /**  2022-01-30 작성자 : 염에녹
     * 기능 : 문자열을 원하는 길이만큼 subString.
     * Ex1) formatterByLeftSpace("안녕하세요", 3) -> "안녕하"
     * Ex2) formatterByLeftSpace("안녕하세요", 10) -> "     안녕하세요"
     */
    @Override
    public String formatterByLeftSpace(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString.substring(0, length);
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(' ');
        }
        sb.append(inputString);
        return sb.toString();
    }
}
