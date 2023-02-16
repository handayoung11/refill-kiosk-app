package kr.co.nicevan.nvcat.retrofit.callback;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginDefaultCallback implements Callback {

    String tag = getClass().getSimpleName();

    @Override
    public void onResponse(Call call, Response response) {
        Log.d(tag, "login 성공");
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        Log.d(tag, "login 실패");
    }
}
