package kr.co.nicevan.nvcat.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.nicevan.nvcat.R;
import kr.co.nicevan.nvcat.util.KeyStoreUtil;

public class CatIdActivity extends AppCompatActivity {

    KeyStoreUtil keyStoreUtil = KeyStoreUtil.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_id);

        EditText editText = findViewById(R.id.cat_id_edit_text);
        findViewById(R.id.cat_id_btn_save).setOnClickListener(e -> {

            keyStoreUtil.storeData(KeyStoreUtil.CAT_ID_KEY, editText.getText().toString());
            Toast.makeText(this, "CATID가 저장되었습니다.(" + keyStoreUtil.getData(KeyStoreUtil.CAT_ID_KEY, null) + ")", Toast.LENGTH_SHORT).show();
        });
    }
}
