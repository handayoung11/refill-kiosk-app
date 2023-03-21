package kr.co.nicevan.nvcat.main_activity_manger;

import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.LABEL;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.RECEIPT;

import kr.co.nicevan.nvcat.AppConfig;
import kr.co.nicevan.nvcat.activity.MainActivity;
import kr.co.nicevan.nvcat.dialog.Dialog100;
import kr.co.nicevan.nvcat.dialog.Dialog200;
import kr.co.nicevan.nvcat.dialog.Dialog250;
import kr.co.nicevan.nvcat.dialog.Dialog400;
import kr.co.nicevan.nvcat.dialog.Dialog500;
import kr.co.nicevan.nvcat.dialog.Dialog900;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;
import kr.co.nicevan.nvcat.service.PrinterService;

public class MainDialogManager {
    MainActivity mainActivity;

    private static MainDialogManager instance;

    private Dialog200 dialog200;
    private Dialog250 dialog250;
    private Dialog400 dialog400;

    private AppConfig appConfig = AppConfig.getInstance();
    private PrinterService printerService = appConfig.printerService();

    private String TAG = this.getClass().getSimpleName();

    private MainDialogManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        dialog250 = new Dialog250(mainActivity);
    }

    public static MainDialogManager init(MainActivity mainActivity) {
        if (instance != null && instance.mainActivity == mainActivity) {
            return instance;
        }
        instance = new MainDialogManager(mainActivity);
        return instance;
    }

    public static MainDialogManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MainDialogManager가 초기화되지 않았습니다.");
        }
        return instance;
    }

    /**
     * 팝업 Dialog100 (결제방법)
     */
    public void popDialog100(String reqType, String totPrice) {

        Dialog100 dialog100 = new Dialog100(mainActivity, reqType, totPrice);
        dialog100.setDialogListener(payType -> {
            // 카드투입 요청
            NicepayManager.getInstance().icPay(payType, totPrice);
        });
        dialog100.show();
    }

    /**
     * 팝업 Dialog200 (결제요청 대기)
     */
    public void popICDialog(String curReqType, String payType, String totPrice) {

        dialog200 = new Dialog200(mainActivity, curReqType, payType, totPrice);
        dialog200.show();
    }

    public void closeICDialog() {
        if (dialog200 != null && dialog200.isShowing()) {
            dialog200.dismiss();
        }
    }

    public void popMsDialog() {
        dialog250.show();
    }

    public void closeMsDialog() {
        if (dialog250.isShowing()) {
            dialog250.dismiss();
        }
    }

    /**
     * 팝업 Dialog400 (프린터 출력중)
     */
    public void popDialog400() {

        dialog400 = new Dialog400(mainActivity);
        dialog400.setDialogListener(new Dialog400.DialogListener() {
            @Override
            public void onPositiveClicked() {
            }

            @Override
            public void onNegativeClicked() {
            }
        });
        dialog400.show();
    }

    public void closeDialog400() {
        if (dialog400 != null && dialog400.isShowing()) {
            dialog400.dismiss();
        }
    }

    /**
     * 팝업 Dialog500 (영수증 출력완료)
     */
    public void popDialog500() {

        Dialog500 dialog500 = new Dialog500(mainActivity, printerService.isCompleted(RECEIPT), printerService.isCompleted(LABEL));
        dialog500.setDialogListener(() -> {
        });
        dialog500.show();
    }


    public void popDialog900(String curReqType, String cancelType) {
        Dialog900 dialog900 = new Dialog900(mainActivity, curReqType, cancelType);
        dialog900.setDialogListener(new Dialog900.DialogListener() {
            @Override
            public void onPositiveClicked() {
            }
        });
        dialog900.show();
    }

    public void popDialogError(ErrorResponse errorResponse){

    }
}
