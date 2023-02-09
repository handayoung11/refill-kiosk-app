package kr.co.nicevan.nvcat.service.receipt;

import androidx.annotation.NonNull;

import java.util.List;

import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.retrofit.error.ErrorResponse;

public interface RevealReceiptRespCallbacks {
    void onSuccess(@NonNull ReceiptDTO.ReceiptResp value);
    void onError(@NonNull ErrorResponse errorResponse);
}
