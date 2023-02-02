package kr.co.nicevan.nvcat.util;

import android.net.http.SslError;
import android.os.Message;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ComponentUtil {

    private static String TAG = ComponentUtil.class.getSimpleName();

    public static void configWebView(WebView webView) {
        webView.setWebContentsDebuggingEnabled(true);
        WebSettings webViewSettings = webView.getSettings();
        // 한글
        webViewSettings.setDefaultTextEncodingName("UTF-8");
        // 캐시파일 사용 금지(운영중엔 주석처리 할 것)
        webViewSettings.setCacheMode(webView.getSettings().LOAD_NO_CACHE);
        // javascript를 실행할 수 있도록 설정
        webViewSettings.setJavaScriptEnabled(true);
        // javascript가 window.open()을 사용할 수 있도록 설정
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 여러개의 윈도우를 사용할 수 있도록 설정
        webViewSettings.setSupportMultipleWindows(true);
        // wide viewport를 사용하도록 설정
        webViewSettings.setUseWideViewPort(true);
        // 네트워크의 이미지의 리소스를 로드하지 않음
        webViewSettings.setBlockNetworkImage(false);
        // 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드하도록 설정
        webViewSettings.setLoadsImagesAutomatically(true);
        webViewSettings.setAppCacheEnabled(true);
        webViewSettings.setDatabaseEnabled(true);
        webViewSettings.setDomStorageEnabled(true);
        webViewSettings.setGeolocationEnabled(true);
        // 확대,축소 기능을 사용할 수 있도록 설정
        webViewSettings.setSupportZoom(false);
        // 줌인 아이콘을 사용할 수 있도록 설정
        webViewSettings.setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            //@TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, WebResourceRequest request) {
                Log.d("shouldOverrideUrl", "url ---- " + request.getUrl());

                webview.loadUrl(request.getUrl().toString());
                return true;

                //return super.shouldOverrideUrlLoading(webview, request);
            }
            @Override
            public void onPageFinished(WebView webview, String url) {
                // 페이지 로딩완료시 호출
                Log.d("onPageFinished", "url :: " + url);

                super.onPageFinished(webview, url);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.d("onReceivedSslError", "url = " + error.getUrl().toString());
                handler.proceed(); // SSL 인증서 무시
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.d("onCreateWindow","onCreateWindow");

                return true;
            }
        });
    }
}
