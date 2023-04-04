package kr.co.nicevan.nvcat.activity;

import static kr.co.nicevan.nvcat.CommonUtil.KIOSK_LOGIN_URL;
import static kr.co.nicevan.nvcat.CommonUtil.KIOSK_ORDER_SUCCESS_URL;
import static kr.co.nicevan.nvcat.CommonUtil.KIOSK_SHOP_SELECT_URL;
import static kr.co.nicevan.nvcat.CommonUtil._대기종료;
import static kr.co.nicevan.nvcat.CommonUtil._승인요청;
import static kr.co.nicevan.nvcat.CommonUtil._승인응답;
import static kr.co.nicevan.nvcat.CommonUtil._취소요청;
import static kr.co.nicevan.nvcat.CommonUtil._취소응답;
import static kr.co.nicevan.nvcat.CommonUtil.bitmapToString;
import static kr.co.nicevan.nvcat.CommonUtil.convertCommaDecimalFormat;
import static kr.co.nicevan.nvcat.CommonUtil.hideSystemUI;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bixolon.commonlib.BXLCommonConst;
import com.bixolon.commonlib.log.LogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kr.co.nicevan.nvcat.AppConfig;
import kr.co.nicevan.nvcat.PrinterControl.PrinterManager;
import kr.co.nicevan.nvcat.R;
import kr.co.nicevan.nvcat.dialog.Dialog300;
import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.KioskOrderDTO;
import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.NicepayDTO;
import kr.co.nicevan.nvcat.dto.OrderDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.main_activity_manger.MainDialogManager;
import kr.co.nicevan.nvcat.main_activity_manger.NicepayManager;
import kr.co.nicevan.nvcat.retrofit.RevealLongCallbacks;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;
import kr.co.nicevan.nvcat.roomdb.Payment;
import kr.co.nicevan.nvcat.roomdb.PaymentDao;
import kr.co.nicevan.nvcat.roomdb.RoomDB;
import kr.co.nicevan.nvcat.service.PrinterService;
import kr.co.nicevan.nvcat.service.common.CommonService;
import kr.co.nicevan.nvcat.service.label.LabelService;
import kr.co.nicevan.nvcat.service.label.RevealLabelRespCallbacks;
import kr.co.nicevan.nvcat.service.login.LoginService;
import kr.co.nicevan.nvcat.service.order.OrderService;
import kr.co.nicevan.nvcat.service.order.RevealOrderRespCallbacks2;
import kr.co.nicevan.nvcat.service.receipt.ReceiptService;
import kr.co.nicevan.nvcat.service.receipt.RevealReceiptRespCallbacks;
import kr.co.nicevan.nvcat.util.ComponentUtil;
import kr.co.nicevan.nvcat.util.KeyStoreUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    String TAG = this.getClass().getSimpleName();
    Context context;

    public WebView webView;
//    public WebView webView2;

    RoomDB db;
    PaymentDao paymentDao;

    // 전문요청코드
    int SEND_REQUEST_CODE = 1;
    int SEND_REQUEST_CHKVALID = 2;
    int SEND_REQUEST_CHKCARDBIN = 3;
    int SEND_REQUEST_CHKCASHIC = 4;
    int SEND_REQUEST_CHKMEMBERSHIP = 5;
    int SEND_REQUEST_NORMAL = 6;

    Dialog300 dialog300; // 결제완료/영수증출력

    int waitTimeCnt = 30; // 제한시간(초)
    boolean isTimeout = false; // 제한시간 초과여부

    // 인코딩 서명이미지
    String signImgString = "";

    // WEB 결제정보 파라미터
    String payAmount = ""; // 거래금액
    String payOrderNo = ""; // 주문번호
    String payUserId = ""; // 주문자 고유 아이디(핸드폰번호)
    String payAgreenum = ""; // 승인번호 (취소요청시만 해당)
    String payAgreedate = ""; // 원거래일자(YYMMDD) (취소요청시만 해당)

    String prtAmount = ""; // 금액
    String prtTax = ""; // 부가세
    String prtTotAmount = ""; // 합계금액

    //Service DI.
    AppConfig appConfig = AppConfig.getInstance();
    ReceiptService receiptService = appConfig.receiptService();
    LabelService labelService = appConfig.labelService();
    PrinterService printerService = appConfig.printerService();
    LoginService loginService = appConfig.loginService();
    OrderService orderService = appConfig.orderService();
    CommonService commonService = appConfig.commonService();


    MainDialogManager mainDialogManager;
    NicepayManager nicePayManager;

    //카드관련 응답데이터 setDTO.
    CardDTO cardInfo = new CardDTO();
    //결제시도시 데이터 setDTO
    OrderDTO orderDTO = new OrderDTO();
    KeyStoreUtil keyStoreUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI(this);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 앱 타이틀 제거
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로 화면으로 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 제거

        setContentView(R.layout.activity_main);

        context = this;
        keyStoreUtil = KeyStoreUtil.init(this);

        final int ANDROID_NOUGAT = 24;
        if (Build.VERSION.SDK_INT >= ANDROID_NOUGAT) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Thread.setDefaultUncaughtExceptionHandler(new AppUncaughtExceptionHandler());

