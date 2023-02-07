package kr.co.nicevan.nvcat.service;

import kr.co.nicevan.nvcat.PrinterControl.PrinterType;
import kr.co.nicevan.nvcat.dto.PrinterDTO;

public interface PrinterService {
    void closeAll();
    void resetPrintData();
    boolean printCommonReceipt(PrinterDTO.CommonReceipt receipt);
    boolean printCommonLabel(PrinterDTO.CommonLabel label);
    boolean isCompleted(PrinterType printerType);
}
