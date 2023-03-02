package kr.co.nicevan.nvcat.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.R;

public class Dialog900 extends NonCancelableDialog {

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

    public Dialog900(@NonNull Context context, String curReqType, String cancelType) {
        super(context);
        this.context = context;
        this.curReqType = curReqType;
        this.cancelType = cancelType;
    }

    public interface DialogListener {
        void onPositiveClicked();
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_900);

        tv_title = findViewById(R.id.tv_title);
        tv_01 = findViewById(R.id.tv_01);
        tv_02 = findViewById(R.id.tv_02);
        ImageView iv = findViewById(R.id.md_od_fail_iv);
        Glide.with(this.context).load(R.drawable.md_od_fail_hourgrass).into(iv);
        Button btn_ok = findViewById(R.id.modal_od_fail_btn_cancel);

        if (curReqType.equals(CommonUtil._승인요청)) {
            if (cancelType.equals(CommonUtil._대기종료)) {
                tv_title.setText("대기시간 만료");
                tv_01.setText(context.getResources().getString(R.string.msg_cancel_pay_03));
            }
        } else if (curReqType.equals(CommonUtil._취소요청)) {
            TextView repayTv = findViewById(R.id.md_od_fail_repay_tv);
            repayTv.setText(R.string.md_od_fail_cancel_pay_repay);
            btn_ok.setText("승인취소 종료");

            if (cancelType.equals(CommonUtil._결제중지)) {
                tv_title.setText("승인취소 중지");
                tv_01.setText(context.getResources().getString(R.string.msg_cancel_pay_02_01));
            } else if (cancelType.equals(CommonUtil._대기종료)) {
                tv_title.setText("대기시간 만료");
                tv_01.setText(context.getResources().getString(R.string.msg_cancel_pay_03_01));
            }
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clockHandler.removeMessages(0);
                dialogListener.onPositiveClicked();
                dismiss();
            }
        });

        clockHandler.sendEmptyMessageDelayed(0, 1000);
    }

    public void setData(String data) {
    }

    @SuppressLint("HandlerLeak")
    public Handler clockHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            waitTimeCnt--;
            tv_02.setText(waitTimeCnt + "초 후 자동 종료됩니다.");
            Log.d(TAG, "waitTimeCnt : " + waitTimeCnt);

            if (waitTimeCnt > 0) {
                clockHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
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