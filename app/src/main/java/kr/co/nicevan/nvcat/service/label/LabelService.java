package kr.co.nicevan.nvcat.service.label;

import androidx.annotation.NonNull;

import kr.co.nicevan.nvcat.dto.LabelDTO;
import kr.co.nicevan.nvcat.dto.ReceiptDTO;
import kr.co.nicevan.nvcat.retrofit.RevealStringListCallbacks;

public interface LabelService {
    void printLabelByOrder(String approvalNo, @NonNull RevealStringListCallbacks callbacks);
    String outStringForLabel(LabelDTO.LabelResp response);
}
