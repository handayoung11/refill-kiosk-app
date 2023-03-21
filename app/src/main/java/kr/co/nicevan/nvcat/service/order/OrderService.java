package kr.co.nicevan.nvcat.service.order;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.dto.KioskOrderDTO;
import kr.co.nicevan.nvcat.retrofit.RevealLongCallbacks;

public interface OrderService {

    void saveKioskOrders(KioskOrderDTO.SaveOrders saveOrders, @NonNull RevealLongCallbacks callbacks);
}