//         NVCAT 모듈앱 재시작요청
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

        //ID, PW 정보 없는 경우 로그인 화면으로 이동
        String id = keyStoreUtil.getData(KeyStoreUtil.ID_KEY, null);
        String pw = keyStoreUtil.getData(KeyStoreUtil.PW_KEY, null);

        if (pw == null || id == null) {
            webView.loadUrl(KIOSK_LOGIN_URL);
        } else {
            //login 실패 시 로그인 페이지로 이동
            loginService.login(id, pw, () -> webView.loadUrl(KIOSK_SHOP_SELECT_URL), () -> webView.loadUrl(KIOSK_LOGIN_URL));
        }
        // webView End =============================================================

        // webView2 Start =============================================================
//        webView2 = findWebViewByIdWithSettings(R.id.webView2);
//        webView2.addJavascriptInterface(new WebViewInterface2(this), "android2");
//        webView2.loadUrl(CommonUtil.BASE_URL + "kiosk/webview");
        // webView2 End =============================================================

        PrinterManager.init(this);
        mainDialogManager = MainDialogManager.init(this);
        nicePayManager = NicepayManager.init(this);

        /**
         * 결제요청 (결제방법선택)
         */
//        findViewById(R.id.btn_01).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 결제방법 선택 팝업
//                nicePayManager.selectPayMethod(_승인요청, new NicepayDTO.ReqPaymentDTO("55000", "", "", "55,000원"));
//            }
//        });

        /**
         * 결제취소요청
         */
