package kr.co.nicevan.nvcat.service.order;

import android.util.Log;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.AppConfig;
import kr.co.nicevan.nvcat.dto.KioskOrderDTO;
import kr.co.nicevan.nvcat.dto.OrderDTO;
import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import kr.co.nicevan.nvcat.retrofit.RevealLongCallbacks;
import kr.co.nicevan.nvcat.retrofit.error.ErrorCode;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;
import kr.co.nicevan.nvcat.retrofit.error.ErrorUtils;
import kr.co.nicevan.nvcat.service.login.LoginService;
import kr.co.nicevan.nvcat.util.KeyStoreUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderServiceImpl implements OrderService {

    LoginService loginService = AppConfig.getInstance().loginService();

    @Override
    public void saveKioskOrders(KioskOrderDTO.SaveOrders saveOrders, @NonNull RevealLongCallbacks callbacks) {
        KeyStoreUtil keyStoreUtil = KeyStoreUtil.getInstance();
        //login 실행 후 api 호출
        loginService.login(keyStoreUtil.getData("id", null), keyStoreUtil.getData("pw", null),
                () -> saveKioskOrdersApi(saveOrders, new RevealOrderRespCallbacks() {
                    @Override
                    public void onSuccess(@NonNull OrderDTO value) {
                        Log.d("결제", "결제 완료, odIdx=[ "+value.getOdId()+" ]");
                        callbacks.on(value.getOdId());
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        Log.d("","==================================================");
                        Log.d(this.getClass().getSimpleName(),"saveKioskOrders");
                        Log.d("결제", "결제 실패");
                        Log.d("[error code] : ", String.valueOf(errorResponse.getStatus()));
                        Log.d("[error title] : ", errorResponse.getTitle());
                        Log.d("[error msg] : ", errorResponse.getMsg());
                        Log.d("","==================================================");
                    }
                }), null);

    }

    private void saveKioskOrdersApi(KioskOrderDTO.SaveOrders saveOrders, @NonNull RevealOrderRespCallbacks callbacks) {
        RetrofitClient.getDefaultResponseAPI()
                .saveKioskOrders(saveOrders)
                .enqueue(new Callback<OrderDTO>() {
                    @Override
                    public void onResponse(Call<OrderDTO> call, Response<OrderDTO> response) {
                        // status code >= 200 || code < 300
                        if(response.isSuccessful()) callbacks.onSuccess(response.body());
                        // status code < 200 || code >= 300
                        else callbacks.onError(ErrorUtils.parseError(response));
                    }

                    @Override
                    public void onFailure(Call<OrderDTO> call, Throwable t) {
                        callbacks.onError(ErrorResponse.of(ErrorCode.RETROFIT_NETWORK_FAILED));
                    }
                });
    }
}
