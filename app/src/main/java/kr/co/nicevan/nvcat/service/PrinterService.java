package kr.co.nicevan.nvcat.service;

import java.util.List;

import kr.co.nicevan.nvcat.PrinterControl.PrinterType;
import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.PrinterDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;

public interface PrinterService {
    void closeAll();
    void resetPrintData();
    boolean isCompleted(PrinterType printerType);
    boolean labelPrint(List<LabelDTO.LabelResp> resps);
    boolean receiptPrint(ReceiptDTO.ReceiptResp resps, CardDTO cardInfo);

}
