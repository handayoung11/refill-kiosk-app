package kr.co.nicevan.nvcat.dto;

import com.google.gson.annotations.Expose;

public class LabelDTO {

    public static class LabelResp{
        @Expose
        private String shoName;
        @Expose
        private String liquidName;
        @Expose
        private Integer odExAmount;
        @Expose
        private String liquidDateOfManufacture; //제조일
        @Expose
        private String liquidDateOfUse; //사용기한
        @Expose
        private String liquidIngredients; //전성분
        @Expose
        private String liquidCaution; //사용시 주의사항
        @Expose
        private String liquidManufacturer; //제조업체명
        @Expose
        private String liquidResponsibleSalesBusiness; //책임판매업자
        @Expose
        private String liquidSellerName; //맞춤판매업체
        private String phone;

        public String getShoName() {
            return shoName;
        }

        public String getLiquidName() {
            return liquidName;
        }

        public Integer getOdExAmount() {
            return odExAmount;
        }

        public String getLiquidDateOfManufacture() {
            return liquidDateOfManufacture;
        }

        public String getLiquidDateOfUse() {
            return liquidDateOfUse;
        }

        public String getLiquidIngredients() {
            return liquidIngredients;
        }

        public String getLiquidCaution() {
            return liquidCaution;
        }

        public String getLiquidManufacturer() {
            return liquidManufacturer;
        }

        public String getLiquidResponsibleSalesBusiness() {
            return liquidResponsibleSalesBusiness;
        }

        public String getLiquidSellerName() {
            return liquidSellerName;
        }

        public String getPhone() {
            return phone;
        }
    }


}
