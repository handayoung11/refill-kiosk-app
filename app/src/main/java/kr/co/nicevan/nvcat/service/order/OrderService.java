package kr.co.nicevan.nvcat.service.order;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.dto.KioskOrderDTO;
import kr.co.nicevan.nvcat.retrofit.RevealLongCallbacks;
import kr.co.nicevan.nvcat.service.label.RevealLabelRespCallbacks;
import okhttp3.ResponseBody;
import retrofit2.Callback;

public interface OrderService {

    void saveKioskOrders(KioskOrderDTO.SaveOrders saveOrders, @NonNull RevealLongCallbacks callbacks);
    void sendRefillAuthApi(String phone, Long odIdx, @NonNull RevealOrderRespCallbacks2 callbacks);
}
