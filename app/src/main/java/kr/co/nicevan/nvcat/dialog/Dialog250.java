package kr.co.nicevan.nvcat.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.R;

public class Dialog250 extends NonCancelableDialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    private DialogListener dialogListener;

    public Dialog250(@NonNull Context context){
        super(context);
        this.context = context;
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
        setContentView(R.layout.dialog_250);

        ImageView iv_01 = (ImageView)findViewById(R.id.iv_01);
        TextView tv_01 = (TextView)findViewById(R.id.tv_01);
        TextView tv_02 = (TextView)findViewById(R.id.tv_02);

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
                dialogListener.onNegativeClicked();
                dismiss();
            }
        });

    }

    public void setData(String data){
    }
}