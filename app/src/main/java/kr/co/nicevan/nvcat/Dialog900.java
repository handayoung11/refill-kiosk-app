package kr.co.nicevan.nvcat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class Dialog900 extends Dialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    private DialogListener dialogListener;

    TextView tv_title;
    TextView tv_01;
    TextView tv_02;

    int waitTimeCnt = 10; // 제한시간(초)
    boolean isTimeout = false; // 제한시간 초과여부

    String curReqType = ""; // 현재 진행중인 거래구분(승인요청/취소요청)
    public String cancelType = ""; // 종료구분

    public Dialog900(@NonNull Context context, String curReqType, String cancelType){
        super(context);
        this.context = context;
        this.curReqType = curReqType;
        this.cancelType = cancelType;
    }

    public interface DialogListener{
        public void onPositiveClicked();
        public void onNegativeClicked();
    }

    public void setDialogListener(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_900);

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

        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_01 = (TextView)findViewById(R.id.tv_01);
        tv_02 = (TextView)findViewById(R.id.tv_02);

        if(curReqType.equals(CommonUtil._승인요청)){
            tv_title.setText("결 제 종 료");
            if(cancelType.equals(CommonUtil._결제중지)){
                tv_01.setText(context.getResources().getString(R.string.msg_cancel_pay_02));
            }else if(cancelType.equals(CommonUtil._대기종료)){
                tv_01.setText(context.getResources().getString(R.string.msg_cancel_pay_03));
            }
        }else if(curReqType.equals(CommonUtil._취소요청)){
            tv_title.setText("승인취소종료");
            if(cancelType.equals(CommonUtil._결제중지)){
                tv_01.setText(context.getResources().getString(R.string.msg_cancel_pay_02_01));
            }else if(cancelType.equals(CommonUtil._대기종료)){
                tv_01.setText(context.getResources().getString(R.string.msg_cancel_pay_03_01));
            }
        }

        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clockHandler.removeMessages(0);
                dialogListener.onPositiveClicked();
                dismiss();
            }
        });

        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        clockHandler.sendEmptyMessageDelayed(0, 1000);
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

    @SuppressLint("HandlerLeak")
    public Handler clockHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            waitTimeCnt--;
            tv_02.setText(waitTimeCnt + "초 후 자동 종료됩니다.");
            Log.d(TAG, "waitTimeCnt : " + waitTimeCnt);

            if(waitTimeCnt > -1) {
                clockHandler.sendEmptyMessageDelayed(0, 1000);
            }else{
                // 대기시간 종료
                isTimeout = true;
                clockHandler.removeMessages(0);

                Log.d(TAG, "타임아웃!");

                dialogListener.onPositiveClicked();
                dismiss();
            }
        }
    };

}