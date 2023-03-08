package kr.co.nicevan.nvcat.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.R;

public class Dialog500 extends NonCancelableDialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    private DialogListener dialogListener;

    TextView tv_01;
    TextView tv_02;

    int waitTimeCnt = 10; // 제한시간(초)
    boolean isTimeout = false; // 제한시간 초과여부

    boolean isCompletePrintReceipt = false;
    boolean isCompletePrintLabel = false;

    public Dialog500(@NonNull Context context, boolean isCompletePrintReceipt, boolean isCompletePrintLabel){
        super(context);
        this.context = context;
        this.isCompletePrintReceipt = isCompletePrintReceipt;
        this.isCompletePrintLabel = isCompletePrintLabel;
    }

    public interface DialogListener{
        void onPositiveClicked();
    }

    public void setDialogListener(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_500);

        tv_01 = (TextView)findViewById(R.id.tv_01);
        tv_02 = (TextView)findViewById(R.id.tv_02);

        if(isCompletePrintReceipt && isCompletePrintLabel){
            tv_01.setText(context.getResources().getString(R.string.msg_05));
        }else if(isCompletePrintLabel){
            tv_01.setText(context.getResources().getString(R.string.msg_05_01));
        }

        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(view -> {
            clockHandler.removeMessages(0);
            dialogListener.onPositiveClicked();
            dismiss();
        });

        clockHandler.sendEmptyMessageDelayed(0, 1000);
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

            if(waitTimeCnt > 0) {
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