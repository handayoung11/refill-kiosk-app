package kr.co.nicevan.nvcat.service.login;

import android.util.Log;

import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginServiceImpl implements LoginService {

    String tag = getClass().getSimpleName();

    @Override
    public void login(String id, String pw) {
        RetrofitClient.getDefaultResponseAPI().login(id, pw).enqueue(new DefaultCallback());
    }

    @Override
    public void login(String id, String pw, Runnable onFailure) {
        RetrofitClient.getDefaultResponseAPI().login(id, pw)
                .enqueue(new DefaultCallback() {
                    @Override
                    public void onFailure(Call call, Throwable t) {
                        super.onFailure(call, t);
                        onFailure.run();
                    }
                });
    }

    class DefaultCallback implements Callback {

        @Override
        public void onResponse(Call call, Response response) {
            Log.d(tag, "login 성공");
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            Log.d(tag, "login 실패");
        }
    }
}
