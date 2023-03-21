package kr.co.nicevan.nvcat.service.order;

import androidx.annotation.NonNull;

import java.util.List;

import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.OrderDTO;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;

public interface RevealOrderRespCallbacks {
    void onSuccess(@NonNull OrderDTO value);
    void onError(@NonNull ErrorResponse errorResponse);
}
