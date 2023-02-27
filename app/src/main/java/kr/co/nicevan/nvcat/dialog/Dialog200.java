package kr.co.nicevan.nvcat.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.R;

public class Dialog200 extends NonCancelableDialog {

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
        public void onNegativeClicked();
    }

    public void setDialogListener(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_200);

        ImageView iv_01 = findViewById(R.id.iv_01);
        TextView tv_01 = findViewById(R.id.tv_01);
        TextView tv_02 = findViewById(R.id.tv_02);

        if(curReqType.equals(CommonUtil._승인요청)){
            if(payType.equals(CommonUtil._신용카드)) {
                iv_01.setImageResource(R.drawable.insert_credit_card);
                tv_01.setText(context.getResources().getString(R.string.msg_01));
                tv_02.setText(context.getResources().getString(R.string.msg_02));
            }else if(payType.equals(CommonUtil._삼성페이)){
                iv_01.setImageResource(R.drawable.img_02);
                tv_01.setText(context.getResources().getString(R.string.msg_01_0));
                tv_02.setText(context.getResources().getString(R.string.msg_02_0));
            }
        }else if(curReqType.equals(CommonUtil._취소요청)){
            if(payType.equals(CommonUtil._신용카드)) {
                iv_01.setImageResource(R.drawable.insert_credit_card);
                tv_01.setText(context.getResources().getString(R.string.msg_01));
                tv_02.setText(context.getResources().getString(R.string.msg_02_01));
            }else if(payType.equals(CommonUtil._삼성페이)){
                iv_01.setImageResource(R.drawable.img_02);
                tv_01.setText(context.getResources().getString(R.string.msg_01_0));
                tv_02.setText(context.getResources().getString(R.string.msg_02_0_01));
            }
        }


        Button btn_cancel = findViewById(R.id.ic_modal_btn_cancel);
        btn_cancel.setOnClickListener(view -> {
            Log.d(TAG, "Dialog200 - 결제취소");
            dialogListener.onNegativeClicked();
            dismiss();
        });

    }

    public void setData(String data){
    }
}