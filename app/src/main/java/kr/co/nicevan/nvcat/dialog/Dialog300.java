package kr.co.nicevan.nvcat.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.R;

public class Dialog300 extends NonCancelableDialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    private DialogListener dialogListener;
    private String curReqType;

    public Dialog300(@NonNull Context context, String curReqType){
        super(context);
        this.context = context;
        this.curReqType = curReqType;
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
        setContentView(R.layout.dialog_300);

        TextView body_text = findViewById(R.id.body_text);

        if(curReqType.equals(CommonUtil._승인요청)) {
            body_text.setText("결제가 완료되었습니다.");
        }else if(curReqType.equals(CommonUtil._취소요청)){
            body_text.setText("결제취소가 완료되었습니다.");
        }


        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.onPositiveClicked();
                dismiss();
            }
        });

        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.onNegativeClicked();
                dismiss();
            }
        });
    }

    public void setData(String data){
    }

}