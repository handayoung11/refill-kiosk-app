package kr.co.nicevan.nvcat;

import static kr.co.nicevan.nvcat.CommonUtil.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bixolon.commonlib.BXLCommonConst;
import com.bixolon.commonlib.connectivity.searcher.BXLUsbDevice;
import com.bixolon.commonlib.log.LogService;
import com.bxl.config.editor.BXLConfigLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import kr.co.nicevan.nvcat.Printer.TabPagerAdapter;
import kr.co.nicevan.nvcat.PrinterControl.BixolonPrinter;
import kr.co.nicevan.nvcat.PrinterControl.DefaultBixolonPrinterUserListener;
import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.NicepayDTO;
import kr.co.nicevan.nvcat.roomdb.Payment;
import kr.co.nicevan.nvcat.roomdb.PaymentDao;
import kr.co.nicevan.nvcat.roomdb.RoomDB;
import kr.co.nicevan.nvcat.service.ReceiptService;
import kr.co.nicevan.nvcat.service.ReceiptServiceImpl;
import kr.co.nicevan.nvcat.util.ComponentUtil;

public class MainActivity extends AppCompatActivity {

    String TAG = this.getClass().getSimpleName();
    Context context;

    public WebView webView;
    public WebView webView2;

    RoomDB db;
    PaymentDao paymentDao;

    // 프린터
    private static BixolonPrinter bxlPrinter = null; // 영수증 프린터
    private static BixolonPrinter bxlPrinter02 = null; // 라벨 프린터
    private static Fragment currentFragment;
    private static TabPagerAdapter mPagerAdapter = null;

    // 전문요청코드
    int SEND_REQUEST_CODE = 1;
    int SEND_REQUEST_CHKVALID = 2;
    int SEND_REQUEST_CHKCARDBIN = 3;
    int SEND_REQUEST_CHKCASHIC = 4;
    int SEND_REQUEST_CHKMEMBERSHIP = 5;
    int SEND_REQUEST_NORMAL = 6;

    char fs = 0x1C;

    // 팝업 다이얼로그
    Dialog100 dialog100; // 결제방법선택
    Dialog200 dialog200; // 카드투입대기
    Dialog250 dialog250; // MS결제투입대기
    Dialog300 dialog300; // 결제완료/영수증출력
    Dialog400 dialog400; // 프린터 출력중
    Dialog500 dialog500; // 영수증 출력완료
    Dialog900 dialog900; // 결제종료

    String curReqType = ""; // 현재 진행중인 거래구분(승인요청/취소요청)
    String curPayType = ""; // 현재 진행중인 결제방법(신용카드/삼성페이)

    int waitTimeCnt = 30; // 제한시간(초)
    boolean isTimeout = false; // 제한시간 초과여부

    // 인코딩 서명이미지
    String signImgString = "";

    // WEB 결제정보 파라미터
    String payAmount = ""; // 거래금액
    String payOrderNo = ""; // 주문번호
    String payUserId = ""; // 주문자 고유 아이디
    String payAgreenum = ""; // 승인번호 (취소요청시만 해당)
    String payAgreedate = ""; // 원거래일자(YYMMDD) (취소요청시만 해당)

    String prtAmount = ""; // 금액
    String prtTax = ""; // 부가세
    String prtTotAmount = ""; // 합계금액

    //RetrofitService DI.
    ReceiptService receiptService = new ReceiptServiceImpl();

    //카드관련 응답데이터 setDTO.
    CardDTO cardInfo = new CardDTO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 앱 타이틀 제거
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로 화면으로 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 제거

        setContentView(R.layout.activity_main);

        context = this;

        final int ANDROID_NOUGAT = 24;
        if (Build.VERSION.SDK_INT >= ANDROID_NOUGAT) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Thread.setDefaultUncaughtExceptionHandler(new AppUncaughtExceptionHandler());

        // NVCAT 모듈앱 재시작요청
        Intent sendIntent = new Intent();
        sendIntent.setAction("NICEVCAT");
        sendIntent.putExtra("NVCATSENDDATA", "RESTART");
        sendIntent.setType("text/plain");
        startActivityForResult(sendIntent, SEND_REQUEST_NORMAL);

        Log.d(TAG, "NVCAT RESTART");

