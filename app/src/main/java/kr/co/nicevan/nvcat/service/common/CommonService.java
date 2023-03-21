package kr.co.nicevan.nvcat.service.common;

public interface CommonService {
    String formatterByLeftSpace(String inputString, int length);
    String formatterByRightEnter(String inputString, int length);
    String formatByPrice(String data);
}
