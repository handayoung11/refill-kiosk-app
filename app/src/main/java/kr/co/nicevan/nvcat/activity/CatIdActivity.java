package kr.co.nicevan.nvcat.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.nicevan.nvcat.R;

public class CatIdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_id);

        findViewById(R.id.button_save).setOnClickListener(e -> {
            Toast.makeText(this, "CATID가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }
}
