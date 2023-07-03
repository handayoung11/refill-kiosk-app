package kr.co.nicevan.nvcat.PrinterControl;

import android.content.Context;
import android.util.Log;

import com.hwasung.HW_API;

public class PrinterManager {
    private HW_API receiptPrinter; // 영수증 프린터
    private HW_API labelPrinter; // 라벨 프린터

    private static PrinterManager instance;

    private String TAG = PrinterManager.class.getSimpleName();
    Context context;

    private PrinterManager(Context context) {
        this.context = context;
        receiptPrinter = new HW_API();
        labelPrinter = new HW_API();
    }

    public static PrinterManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PrinterManager가 초기화되지 않았습니다.");
        }
        return instance;
    }

    public static PrinterManager init(Context context) {
        if (instance != null) {
            return instance;
        }
        instance = new PrinterManager(context);
        return instance;
    }

    /**
     * 프린터 오픈
     */
    public void openPrinter(HW_API hw){
        //영수증 프린터
        int c = hw.usbOpen(context);
        Log.d(TAG, "receipt Printer code: " + c);
    }

    public HW_API getRecPrt() {
        openPrinter(receiptPrinter); // 영수증 & 라벨 프린터 오픈
        return receiptPrinter;
    }

    public HW_API getLabPrt() {
        openPrinter(labelPrinter);
        return labelPrinter;
    }

    public Context getContext() {
        return context;
    }
}
