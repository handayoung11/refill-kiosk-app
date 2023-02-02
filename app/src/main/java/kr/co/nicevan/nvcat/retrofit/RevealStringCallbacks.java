package kr.co.nicevan.nvcat.retrofit;


import android.support.annotation.NonNull;

public interface RevealStringCallbacks {
    void onSuccess(@NonNull String value);
    void onError(@NonNull Throwable throwable);
}