        db = RoomDB.getDBInstance(getApplicationContext());
        paymentDao = db.paymentDao();

        // webView Start =============================================================
        webView = findWebViewByIdWithSettings(R.id.webView);
        webView.addJavascriptInterface(new WebViewInterface(this), "android");
        webView.loadUrl("https://refillcycle.com");
        // webView End =============================================================

        // webView2 Start =============================================================
        webView2 = findWebViewByIdWithSettings(R.id.webView2);
        webView2.addJavascriptInterface(new WebViewInterface2(this), "android2");
        webView2.loadUrl("https://refillcycle.com/kiosk/webview");
        // webView2 End =============================================================

        // 영수증 프린터
        bxlPrinter = new BixolonPrinter(getApplicationContext());
        bxlPrinter.setUserListener(new DefaultBixolonPrinterUserListener("PrintEvent01") {
            @Override
            public void onPrintEventOutputCompleteOccurred(int eventCode) {
                Log.d(TagPrint, "onPrintEventOutputCompleteOccurred : " + eventCode);
            }
        });

        // 라벨 프린터
        bxlPrinter02 = new BixolonPrinter(getApplicationContext());
        bxlPrinter02.setUserListener(new DefaultBixolonPrinterUserListener("PrintEvent02") {
            @Override
            public void onPrintEventOutputCompleteOccurred(int eventCode) {
                Log.d(TagPrint, "onPrintEventOutputCompleteOccurred : " + eventCode);

                // 프린터 출력중 팝업 닫기
                if (dialog400 != null && dialog400.isShowing()) {
                    dialog400.dismiss();
                }

                runOnUiThread(() -> {
                        Log.d(TAG, "출력 완료");
                        // 출력완료 팝업
                        popDialog500();
                });
            }
        });

        printOpen(); // 영수증 & 라벨 프린터 오픈

