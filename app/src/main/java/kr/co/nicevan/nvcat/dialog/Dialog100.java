package kr.co.nicevan.nvcat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.R;

public class Dialog100 extends Dialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    private DialogListener dialogListener;

    String curReqType = ""; // 현재 거래구분(승인/취소)
    String payType = ""; // 결제방법(신용카드/삼성페이)

    public Dialog100(@NonNull Context context, String curReqType){
        super(context);
        this.context = context;
        this.curReqType = curReqType;
    }

    public interface DialogListener{
        public void onPositiveClicked(String data);
        public void onNegativeClicked();
        public void onClickedBtn01();
        public void onClickedBtn02();
        public void choPayType(String payType);
    }

    public void setDialogListener(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_100);

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

        TextView tv_title = (TextView)findViewById(R.id.tv_title);
        Button btn_01 = (Button)findViewById(R.id.btn_01);
        Button btn_02 = (Button)findViewById(R.id.btn_02);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);

        if(curReqType.equals(CommonUtil._승인요청)) {
            tv_title.setText("결제 방법 선택");
            btn_cancel.setText("결 제 취 소");
        }else if(curReqType.equals(CommonUtil._취소요청)){
            tv_title.setText("승인취소 방법 선택");
            btn_cancel.setText("승인취소종료");
        }

        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        // 결제취소
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.onNegativeClicked();
                dismiss();
            }
        });

        // 신용카드 선택
        btn_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payType = CommonUtil._신용카드;
                dialogListener.choPayType(payType);
                dismiss();
            }
        });

        // 삼성페이 선택
        btn_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payType = CommonUtil._삼성페이;
                dialogListener.choPayType(payType);
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