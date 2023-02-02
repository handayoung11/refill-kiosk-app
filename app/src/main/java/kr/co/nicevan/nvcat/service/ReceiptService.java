package kr.co.nicevan.nvcat.service;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.dto.CardDTO;
import kr.co.nicevan.nvcat.dto.RequestDTO;
import kr.co.nicevan.nvcat.dto.ResponseDTO;
import kr.co.nicevan.nvcat.retrofit.RevealStringCallbacks;

public interface ReceiptService {
    void printReceiptByOrder(RequestDTO.ReceiptDTO request, CardDTO card, @NonNull RevealStringCallbacks callbacks);
    String outStringForReceipt(ResponseDTO.ReceiptDTO response, CardDTO card);
    String formatterByLeftSpace(String inputString, int length);
}
