package kr.co.nicevan.nvcat.service.receipt;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.dto.CardDTO;

public interface ReceiptService {
    void printReceiptByOrder(CardDTO card, @NonNull RevealReceiptRespCallbacks callbacks);
}
