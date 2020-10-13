package kr.co.thepion.www;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import static kr.co.thepion.www.MainActivity.WishWebViewClient.GOOGLE_PLAY_STORE_PREFIX;
import static kr.co.thepion.www.MainActivity.WishWebViewClient.INTENT_PROTOCOL_END;
import static kr.co.thepion.www.MainActivity.WishWebViewClient.INTENT_PROTOCOL_INTENT;
import static kr.co.thepion.www.MainActivity.WishWebViewClient.INTENT_PROTOCOL_START;

public class PopViewActivity extends Activity {
    private static final String TAG = "PopViewActivity";
    private String url;

    WebView pWebView;
    ProgressBar progressBar;
    public static PopViewActivity openerActivity;
    private ValueCallback mFilePathCallback;
    private AndroidBridge androidBridge;
    private boolean loadingPageCall = false;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_webview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로고정
        Util.CURRENT_CONTEXT = this;
        Log.e(TAG, "onCreate: popview context = " + Util.CURRENT_CONTEXT);
        openerActivity = PopViewActivity.this;
        Intent intent = getIntent();
        String newURL = intent.getExtras().getString("url");
        pWebView = findViewById(R.id.popView);
        Log.e("PopViewActivity", newURL);
        url = newURL;
        progressBar = this.findViewById(R.id.progress2);

        browserSettings();

        pWebView.loadUrl(newURL);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("resultCode:: ", String.valueOf(resultCode));
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            } else {
                mFilePathCallback.onReceiveValue(new Uri[]{data.getData()});
            }
            mFilePathCallback = null;
        } else {
            mFilePathCallback.onReceiveValue(null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        progressBar.setVisibility(View.VISIBLE);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && pWebView.canGoBack()) {
            pWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class WishWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("WishWebViewClient", "첫번째 팝뷰 진입");
            view.loadUrl(url);
            if (url.startsWith(INTENT_PROTOCOL_START)) {
                final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                if (customUrlEndIndex < 0) {
                    return false;
                } else {
                    final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                    try {
                        openerActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                    } catch (ActivityNotFoundException e) {
                        final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                        final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                        final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                        openerActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                    }
                    return true;
                }
            } else {
                view.loadUrl(url);
            }
            progressBar.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            switch (errorCode) {
                case ERROR_AUTHENTICATION: // 서버에서 사용자 인증 실패
                case ERROR_BAD_URL: // 잘못된 URL
                case ERROR_CONNECT: // 서버로 연결 실패
                case ERROR_FAILED_SSL_HANDSHAKE: // SSL handshake 수행 실패
                case ERROR_FILE: // 일반 파일 오류
                case ERROR_FILE_NOT_FOUND: // 파일을 찾을 수 없습니다
                case ERROR_HOST_LOOKUP: // 서버 또는 프록시 호스트 이름 조회 실패
                case ERROR_IO: // 서버에서 읽거나 서버로 쓰기 실패
                case ERROR_PROXY_AUTHENTICATION: // 프록시에서 사용자 인증 실패
                case ERROR_REDIRECT_LOOP: // 너무 많은 리디렉션
                case ERROR_TIMEOUT: // 연결 시간 초과
                case ERROR_TOO_MANY_REQUESTS: // 페이지 로드중 너무 많은 요청 발생
                case ERROR_UNKNOWN: // 일반 오류
                case ERROR_UNSUPPORTED_AUTH_SCHEME: // 지원되지 않는 인증 체계
                    //                case ERROR_UNSUPPORTED_SCHEME:
                    pWebView.loadUrl("about:blank"); // 빈페이지 출력
                    Log.e(TAG, "onReceivedError: " + errorCode);
                    AlertDialog.Builder builder = new AlertDialog.Builder(PopViewActivity.this);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();// 확인버튼 클릭시 이벤트
                        }
                    });
                    builder.setMessage("네트워크 상태가 원활하지 않습니다. 어플을 종료합니다.");
                    builder.setCancelable(false); // 뒤로가기 버튼 차단
                    builder.show(); // 다이얼로그실행
                    break;
            }
        }
    }

    @SuppressLint("AddJavascriptInterface")
    private void browserSettings() {
        pWebView.setBackgroundColor(0); //배경색
        pWebView.setHorizontalScrollBarEnabled(false); //가로 스크롤
        pWebView.setVerticalScrollBarEnabled(false); //세로 스크롤
        // Bridge 인스턴스 등록
        pWebView.addJavascriptInterface(new AndroidBridge(pWebView, 2), "HybridApp");
        //mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); //스크롤 노출타입
        if (Build.VERSION.SDK_INT >= 21) {
            pWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        WebSettings settings = pWebView.getSettings();
        settings.setJavaScriptEnabled(true); //javascript 허용
        settings.setSupportMultipleWindows(true); //다중웹뷰 허용
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//javascript의 window.open 허용

        //HTML을 파싱하여 웹뷰에서 보여주거나 하는 작업에서 width , height 가 화면 크기와 맞지 않는 현상이 발생한다
        //이를 잡아주기 위한 코드
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //캐시파일 사용 금지(운영중엔 주석처리 할 것)
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); //개발중엔 no_cache, 배포중엔 load_default

        //zoom 허용
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);

        //zoom 하단 도움바 삭제
        settings.setDisplayZoomControls(false);

        //meta태그의 viewport사용 가능
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        pWebView.setWebViewClient(new WishWebViewClient());
        pWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(intent, 0);
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (!loadingPageCall) {
                    androidBridge.androidLoadingPageShow();
                    loadingPageCall = true;
                }
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    androidBridge.androidLoadingPageHide();
                    loadingPageCall = false;

                } else {
                    progressBar.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public boolean onCreateWindow(final WebView view, boolean dialog,
                                          boolean userGesture, Message resultMsg) {
                WebView newWebView2 = new WebView(PopViewActivity.this);
                WebView.WebViewTransport transport
                        = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView2);
                resultMsg.sendToTarget();

                newWebView2.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent2 = new Intent(PopViewActivity.this, PopMoreViewActivity.class);
                        browserIntent2.setData(Uri.parse(url));
                        browserIntent2.putExtra("url", url);
                        PopViewActivity.this.startActivity(browserIntent2);
                        return true;
                    }
                });

                return true;
            }

        });


        // Bridge 인스턴스 등록
        androidBridge = new AndroidBridge(pWebView, 1);
        pWebView.addJavascriptInterface(androidBridge, "HybridApp");
    }


    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: 현재 url : " + pWebView.getUrl());

        if (!pWebView.canGoBack()) {
            Log.e(TAG, "onBackPressed: canGoBack false");

            if (pWebView.getUrl().contains("https://www.thepion.co.kr:444/Mobile/m_Views/SignIn/mSignInCheck?saKey")) {
                Log.e(TAG, "onBackPressed: 페이지 포함함");
                androidBridge.androidLoadingPageHide();
            }

            this.finish();
        }
    }
}
