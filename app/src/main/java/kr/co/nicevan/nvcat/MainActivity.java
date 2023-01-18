package kr.co.nicevan.nvcat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Collection;

import kr.co.nicevan.nvcat.Printer.TabPagerAdapter;
import kr.co.nicevan.nvcat.PrinterControl.BixolonPrinter;

public class MainActivity extends AppCompatActivity {

    String TAG = this.getClass().getSimpleName();
    Context context;

    // 프린터
    private static BixolonPrinter bxlPrinter = null;
    private static Fragment currentFragment;
    private static int currentPosition = 0;
    private TabLayout mTabLayout = null;
    private ViewPager mViewPager = null;
    private static TabPagerAdapter mPagerAdapter = null;

    // 전문요청코드
    int SEND_REQUEST_CODE = 1;
    int SEND_REQUEST_CHKVALID = 2;
    int SEND_REQUEST_CHKCARDBIN = 3;
    int SEND_REQUEST_CHKCASHIC = 4;
    int SEND_REQUEST_CHKMEMBERSHIP = 5;
    int SEND_REQUEST_NORMAL = 6;

    char fs = 0x1C;

    // 응답 전문 데이터
    String strRecv01, strRecv02, strRecv03, strRecv04, strRecv05, strRecv06, strRecv07, strRecv08, strRecv09, strRecv10, strRecv11, strRecv12, strRecv13, strRecv14, strRecv15, strRecv16, strRecv17, strRecv18, strRecv19, strRecv20, strRecv21, strRecv22, strRecv23, strRecv24, strRecv25, strRecv26, strRecv27, strRecv28, strRecv29, strRecv30;

    // 팝업 다이얼로그
    String dialogNo = ""; // 다이얼로그 번호
    Dialog100 dialog100; // 결제방법선택
    Dialog200 dialog200; // 카드투입대기
    Dialog250 dialog250; // MS결제투입대기
    Dialog300 dialog300; // 결제완료/영수증출력
    Dialog400 dialog400; // 영수증 출력중
    Dialog500 dialog500; // 영수증 출력완료
    Dialog900 dialog900; // 결제종료

    // 인코딩 서명이미지
    String imgString = "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";

    String curReqType = ""; // 현재 진행중인 거래구분(승인요청/취소요청)

    int waitTimeCnt = 30; // 제한시간(초)
    boolean isTimeout = false; // 제한시간 초과여부

    // 프린트용 데이터
    String prtAmount = ""; // 금액
    String prtTax = ""; // 부가세
    String prtTotAmount = ""; // 합계금액


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

