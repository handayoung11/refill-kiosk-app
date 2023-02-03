package kr.co.nicevan.nvcat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

public abstract class NonCancelableDialog extends Dialog {

    public NonCancelableDialog(@NonNull Context context) {
        super(context, false, null);
        setCanceledOnTouchOutside(false);
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
