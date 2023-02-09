package kr.co.nicevan.nvcat.service.receipt;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.retrofit.RevealStringCallbacks;

public interface ReceiptService {
    void printReceiptByOrder(CardDTO card, @NonNull RevealReceiptRespCallbacks callbacks);
}