        // 프린터
        bxlPrinter = new BixolonPrinter(getApplicationContext());
        bxlPrinter.setUserListener(new BixolonPrinter.UserListener() {
            @Override
            public void onEvent(int outId) {
                String data = String.valueOf(outId);
                Log.d("MainActivity", "resPrintCompleteOccurred : " + data);

                if(outId == 3) { // 프린터 명령수 만큼

                    // 영수증 출력중 팝업 닫기
                    if (dialog400 != null && dialog400.isShowing()) {
                        dialog400.dismiss();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "영수증 출력 완료");
                            // 영수증 출력완료 팝업
                            popDialog500();
                        }
                    });

                    bxlPrinter.printerClose();

                    // 프린트 완료 후속처리
                    completePrintReceipt();
                }
            }
        });

        /**
         * 결제취소요청
         */
        findViewById(R.id.iv_01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 현재 거래구분(취소)
                curReqType = CommonUtil._취소요청;

                // 결제방법 선택 팝업
                popDialog100();
            }
        });

        /**
         * 결제요청 (결제방법선택)
         */
        findViewById(R.id.iv_02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 현재 거래구분(승인)
                curReqType = CommonUtil._승인요청;

                // 결제방법 선택 팝업
                popDialog100();
            }
        });

        /**
         * 결제요청 (결제방법선택)
         */
        findViewById(R.id.iv_03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 현재 거래구분(승인)
                curReqType = CommonUtil._승인요청;

                // 결제방법 선택 팝업
                popDialog100();
            }
        });


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
    }


    public static BixolonPrinter getPrinterInstance() {
        return bxlPrinter;
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
                closePayment(CommonUtil._결제중지);
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

        if(payType.equals(CommonUtil._신용카드)){
            dialogNo = CommonUtil._결제요청대기_신용카드;
        }else if(payType.equals(CommonUtil._삼성페이)){
            dialogNo = CommonUtil._결제요청대기_삼성페이;
        }
        dialog200 = new Dialog200(MainActivity.this, curReqType, payType);
        dialog200.setDialogListener(new Dialog200.DialogListener() {
            @Override
            public void onPositiveClicked(String data) {
            }
            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "popDialog200 - 결제중지");
                // 결제종료
                closePayment(CommonUtil._결제중지);
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
                closePayment(CommonUtil._결제중지);
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
                // 영수증 출력중 팝업
                //popDialog400();

                // 영수증 출력
                printReceipt();
            }
            @Override
            public void onNegativeClicked() {
                // 영수증 미출력

                // 라벨 출력 완료 가정
                // 영수증 출력완료 팝업
                popDialog500();
            }
        });
        dialog300.setCanceledOnTouchOutside(false);
        dialog300.setCancelable(false);
        dialog300.show();
    }

    /**
     * 팝업 Dialog400 (영수증 출력중)
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

        dialog500 = new Dialog500(MainActivity.this);
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
        Toast.makeText(getApplicationContext(), "결제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "closePayment()");
        // 결제취소 전달

        // 결제종료 팝업
        popDialog900(curReqType, cancelType);
    }

    /**
     * 결제단계 200 (결제요청)
     */
    public void payStep200(String payType){
        // 결제요청 대기 팝업
        popDialog200(payType);

        // NVCAT 결제요청
        reqPayment(curReqType, CommonUtil._IC카드);
    }

    /**
     * 결제단계 250 (MS 결제요청)
     */
    public void payStep250(){
        // MS결제요청 대기 팝업
        popDialog250();

        // NVCAT 결제요청
        reqPayment(curReqType, CommonUtil._MS카드);
    }


    /**
     * IC결제요청
     */
    public void reqPayment(String reqType, String wcc){

        curReqType = reqType;

        // 응답 전문 데이터 초기화
        strRecv01 = ""; strRecv02 = ""; strRecv03 = ""; strRecv04 = ""; strRecv05 = ""; strRecv06 = ""; strRecv07 = ""; strRecv08 = ""; strRecv09 = ""; strRecv10 = ""; strRecv11 = ""; strRecv12 = ""; strRecv13 = ""; strRecv14 = ""; strRecv15 = ""; strRecv16 = ""; strRecv17 = ""; strRecv18 = ""; strRecv19 = ""; strRecv20 = ""; strRecv21 = ""; strRecv22 = ""; strRecv23 = ""; strRecv24 = ""; strRecv25 = ""; strRecv26 = ""; strRecv27 = ""; strRecv28 = ""; strRecv29 = ""; strRecv30 = "";

        // 프린트용 데이터 초기화
        prtAmount = ""; prtTax = ""; prtTotAmount = "";

        // WEB 결제정보 파라미터
        String userId = ""; // 주문자 고유아이디
        String orderNo = ""; // 주문번호
        int amount = 55000; // 거래금액
        int tax = amount * 10 / 110; // 부가세

        String agreenum = "11586893"; // 승인번호 (취소요청시만 해당)
        String agreedate = "230117"; // 원거래일자(YYMMDD) (취소요청시만 해당)

        // 결제정보 세팅
        String spAmount = String.valueOf(amount); // 거래금액
        String spTax = String.valueOf(tax); // 부가세
        String spBongsa = "0"; // 봉사료
        String spHalbu = "00"; // 할부
        String spAgreenum = agreenum; // 승인번호
        String spAgreedate = agreedate; // 원거래일자(YYMMDD)
        String spMyunse = "0"; // 면세금액
        String spTxtnum = ""; // 전문관리번호(CATID(10) + MMDDhhmmss)
        String spFiller = ""; // Filler
        String spTxt = ""; // 전문TEXT
        String spDevicegb = ""; // 기종구분
        String spSigndata = ""; // 서명데이터

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
        senddata = senddata + CommonUtil.CATID + fs; // CATID
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
                    closePayment(CommonUtil._대기종료);
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
        int i, j = 0, k = 0;
        for (i = 0; i < recvdata.length(); i++) {
            if (recvdata.substring(i, i + 1).equals(String.valueOf(fs))) {
                k++;

                switch (k) {
                    case 1: //거래구분
                        strRecv01 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 2: //거래유형
                        strRecv02 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 3: //응답코드
                        strRecv03 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 4: //거래금액
                        strRecv04 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 5: //부가세
                        strRecv05 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 6: //봉사료
                        strRecv06 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 7: //할부
                        strRecv07 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 8: //승인번호
                        strRecv08 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 9: //승인일자
                        strRecv09 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 10: //발급사코드
                        strRecv10 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 11: //발급사명
                        strRecv11 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 12: //매입사코드
                        strRecv12 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 13: //매입사명
                        strRecv13 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 14: //가맹점번호
                        strRecv14 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 15: //승인CATID
                        strRecv15 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 16: //잔액
                        strRecv16 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 17: //응답메시지
                        strRecv17 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 18: //카드BIN
                        strRecv18 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 19: //카드구분
                        strRecv19 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 20: //전문관리번호
                        strRecv20 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 21: //거래일련번호
                        strRecv21 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 22: //발생포인트(할인금액)
                        strRecv22 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 23: //가용포인트(지불금액)
                        strRecv23 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 24: //누적포인트(잔액한도)
                        strRecv24 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 25: //캐시백가맹점
                        strRecv25 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 26: //캐시백승인번호
                        strRecv26 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 27:
                        strRecv27 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 28:
                        strRecv28 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 29:
                        strRecv29 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                    case 30:
                        strRecv30 = recvdata.substring(j, i);
                        j = i + 1;
                        break;
                }
            }
        }

        Log.d(TAG, "strRecv01 - 거래구분 : " + strRecv01); // 거래구분
        Log.d(TAG, "strRecv02 - 거래유형 : " + strRecv02); // 거래유형
        Log.d(TAG, "strRecv03 - 응답코드 : " + strRecv03); // 응답코드
        Log.d(TAG, "strRecv04 - 거래금액 : " + strRecv04); // 거래금액 (앞부분 0 제거)
        Log.d(TAG, "strRecv05 - 부가세 : " + strRecv05); // 부가세 (앞부분 0 제거)
        Log.d(TAG, "strRecv06 - 봉사료 : " + strRecv06); // 봉사료 (앞부분 0 제거)
        Log.d(TAG, "strRecv07 - 할부 : " + strRecv07); // 할부
        Log.d(TAG, "strRecv08 - 승인번호 : " + strRecv08);// 승인번호 (공백제거)
        Log.d(TAG, "strRecv09 - 승인일자 : " + strRecv09); // 승인일자
        Log.d(TAG, "strRecv10 - 발급사코드 : " + strRecv10); // 발급사코드
        Log.d(TAG, "strRecv11 - 발급사명 : " + strRecv11); // 발급사명 (공백제거)
        Log.d(TAG, "strRecv12 - 매입사코드 : " + strRecv12); // 매입사코드
        Log.d(TAG, "strRecv13 - 매입사명 : " + strRecv13); // 매입사명 (공백제거)
        Log.d(TAG, "strRecv14 - 가맹점번호 : " + strRecv14); // 가맹점번호 (공백제거)
        Log.d(TAG, "strRecv15 - 승인CATID : " + strRecv15); // 승인CATID
        Log.d(TAG, "strRecv16 - 잔액 : " + strRecv16); // 잔액 (공백제거)
        Log.d(TAG, "strRecv17 - 응답메시지 : " + strRecv17); // 응답메시지 (공백제거)
        Log.d(TAG, "strRecv18 - 카드BIN : " + strRecv18); // 카드BIN
        Log.d(TAG, "strRecv19 - 카드구분 : " + strRecv19); // 카드구분
        Log.d(TAG, "strRecv20 - 전문관리번호 : " + strRecv20); // 전문관리번호
        Log.d(TAG, "strRecv21 - 거래일련번호 : " + strRecv21); // 거래일련번호
        Log.d(TAG, "strRecv22 : " + strRecv22);
        Log.d(TAG, "strRecv23 : " + strRecv23);
        Log.d(TAG, "strRecv24 : " + strRecv24);
        Log.d(TAG, "strRecv25 : " + strRecv25);
        Log.d(TAG, "strRecv26 : " + strRecv26);
        Log.d(TAG, "strRecv27 : " + strRecv27);
        Log.d(TAG, "strRecv28 : " + strRecv28);
        Log.d(TAG, "strRecv29 : " + strRecv29);
        Log.d(TAG, "strRecv30 : " + strRecv30);


        // 승인요청 정상처리
        if( strRecv03.equals("0000") && strRecv01.equals(CommonUtil._승인응답) ) {
            // 응답 거래구분 (승인 : 0210)
            Log.d(TAG, "결제승인 정상 응답");

            // 영수증 출력확인 팝업
            popDialog300();

        // 취소요청 정상처리
        }else if( strRecv03.equals("0000") && strRecv01.equals(CommonUtil._취소응답) ){
                // 응답 거래구분 (취소 : 0430)
                Log.d(TAG, "승인취소 정상 응답");

                // 영수증 출력확인 팝업
                popDialog300();

        }else{
            Log.d(TAG, "결제승인 비정상 응답");
        }

        // 결과 web 전달
        //returnPaymentResult();
    }


    /**
     * 영수증 출력
     */
    public void printReceipt(){

        mToastHandler.obtainMessage(0,0,0,"print Start").sendToTarget();

        // 프린터 설정 (고정)
        int portType = 2;
        String logicalName = "BK3-3";
        String address = "";
        Boolean checkBoxAsyncMode = true;

        if (MainActivity.getPrinterInstance().printerOpen(portType, logicalName, address, checkBoxAsyncMode)) {
            Log.d(TAG, "P-printer open!!!");

            String strData = "";
            strData = strData + "\n";
            strData = strData + "\n";
            strData = strData + "====================\n";
            strData = strData + "영 수 증\n";
            strData = strData + "금  액 : 2,700원\n";
            strData = strData + "부가세 :   300원\n";
            strData = strData + "합  계 : 3,000원\n";
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

            MainActivity.getPrinterInstance().printText(strData, alignment, attribute, (spinnerSize + 1));

            /*
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sign_sample);
            imgString = bitmapToString(bitmap);
            Log.d(TAG,"imgString:[" + imgString.length() + "]" + imgString + ">>");
            */

            Bitmap stringBitmap = CommonUtil.stringToBitmap(imgString);
            MainActivity.getPrinterInstance().printImage(stringBitmap, 384, -1, 50, 0, 1);

            MainActivity.getPrinterInstance().cutPaper();

        } else {
            mToastHandler.obtainMessage(0, 0, 0, "Fail to printer open").sendToTarget();
        }


    }

    public void completePrintReceipt(){
        Log.d(TAG, "completePrintReceipt()");

    }

    /**
     * 결제결과 WEB 전달
     */
    public void returnPaymentResult(){
        Log.d(TAG, "returnPayResult()");

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



}
