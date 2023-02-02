package kr.co.nicevan.nvcat.dto;

public class CardDTO {
        private String dutiableVal; //과세물품가액
        private String tax; //부가세
        private String totPrice; //매출합계
        private String cardBin; //카드빈번호 (앞6자리)
        private String installment; //할부개월
        private String cardName;//카드사명
        private String approvalNo; //승인번호
        private String approvalDate; //승인일자

        public String getDutiableVal() {
            return dutiableVal;
        }
        public String getTax() {
            return tax;
        }
        public String getTotPrice() {
            return totPrice;
        }
        public String getCardBin() {
            return cardBin;
        }
        public String getInstallment() {
            return installment;
        }
        public String getCardName() {
            return cardName;
        }
        public String getApprovalNo() {
            return approvalNo;
        }
        public String getApprovalDate() {
            return approvalDate;
        }

    public CardDTO() {
    }

    public CardDTO(String dutiableVal, String tax, String totPrice, String cardBin, String installment, String cardName, String approvalNo, String approvalDate) {
            this.dutiableVal = dutiableVal;
            this.tax = tax;
            this.totPrice = totPrice;
            this.cardBin = cardBin;
            this.installment = installment;
            this.cardName = cardName;
            this.approvalNo = approvalNo;
            this.approvalDate = approvalDate;
        }
}
