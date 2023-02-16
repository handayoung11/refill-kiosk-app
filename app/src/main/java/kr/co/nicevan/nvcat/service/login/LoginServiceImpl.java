package kr.co.nicevan.nvcat.service.login;

import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import kr.co.nicevan.nvcat.retrofit.callback.LoginDefaultCallback;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginServiceImpl implements LoginService {

    String tag = getClass().getSimpleName();

    @Override
    public void login(String id, String pw) {
        login(id, pw, new LoginDefaultCallback());
    }

    @Override
    public void login(String id, String pw, Runnable onSuccess, Runnable onFailure) {
        login(id, pw, new LoginDefaultCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (onSuccess != null)  {
                    onSuccess.run();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);
                if (onFailure != null) {
                    onFailure.run();
                }
            }
        });
    }

    public void login(String id, String pw, Callback<ResponseBody> callback) {
        RetrofitClient.getDefaultResponseAPI().login(id, pw).enqueue(callback);
    }
}
