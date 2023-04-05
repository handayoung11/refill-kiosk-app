package kr.co.nicevan.nvcat.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import kr.co.nicevan.nvcat.CommonUtil;
import kr.co.nicevan.nvcat.R;

public class Dialog250 extends NonCancelableDialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    String curReqType = ""; // 현재 진행중인 거래구분(승인요청/취소요청)

    public Dialog250(String curReqType, @NonNull Context context){
        super(context);
        this.context = context;
        this.curReqType = curReqType;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_250);

        TextView tv_title = findViewById(R.id.tv_title);
        TextView body_text = findViewById(R.id.body_text);

        if(curReqType.equals(CommonUtil._승인요청)) {
            tv_title.setText("마그네틱 결제");
            body_text.setText("IC 카드를 인식하지 못하여\\nMS 결제를 진행합니다.");
        }else if(curReqType.equals(CommonUtil._취소요청)){
            tv_title.setText("마그네틱 결제 취소");
            body_text.setText("IC 카드를 인식하지 못하여\\nMS 결제취소를 진행합니다.");
        }

        ImageView iv = findViewById(R.id.ms_pay_md_iv);
        Glide.with(this.context).load(R.drawable.ms_pay_md_card).into(iv);
    }

    public void setData(String data){
    }
}