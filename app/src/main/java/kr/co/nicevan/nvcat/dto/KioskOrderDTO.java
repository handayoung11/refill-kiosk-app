package kr.co.nicevan.nvcat.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Getter;

public class KioskOrderDTO {

    @Getter
    public static class SaveOrders {
        @SerializedName("itemOfShopIdList")
        private ArrayList<Long> itemOfShopIdList;
        @SerializedName("volumes")
        private ArrayList<Integer> volumes;
        private String pNum;
        private String approvementId;
        private Long memberId;
    }
}
