package kr.co.nicevan.nvcat.service;

import static kr.co.nicevan.nvcat.CommonUtil.stringToBitmap;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterManager.*;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.*;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.PrinterControl.BixolonPrinter;
import kr.co.nicevan.nvcat.PrinterControl.PrinterManager;
import kr.co.nicevan.nvcat.PrinterControl.PrinterType;
import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.PrinterDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.service.common.CommonService;

public class PrinterServiceImpl implements PrinterService {


    private final CommonService commonService;

    public PrinterServiceImpl(CommonService commonService) {
        this.commonService = commonService;
    }

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
    private boolean printOut(PrinterDTO printerDTO, PrinterType type) {

        // Printer 객체 취득.
        BixolonPrinter printer;
        if(type.equals(RECEIPT)) printer = getInstance().getRecPrt();
        else if(type.equals(LABEL)) printer = getInstance().getLabPrt();
        else return false;

        //프린트 출력.
        if(!printer.isOpen()) return false;
        printer.beginTransactionPrint();
        print(printer, printerDTO.getOutput());

        //프린트 이미지 출력 - [영수증:사인],[라벨:친환경마크]
        if(printerDTO.getImg() != ""){
            Bitmap stringBitmap = stringToBitmap(printerDTO.getImg());
            printer.printImage(stringBitmap, 384, -1, 50, 0, 1);
        }

        //프린트 종료.
        if(type.equals(PrinterType.RECEIPT)) printer.cutPaper();
        else if(type.equals(PrinterType.LABEL)) printer.formFeed();
        printer.endTransactionPrint();

        return true;
    }

    /**
     * 출력 공통 함수
     */
    private void print(BixolonPrinter printer,List<String> output){
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

    public boolean labelPrint(List<LabelDTO.LabelResp> resps) {
        List<String> res = new ArrayList<>();
        for(LabelDTO.LabelResp label : resps) res.add(outStringForLabel(label));
        PrinterDTO printerDTO = new PrinterDTO(res, "");
        return printOut(printerDTO, LABEL);
    }

    public boolean receiptPrint(ReceiptDTO.ReceiptResp resp, CardDTO card) {
        List<String> res = new ArrayList<>();
        res.add(outStringForReceipt(resp, card));
        PrinterDTO printerDTO = new PrinterDTO(res, card.getSingImg());
        return printOut(printerDTO, RECEIPT);
    }


    /**  2022-01-30 작성자 : 염에녹
     * 기능 : 라벨 formatter.
     */
    private String outStringForLabel(LabelDTO.LabelResp response) {
        String strData = "";
        strData += "[상품명] "+response.getLiquidName()+"\t\t\t"+"[용량] "+ response.getOdExAmount()+"\n";
        strData += "[제조일] "+ response.getLiquidDateOfManufacture()+"\t\t\t"+"[사용기한] "+ response.getLiquidDateOfUse()+"까지\n";
        strData += "--------------------------------------------------------\n";
        strData += "[전성분]\n";
        strData += commonService.formatterByRightEnter(response.getLiquidIngredients(),40)+"\n";
        strData += "\n\n";
        strData += "[사용시 주의사항]\n";
        strData += commonService.formatterByRightEnter(response.getLiquidCaution(),40)+"\n";
        strData += "\n\n";
        strData += "[제조업체]\n";
        strData += response.getLiquidDateOfManufacture()+"\n";
        strData += "\n\n";
        strData += "[책임판매업체]\n";
        strData += response.getLiquidResponsibleSalesBusiness()+"\n";
        strData += "\n\n";
        strData += "[맞춤판매업체]\n";
        strData += response.getLiquidSellerName()+"\n";
        strData += "--------------------------------------------------------\n";
        return strData;
    }


    /**  2022-01-30 작성자 : 염에녹
     * 기능 : 영수증 formatter.
     */
    private String outStringForReceipt(ReceiptDTO.ReceiptResp response, CardDTO card) {
        String strData = "영수증\n";
        strData += "[매장명] "+response.getShopName()+"\n";
        strData += "[사업자번호] "+response.getCompanyNo()+"\n";
        strData += "[주소] "+ response.getAddress()+"\n";
        strData += "[대표자] "+ response.getOwner()+"\t\t\t"+"[TEL] "+ response.getTell()+"\n";
        strData += "[매출일] "+ response.getPayDate()+"\n";
        strData += "=======================================\n";
        strData += "\t\t상품명\t\t단가\t\t수량\t\t금액\t\n ";
        strData += "---------------------------------------\n";
        for(ReceiptDTO.ReceiptResp.ItemDTO i : response.getItems()){
            String name = commonService.formatterByLeftSpace(i.getItemName(), 11);
            String unitPrice = commonService.formatterByLeftSpace(String.valueOf((i.getPrice() / i.getQuantity())), 7);
            String quantity = commonService.formatterByLeftSpace(i.getQuantity() + i.getUnit(), 5);
            String price = CommonUtil.convertCommaDecimalFormat(String.valueOf(i.getPrice()));
            strData += name+"\t"+unitPrice+"\t"+quantity+"\t"+price+"\n";
        }
        strData += "---------------------------------------\n";
        strData += "합계금액\t\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "---------------------------------------\n";
        strData += "과세물품가액\t\t\t\t\t\t"+card.getDutiableVal()+"\n";
        strData += "부  가  세\t\t\t\t\t\t"+card.getTax()+"\n";
        strData += "매출합계(카드)\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "=======================================\n";
        strData += "[카드번호]\t\t"+card.getCardBin()+"\n";
        strData += "[할부개월]\t\t\t\t\t\t\t"+card.getInstallment()+"\n";
        strData += "[카드사명]\t\t\t\t\t\t"+card.getCardName()+"\n";
        strData += "[승인번호]\t\t\t\t"+card.getApprovalNo()+"\n";
        strData += "[승인일자]\t\t\t\t\t"+card.getApprovalDate()+"\n";
        strData += "[결제금액]\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "---------------------------------------\n";
        return strData;
    }

}
