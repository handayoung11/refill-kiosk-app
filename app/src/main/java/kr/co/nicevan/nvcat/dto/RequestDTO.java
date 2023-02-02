package kr.co.nicevan.nvcat.dto;

import lombok.Data;

public class RequestDTO {

    public static class ReceiptDTO{
        private String approvalNo;

        public String getApprovalNo() {
            return approvalNo;
        }
        public void setApprovalNo(String approvalNo) {
            this.approvalNo = approvalNo;
        }
    }
}