//        findViewById(R.id.btn_02).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 결제방법 선택 팝업
//                nicePayManager.selectPayMethod(_취소요청, new NicepayDTO.ReqPaymentDTO("550000", "11586893", "230117", "55,000원"));
//            }
//        });

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
    protected void onResume() {
        super.onResume();
        hideSystemUI(this); // 전체화면
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            printerService.closeAll();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
        public void completeLogin(String id, String pw) {
            keyStoreUtil.storeData(KeyStoreUtil.ID_KEY, id);
            keyStoreUtil.storeData(KeyStoreUtil.PW_KEY, pw);
        }

        @JavascriptInterface
        public void showCatIdScreen() {
            Intent intent = new Intent(mContext, CatIdActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public boolean isCatIdEmpty() {
            return keyStoreUtil.getData(KeyStoreUtil.CAT_ID_KEY, null) == null;
        }

        @JavascriptInterface
        public String getCatId(){
            return keyStoreUtil.getData(KeyStoreUtil.CAT_ID_KEY, null);
        }


        @JavascriptInterface
        public void requestPayment(String orderJsonData) {

            //주문정보 DTO 저장
            orderDTO.dtoByJson(orderJsonData);
            orderDTO.formatToPrice(orderDTO.getAmount(), commonService);

            String curReqType = "";
            // 현재 거래구분(승인/취소)
            if (orderDTO.getReqType().equals("승인")) {
                curReqType = _승인요청;
            } else if (orderDTO.getReqType().equals("취소")) {
                curReqType = _취소요청;
            }

            payAmount = orderDTO.getAmount(); // 거래금액
            payOrderNo = ""; // 주문번호
            payUserId = orderDTO.getPhone(); // 주문자 번호
            payAgreenum = orderDTO.getAgreeNum(); // 승인번호 (취소요청시만 해당)

            if (orderDTO.getAgreeDate().length() >= 6) {
                payAgreedate = orderDTO.getAgreeDate().substring(0, 6); // 원거래일자(YYMMDD) (취소요청시만 해당)
            }
            // 결제방법 선택 팝업
            nicePayManager.selectPayMethod(curReqType, new NicepayDTO.ReqPaymentDTO(payAmount, payAgreenum, payAgreedate, commonService.formatByPrice(orderDTO.getAmount())));
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
     * 팝업 Dialog300 (결제완료/영수증 출력)
     */
    public void popDialog300() {
        Log.d(TAG, "popDialog300()");
        dialog300 = new Dialog300(MainActivity.this);
        dialog300.setDialogListener(new Dialog300.DialogListener() {
            @Override
            public void onPositiveClicked() {
                // 프린터 출력중 팝업
                mainDialogManager.popDialog400();
                // 프린트용 데이터 초기화
                printerService.resetPrintData();
                // 영수증 출력
                printReceipt(cardInfo);
                // 라벨 출력
                printLabel(cardInfo);
                mainDialogManager.closeDialog400();
            }

            @Override
            public void onNegativeClicked() {
                // 프린터 출력중 팝업
                mainDialogManager.popDialog400();
                // 라벨 출력
                printLabel(cardInfo);
                mainDialogManager.closeDialog400();
            }
        });
        dialog300.show();
    }

    /**
     * 전문데이터 전송
     *
     * @param senddata
     */
    public void send(String senddata) {

        Log.d(TAG, "send() - senddata : " + senddata);

        Intent sendIntent = new Intent();
        sendIntent.setAction("NICEVCAT"); //setAction에 함수명
        sendIntent.putExtra("NVCATSENDDATA", senddata); //NVCATSENDDATA에 요청전문
        sendIntent.setType("text/plain"); //setType은 text/plain 필수
        startActivityForResult(sendIntent, SEND_REQUEST_CODE);
    }

    /**
     * 전문 응답
     *
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
            mainDialogManager.closeICDialog();
            mainDialogManager.closeMsDialog();

            // resultCode == -1
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "RESULT_OK", Toast.LENGTH_SHORT).show();

                // 요청정상처리
                if (NVCAT_RETURN_CODE == 1) {
                    // 응답 전문 데이터 추출
                    RecvFS(NVCAT_RECV_DATA);
                }

                // resultCode == 0
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "RESULT_CANCELED", Toast.LENGTH_SHORT).show();

                // IC 카드리딩실패 (타임아웃)
                String errorCode = NVCAT_RECV_DATA.substring(NVCAT_RECV_DATA.length() - 2);
                if (NVCAT_RETURN_CODE == -7 || (NVCAT_RETURN_CODE == -8 && errorCode.equals("F2"))) {
                    // 대기시간 만료 안내모달
                    NicepayManager.getInstance().closePayment(_대기종료);
                }

                if (NVCAT_RETURN_CODE == -9) {
                    // MS 거래 요청
                    String sendData = nicePayManager.msPay();
                    send(sendData);
                }
            }

        } else {
            Log.d(TAG, "onActivityResult() - requestCode != SEND_REQUEST_CODE");
        }
    }

    /**
     * 응답 전문 데이터 추출
     *
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
        signImgString = getSignBitmapString();
        cardInfo = new CardDTO(prtAmount, prtTax, prtTotAmount, respDTO.getData(17), respDTO.getData(6),
                respDTO.getData(18), respDTO.getData(7), respDTO.getData(8), signImgString);


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
        if (rstResCode.equals("0000") && rstReqType.equals(_승인응답)) {
            // 응답 거래구분 (승인 : 0210)
            Log.d(TAG, "결제승인 정상 응답");
            rstResult = "정상승인"; // 코드화 할 것

            // 취소요청 정상처리
        } else if (rstResCode.equals("0000") && rstReqType.equals(_취소응답)) {
            // 응답 거래구분 (취소 : 0430)
            Log.d(TAG, "승인취소 정상 응답");
            rstResult = "정상취소"; // 코드화 할 것

        } else {
            Log.d(TAG, "결제승인 비정상 응답");
            rstResult = "비정상"; // 코드화 할 것
        }

        if (rstResCode.equals("0000")) {

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

            // 결과 web 전달
            returnPaymentResult(rstResult, rstAmount, rstOrderNo, rstUserId, rstAgreenum, rstAgreedate, rstJson);

            // 영수증 출력확인 팝업
            popDialog300();
        }
    }

    /**
     * 영수증 출력
     */
    public  void printReceipt(CardDTO cardInfo) {
        mToastHandler.obtainMessage(0, 0, 0, "print Start").sendToTarget();
        receiptService.printReceiptByOrder(cardInfo, new RevealReceiptRespCallbacks() {
            @Override
            public void onSuccess(@NonNull ReceiptDTO.ReceiptResp value) {
                Log.d("RevealCallbacks","onSuccess");
                boolean printed = printerService.receiptPrint(value, cardInfo);
                if (!printed) mToastHandler.obtainMessage(0, 0, 0, "Fail to printer open").sendToTarget();

            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Log.d("","==================================================");
                Log.d(this.getClass().getSimpleName(),"RevealReceiptRespCallbacks");
                Log.d("[error code] : ", String.valueOf(errorResponse.getStatus()));
                Log.d("[error title] : ", errorResponse.getTitle());
                Log.d("[error msg] : ", errorResponse.getMsg());
                Log.d("","==================================================");
                mToastHandler.obtainMessage(0, 0, 0, errorResponse.getMsg()).sendToTarget();
            }
        });
    }

    /**
     * 라벨 출력
     */
    public void printLabel(CardDTO cardInfo) {
        mToastHandler.obtainMessage(0, 0, 0, "print Start").sendToTarget();
        labelService.printLabelByOrder(cardInfo.getApprovalNo(), new RevealLabelRespCallbacks() {
            @Override
            public void onSuccess(@NonNull List<LabelDTO.LabelResp> value) {
                boolean printed = printerService.labelPrint(value);
                if (!printed) mToastHandler.obtainMessage(0, 0, 0, "Fail to printer02 open").sendToTarget();

            }
            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Log.d("","==================================================");
                Log.d(this.getClass().getSimpleName(),"RevealLabelRespCallbacks");
                Log.d("[error code] : ", String.valueOf(errorResponse.getStatus()));
                Log.d("[error title] : ", errorResponse.getTitle());
                Log.d("[error msg] : ", errorResponse.getMsg());
                Log.d("","==================================================");
                mToastHandler.obtainMessage(0, 0, 0, errorResponse.getMsg()).sendToTarget();
            }
        });
    }


    /**
     * 결제결과 WEB 전달
     */
    public void returnPaymentResult(String rstResult, String rstAmount, String rstOrderNo, String rstUserId, String rstAgreenum, String rstAgreedate, String rstJson) {
        Log.d(TAG, "returnPayResult()");

        // 결제결과 전달
        webView.loadUrl("javascript:retrunPayment('" + rstResult + "', '" + rstAmount + "', '" + rstOrderNo + "', '" + rstUserId + "', '" + rstAgreenum + "', '" + rstAgreedate + "')");
        /*
        WEB 자바스크립트에서 아래 형태로 처리
        function retrunPayment(rstResult, rstAmount, rstOrderNo, rstUserId, rstAgreenum, rstAgreedate) {
        }
        */

        // 결제정보 저장
//        webView2.loadUrl("javascript:savePayment('" + rstJson + "')");
        orderService.saveKioskOrders(
                new KioskOrderDTO.SaveOrders(orderDTO.getIosIds(), orderDTO.getVolumes(), orderDTO.getPhone(), rstAgreenum, null, keyStoreUtil.getData(KeyStoreUtil.CAT_ID_KEY, null)),
                new RevealLongCallbacks() {
                    @Override
                    public void on(@NonNull Long value) {
                        orderService.sendRefillAuthApi(orderDTO.getPhone(), value, new RevealOrderRespCallbacks2() {
                                    @Override
                                    public void onError(@NonNull ErrorResponse errorResponse) {
                                        Log.d("","==================================================");
                                        Log.d(this.getClass().getSimpleName(),"RevealOrderRespCallbacks2");
                                        Log.d("[error code] : ", String.valueOf(errorResponse.getStatus()));
                                        Log.d("[error title] : ", errorResponse.getTitle());
                                        Log.d("[error msg] : ", errorResponse.getMsg());
                                        Log.d("","==================================================");
                                    }
                                });
                        webView.loadUrl(KIOSK_ORDER_SUCCESS_URL + value);
                    }
                });
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
     * 사인 이미지 인코딩 문자 가져오기
     */
    public String getSignBitmapString() {

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

        } catch (Exception e) {
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
}
