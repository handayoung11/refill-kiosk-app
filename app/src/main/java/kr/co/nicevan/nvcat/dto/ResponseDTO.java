package kr.co.nicevan.nvcat.dto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;


public class ResponseDTO {

    public static class ReceiptDTO {
        @SerializedName("shopName")
        @Expose
        private String shopName; //매장명
        @SerializedName("companyNo")
        @Expose
        private String companyNo; //사업자번호
        @SerializedName("address")
        @Expose
        private String address; //주소
        @SerializedName("owner")
        @Expose
        private String owner; //대표자
        @SerializedName("tell")
        @Expose
        private String tell; //전화
        @SerializedName("payDate")
        private String payDate; //구매일
        @SerializedName("items")
        @Expose
        private List<ItemDTO> items;
        public String getShopName() {
            return shopName;
        }
        public String getCompanyNo() {
            return companyNo;
        }
        public String getAddress() {
            return address;
        }
        public String getOwner() {
            return owner;
        }
        public String getTell() {
            return tell;
        }
        public String getPayDate() {
            return payDate;
        }
        public List<ItemDTO> getItems() {
            return items;
        }

        public class ItemDTO {
            String itemName; //상품명(8자까지 표출)
            int price; //구매가격
            String unit; //g or 개
            int quantity; //수량

            public String getItemName() {
                return itemName;
            }
            public int getPrice() {
                return price;
            }
            public String getUnit() {
                return unit;
            }
            public int getQuantity() {
                return quantity;
            }
        }
    }
}
