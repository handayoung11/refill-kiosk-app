package kr.co.nicevan.nvcat.dto;

public class PrinterDTO {

    public static class CommonReceipt {
        String prtAmount; // 금액
        String prtTax; // 부가세
        String prtTotAmount; // 합계금액
        String signImgString;

        public CommonReceipt(String prtAmount, String prtTax, String prtTotAmount, String signImgString) {
            this.prtAmount = prtAmount;
            this.prtTax = prtTax;
            this.prtTotAmount = prtTotAmount;
            this.signImgString = signImgString;
        }

        public String getPrtAmount() {
            return prtAmount;
        }

        public String getPrtTax() {
            return prtTax;
        }

        public String getPrtTotAmount() {
            return prtTotAmount;
        }

        public String getSignImgString() {
            return signImgString;
        }
    }

    public static class CommonLabel {

    }
}
