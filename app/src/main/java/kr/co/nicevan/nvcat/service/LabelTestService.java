package kr.co.nicevan.nvcat.service;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * 이미지 인쇄 service
 * */
public class LabelTestService {

    private final int width = 600; //이미지 넓이.
    private ImgPrintConfig config; //이미지 인쇄 옵션 설정 객체.

    /**
     * 생성자에 있는 참조변수 외에는 전부 고정 부탁드립니다.
     */
    public LabelTestService() {
        this.config = ImgPrintConfig.of(width, -1, 50, 0, 1);
    }

    public LabelTestService(int alignment, int brightness, int dither, int compress) {
        this.config = ImgPrintConfig.of(width, alignment, brightness, dither, compress);
    }

    private String outStringForLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append("꽃마리 버블 핸드솝 100g\n");
        sb.append("[제조일] 2022-01-01\t\t\t\t\t[사용기한]  2022-01-01"+"까지\n");
        sb.append("---------------------------------------------------------------------------------------------------------------------------------------\n");
        sb.append("[전성분]\n");
        sb.append("해바라기씨오일, 폴리글리세릴-2세스퀴올리에이트(식물유래유화제), 살구씨오일, 1)코코넛야자오일(10%), 스위트아몬드오일, 폴리글리세릴-6트라이카프릴레이트(식물유래유화제), 폴리글리세릴-6카프릴레이트(식물유래유화제), 올리브오일, 식물성오일, 올리브껍질오일, 토코페롤(비타민E), 2)라벤더오일, 2)레몬껍질오일, 2)오렌지껍질오일, 2)리날룰(라벤더오일 포함 성분), 2)리모넨(라벤더오일 포함 성분) 1)유기농 인증원료 2)천연 에센셜 오일과 그 안에 포함된 성분\n\n");
        sb.append("[사용시 주의사항]\n");
        sb.append("1. 화장품 사용 시 또는 사용 후 직사광선에 의하여 사용부위가 붉은 반점, 부어오름 또는 가려움증 등의 이상 증상이나 부작용이 있는 경우 전문의 등과 상담할 것 2. 상처가 있는 부위 등에는 사용을 자제할 것 3. 보관 및 취급 시의 주의사항 가) 어린이의 손이 닿지 않는 곳에 보관할 것 나) 직사광선을 피해서 보관할 것 4. 제품 사용 전 (윗팔 안쪽 부위에) 패치 테스트 후 사용할 것 5. 천연 성분으로 인해 색상이 변할 수 있음\n\n");
        sb.append("[제조업체] 꽃마리 협동조합\t\t[책임판매업체] 꽃마리 협동조합\t\t[맞춤판매업체] 자연상점\n");
        sb.append("---------------------------------------------------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }
    private Bitmap formatBitMapByText(String text, int width){
        //new paint
        Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
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
    public ImgPrintConfig getConfig() {return config;}
    public Bitmap getBitMap(){return formatBitMapByText(outStringForLabel(), this.width);}


    /**
     * 이미지 프린트 config 객체
     * */
    public static class ImgPrintConfig {
        private int width;
        private int alignment;
        private int brightness;
        private int dither;
        private int compress;

        private ImgPrintConfig(int width, int alignment, int brightness, int dither, int compress) {
            this.width = width;
            this.alignment = alignment;
            this.brightness = brightness;
            this.dither = dither;
            this.compress = compress;
        }
        private static ImgPrintConfig of(int width, int alignment, int brightness, int dither, int compress){
            return new ImgPrintConfig(width-50, alignment, brightness, dither, compress);
        }
        public int getWidth() {return width;}
        public int getAlignment() {return alignment;}
        public int getBrightness() {return brightness;}
        public int getDither() {return dither;}
        public int getCompress() {return compress;}
    }
}
