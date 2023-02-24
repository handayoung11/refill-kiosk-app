package kr.co.nicevan.nvcat.main_activity_manger;

import static kr.co.nicevan.nvcat.CommonUtil._결제중지;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.LABEL;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.RECEIPT;

import android.util.Log;

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

    Dialog200 dialog200;
    Dialog250 dialog250;
    Dialog400 dialog400;

    AppConfig appConfig = AppConfig.getInstance();
    PrinterService printerService = appConfig.printerService();

    private String TAG = this.getClass().getSimpleName();

    private MainDialogManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        dialog250 = new Dialog250(mainActivity);
        dialog250.setDialogListener(new Dialog250.DialogListener() {
            @Override
            public void onPositiveClicked(String data) {
            }

            @Override
            public void onNegativeClicked() {
                // 결제종료
                NicepayManager.getInstance().closePayment(_결제중지);
            }
        });
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
    public void popDialog100(String reqType) {

        Dialog100 dialog100 = new Dialog100(mainActivity, reqType);
        dialog100.setDialogListener(new Dialog100.DialogListener() {

            @Override
            public void onNegativeClicked() {
                // 결제종료
                NicepayManager.getInstance().closePayment(_결제중지);
            }

            @Override
            public void choPayType(String payType) {
                // 카드투입 요청
                NicepayManager.getInstance().icPay(payType);
            }
        });
        dialog100.show();
    }

    /**
     * 팝업 Dialog200 (결제요청 대기)
     */
    public void popICDialog(String curReqType, String payType) {

        dialog200 = new Dialog200(mainActivity, curReqType, payType);
        dialog200.setDialogListener(new Dialog200.DialogListener() {

            @Override
            public void onNegativeClicked() {
                Log.d(TAG, "popDialog200 - 결제중지");
                // 결제종료
                NicepayManager.getInstance().closePayment(_결제중지);
            }
        });
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
        dialog500.setDialogListener(new Dialog500.DialogListener() {
            @Override
            public void onPositiveClicked() {
            }

            @Override
            public void onNegativeClicked() {
            }
        });
        dialog500.show();
    }


    public void popDialog900(String curReqType, String cancelType) {
        Dialog900 dialog900 = new Dialog900(mainActivity, curReqType, cancelType);
        dialog900.setDialogListener(new Dialog900.DialogListener() {
            @Override
            public void onPositiveClicked() {
            }

            @Override
            public void onNegativeClicked() {
            }
        });
        dialog900.show();
    }

    public void popDialogError(ErrorResponse errorResponse){

    }
}
