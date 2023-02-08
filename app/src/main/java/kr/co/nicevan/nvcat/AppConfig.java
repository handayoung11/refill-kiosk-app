package kr.co.nicevan.nvcat;

import kr.co.nicevan.nvcat.service.PrinterService;
import kr.co.nicevan.nvcat.service.PrinterServiceImpl;
import kr.co.nicevan.nvcat.service.label.LabelService;
import kr.co.nicevan.nvcat.service.label.LabelServiceImpl;
import kr.co.nicevan.nvcat.service.receipt.ReceiptService;
import kr.co.nicevan.nvcat.service.receipt.ReceiptServiceImpl;
import kr.co.nicevan.nvcat.service.common.CommonService;
import kr.co.nicevan.nvcat.service.common.CommonServiceImpl;

public class AppConfig {
    public ReceiptService receiptService(){
        return new ReceiptServiceImpl(commonService());
    }
    public LabelService labelService(){
        return new LabelServiceImpl(commonService());
    }
    public CommonService commonService(){
        return new CommonServiceImpl();
    }
    public PrinterService printerService(){ return new PrinterServiceImpl(); }
}
