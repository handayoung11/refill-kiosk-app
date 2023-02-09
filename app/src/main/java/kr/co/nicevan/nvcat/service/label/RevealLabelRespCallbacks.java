package kr.co.nicevan.nvcat.service.label;

import androidx.annotation.NonNull;

import java.util.List;

import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;

public interface RevealLabelRespCallbacks {
    void onSuccess(@NonNull List<LabelDTO.LabelResp> value);
    void onError(@NonNull ErrorResponse errorResponse);
}
