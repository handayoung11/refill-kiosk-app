package kr.co.nicevan.nvcat.retrofit.error;

import java.io.IOException;
import java.lang.annotation.Annotation;
import kr.co.nicevan.nvcat.retrofit.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {
    public static ErrorResponse parseError(Response<?> response) {
        Converter<ResponseBody, ErrorResponse> converter = RetrofitClient.getInstance().responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse error;
        try {
            error = converter.convert(response.errorBody());
            error.setStatus(response.code());
        } catch (IOException e) {
            return ErrorResponse.of();
        }
        return ErrorResponse.NotNull(error);
    }
}
