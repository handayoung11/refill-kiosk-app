package kr.co.nicevan.nvcat.retrofit;

import androidx.annotation.NonNull;

import java.util.List;

public interface RevealStringListCallbacks {
    void onSuccess(@NonNull List<String> value);
    void onError(@NonNull Throwable throwable);
}
