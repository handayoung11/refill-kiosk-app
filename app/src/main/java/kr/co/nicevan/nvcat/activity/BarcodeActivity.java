package kr.co.nicevan.nvcat.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.nicevan.nvcat.R;

public class BarcodeActivity extends AppCompatActivity {

    String TAG = this.getClass().getSimpleName();

    EditText et_01;
    Button btn_01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode);

        et_01 = (EditText) findViewById(R.id.et_01);
        btn_01 = (Button) findViewById(R.id.btn_01);

        btn_01.requestFocus();
        btn_01.setCursorVisible(true);
        btn_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_01.setText("");
                et_01.requestFocus();
                et_01.setCursorVisible(true);
                Toast.makeText(getApplicationContext(), "바코드를 리더기에 인식시켜 주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        et_01.setFocusableInTouchMode(true);
        et_01.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "Foucs in :" + et_01.getText());
                    hideIME(BarcodeActivity.this, et_01);
                } else {
                    Log.d(TAG, "Foucs out :" + et_01.getText());
                }
            }
        });

        et_01.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged :" + et_01.getText());
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged :" + et_01.getText());
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                Log.d(TAG, "afterTextChanged :" + et_01.getText());
                String barcodeData = et_01.getText().toString();
                Log.d(TAG, "indexOf :" + barcodeData.indexOf("\n"));
                if(barcodeData.indexOf("\n") > 0){
                    Toast.makeText(getApplicationContext(), barcodeData.replaceAll("\n",""), Toast.LENGTH_LONG).show();
                    et_01.setText("");
                }
            }
        });

        hideIME(BarcodeActivity.this, et_01);

    }


    public static void showIME(Context context, View view) {
        view.requestFocus();
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void hideIME(Context context, View view) {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

}