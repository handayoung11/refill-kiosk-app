package kr.co.nicevan.nvcat.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitResponseAPI responseAPI;
    //사용하고 있는 서버 BASE 주소
//    private static String BASE_URL = "http://10.0.2.2:8080/";
    private static String BASE_URL = "http://192.168.0.6:8080/";
//    private static String BASE_URL = "https://refillcycle.com/";

    private static RetrofitClient instance;

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public static RetrofitResponseAPI getRetrofitInterface() {
        return responseAPI;
    }

    private RetrofitClient() {
        //로그를 보기 위한 Interceptor
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        //retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) //로그 기능 추가
                .build();

        responseAPI = retrofit.create(RetrofitResponseAPI.class);
    }

}
