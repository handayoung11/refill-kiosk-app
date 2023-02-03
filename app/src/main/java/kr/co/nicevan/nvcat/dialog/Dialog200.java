package kr.co.nicevan.nvcat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.R;

public class Dialog200 extends Dialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    private DialogListener dialogListener;

    String curReqType = ""; // 현재 진행중인 거래구분(승인요청/취소요청)
    String payType = ""; // 결제방법(신용카드/삼성페이)

    public Dialog200(@NonNull Context context, String curReqType, String payType){
        super(context);
        this.context = context;
        this.curReqType = curReqType;
        this.payType = payType;
    }

    public interface DialogListener{
        public void onPositiveClicked(String data);
        public void onNegativeClicked();
    }

    public void setDialogListener(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_200);

        // 다이얼로그 사이즈 조정
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        layoutParams.width = (int) (size.x * 0.9f);
        layoutParams.height = (int) (size.y * 0.8f);
        getWindow().setAttributes(layoutParams);

        ImageView iv_01 = (ImageView)findViewById(R.id.iv_01);
        TextView tv_01 = (TextView)findViewById(R.id.tv_01);
        TextView tv_02 = (TextView)findViewById(R.id.tv_02);

        if(curReqType.equals(CommonUtil._승인요청)){
            if(payType.equals(CommonUtil._신용카드)) {
                iv_01.setImageResource(R.drawable.img_01);
                tv_01.setText(context.getResources().getString(R.string.msg_01));
                tv_02.setText(context.getResources().getString(R.string.msg_02));
            }else if(payType.equals(CommonUtil._삼성페이)){
                iv_01.setImageResource(R.drawable.img_02);
                tv_01.setText(context.getResources().getString(R.string.msg_01_0));
                tv_02.setText(context.getResources().getString(R.string.msg_02_0));
            }
        }else if(curReqType.equals(CommonUtil._취소요청)){
            if(payType.equals(CommonUtil._신용카드)) {
                iv_01.setImageResource(R.drawable.img_01);
                tv_01.setText(context.getResources().getString(R.string.msg_01));
                tv_02.setText(context.getResources().getString(R.string.msg_02_01));
            }else if(payType.equals(CommonUtil._삼성페이)){
                iv_01.setImageResource(R.drawable.img_02);
                tv_01.setText(context.getResources().getString(R.string.msg_01_0));
                tv_02.setText(context.getResources().getString(R.string.msg_02_0_01));
            }
        }


        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.onPositiveClicked("OK");
            }
        });

        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Dialog200 - 결제취소");
                dialogListener.onNegativeClicked();
                dismiss();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // OUTSIDE 터치시 닫히지 않도록 처리
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // 네비게이션바 Back 버튼 막기
        return;
    }

    public void setData(String data){
    }
}