package kr.co.nicevan.nvcat.service.order;

import android.util.Log;

import kr.co.nicevan.nvcat.AppConfig;
import kr.co.nicevan.nvcat.dto.KioskOrderDTO;
import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import kr.co.nicevan.nvcat.service.login.LoginService;
import kr.co.nicevan.nvcat.util.KeyStoreUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderServiceImpl implements OrderService{

    LoginService loginService = new AppConfig().loginService();
    KeyStoreUtil keyStoreUtil = KeyStoreUtil.getInstance();

    @Override
    public void saveKioskOrders(KioskOrderDTO.SaveOrders saveOrders) {
        //login 실행
        loginService.login(keyStoreUtil.getData("id", null), keyStoreUtil.getData("pw", null));
        RetrofitClient.getDefaultResponseAPI().saveKioskOrders(saveOrders)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("결제", "결제 완료");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
