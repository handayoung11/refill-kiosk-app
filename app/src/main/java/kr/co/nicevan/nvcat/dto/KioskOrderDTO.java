package kr.co.nicevan.nvcat.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KioskOrderDTO {

    public static class SaveOrders {
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
}
