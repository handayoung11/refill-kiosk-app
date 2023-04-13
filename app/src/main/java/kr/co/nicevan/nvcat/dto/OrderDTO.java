package kr.co.nicevan.nvcat.dto;

import android.util.Log;

import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.nicevan.nvcat.service.common.CommonService;

public class OrderDTO {
    @Expose
    private Long odId;
    private ArrayList<Long> iosIds;
    private ArrayList<Integer> volumes;
    private String phone;
    private String reqType;
    private String amount;
    private String formatPrice;
    private String agreeNum;
    private String agreeDate;

    public void dtoByJson(String json){
        try {
            JSONObject data = new JSONObject(json);
            JSONArray iosIds = data.getJSONArray("itemOfShopIdList");
            JSONArray volumes = data.getJSONArray("volumes");
            this.iosIds = new ArrayList<>();
            this.volumes = new ArrayList<>();
            for(int i=0; i<iosIds.length();i++){
                this.iosIds.add(Long.parseLong((String) iosIds.get(i)));
                String volumeNum = ((String) volumes.get(i)).replace(",", "");
                this.volumes.add(Integer.parseInt(volumeNum));
            }
            this.phone = data.getString("pNum");
            this.reqType = data.getString("reqType");
            this.amount = data.getString("amount");
            this.agreeNum = data.getString("agreeNum");
            this.agreeDate = data.getString("agreeDate");
            // 환불 요청 시 문제 없지만, 결제 요청 시 odId가 null이라서 Exception 발생, 맨 아래 둘 것
            this.odId = data.getLong("odId");
        }
        catch (Exception e){
            Log.i("TAG","error : " + e);
        }
    }

    public void updateOdId(Long id){ this.odId = id; }

    public Long getOdId() { return odId;}

    public ArrayList<Long> getIosIds() { return iosIds; }

    public ArrayList<Integer> getVolumes() {
        return volumes;
    }

    public String getPhone() {return phone;}

    public String getReqType() {
        return reqType;
    }

    public String getAmount() {
        return amount;
    }

    public String getAgreeNum() {
        return agreeNum;
    }

    public String getAgreeDate() {
        return agreeDate;
    }

    public void formatToPrice(String price, CommonService commonService) {
        this.formatPrice = commonService.formatByPrice(price);
    }
}
