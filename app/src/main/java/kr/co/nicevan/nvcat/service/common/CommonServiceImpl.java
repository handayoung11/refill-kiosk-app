package kr.co.nicevan.nvcat.service.common;

public class CommonServiceImpl implements CommonService{

    /**  2022-01-30 작성자 : 염에녹
     * 기능 : 문자열을 원하는 길이만큼 subString.
     * Ex1) formatterByLeftSpace("안녕하세요", 3) -> "안녕하"
     * Ex2) formatterByLeftSpace("안녕하세요", 10) -> "     안녕하세요"
     */
    @Override
    public String formatterByLeftSpace(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString.substring(0, length);
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(' ');
        }
        sb.append(inputString);
        return sb.toString();
    }

    @Override
    public String formatterByRightEnter(String inputString, int length) {
        StringBuilder sb = new StringBuilder();
        while (inputString.length() > length){
            sb.append(inputString.substring(0, length));
            sb.append("\n");
            inputString = inputString.substring(length);
        }
        sb.append(inputString);
        return sb.toString();
    }


}
