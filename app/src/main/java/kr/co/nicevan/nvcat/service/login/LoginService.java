package kr.co.nicevan.nvcat.service.login;

public interface LoginService {
    void login(String id, String pw);

    void login(String id, String pw, Runnable onFailure);
}
