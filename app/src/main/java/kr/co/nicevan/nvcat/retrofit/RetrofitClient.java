package kr.co.nicevan.nvcat.retrofit;

import static kr.co.nicevan.nvcat.CommonUtil.BASE_URL;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        //로그를 보기 위한 Interceptor
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //retrofit cookie config
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        CookieJar cookieJar = new JavaNetCookieJar(CookieHandler.getDefault());
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).cookieJar(cookieJar).build();

        //retrofit 객체 생성.
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = getClient();
        }
        return retrofit;
    }

    public static RetrofitResponseAPI getDefaultResponseAPI() {
        return getInstance().create(RetrofitResponseAPI.class);
    }
}
