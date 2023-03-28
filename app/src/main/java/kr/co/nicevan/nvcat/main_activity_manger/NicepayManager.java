package kr.co.nicevan.nvcat.main_activity_manger;

import static kr.co.nicevan.nvcat.CommonUtil._IC카드;
import static kr.co.nicevan.nvcat.CommonUtil._MS카드;
import static kr.co.nicevan.nvcat.dto.NicepayDTO.fs;

import android.util.Log;

import kr.co.nicevan.nvcat.activity.MainActivity;
import kr.co.nicevan.nvcat.dto.NicepayDTO;
import kr.co.nicevan.nvcat.util.KeyStoreUtil;

public class NicepayManager {

    private static NicepayManager instance;

    private MainActivity mainActivity;

    private String curReqType;

    private String TAG = this.getClass().getSimpleName();

    NicepayDTO.ReqPaymentDTO payInfo;

    private NicepayManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static NicepayManager init(MainActivity mainActivity) {
        if (instance != null && instance.mainActivity == mainActivity) {
            return instance;
        }
        instance = new NicepayManager(mainActivity);
        return instance;
    }

    public static NicepayManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NicepayManager가 초기화되지 않았습니다.");
        }
        return instance;
    }

    public void selectPayMethod(String curReqType, NicepayDTO.ReqPaymentDTO dto) {
        this.curReqType = curReqType;
        payInfo = dto;
        MainDialogManager.getInstance().popDialog100(curReqType, dto.getPayAmountFormat());
    }

    public String msPay() {
        // MS결제요청 대기 팝업
        MainDialogManager.getInstance().popMsDialog();

        // NVCAT 결제요청
        return reqPayment(_MS카드);
    }

    /**
     * IC 결제
     */
    public String icPay(String payType, String totPrice) {
        // IC,삼성카드 결제요청 대기 팝업
        MainDialogManager.getInstance().popICDialog(curReqType, payType, totPrice);

        // NVCAT 결제요청
        String sendData = reqPayment(_IC카드);
        mainActivity.send(sendData);

        return sendData;
    }

    private String reqPayment(String wcc) {

        Log.d(TAG, "reqType:" + curReqType + ", wcc:" + wcc);
        int tax = Integer.parseInt(payInfo.getPayAmount()) * 10 / 110; // 부가세

        // 결제정보 세팅
        // reqType: 거래구분 (승인:0200, 취소:0420)
        String spReqStyle = "10"; //거래유형
        // wcc: WCC(카드:I, FALLBACK:F)
        String spAmount = payInfo.getPayAmount(); // 거래금액
        String spTax = String.valueOf(tax); // 부가세
        String spBongsa = "0"; // 봉사료
        String spHalbu = "00"; // 할부
        String spAgreenum = payInfo.getPayAgreenum(); // 승인번호
        String spAgreedate = payInfo.getPayAgreedate(); // 원거래일자(YYMMDD)
        String spMyunse = "0"; // 면세금액
        String spTxtnum = ""; // 전문관리번호(CATID(10) + MMDDhhmmss)
        String spFiller = ""; // Filler
        String spTxt = ""; // 전문TEXT
        String spDevicegb = ""; // 기종구분
        String spSigndata = ""; // 서명데이터 (NVCAT 모듈에서 알아서 전송)

        // 전문데이터 세팅
        String sendDataArr[] = {curReqType, spReqStyle, wcc, spAmount, spTax, spBongsa, spHalbu, spAgreenum, spAgreedate,
                KeyStoreUtil.getInstance().getData(KeyStoreUtil.CAT_ID_KEY, null), "", "", "",
                spMyunse, "", "", spTxtnum, spFiller, "", spTxt, spDevicegb, "", "", "", spSigndata, "", "", "", "", ""};
        StringBuilder sendData = new StringBuilder();

        for (int i = 0; i < sendDataArr.length; i++) {
            sendData.append(sendDataArr[i]);
            sendData.append(fs);
        }

        Log.d(TAG, sendData.toString());
        return sendData.toString();
    }

    public void closePayment(String cancelType) {
        Log.d(TAG, "closePayment()");

        // 결제종료 팝업
        MainDialogManager.getInstance().popDialog900(curReqType, cancelType);
    }
}
