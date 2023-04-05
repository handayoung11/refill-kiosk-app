package kr.co.nicevan.nvcat.service;

import static kr.co.nicevan.nvcat.CommonUtil.stringToBitmap;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterManager.*;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.*;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
     * 프린트 공통 함수
     */
    private boolean printOut(PrinterDTO printerDTO) {
        // Printer 객체 취득.
        BixolonPrinter printer;
        if(printerDTO.getType().equals(RECEIPT)) printer = getInstance().getRecPrt();
        else if(printerDTO.getType().equals(LABEL)) printer = getInstance().getLabPrt();
        else return false;

        //프린트 출력.
        if(!printer.isOpen()) return false;
        printer.beginTransactionPrint();
        Log.d(TAG, "P-alignment : " + printerDTO.getConfig().getAlignment());
        Log.d(TAG, "P-attribute : " + printerDTO.getConfig().getAttribute());
        Log.d(TAG, "P-spinnerSize : " + printerDTO.getConfig().getSpinnerSize());
        if(printerDTO.getType().equals(RECEIPT)) printByText(printer, printerDTO);
        else if(printerDTO.getType().equals(LABEL)) printByBitMap(printer, printerDTO);

        //프린트 종료.
        if(printerDTO.getType().equals(RECEIPT)) printer.cutPaper();
        else if(printerDTO.getType().equals(LABEL)) printer.formFeed();
        printer.endTransactionPrint();
        return true;
    }

    /**
     * 출력 공통 함수
     */
    private void printByText(BixolonPrinter printer, PrinterDTO p){
        //프린트 텍스트 출력
        for(String s : p.getOutput()){
            Log.d("",s);
            printer.printText(s, p.getConfig().getAlignment(), p.getConfig().getAttribute(), p.getConfig().getSpinnerSize());
        }
        if(p.getImg() == ""){
            Bitmap stringBitmap = stringToBitmap(p.getImg());
            printer.printImage(stringBitmap, 384, -1, 50, 0, 1);
        }
    }

    private void printByBitMap(BixolonPrinter printer, PrinterDTO p){
        //프린트 텍스트 출력
        for(String s : p.getOutput()){
            Log.d("",s);
            Bitmap bitmap = getBitMapText(s, printer.getPrinterMaxWidth(), printer.getContext().getAssets());
//            saveBitmapToJpg(bitmap, "label","labelTest");
            printer.printImage(bitmap, printer.getPrinterMaxWidth(), -1, 50, 0, 1);
        }
    }

    public boolean labelPrint(List<LabelDTO.LabelResp> resps) {
        List<String> res = new ArrayList<>();
        for(LabelDTO.LabelResp label : resps) res.add(outStringForLabel(label));
        return printOut(PrinterDTO.of(res, "", LABEL));
    }

    public boolean receiptPrint(ReceiptDTO.ReceiptResp resp, CardDTO card) {
        List<String> res = new ArrayList<>();
        res.add(outStringForReceipt(resp, card));
        return printOut(PrinterDTO.of(res, card.getSingImg(), RECEIPT));
    }

    /**  2023-01-30 작성자 : 염에녹
     * 기능 : 라벨 formatter.
     */
    private String outStringForLabel(LabelDTO.LabelResp response) {
        StringBuilder sb = new StringBuilder();
        sb.append(response.getLiquidName()+" "+response.getOdExAmount()+"g\n");
        sb.append("[제조일] "+ response.getLiquidDateOfManufacture()+"\t\t\t\t\t"+"[사용기한] "+ response.getLiquidDateOfUse()+"까지\n");
        sb.append("[제조일] 2022-01-01"+"\t\t\t\t\t"+"[사용기한]  2022-01-01"+"까지\n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append("[전성분]\n");
        sb.append(response.getLiquidIngredients()+"\n\n");
        sb.append("[사용시 주의사항]\n");
        sb.append(response.getLiquidCaution()+"\n\n");
        sb.append("[제조업체] "+response.getLiquidManufacturer()+"\t\t"+ "[책임판매업체] "+response.getLiquidResponsibleSalesBusiness()+"\t\t"+"[맞춤판매업체] "+response.getLiquidSellerName()+"\n");
        sb.append("-----------------------------------------------------------------\n");
        return sb.toString();
    }


    /**  2023-01-30 작성자 : 염에녹
     * 기능 : 영수증 formatter.
     */
    private String outStringForReceipt(ReceiptDTO.ReceiptResp response, CardDTO card) {
        String strData = "영수증("+card.getRstResult()+")\n";
        strData += "[매장명] "+response.getShopName()+"\n";
        strData += "[사업자번호] "+response.getCompanyNo()+"\n";
        strData += "[주소] "+ response.getAddress()+"\n";
        strData += "[대표자] "+ response.getOwner()+"\t\t"+"[TEL] "+ response.getTell()+"\n";
        strData += "[매출일] "+ response.getPayDate()+"\n";
        strData += "================================================\n";
        strData += "\t상품명\t\t단가\t\t수량\t\t금액\t\t\n ";
        strData += "-----------------------------------------------\n";
        for(ReceiptDTO.ReceiptResp.ItemDTO i : response.getItems()){
            String name = commonService.formatterByLeftSpace(i.getItemName(), 11);
            String unitPrice = commonService.formatterByLeftSpace(String.valueOf((i.getPrice() / i.getQuantity())), 7);
            String quantity = commonService.formatterByLeftSpace(i.getQuantity() + i.getUnit(), 5);
            String price = CommonUtil.convertCommaDecimalFormat(String.valueOf(i.getPrice()));
            strData += name+"\t"+unitPrice+"\t"+quantity+"\t"+price+"\n";
        }
        strData += "------------------------------------------------\n";
        strData += "합계금액\t\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "------------------------------------------------\n";
        strData += "과세물품가액\t\t\t\t\t\t"+card.getDutiableVal()+"\n";
        strData += "부  가  세\t\t\t\t\t\t"+card.getTax()+"\n";
        strData += "매출합계(카드)\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "================================================\n";
        strData += "[카드번호]\t\t"+card.getCardBin()+"\n";
        strData += "[할부개월]\t\t\t\t\t\t\t"+card.getInstallment()+"\n";
        strData += "[카드사명]\t\t\t\t\t\t"+card.getCardName()+"\n";
        strData += "[승인번호]\t\t\t\t"+card.getApprovalNo()+"\n";
        strData += "[승인일자]\t\t\t\t\t"+card.getApprovalDate()+"\n";
        strData += "[결제금액]\t\t\t\t\t\t"+card.getTotPrice()+"\n";
        strData += "------------------------------------------------\n";
        return strData;
    }

    private Bitmap getBitMapText(String text, int width, AssetManager am){
        //new paint
        Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);

        //set font

        Typeface typeface = Typeface.createFromAsset(am, "fonts/hline.ttf");
        paint.setTypeface(typeface);

        //draw text with paint to layout
        TextPaint textPaint = new TextPaint(paint);
        StaticLayout textLayout = StaticLayout
                .Builder
                .obtain(text, 0 ,text.length(), textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .setIncludePad(true)
                .build();

        int height = textLayout.getHeight();
        if(height < 400) height = 400;
        //new bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //new canvas set bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        textLayout.draw(canvas);
        return bitmap;
    }

    private void saveBitmapToJpg(Bitmap bitmap, String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()) file_path.mkdirs();
            FileOutputStream out = new FileOutputStream(string_path+file_name, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}
