package kr.co.nicevan.nvcat.service;

import java.util.List;

import kr.co.nicevan.nvcat.PrinterControl.BixolonPrinter;
import kr.co.nicevan.nvcat.PrinterControl.PrinterType;
import kr.co.nicevan.nvcat.dto.PrinterDTO;

public interface PrinterService {
    void closeAll();
    void resetPrintData();
    void print(BixolonPrinter printer, List<String> output);
    boolean printOut(PrinterDTO printerDTO, PrinterType type);
    boolean isCompleted(PrinterType printerType);
}
