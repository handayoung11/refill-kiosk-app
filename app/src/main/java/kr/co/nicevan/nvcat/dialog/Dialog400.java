package kr.co.nicevan.nvcat.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.R;

public class Dialog400 extends NonCancelableDialog {

    String TAG = this.getClass().getSimpleName();

    private Context context;
    private DialogListener dialogListener;

    public Dialog400(@NonNull Context context){
        super(context);
        this.context = context;
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
        setContentView(R.layout.dialog_400);

        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public void setData(String data){
    }

}