package kr.co.nicevan.nvcat.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.R;

public class Dialog100 extends NonCancelableDialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    private DialogListener dialogListener;

    String curReqType = ""; // 현재 거래구분(승인/취소)
    String payType = ""; // 결제방법(신용카드/삼성페이)

    public Dialog100(@NonNull Context context, String curReqType){
        super(context, R.layout.dialog_100);
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

        TextView tv_title = (TextView)findViewById(R.id.tv_title);
        Button btn_01 = (Button)findViewById(R.id.btn_01);
        Button btn_02 = (Button)findViewById(R.id.btn_02);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);

        if(curReqType.equals(CommonUtil._승인요청)) {
            tv_title.setText("결제 방법 선택");
            btn_cancel.setText("결제취소");
        }else if(curReqType.equals(CommonUtil._취소요청)){
            tv_title.setText("승인취소 방법 선택");
            btn_cancel.setText("승인취소종료");
        }

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

    public void setData(String data){
    }
}