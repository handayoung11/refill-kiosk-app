package kr.co.nicevan.nvcat.service;

import static kr.co.nicevan.nvcat.CommonUtil.stringToBitmap;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterManager.*;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.*;

import android.graphics.Bitmap;
import android.util.Log;

import kr.co.nicevan.nvcat.PrinterControl.BixolonPrinter;
import kr.co.nicevan.nvcat.PrinterControl.PrinterManager;
import kr.co.nicevan.nvcat.PrinterControl.PrinterType;
import kr.co.nicevan.nvcat.dto.PrinterDTO;

public class PrinterServiceImpl implements PrinterService {

    String TAG = PrinterServiceImpl.class.getSimpleName();

    @Override
    public void closeAll() {
        PrinterManager instance = getInstance();
        instance.getRecPrt().printerClose();
        instance.getLabPrt().printerClose();
    }

    @Override
    public void resetPrintData() {
        PrinterManager instance = getInstance();
        instance.getRecPrt().resetPrintData();
        instance.getLabPrt().resetPrintData();
    }

    /**
     * 영수증 출력
     */
    @Override
    public boolean printCommonReceipt(PrinterDTO.CommonReceipt receipt) {

        BixolonPrinter printer = getInstance().getRecPrt();
        if (!printer.isOpen()) {
            return false;
        }

        Log.d(TAG, "Receipt Printer open!!!");

        int alignment = 1;
        int attribute = 1;
        int spinnerSize = 0;

        String strData = "";
        strData = strData + "\n";
        strData = strData + "\n";
        strData = strData + "====================\n";
        strData = strData + "영 수 증\n";
        strData = strData + "금  액 : " + receipt.getPrtAmount() + "원\n";
        strData = strData + "부가세 : " + receipt.getPrtTax() + "원\n";
        strData = strData + "합  계 : " + receipt.getPrtTotAmount() + "원\n";
        strData = strData + "====================";
        strData = strData + "\n";
        strData = strData + "\n";
        strData = strData + "\n";
        strData = strData + "\n";

        Log.d(TAG, "P-strData : " + strData);
        Log.d(TAG, "P-alignment : " + alignment);
        Log.d(TAG, "P-attribute : " + attribute);
        Log.d(TAG, "P-spinnerSize : " + spinnerSize);

        printer.printText(strData, alignment, attribute, (spinnerSize + 1));
        Bitmap stringBitmap = stringToBitmap(receipt.getSignImgString());
        printer.printImage(stringBitmap, 384, -1, 50, 0, 1);

        printer.cutPaper();

        return true;
    }

    @Override
    public boolean printCommonLabel(PrinterDTO.CommonLabel label) {
        BixolonPrinter printer = getInstance().getLabPrt();

        if (!printer.isOpen()) {
            return false;
        }
        Log.d(TAG, "Label Printer open!!!");

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

        printer.printText(strData, alignment, attribute, (spinnerSize + 1));
        printer.formFeed();

        return true;
    }

    @Override
    public boolean isCompleted(PrinterType type) {
        PrinterManager instance = getInstance();
        if (type == LABEL) {
            return instance.getLabPrt().isCompleted();
        } else if (type == RECEIPT) {
            return instance.getRecPrt().isCompleted();
        } else {
            throw new IllegalArgumentException("지원하지 않는 PrinterType입니다.");
        }
    }
}