        /**
         * 결제요청 (결제방법선택)
         */
        findViewById(R.id.btn_01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 현재 거래구분(승인)
                curReqType = _승인요청;

                // 결제방법 선택 팝업
                popDialog100();
            }
        });

        /**
         * 결제취소요청
         */
        findViewById(R.id.btn_02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 현재 거래구분(취소)
                curReqType = _취소요청;

                // 결제방법 선택 팝업
                popDialog100();
            }
        });

        /**
         * 테스트
         */
        findViewById(R.id.btn_03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testFunction();
            }
        });


        String strPathLOG = "";
        File[] mediaDirs = MainActivity.this.getExternalMediaDirs();
        if (mediaDirs != null && mediaDirs.length > 0) {
            strPathLOG = mediaDirs[0].getPath() + "/Log/";
        } else {
            strPathLOG = MainActivity.this.getFilesDir().getParent() + "/Bixolon/Log/";
        }

        LogService.InitDebugLog(true,
                true,
                BXLCommonConst._LOG_LEVEL_HIGH,
                128,
                128,
                (1024 * 1024) * 10 /* 10MB */,
                0,
                strPathLOG,
                "bixolon.log");

    }

    @Override
    protected void onResume(){
        super.onResume();

        //hideSystemUI(); // 전체화면
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPrinterInstance().printerClose();
        getPrinterInstance02().printerClose();
    }


    public static BixolonPrinter getPrinterInstance() {
        return bxlPrinter;
    }
    public static BixolonPrinter getPrinterInstance02() {
        return bxlPrinter02;
    }

    public static Fragment getVisibleFragment() {
        //currentFragment = mPagerAdapter.getRegisteredFragment(currentPosition);
        return currentFragment;
    }

    public class AppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, final Throwable ex) {
            ex.printStackTrace();

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    public WebView findWebViewByIdWithSettings(int webViewId) {
        WebView webView = findViewById(webViewId);
        ComponentUtil.configWebView(webView);
        return webView;
    }


    /**
     * JavascriptInterface
     */
    public class WebViewInterface {
        Context mContext;

        WebViewInterface(Context ctx) {
            mContext = ctx;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void requestPayment(String reqType, String amount, String orderNo, String userId, String agreenum, String agreedate){
            /*
            WEB 자바스크립트에서 아래 형태로 호출
            android.requestPayment('승인', '3000', '2301101234', '01011112222', '', '');
            */

            // 현재 거래구분(승인/취소)
            if(reqType.equals("승인")) {
                curReqType = _승인요청;
            }else if(reqType.equals("취소")) {
                curReqType = _취소요청;
            }

            payAmount = amount; // 거래금액
            payOrderNo = orderNo; // 주문번호
            payUserId = userId; // 주문자 고유 아이디
            payAgreenum = agreenum; // 승인번호 (취소요청시만 해당)

            if(agreedate.length() >= 6) {
                payAgreedate = agreedate.substring(0,6); // 원거래일자(YYMMDD) (취소요청시만 해당)
            }

            // 결제방법 선택 팝업
            popDialog100();
        }
    }

    /**
     * JavascriptInterface
     */
    public class WebViewInterface2 {
        Context mContext;

        WebViewInterface2(Context ctx) {
            mContext = ctx;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 팝업 Dialog100 (결제방법)
     */
    public void popDialog100(){

        dialog100 = new Dialog100(MainActivity.this, curReqType);
        dialog100.setDialogListener(new Dialog100.DialogListener() {
            @Override
            public void onPositiveClicked(String data) {
            }
            @Override
            public void onNegativeClicked() {
                // 결제종료
                closePayment(_결제중지);
            }
            @Override
            public void onClickedBtn01(){
            }
            @Override
            public void onClickedBtn02(){
            }
            @Override
            public void choPayType(String payType){
                // 카드투입 요청
                payStep200(payType);
            }
        });
        dialog100.setCanceledOnTouchOutside(false);
        dialog100.setCancelable(false);
        dialog100.show();
    }

    /**
     * 팝업 Dialog900 (결제종료)
     */
    public void popDialog900(String curReqType, String cancelType){

        dialog900 = new Dialog900(MainActivity.this, curReqType, cancelType);
        dialog900.setDialogListener(new Dialog900.DialogListener() {
            @Override
            public void onPositiveClicked() {
            }
            @Override
            public void onNegativeClicked() {
            }
        });
        dialog900.setCanceledOnTouchOutside(false);
        dialog900.setCancelable(false);
        dialog900.show();
    }

    /**
     * 팝업 Dialog200 (결제요청 대기)
     */
    public void popDialog200(String payType){

        dialog200 = new Dialog200(MainActivity.this, curReqType, payType);
        dialog200.setDialogListener(new Dialog200.DialogListener() {
            @Override
            public void onPositiveClicked(String data) {
            }
            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "popDialog200 - 결제중지");
                // 결제종료
                closePayment(_결제중지);
            }
        });
        dialog200.setCanceledOnTouchOutside(false);
        dialog200.setCancelable(false);
        dialog200.show();
    }

    /**
     * 팝업 Dialog250 (MS결제요청 대기)
     */
    public void popDialog250(){

        dialog250 = new Dialog250(MainActivity.this);
        dialog250.setDialogListener(new Dialog250.DialogListener() {
            @Override
            public void onPositiveClicked(String data) {
            }
            @Override
            public void onNegativeClicked() {
                // 결제종료
                closePayment(_결제중지);
            }
        });
        dialog250.setCanceledOnTouchOutside(false);
        dialog250.setCancelable(false);
        dialog250.show();
    }

    /**
     * 팝업 Dialog300 (결제완료/영수증 출력)
     */
    public void popDialog300(){
        Log.d(TAG, "popDialog300()");

        dialog300 = new Dialog300(MainActivity.this);
        dialog300.setDialogListener(new Dialog300.DialogListener() {
            @Override
            public void onPositiveClicked() {
                // 프린터 출력중 팝업
                //popDialog400();

                // 프린트용 데이터 초기화
                getPrinterInstance().resetPrintData();
                getPrinterInstance02().resetPrintData();
                prtAmount = ""; prtTax = ""; prtTotAmount = "";
                cardInfo = new CardDTO();

                // 영수증 출력
                printReceipt(cardInfo.getApprovalNo());

                // 라벨 출력
                printLabel();
            }
            @Override
            public void onNegativeClicked() {
                // 프린터 출력중 팝업
                //popDialog400();

                // 라벨 출력
                printLabel();
            }
        });
        dialog300.setCanceledOnTouchOutside(false);
        dialog300.setCancelable(false);
        dialog300.show();
    }

    /**
     * 팝업 Dialog400 (프린터 출력중)
     */
    public void popDialog400(){

        dialog400 = new Dialog400(MainActivity.this);
        dialog400.setDialogListener(new Dialog400.DialogListener() {
            @Override
            public void onPositiveClicked() {
            }
            @Override
            public void onNegativeClicked() {
            }
        });
        dialog400.setCanceledOnTouchOutside(false);
        dialog400.setCancelable(false);
        dialog400.show();
    }

    /**
     * 팝업 Dialog500 (영수증 출력완료)
     */
    public void popDialog500(){

        dialog500 = new Dialog500(MainActivity.this, getPrinterInstance().isCompleted(), getPrinterInstance02().isCompleted());
        dialog500.setDialogListener(new Dialog500.DialogListener() {
            @Override
            public void onPositiveClicked() {
            }
            @Override
            public void onNegativeClicked() {
            }
        });
        dialog500.setCanceledOnTouchOutside(false);
        dialog500.setCancelable(false);
        dialog500.show();
    }


    /**
     * 결제종료
     */
    public void closePayment(String cancelType) {
        Log.d(TAG, "closePayment()");
        //Toast.makeText(getApplicationContext(), "결제가 취소되었습니다.", Toast.LENGTH_SHORT).show();

        // 결제취소 전달
        webView.loadUrl("javascript:cancelPayment('" + cancelType + "', '" + payOrderNo + "', '" + payUserId + "')");
        /*
        WEB 자바스크립트에서 아래 형태로 처리
        function cancelPayment(cancelType, payOrderNo, payUserId) {
        }
        */

        // 결제종료 팝업
        popDialog900(curReqType, cancelType);
    }

    /**
     * 결제단계 200 (결제요청)
     */
    public void payStep200(String payType){

        this.curPayType = payType;

        // 결제요청 대기 팝업
        popDialog200(payType);

        // NVCAT 결제요청
        reqPayment(curReqType, _IC카드);
    }

    /**
     * 결제단계 250 (MS 결제요청)
     */
    public void payStep250(){
        // MS결제요청 대기 팝업
        popDialog250();

        // NVCAT 결제요청
        reqPayment(curReqType, _MS카드);
    }


    /**
     * IC결제요청
     */
    public void reqPayment(String reqType, String wcc){

        Log.d(TAG, "reqType:" + reqType + ", wcc:" + wcc);

        // 데이터 초기화
        signImgString = ""; // 인코딩 서명이미지

        // WEB 결제정보 파라미터 테스트 데이터
        payAmount = "55000"; // 거래금액
        payOrderNo = ""; // 주문번호
        payUserId = ""; // 주문자 고유 아이디
        payAgreenum = "11586893"; // 승인번호 (취소요청시만 해당)
        payAgreedate = "230117"; // 원거래일자(YYMMDD) (취소요청시만 해당)

        int tax = Integer.parseInt(payAmount) * 10 / 110; // 부가세

        // 결제정보 세팅
        String spAmount = payAmount; // 거래금액
        String spTax = String.valueOf(tax); // 부가세
        String spBongsa = "0"; // 봉사료
        String spHalbu = "00"; // 할부
        String spAgreenum = payAgreenum; // 승인번호
        String spAgreedate = payAgreedate; // 원거래일자(YYMMDD)
        String spMyunse = "0"; // 면세금액
        String spTxtnum = ""; // 전문관리번호(CATID(10) + MMDDhhmmss)
        String spFiller = ""; // Filler
        String spTxt = ""; // 전문TEXT
        String spDevicegb = ""; // 기종구분
        String spSigndata = ""; // 서명데이터 (NVCAT 모듈에서 알아서 전송)

        // 전문데이터 세팅
        String senddata = "";
        senddata = senddata + reqType + fs; // 거래구분 (승인:0200, 취소:0420)
        senddata = senddata + "10" + fs; // 거래유형 (신용:10)
        senddata = senddata + wcc + fs; // WCC(카드:I, FALLBACK:F)
        senddata = senddata + spAmount + fs; // 거래금액
        senddata = senddata + spTax + fs; // 부가세
        senddata = senddata + spBongsa + fs; // 봉사료
        senddata = senddata + spHalbu + fs; // 할부
        senddata = senddata + spAgreenum + fs; // 승인번호
        senddata = senddata + spAgreedate + fs; // 원거래일자(YYMMDD)
        senddata = senddata + CATID + fs; // CATID
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + spMyunse + fs; // 면세금액
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + spTxtnum + fs; // 전문관리번호(CATID(10) + MMDDhhmmss)
        senddata = senddata + spFiller + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + spTxt + fs; // 전문TEXT
        senddata = senddata + spDevicegb + fs; // 기종구분
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + spSigndata + fs; // 서명데이터
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;
        senddata = senddata + "" + fs;

        // 전문 전송
        send(senddata);
    }

    /**
     * 전문데이터 전송
     * @param senddata
     */
    private void send(String senddata) {

        Log.d(TAG, "send() - senddata : " + senddata);

        Intent sendIntent = new Intent();
        sendIntent.setAction("NICEVCAT"); //setAction에 함수명
        sendIntent.putExtra("NVCATSENDDATA", senddata); //NVCATSENDDATA에 요청전문
        sendIntent.setType("text/plain"); //setType은 text/plain 필수
        startActivityForResult(sendIntent, SEND_REQUEST_CODE);
    }

    /**
     * 전문 응답
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int NVCAT_RETURN_CODE = data.getIntExtra("NVCATRETURNCODE", -99); // 리턴코드
        String NVCAT_RECV_DATA = data.getStringExtra("NVCATRECVDATA"); // 응답메세지

        Log.d(TAG, "onActivityResult() - requestCode : " + requestCode);
        Log.d(TAG, "onActivityResult() - resultCode : " + resultCode);
        Log.d(TAG, "onActivityResult() - NVCATRETURNCODE : " + NVCAT_RETURN_CODE);
        Log.d(TAG, "onActivityResult() - NVCATRECVDATA : " + NVCAT_RECV_DATA);

        // requestCode == 1
        if (requestCode == SEND_REQUEST_CODE) {

            // 카드투입 대기팝업 닫기
            if (dialog200 != null && dialog200.isShowing()) {
                dialog200.dismiss();
            }
            if (dialog250 != null && dialog250.isShowing()) {
                dialog250.dismiss();
            }

            // resultCode == -1
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "RESULT_OK", Toast.LENGTH_SHORT).show();

                // 요청정상처리
                if(NVCAT_RETURN_CODE == 1) {
                    // 응답 전문 데이터 추출
                    RecvFS(NVCAT_RECV_DATA);
                }

            // resultCode == 0
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "RESULT_CANCELED", Toast.LENGTH_SHORT).show();

                // IC 카드리딩실패 (타임아웃)
                if(NVCAT_RETURN_CODE == -7) {
                    // 결제중지
                    closePayment(_대기종료);
                }

                // FALLBACK 발생 - NVCATRETURNCODE : -9, NVCATRECVDATA : FALLBACK 재시도 사용 안함
                if(NVCAT_RETURN_CODE == -9) {
                    // MS 거래 요청
                    payStep250();
                }
            }

        }else{
            Log.d(TAG, "onActivityResult() - requestCode != SEND_REQUEST_CODE");
        }
    }

    /**
     * 응답 전문 데이터 추출
     * @param recvdata
     */
    private void RecvFS(String recvdata) {
        NicepayDTO.CompleteOrdereRespDTO respDTO =
                new NicepayDTO.CompleteOrdereRespDTO(recvdata);

        // 프린트 데이터
        int surtax = respDTO.getIntData(4); //부가세
        int totalPrice = respDTO.getIntData(3); //거래금액
        int tmpAmount = totalPrice - surtax;
        prtAmount = convertCommaDecimalFormat(tmpAmount + ""); // 금액
        prtTax = convertCommaDecimalFormat(surtax + ""); // 부가세 포맷
        prtTotAmount = convertCommaDecimalFormat(totalPrice + ""); // 합계금액
        cardInfo = new CardDTO(prtAmount, prtTax, prtTotAmount, respDTO.getData(17), respDTO.getData(6),
                respDTO.getData(18), respDTO.getData(7), respDTO.getData(8));

        // 사인 이미지 추출
        signImgString = getSignBitmapString();

        // WEB 결과 데이터
        String rstResult = "";
        String rstOrderNo = payOrderNo; // 주문번호
        String rstUserId = payUserId; // 주문자 고유 아이디
        String rstReqType = respDTO.getData(0); // 거래구분
        String rstReqKind = respDTO.getData(1); // 거래유형
        String rstResCode = respDTO.getData(2); // 응답코드
        String rstAmount = Integer.toString(Integer.parseInt(respDTO.getData(3))); // 거래금액
        String rstTax = Integer.toString(Integer.parseInt(respDTO.getData(4))); // 부가세
        String rstBongsa = Integer.toString(Integer.parseInt(respDTO.getData(5))); // 봉사료
        String rstHalbu = respDTO.getData(6); // 할부
        String rstAgreenum = respDTO.getData(7).trim(); // 승인번호
        String rstAgreedate = respDTO.getData(8).trim(); // 승인일자
        String rstBankCd1 = respDTO.getData(9); // 발급사코드
        String rstBankNm1 = respDTO.getData(10).trim(); // 발급사명
        String rstBankCd2 = respDTO.getData(11); // 매입사코드
        String rstBankNm2 = respDTO.getData(12).trim(); // 매입사명
        String rstStoreNo = respDTO.getData(13).trim(); // 가맹점번호
        String rstCatId = respDTO.getData(14); // CATID
        String rstResMsg = respDTO.getData(16).trim(); // 응답메세지
        String rstCardNo = respDTO.getData(17); // 카드번호
        String rstCardGubun = respDTO.getData(18); // 카드구분
        String rstMngNo = respDTO.getData(19); // 전문관리번호
        String rstSeqNo = respDTO.getData(20); // 거래일련번호
        String rstSignImg = signImgString; // 서명이미지
        String rstJson = "";


        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("order_no", rstOrderNo);
        map.put("user_id", rstUserId);
        map.put("req_type", rstReqType);
        map.put("req_kind", rstReqKind);
        map.put("res_code", rstResCode);
        map.put("amount", rstAmount);
        map.put("tax", rstTax);
        map.put("bongsa", rstBongsa);
        map.put("halbu", rstHalbu);
        map.put("agree_num", rstAgreenum);
        map.put("agree_date", rstAgreedate);
        map.put("bank_cd1", rstBankCd1);
        map.put("bank_nm1", rstBankNm1);
        map.put("bank_cd2", rstBankCd2);
        map.put("bank_nm2", rstBankNm2);
        map.put("store_no", rstStoreNo);
        map.put("cat_id", rstCatId);
        map.put("res_msg", rstResMsg);
        map.put("card_no", rstCardNo);
        map.put("card_gubun", rstCardGubun);
        map.put("mng_no", rstMngNo);
        map.put("seq_no", rstSeqNo);
        map.put("sign_img", rstSignImg);
        try {
            rstJson = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        // 승인요청 정상처리
        if(rstResCode.equals("0000") && rstReqType.equals(_승인응답) ) {
            // 응답 거래구분 (승인 : 0210)
            Log.d(TAG, "결제승인 정상 응답");
            rstResult = "정상승인"; // 코드화 할 것

        // 취소요청 정상처리
        }else if(rstResCode.equals("0000") && rstReqType.equals(_취소응답) ){
            // 응답 거래구분 (취소 : 0430)
            Log.d(TAG, "승인취소 정상 응답");
            rstResult = "정상취소"; // 코드화 할 것

        }else{
            Log.d(TAG, "결제승인 비정상 응답");
            rstResult = "비정상"; // 코드화 할 것
        }

        if(rstResCode.equals("0000") ){

            // 로컬DB 저장
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Payment payment = new Payment();

                    payment.order_no = rstOrderNo;
                    payment.user_id = rstUserId;
                    payment.req_type = rstReqType;
                    payment.req_kind = rstReqKind;
                    payment.res_code = rstResCode;
                    payment.amount = rstAmount;
                    payment.tax = rstTax;
                    payment.bongsa = rstBongsa;
                    payment.halbu = rstHalbu;
                    payment.agree_num = rstAgreenum;
                    payment.agree_date = rstAgreedate;
                    payment.bank_cd1 = rstBankCd1;
                    payment.bank_nm1 = rstBankNm1;
                    payment.bank_cd2 = rstBankCd2;
                    payment.bank_nm2 = rstBankNm2;
                    payment.store_no = rstStoreNo;
                    payment.cat_id = rstCatId;
                    payment.res_msg = rstResMsg;
                    payment.card_no = rstCardNo;
                    payment.card_gubun = rstCardGubun;
                    payment.mng_no = rstMngNo;
                    payment.seq_no = rstSeqNo;
                    payment.sign_img = rstSignImg;

                    paymentDao.insertPayment(payment);
                }
            }).start();

            // 영수증 출력확인 팝업
            popDialog300();
        }

        // 결과 web 전달
        returnPaymentResult(rstResult, rstAmount, rstOrderNo, rstUserId, rstAgreenum, rstAgreedate, rstJson);
    }

    /**
     * 프린터 오픈
     */
    public void printOpen(){

        int portType = BXLConfigLoader.DEVICE_BUS_USB;
        Boolean checkBoxAsyncMode = true;

        Set<UsbDevice> usbDevices = BXLUsbDevice.refreshUsbDevicesList(this, false);
        if (usbDevices != null && !usbDevices.isEmpty()) {
            for (UsbDevice device : usbDevices) {
                //logical name
                String productName = device.getProductName();
                //address
                String deviceName = device.getDeviceName();

                if(productName.equals("BK3-3")){ // 영수증 프린터
                    getPrinterInstance().printerOpen(portType, productName, deviceName, checkBoxAsyncMode);
                }

                if(productName.equals("BK5-3")){ // 라벨 프린터
                    getPrinterInstance02().printerOpen(portType, productName, deviceName, checkBoxAsyncMode);
                }
            }
        }else {
            Log.d(TAG, "Not found USB devices");
        }
    }

    /**
     * 영수증 출력
     */
    public void printReceipt(String approvalNo){

        mToastHandler.obtainMessage(0,0,0,"print Start").sendToTarget();

        if (getPrinterInstance().isOpen()) {
            Log.d(TAG, "P-printer open!!!");

            prtAmount = "2,700"; // 금액
            prtTax = "300"; // 부가세
            prtTotAmount = "3,000"; // 합계금액

            String strData = "";
            strData = strData + "\n";
            strData = strData + "\n";
            strData = strData + "====================\n";
            strData = strData + "영 수 증\n";
            strData = strData + "금  액 : " + prtAmount + "원\n";
            strData = strData + "부가세 : " + prtTax + "원\n";
            strData = strData + "합  계 : " + prtTotAmount + "원\n";
            strData = strData + "====================";
            strData = strData + "\n";
            strData = strData + "\n";
            strData = strData + "\n";
            strData = strData + "\n";

            int alignment = 1;
            int attribute = 1;
            int spinnerSize = 0;

            Log.d(TAG, "P-strData : " + strData);
            Log.d(TAG, "P-alignment : " + alignment);
            Log.d(TAG, "P-attribute : " + attribute);
            Log.d(TAG, "P-spinnerSize : " + spinnerSize);

            getPrinterInstance().printText(strData, alignment, attribute, (spinnerSize + 1));

            Bitmap stringBitmap = stringToBitmap(signImgString);
            getPrinterInstance().printImage(stringBitmap, 384, -1, 50, 0, 1);

            getPrinterInstance().cutPaper();


            /**
             * <프린터 출력 기능 개발완료>
             * 개발환경이 상이하여 해당 코드 사용시 일부 환경에서 error 발생됨.
             * 개발완료한 코드 우선 주석처리.
             */
//            RequestDTO.ReceiptDTO request = new RequestDTO.ReceiptDTO();
//            request.setApprovalNo(approvalNo);
//            receiptService.printReceiptByOrder(
//                    request,
//                    cardInfo,
//                    new RevealStringCallbacks() {
//                        @Override
//                        public void onSuccess(@NonNull String value) {
//                            Log.d("RevealCallbacks","onSuccess");
//
//                            String strData = "출력데이터: " + value;
//                            int alignment = 1;
//                            int attribute = 1;
//                            int spinnerSize = 0;
//
//                            Log.d(TAG, "P-strData : " + strData);
//                            Log.d(TAG, "P-alignment : " + alignment);
//                            Log.d(TAG, "P-attribute : " + attribute);
//                            Log.d(TAG, "P-spinnerSize : " + spinnerSize);
//
//                            getPrinterInstance().printText(strData, alignment, attribute, (spinnerSize + 1));
//
//                            Bitmap stringBitmap = CommonUtil.stringToBitmap(signImgString);
//                            getPrinterInstance().printImage(stringBitmap, 384, -1, 50, 0, 1);
//
//                            getPrinterInstance().cutPaper();
//                        }
//
//                        @Override
//                        public void onError(@NonNull Throwable throwable) {
//                            Log.d("RevealCallbacks","onError");
//                            Log.d(this.getClass().getSimpleName(),"RevealReceiptPlaceCallbacks");
//                            Log.d(this.getClass().getSimpleName(), throwable.toString());
//                        }
//                    });
        } else {
            mToastHandler.obtainMessage(0, 0, 0, "Fail to printer open").sendToTarget();
        }
    }

    /**
     * 라벨 출력
     */
    public void printLabel(){

        mToastHandler.obtainMessage(0,0,0,"print Start").sendToTarget();

        if (getPrinterInstance02().isOpen()) {
            Log.d(TAG, "P-printer02 open!!!");

            String strData = "";
            strData = strData + "====================\n";
            strData = strData + "라벨출력\n";

            int alignment = 1;
            int attribute = 1;
            int spinnerSize = 0;

            Log.d(TAG, "P-strData : " + strData);
            Log.d(TAG, "P-alignment : " + alignment);
            Log.d(TAG, "P-attribute : " + attribute);
            Log.d(TAG, "P-spinnerSize : " + spinnerSize);

            getPrinterInstance02().printText(strData, alignment, attribute, (spinnerSize + 1));

            getPrinterInstance02().formFeed();

        } else {
            mToastHandler.obtainMessage(0, 0, 0, "Fail to printer02 open").sendToTarget();
        }
    }


    /**
     * 결제결과 WEB 전달
     */
    public void returnPaymentResult(String rstResult, String rstAmount, String rstOrderNo, String rstUserId, String rstAgreenum, String rstAgreedate, String rstJson){
        Log.d(TAG, "returnPayResult()");

        // 결제결과 전달
        webView.loadUrl("javascript:retrunPayment('" + rstResult + "', '" + rstAmount + "', '" + rstOrderNo + "', '" + rstUserId + "', '" + rstAgreenum + "', '" + rstAgreedate + "')");
        /*
        WEB 자바스크립트에서 아래 형태로 처리
        function retrunPayment(rstResult, rstAmount, rstOrderNo, rstUserId, rstAgreenum, rstAgreedate) {
        }
        */

        // 결제정보 저장
        webView2.loadUrl("javascript:savePayment('" + rstJson + "')");
    }

    public final Handler mToastHandler = new Handler(new Handler.Callback() {
        @SuppressWarnings("unchecked")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String data1 = (String) msg.obj;
                    if (data1 != null && data1.length() > 0) {
                        Toast.makeText(getApplicationContext(), data1, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    break;
            }
            return false;
        }
    });


    /**
     * 전체화면
     */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        decorView.requestLayout();
    }

    /**
     * 사인 이미지 인코딩 문자 가져오기
     */
    public String getSignBitmapString(){

        String bitmapString = ""; // 변환된 인코딩 문자

        try {
            // NVCAT 모듈에서 생성한 서명 이미지
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "";
            Log.d(TAG, "path :: " + path);
            File dir = new File(path);
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String filename = pathname.getName().toLowerCase(Locale.US);

                    Log.d(TAG, "pathname : " + filename);
                    Log.d(TAG, "startsWith : " + filename.startsWith("signature_"));
                    Log.d(TAG, "endsWith : " + filename.endsWith(".bmp"));

                    return filename.startsWith("signature_") && filename.endsWith(".bmp");
                }
            });

            String signFilePath = ""; // 파일경로
            Log.d(TAG, "files.length : " + files.length);
            if (files.length == 1) {
                signFilePath = files[0].getAbsolutePath();
                Log.d(TAG, "signFilePath :: " + signFilePath);
                Bitmap bitmap = BitmapFactory.decodeFile(signFilePath);
                bitmapString = bitmapToString(bitmap);
                Log.d(TAG, "bitmapString :: " + bitmapString);
            }

            for (File f : files) {
                f.delete();
            }

        }catch(Exception e){
            Log.d(TAG, "Exception : " + e.getMessage());
            return "";
        }

        return bitmapString;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void testFunction(){
    }

}
