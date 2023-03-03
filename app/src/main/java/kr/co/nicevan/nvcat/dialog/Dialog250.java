package kr.co.nicevan.nvcat.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import kr.co.nicevan.nvcat.R;

public class Dialog250 extends NonCancelableDialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;

    public Dialog250(@NonNull Context context){
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.dialog_250);

        ImageView iv = findViewById(R.id.ms_pay_md_iv);
        Glide.with(this.context).load(R.drawable.ms_pay_md_card).into(iv);
    }

    public void setData(String data){
    }
}