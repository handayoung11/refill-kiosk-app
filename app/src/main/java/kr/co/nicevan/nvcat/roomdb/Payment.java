package kr.co.nicevan.nvcat.roomdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Payment {

    @PrimaryKey(autoGenerate = true)
    public int uid;


    @ColumnInfo(name = "order_no")
    public String order_no; // 주문번호

    @ColumnInfo(name = "user_id")
    public String user_id; // 주문자 고유 아이디


    @ColumnInfo(name = "req_type")
    public String req_type; // 거래구분

    @ColumnInfo(name = "req_kind")
    public String req_kind; // 거래유형

    @ColumnInfo(name = "res_code")
    public String res_code; // 응답코드

    @ColumnInfo(name = "amount")
    public String amount; // 거래금액

    @ColumnInfo(name = "tax")
    public String tax;// 부가세

    @ColumnInfo(name = "bongsa")
    public String bongsa;// 봉사료

    @ColumnInfo(name = "halbu")
    public String halbu; // 할부

    @ColumnInfo(name = "agree_num")
    public String agree_num; // 승인번호

    @ColumnInfo(name = "agree_date")
    public String agree_date; // 승인일시

    @ColumnInfo(name = "bank_cd1")
    public String bank_cd1; // 발급사코드

    @ColumnInfo(name = "bank_nm1")
    public String bank_nm1; // 발급사명

    @ColumnInfo(name = "bank_cd2")
    public String bank_cd2; // 매입사코드

    @ColumnInfo(name = "bank_nm2")
    public String bank_nm2; // 매입사명

    @ColumnInfo(name = "store_no")
    public String store_no; // 가맹점번호

    @ColumnInfo(name = "cat_id")
    public String cat_id; // CATID

    @ColumnInfo(name = "res_msg")
    public String res_msg; // 응답메세지

    @ColumnInfo(name = "card_no")
    public String card_no; // 카드번호

    @ColumnInfo(name = "card_gubun")
    public String card_gubun; // 카드구분

    @ColumnInfo(name = "mng_no")
    public String mng_no; // 전문관리번호

    @ColumnInfo(name = "seq_no")
    public String seq_no; // 거래일련번호

    @ColumnInfo(name = "sign_img")
    public String sign_img; // 서명이미지
}
