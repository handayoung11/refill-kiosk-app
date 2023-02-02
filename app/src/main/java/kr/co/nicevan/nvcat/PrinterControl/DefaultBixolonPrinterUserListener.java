package kr.co.nicevan.nvcat.PrinterControl;

import android.util.Log;

import lombok.RequiredArgsConstructor;

public abstract class DefaultBixolonPrinterUserListener implements BixolonPrinter.UserListener {

    protected final String TagPrint;

    protected DefaultBixolonPrinterUserListener(String tagPrint) {
        TagPrint = tagPrint;
    }

    @Override
    public void onPrintEventErrorOccurred(int eventCode){
        Log.d(TagPrint, "onPrintEventErrorOccurred : " + eventCode);

        switch (eventCode) {
            case 201:
                Log.d(TagPrint, "프린터 Cover Open");
                break;
            case 203:
                Log.d(TagPrint, "프린터 Paper Empty");
                break;
            case 217:
                Log.d(TagPrint, "프린터 off-line");
                break;
        }
    }

    @Override
    public void onPrintEventStatusUpdateOccurred(int eventCode){
        Log.d(TagPrint, "onPrintEventStatusUpdateOccurred : " + eventCode);

        switch (eventCode) {
            case 2001 :
                Log.d(TagPrint, "프린터 Pown On");
                break;
            case 2004 :
                Log.d(TagPrint, "프린터 Pown Off");
                break;
            case 11 :
                Log.d(TagPrint, "프린터 Cover Open");
                break;
            case 12 :
                Log.d(TagPrint, "프린터 Cover OK");
                break;
            case 24 :
                Log.d(TagPrint, "프린터 Paper Empty");
                break;
            case 25 :
                Log.d(TagPrint, "프린터 Paper Near Empty");
                break;
            case 26 :
                Log.d(TagPrint, "프린터 Paper OK");
                break;
            case 53 :
                Log.d(TagPrint, "프린터 off-line");
                break;
            case 54 :
                Log.d(TagPrint, "프린터 on-line");
                break;
        }
    }
}
