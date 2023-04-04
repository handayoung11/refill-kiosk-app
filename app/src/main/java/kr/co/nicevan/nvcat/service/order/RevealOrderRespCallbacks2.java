package kr.co.nicevan.nvcat.service.order;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.dto.OrderDTO;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;

public interface RevealOrderRespCallbacks2 {
    void onError(@NonNull ErrorResponse errorResponse);
}
