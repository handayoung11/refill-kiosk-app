package kr.co.nicevan.nvcat;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Base64;

public class CommonUtil {

    public static String TAG = "CommonUtil";
    public static String BASE_URL = "https://refillcycle.com/";
    public static String KIOSK_LOGIN_URL = BASE_URL + "kiosk/auth/login";
    public static String KIOSK_SHOP_SELECT_URL = BASE_URL + "kiosk/item-of-shop/shop";
    public static String KIOSK_HOME_URL = BASE_URL + "kiosk/item-of-shop?shopIdx=";
    public static String KIOSK_ORDER_SUCCESS_URL = BASE_URL + "kiosk/order/complete/";
    public static String KIOSK_ORDER_DETAIL_URL = BASE_URL + "kiosk/order/detail/";

    // 키오스크 정보 (키오스크 정보 관리자 화면에서 저장한 값 가져오기)
    public static String CATID = "2393300001"; // CATID
    public static String corpNo = "2208115770"; // 사업자번호

    // 결제방법
    public static String _신용카드 = "신용카드";
    public static String _삼성페이 = "삼성페이";

    // 전문요청 거래구분 (코드)
    public static String _승인요청 = "0200";
    public static String _취소요청 = "0420";

    // 전문응답 거래구분 (코드)
    public static String _승인응답 = "0210";
    public static String _취소응답 = "0430";

    // 전문요청 WCC 구분 (코드)
    public static String _IC카드 = "I"; // 카드
    public static String _MS카드 = "F"; // FALLBACK

    // 결제중지 구분
    public static String _결제중지 = "결제중지";
    public static String _대기종료 = "대기종료";


    /**
     * 비트맵 -> 문자 변환
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap){
        String image = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            image = Base64.getEncoder().encodeToString(byteArray);
        }
        return image;
    }

    /**
     * 비트맵문자 -> 비트맵 변환
     * @param data
     * @return
     */
    public static Bitmap stringToBitmap(String data){
        Bitmap bitmap = null;
        byte[] byteArray = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byteArray = Base64.getDecoder().decode(data);
        }
        InputStream stream = new ByteArrayInputStream(byteArray);
        bitmap = BitmapFactory.decodeStream(stream);
        return bitmap;
    }

    /**
     * 문자 null 체크
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 콤마가 포함된 가격 String 을 콤마를 제거한 Integer 로 변환
     * @param value
     * @return
     */
    public static int convertDecimalFormatToInteger(String value) {
        String retVal = "0";
        if (!isNull(value)) {
            retVal = value.replaceAll("\\,", "");
        }
        return Integer.parseInt(retVal);
    }

    /**
     * 숫자형 문자 3자리 마다 콤마
     * @return
     */
    public static String convertCommaDecimalFormat(String orgVal){
        String rtnVal = "";
        try {
            String tempVal = "";
            if (orgVal == null) {
                tempVal = "";
            } else {
                tempVal = orgVal.trim();
            }
            tempVal = Integer.toString(Integer.parseInt(tempVal));
            DecimalFormat decFormat = new DecimalFormat("###,###,###");
            rtnVal = decFormat.format(Integer.parseInt(tempVal));
            Log.d(TAG, "rtnVal : " + rtnVal);
        }catch(Exception e){
            Log.d(TAG, "Exception : " + e.getMessage());
        }
        return rtnVal;
    }

    /**
     * 전체화면
     */
    public static void hideSystemUI(Object context) {
        View decorView;
        if (context instanceof Activity) {
            decorView = ((Activity) context).getWindow().getDecorView();
        } else if (context instanceof Dialog) {
            decorView = ((Dialog) context).getWindow().getDecorView();
        } else {
            return;
        }

        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
//        decorView.requestLayout();
    }
}
