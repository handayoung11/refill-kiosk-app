package kr.co.nicevan.nvcat.PrinterControl;

import android.app.Activity;
import android.util.Log;

import kr.co.nicevan.nvcat.main_activity_manger.MainDialogManager;

public class DefaultBixolonPrinterUserListener implements BixolonPrinter.UserListener {

    @Override
    public void onPrintEventErrorOccurred(int eventCode, BixolonPrinter printer){

        String tagPrint = getTagPrint(printer.getType());

        Log.d(tagPrint, "onPrintEventErrorOccurred : " + eventCode);

        switch (eventCode) {
            case 201:
                Log.d(tagPrint, "프린터 Cover Open");
                break;
            case 203:
                Log.d(tagPrint, "프린터 Paper Empty");
                break;
            case 217:
                Log.d(tagPrint, "프린터 off-line");
                break;
        }
    }

    @Override
    public void onPrintEventStatusUpdateOccurred(int eventCode, BixolonPrinter printer){
        String tagPrint = getTagPrint(printer.getType());
        Log.d(tagPrint, "onPrintEventStatusUpdateOccurred : " + eventCode);

        switch (eventCode) {
            case 2001 :
                Log.d(tagPrint, "프린터 Pown On");
                break;
            case 2004 :
                Log.d(tagPrint, "프린터 Pown Off");
                break;
            case 11 :
                Log.d(tagPrint, "프린터 Cover Open");
                break;
            case 12 :
                Log.d(tagPrint, "프린터 Cover OK");
                break;
            case 24 :
                Log.d(tagPrint, "프린터 Paper Empty");
                break;
            case 25 :
                Log.d(tagPrint, "프린터 Paper Near Empty");
                break;
            case 26 :
                Log.d(tagPrint, "프린터 Paper OK");
                break;
            case 53 :
                Log.d(tagPrint, "프린터 off-line");
                break;
            case 54 :
                Log.d(tagPrint, "프린터 on-line");
                break;
        }
    }

    @Override
    public void onPrintEventOutputCompleteOccurred(int eventCode, BixolonPrinter printer) {
        String tagPrint = getTagPrint(printer.getType());
        Log.d(tagPrint, "onPrintEventOutputCompleteOccurred : " + eventCode);

        if (printer.getType() == PrinterType.LABEL) {
            if (printer.getContext() instanceof Activity){
                Activity activity = (Activity) printer.getContext();
                // 프린터 출력중 팝업 닫기
                MainDialogManager.getInstance().closeDialog400();

                activity.runOnUiThread(() -> {
                    Log.d(tagPrint, "출력 완료");
                    // 출력완료 팝업
                    MainDialogManager.getInstance().popDialog500();
                });
            }
        }
    }

    private String getTagPrint(PrinterType type) {
        if (type == PrinterType.LABEL) {
            return "LABEL PRINTER EVENT";
        } else if (type == PrinterType.RECEIPT) {
            return "RECEIPT PRINTER EVENT";
        } else {
            throw new IllegalArgumentException("지원하지 않는 PrinterType입니다.");
        }
    }
}
