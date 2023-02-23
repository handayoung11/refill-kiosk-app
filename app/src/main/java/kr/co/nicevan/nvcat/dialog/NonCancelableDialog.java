package kr.co.nicevan.nvcat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public abstract class NonCancelableDialog extends Dialog {

    public NonCancelableDialog(@NonNull Context context) {
        super(context, false, null);
        setCanceledOnTouchOutside(false);
    }

    protected void onCreate(Bundle savedInstanceState, int contentViewId) {
        super.onCreate(savedInstanceState);
        setContentView(contentViewId);

        // 다이얼로그 사이즈 조정
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        layoutParams.width = (int) (size.x * 0.9f);
        layoutParams.height = (int) (size.y * 0.8f);
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onBackPressed() {
        // 네비게이션바 Back 버튼 막기
        return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // OUTSIDE 터치시 닫히지 않도록 처리
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}
