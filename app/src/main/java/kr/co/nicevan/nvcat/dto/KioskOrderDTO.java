package kr.co.nicevan.nvcat.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KioskOrderDTO {

    public static class SaveOrders {
        private String rstResult;
        @SerializedName("itemOfShopIdList")
        private ArrayList<Long> itemOfShopIdList;
        @SerializedName("volumes")
        private ArrayList<Integer> volumes;
        private String phone;
        private String approvementId;
        private Long memberId;

        private String catId;
        public SaveOrders(ArrayList<Long> itemOfShopIdList, ArrayList<Integer> volumes, String phone, String approvementId, Long memberId, String catId) {
            this.itemOfShopIdList = itemOfShopIdList;
            this.volumes = volumes;
            this.phone = phone;
            this.approvementId = approvementId;
            this.memberId = memberId;
            this.catId = catId;
        }

        public ArrayList<Long> getItemOfShopIdList() {
            return itemOfShopIdList;
        }

        public ArrayList<Integer> getVolumes() {
            return volumes;
        }

        public String getPhone() {return phone;}

        public String getApprovementId() {
            return approvementId;
        }

        public Long getMemberId() {
            return memberId;
        }
    }

    public static class SendRefillAuth{
        private Long odIdx;
        private String odPhone;

        public SendRefillAuth(Long odIdx, String odPhone) {
            this.odIdx = odIdx;
            this.odPhone = odPhone;
        }

        public Long getOdIdx() {
            return odIdx;
        }

        public String getOdPhone() {
            return odPhone;
        }
    }

    public static class RefundOrderDTO {
        private Long odIdx;
        private String agreeNum;

        public RefundOrderDTO(Long odIdx, String agreeNum) {
            this.odIdx = odIdx;
            this.agreeNum = agreeNum;
        }
    }
}
