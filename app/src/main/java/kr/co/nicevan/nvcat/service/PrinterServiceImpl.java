package kr.co.nicevan.nvcat.service;

import static kr.co.nicevan.nvcat.CommonUtil.stringToBitmap;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterManager.*;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.*;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.List;

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

    /**
     *
     * 프린트 공통함수
     */
    @Override
    public boolean printOut(PrinterDTO printerDTO, PrinterType type) {

        // Printer 객체 취득.
        BixolonPrinter printer;
        if(type.equals(PrinterType.RECEIPT)) printer = getInstance().getRecPrt();
        else if(type.equals(PrinterType.LABEL)) printer = getInstance().getLabPrt();
        else return false;

        //프린트 출력.
        if(!printer.isOpen()) return false;
        print(printer, printerDTO.getOutput());

        //프린트 이미지 출력 - [영수증:사인],[라벨:친환경마크]
        Bitmap stringBitmap = stringToBitmap(printerDTO.getImg());
        printer.printImage(stringBitmap, 384, -1, 50, 0, 1);


        //프린트 종료.
        if(type.equals(PrinterType.RECEIPT)) printer.cutPaper();
        else if(type.equals(PrinterType.LABEL)) printer.formFeed();
        return true;
    }

    /**
     * 출력 공통 함수
     */
    @Override
    public void print(BixolonPrinter printer,List<String> output){
        int alignment = 1;
        int attribute = 1;
        int spinnerSize = 0;
        Log.d(TAG, "P-alignment : " + alignment);
        Log.d(TAG, "P-attribute : " + attribute);
        Log.d(TAG, "P-spinnerSize : " + spinnerSize);
        for(String s : output) {
            Log.d(TAG, "P-strData : " + s);
            printer.printText(s, 1, 1, 1);
        }
    }

}
