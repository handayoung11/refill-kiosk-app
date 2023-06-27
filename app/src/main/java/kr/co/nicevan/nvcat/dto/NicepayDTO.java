package kr.co.nicevan.nvcat.dto;

import android.util.Log;

public class NicepayDTO {
    static public char fs = 0x1C;

    public static class CompleteOrdereRespDTO {
        private String strRecv[] = new String[30];
        private String TAG = this.getClass().getSimpleName();

         /*
        0: 거래구분     1: 거래유형
        2: 응답코드     3: 거래금액
        4: 부가세      5: 봉사료
        6: 할부       7: 승인번호
        8: 승인일자     9: 발급사코드
        10: 발급사명     11: 매입사코드
        12: 매입사명    13: 가맹점번호
        14: 승인CATID     15: 잔액
        16: 응답메시지       17: 카드BIN
        18: 카드구분: 0-신용카드,1:체크카드,2:선불카드        19: 전문관리번호
        20: 거래일련번호      21: 발생포인트(할인금액)
        22: 가용포인트(지불금액)     23: 누적포인트(잔액한도)
        24: 캐시백가맹점      25: 캐시백승인번호
         */

        private String labels[] = {
                "거래구분", "거래유형", "응답코드", "거래금액",
                "부가세", "봉사료", "할부", "승인번호",
                "승인일자", "발급사코드", "발급사명", "매입사코드",
                "매입사명", "가맹점번호", "승인CATID", "잔액",
                "응답메시지", "카드BIN", "카드구분", "전문관리번호",
                "거래일련번호", "발생포인트(할인금액)", "가용포인트(지불금액)",
                "누적포인트(잔액한도)", "캐시백가맹점", "캐시백승인번호",
                "", "", "", "", ""
        };

        public CompleteOrdereRespDTO(String recvData) {
            int i, j = 0, k = 0;
            for (i = 0; i < recvData.length(); i++) {
                if (recvData.substring(i, i + 1).equals(String.valueOf(fs))) {
                    strRecv[k] = recvData.substring(j, i);
                    j = i + 1;
                    k++;
                }
            }
            strRecv[18] = strRecv[18].equals("0") ? "신용카드" : strRecv[18].equals("1") ? "체크카드" : "선불카드";
            printRecvData();
        }

        private void printRecvData() {
            for (int i = 0; i < strRecv.length; i++) {
                Log.d(TAG, "strRecv[" + i + "] - " + labels[i] + " : " + strRecv[i]);
            }
        }

        public String getData(int idx) {
            return strRecv[idx];
        }

        public int getIntData(int idx) {
            return Integer.parseInt(strRecv[idx]);
        }
    }

    public static class ReqPaymentDTO {

        String payAmount; // 거래금액
        String payAgreenum; // 승인번호 (취소요청시만 해당)
        String payAgreedate; // 원거래일자(YYMMDD) (취소요청시만 해당)
        String payAmountFormat; //포맷된 거래금액 (#,###원)

        public ReqPaymentDTO(String payAmount, String payAgreenum, String payAgreedate, String payAmountFormat) {
            this.payAmount = payAmount;
            this.payAgreenum = payAgreenum;
            this.payAgreedate = payAgreedate;
            this.payAmountFormat = payAmountFormat;
        }

        public String getPayAmount() { return payAmount; }

        public String getPayAgreenum() {
            return payAgreenum;
        }

        public String getPayAgreedate() {
            return payAgreedate;
        }

        public String getPayAmountFormat() { return payAmountFormat; }
    }
}
