package kr.co.nicevan.nvcat.service;

import static com.hwasung.HW_API.FULL;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterManager.getInstance;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.LABEL;
import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.RECEIPT;

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

import com.hwasung.HW_API;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.PrinterControl.PrinterManager;
import kr.co.nicevan.nvcat.PrinterControl.PrinterType;
import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.PrinterDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.service.common.CommonService;

public class PrinterServiceImpl implements PrinterService {


    private final CommonService commonService;
    String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();


    public PrinterServiceImpl(CommonService commonService) {
        this.commonService = commonService;
    }

    String TAG = PrinterServiceImpl.class.getSimpleName();

    /**
     * 영수증 출력
     */
    @Override
    public boolean isCompleted(PrinterType type) {
//        PrinterManager instance = getInstance();
        if (type == LABEL) {
//            return instance.getLabPrt().isCompleted();
            return true;
        } else if (type == RECEIPT) {
//            return instance.getRecPrt().isCompleted();
            return true;
        } else {
            throw new IllegalArgumentException("지원하지 않는 PrinterType입니다.");
        }
    }

    /**
     *
     * 프린트 공통 함수
     */
    private boolean printOut(PrinterDTO printerDTO) {
        HW_API printer;
        if(printerDTO.getType().equals(RECEIPT)) {
            printer = getInstance().getRecPrt();
//            if(printer.status() != 0) return false;
            printByText(printer, printerDTO);
            printer.cut(FULL);
        }
        else if(printerDTO.getType().equals(LABEL)) {
            printer = getInstance().getLabPrt();
//            if(printer.status() != 0) return false;
            printByBitMap(printer, printerDTO);
        }
        else return false;

        Log.d(TAG, "printer status: " + printer.status());
        printer.usbClose();
        return true;
    }

    /**
     * 출력 공통 함수
     */
    private void printByText(HW_API printer, PrinterDTO p){
        //프린트 텍스트 출력
        for(String s : p.getOutput()){
            Log.d("",s);
            printer.sendStr(s);
        }
        printer.feedLine(2);
    }

    private void printByBitMap(HW_API printer, PrinterDTO p){
        //프린트 텍스트 출력
        for(String s : p.getOutput()){
            Log.d("",s);
            Bitmap bitmap = getBitMapText(s, 576, PrinterManager.getInstance().getContext().getAssets());
            saveBitmapToJpg(bitmap, "label","labelTest");
//            printer.printImage(bitmap, printer.getPrinterMaxWidth(), -1, 50, 0, 1);
            printer.printImage(ex_storage + "/label/labelTest.jpg");
            printer.feedLine(2);
            printer.cut(FULL);
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
     * 8cm 용지 기준으로 한 줄은 66칸(한글은 글자 당 2칸, \t(탭)은 8칸)
     */
    private String outStringForReceipt(ReceiptDTO.ReceiptResp response, CardDTO card) {
        StringBuilder strData = new StringBuilder("영수증("+card.getRstResult()+")\n");
        strData.append("[매장명] "+response.getShopName()+"\n");
        strData.append("[사업자번호] "+response.getCompanyNo()+"\n");
        strData.append("[주소] "+ response.getAddress()+"\n");
        strData.append("[대표자] "+ response.getOwner()+"\t\t"+"[TEL] "+ response.getTell()+"\n");
        strData.append("[매출일] "+ response.getPayDate()+"\n");
        strData.append("================================================\n");
        strData.append("\t상품명\t\t단가\t\t수량\t\t금액\t\t\n ");
        strData.append("-----------------------------------------------\n");
        for(ReceiptDTO.ReceiptResp.ItemDTO i : response.getItems()){
            String name = commonService.formatterByLeftSpace(i.getItemName(), 11);
            String unitPrice = commonService.formatterByLeftSpace(String.valueOf((i.getPrice() / i.getQuantity())), 7);
            String quantity = commonService.formatterByLeftSpace(i.getQuantity() + i.getUnit(), 5);
            String price = CommonUtil.convertCommaDecimalFormat(String.valueOf(i.getPrice()));
            strData.append(name+"\t"+unitPrice+"\t"+quantity+"\t"+price+"\n");
        }
        strData.append("------------------------------------------------\n");
        strData.append("합계금액\t\t\t\t\t\t\t"+card.getTotPrice()+"\n");
        strData.append("------------------------------------------------\n");
        strData.append("과세물품가액\t\t\t\t\t\t"+card.getDutiableVal()+"\n");
        strData.append("부  가  세\t\t\t\t\t\t"+card.getTax()+"\n");
        strData.append("매출합계(카드)\t\t\t\t\t\t"+card.getTotPrice()+"\n");
        strData.append("================================================\n");
        strData.append("[할부개월]\t\t\t\t\t\t\t      "+card.getInstallment()+"\n");
        strData.append("[카드구분]\t\t\t\t\t\t\t"+card.getCardName()+"\n");
        strData.append("[승인번호]\t\t\t\t"+card.getApprovalNo()+"\n");
        strData.append("[승인일자]\t\t\t    "+card.getApprovalDate()+"\n");
        strData.append("[카드번호]\t\t\t"+card.getCardBin()+"\n");
        StringBuilder space = new StringBuilder();
        for (int i =  7 - card.getTotPrice().length() ; i > 0; i--) {
            space.append(" ");
        }
        strData.append("[결제금액]\t\t\t\t\t\t " +space+card.getTotPrice()+"\n");
        strData.append("------------------------------------------------\n");
        return strData.toString();
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
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        Log.d(TAG, "external path: " + string_path);
        File file;
        try{
            file = new File(string_path);
            if(!file.isDirectory()) file.mkdirs();
            FileOutputStream out = new FileOutputStream(string_path+file_name, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}
