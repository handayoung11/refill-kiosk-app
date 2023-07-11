package kr.co.nicevan.nvcat.PrinterControl;

import android.content.Context;
import android.util.Log;

import com.hwasung.HW_API;


public class PrinterManager {
    private HW_API printer; // 영수증 프린터
    private static PrinterManager instance;

    private String TAG = PrinterManager.class.getSimpleName();
    Context context;

    private PrinterManager(Context context) {
        this.context = context;
        printer = new HW_API();
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
    public void openPrinter(String model){
        //영수증 프린터
        int c = printer.usbOpen(context, model);
        Log.d(TAG, model + " Printer code: " + c);
    }

    public HW_API getRecPrt() {
        openPrinter("HP-380");
        return printer;
    }

    public HW_API getLabPrt() {
        openPrinter("HMK-830");
        return printer;
    }

    public Context getContext() {
        return context;
    }
}
