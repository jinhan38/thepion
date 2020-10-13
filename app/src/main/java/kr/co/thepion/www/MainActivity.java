package kr.co.thepion.www;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;


public class MainActivity extends Activity implements ViewSwitcher.ViewFactory {

    private ValueCallback<Uri> filePathCallbackNormal;
    private ValueCallback<Uri[]> filePathCallbackLollipop;
    private Uri mCapturedImageURI;
    private static final String TAG = "MainActivity";

    public final static String TYPE_REGISTER = "register";
    public final static String TYPE_LICENSEREGIST = "licenseRegist";
    public final static String TYPE_STATUS_NOTICE = "status_notice";
    public final static String TYPE_PW_SEARCH = "pw_search";
    public final static String TYPE_BID_REGIST = "bid_regist";
    public final static String TYPE_BID_SELECT = "bid_select";
    public final static String TYPE_BID_CURRENT = "bid_current";
    public final static String TYPE_BID_CANCEL = "bid_cancle";
    public final static String TYPE_BID_COMPLETE = "bid_complete";
    public final static String TYPE_LEAVECANCEL = "leaveCancle";


    WebView mWebView;
    ProgressBar progressBar;
    ProgressBar progressBarCircle;
    ProgressBar progressBarCircle_2;
    LinearLayout ll_loading;
    LinearLayout mLayout;
    TextView tv_loadMsg;
    TextView tv_loadPer;
    TextView tv_switch;
    Switch switch1;
    ValueCallback mFilePathCallback;
    ImageView logo;
    ImageView iv_setting;
    Boolean isStartAction = true;
    Boolean isWebLoaded = false;
    Boolean isAniLoaded = false;
    private long backKeyPressedTime = 0;
    AndroidBridge androidBridge;
    Timer timer;
    public static MainActivity mainActivity;
    int pStatus = 0;
    private boolean loadingPageCall = false;


    private Handler handler = new Handler();


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Util.CURRENT_CONTEXT = this;
        Util.VERSION = getString(R.string.ver);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로고정
        this.mainActivity = MainActivity.this;
        Log.e(TAG, "onCreate: main context = " + mainActivity);
        progressBar = findViewById(R.id.progress);
        progressBarCircle = findViewById(R.id.circularProgressbar);
        progressBarCircle_2 = findViewById(R.id.circularProgressbar_2);
        mLayout = findViewById(R.id.mainLayout);
        ll_loading = findViewById(R.id.ll_loading);
        tv_loadMsg = findViewById(R.id.tv_loadMsg);
        tv_loadPer = findViewById(R.id.tv_loadPer);
        tv_switch = findViewById(R.id.tv_switch);
        switch1 = findViewById(R.id.switch1);
        logo = findViewById(R.id.logo);

        getHashKey();

        progressBarCircle_2.setIndeterminate(false);
        new Thread(() -> {

            while (pStatus < 100) {
                pStatus += 1;
                handler.post(() -> {

                    progressBarCircle_2.setProgress(pStatus);
                });
                try {
                    Thread.sleep(12); //thread will take approx 1.5 seconds to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }).start();

        timer = new Timer(true);

        fadeInAndShowImageView(logo);
        mainAnimationStart();

        mWebView = findViewById(R.id.webView);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Util.TOKEN = newToken;
            Log.e("newToken", newToken);
        });

        browserSettings();

        pushTask();

        if (UserInfo.isGetPushClick()) {
            //푸시메세지 클릭한 경우

            if (UserInfo.getNeedLogin().equals("y")) {
                //로그인이 필요한 경우
                Log.e(TAG, "sendNotification: 로그인 필요성 체크");

                if (SharedPreference.getIsLogin().equals("y")) {
                    Log.e(TAG, "sendNotification: 로그인 여부 체크");

                    Log.e(TAG, "onCreate: shared레벨 : " + SharedPreference.getLevel());
                    Log.e(TAG, "onCreate: shared레벨 : " + UserInfo.getLevel());
                    if (SharedPreference.getLevel().equals(UserInfo.getLevel())) {
                        //기존에 로그인된 레벨과 푸시메세지로 받은 레벨이 같은 경우 레벨에 따라 내용 분류
                        Log.e(TAG, "sendNotification: 레벨체크 통과");

                        switch (UserInfo.getLevel()) {
                            case "111":
                                if (String.valueOf(UserInfo.getPushUrl()).contains("AhNum")) {
                                    mWebView.loadUrl(getString(R.string.pionUrl) + UserInfo.getPushUrl());
                                }
                                break;
                            case "11":
                                if (String.valueOf(UserInfo.getPushUrl()).contains("LiNum")) {
                                    mWebView.loadUrl(getString(R.string.pionUrl) + UserInfo.getPushUrl());
                                }
                                break;
                            case "999":
                                Log.e(TAG, "onCreate: 관리자");
                                mWebView.loadUrl(getString(R.string.pionUrl) + UserInfo.getPushUrl());
                                break;
                            default:
                                mWebView.loadUrl(getString(R.string.pionUrl));
                                break;
                        }

                    } else {
                        Log.e(TAG, "onCreate:레벨체크 통과 못함");
                        //기존에 로그인된 레벨과 푸시메세지로 받은 레벨이 다른 경우
                        mWebView.loadUrl(getString(R.string.pionUrl));
                    }

                } else {
                    Log.e(TAG, "onCreate: 로그인 안된 상황");
                }

            } else {
                //로그인이 필요 없는 경우
                Log.e(TAG, "onCreate: 로그인이 필요 없는 경우 : " + getString(R.string.pionUrl) + UserInfo.getPushUrl());
                if (String.valueOf(UserInfo.getPushUrl()).length() > 0) mWebView.loadUrl(getString(R.string.pionUrl) + UserInfo.getPushUrl());
            }

            UserInfo.setGetPushClick(false);

        } else {
            //푸시메세지 클릭 안하고 그냥 접속한 경우
            mWebView.loadUrl(getString(R.string.pionUrl));

        }

        new kr.co.thepion.www.DownloadManager(this.getApplicationContext());
    }


    private void pushTask() {
        //FCM 데이터 보낸 데이터 받는 코드
        Bundle bundle = getIntent().getExtras();
        Log.e(TAG, "pushTask: 진입");

        //bundle로 받는 값 url, type, level, usernum
        if (bundle != null) {
            if (bundle.get("url") != null) UserInfo.setPushUrl(bundle.get("url"));
            else UserInfo.setPushUrl("");

            Log.e(TAG, "pushTask: url : " + UserInfo.getPushUrl());
            UserInfo.setType(bundle.get("type"));
            UserInfo.setLoanNum(bundle.get("loanNum"));
            UserInfo.setLevel(bundle.get("level"));
            Log.e(TAG, "pushTask: bundle Level : " + bundle.get("level"));
            UserInfo.setNeedLogin(bundle.get("needLogin"));
            UserInfo.setGetPushClick(true);
        }

        Log.e(TAG, "pushTask: out");

    }


    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void browserSettings() {
        mWebView.setBackgroundColor(0); //배경색
        mWebView.setHorizontalScrollBarEnabled(false); //가로 스크롤
        mWebView.setVerticalScrollBarEnabled(false); //세로 스크롤
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        androidBridge = new AndroidBridge(mWebView, 0);
        mWebView.addJavascriptInterface(androidBridge, "HybridApp");


        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true); //javascript 허용
        settings.setSupportMultipleWindows(true); //다중웹뷰 허용
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//javascript의 window.open 허용
        settings.setDomStorageEnabled(true);

        //HTML을 파싱하여 웹뷰에서 보여주거나 하는 작업에서 width , height 가 화면 크기와 맞지 않는 현상을 잡아주는 코드
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

        //기본 웹뷰 세팅
        //메인 추가 웹뷰 세팅
        settings.setAllowFileAccess(true);//파일 엑세스
        settings.setLoadWithOverviewMode(true);
        settings.setAppCachePath(mainActivity.getApplicationContext().getCacheDir().getAbsolutePath());
        settings.setPluginState(WebSettings.PluginState.ON);

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            mWebView.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(url));
            Log.e("logURL", url);
        });

        mWebView.setWebViewClient(new WishWebViewClient());
        mWebView.setWebChromeClient(new FullscreenableChromeClient(mainActivity) {


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
                progressBarCircle.setProgress(progress);

                if (!loadingPageCall) {
                    loadingPageCall = true;
                }

                Log.e(TAG, "onProgressChanged: progress : " + progress);

                if (Get_Internet(MainActivity.this) > 0) {
                    if (progress == 100) {
                        loadingPageCall = false;
                        isWebLoaded = true;

                        androidBridge.connectAndroidApp();


                    } else {

                        if (isStartAction) {
                            tv_loadPer.setText("(" + progress + " / 100)");

                        }

                    }
                } else {
                    tv_loadMsg.setText("인터넷에 연결되지 않았습니다.\n잠시후 다시 접속해주세요");
                }
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public boolean onCreateWindow(final WebView view, boolean dialog, boolean userGesture, Message resultMsg) {

                WebView newWebView = new WebView(MainActivity.this);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.e(TAG, "shouldOverrideUrlLoading: " + url);
                        Intent browserIntent = new Intent(MainActivity.this, PopViewActivity.class);
                        browserIntent.setData(Uri.parse(url));
                        browserIntent.putExtra("url", url);
                        MainActivity.this.startActivity(browserIntent);
                        return true;
                    }

                });
                return true;
            }
        });

        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);

            //------------------------COOKIE!!------------------------
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            //------------------------COOKIE!!------------------------

            request.setDescription("Downloading file...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                    mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                            url, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "Downloading File",
                    Toast.LENGTH_LONG).show();
        });

    }

    /**
     * 스크립트로 받은 값 다시 스크립트로 보내기
     *
     * @param str
     */
    public void callJavaScript(String str) {
        Log.e("callJavaScript", str);
        mWebView.loadUrl("javascript:sendParam('" + str + "')"); //자바스크립트로 보낼 str값 입력, 이때 sendParam에서 앞글자인 s는 대문자 안됨
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
        if (mWebView.getUrl().toLowerCase().contains("/mobile/index_m") &&
                !mWebView.getUrl().toLowerCase().contains("#menu") &&
                !mWebView.getUrl().toLowerCase().contains("#mypage")) {
            //mWebView.clearCache(true);

            mWebView.clearHistory();
            Log.e("webView", "clearHistory");
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public View makeView() {

        TextView textSwitcherItem = new TextView(MainActivity.this);
        textSwitcherItem.setTextColor(Color.WHITE);
        textSwitcherItem.setTextSize(10);
        textSwitcherItem.setGravity(Gravity.CENTER);
        return textSwitcherItem;
    }

    public class WishWebViewClient extends WebViewClient {
        public static final String INTENT_PROTOCOL_START = "intent:";
        public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
        public static final String INTENT_PROTOCOL_END = ";end;";
        public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";
        public static final String INTENT_PROTOCOL_TEL = "tel:";
        public static final String INTENT_PROTOCOL_MAILTO = "mailto:";


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.e(TAG, "onPageStarted: ");
            progressBar.setVisibility(View.VISIBLE);
            androidBridge.androidLoadingPageShow();

            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e(TAG, "onPageFinished: ");
            progressBar.setVisibility(View.GONE);
            androidBridge.androidLoadingPageHide();

            if (isAniLoaded && isStartAction) {
                fadeOutAndHideLinearLayout(ll_loading);
                progressBarCircle.setVisibility(View.GONE);
                progressBarCircle_2.setVisibility(View.GONE);

            }
            super.onPageFinished(view, url);
        }

        @SuppressLint("LongLogTag")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            if (url.startsWith(INTENT_PROTOCOL_START)) {
                final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                if (customUrlEndIndex < 0) {
                    return false;
                } else {
                    final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                    try {
                        mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                    } catch (ActivityNotFoundException e) {
                        final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                        final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                        final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                        mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                    }
                    return true;
                }
            } else {
                view.loadUrl(url);
            }


            if (url.startsWith(INTENT_PROTOCOL_TEL)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith(INTENT_PROTOCOL_MAILTO)) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(i);
                return true;
            }

            return false;
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
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
                    mWebView.loadUrl("about:blank"); // 빈페이지 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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


    @Override
    public void onBackPressed() {

        Log.e(TAG, "onBackPressed: 현재 url : " + mWebView.getUrl());
//        if (mWebView.getUrl() )
        if (mWebView.getUrl().equals("https://www.thepion.co.kr:444/Mobile/index_m") ||
                mWebView.getUrl().equals("http://test.thepion.co.kr/Mobile/index_m")) {
            backPressAppDestroy();
        } else {

            if (mWebView.canGoBack()) {
                mWebView.goBack();
                Log.e(TAG, "onBackPressed: goBack");
            } else {
                backPressAppDestroy();
            }
        }

    }

    private void backPressAppDestroy() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(mainActivity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            finish();
        }
    }

    private void mainAnimationStart() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (isWebLoaded) {
                fadeOutAndHideLinearLayout(ll_loading);
            }
        }, 2500); //2500
        final Handler handler2 = new Handler();
        handler2.postDelayed(() -> {
            if (!isWebLoaded) {
                tv_loadMsg.setVisibility(View.VISIBLE);
                if (Get_Internet(MainActivity.this) == 0) { //연결되지 않았다면
                    tv_loadMsg.setText("인터넷에 연결되지 않았습니다.");
                }
            } else {
                if (!isAniLoaded) {
                    fadeOutAndHideLinearLayout(ll_loading);
                }

            }
        }, 4500); //4500

        final Handler handler3 = new Handler();
        handler3.postDelayed(() -> {
            if (!isWebLoaded) {
                if (Get_Internet(MainActivity.this) == 0) { //연결되지 않았다면
                    tv_loadMsg.setText("인터넷에 연결되지 않았습니다.\n잠시후 다시 접속해주세요");
                } else if (Get_Internet(MainActivity.this) == 1) { //wifi 연결
                    tv_loadMsg.setText("wifi 연결상태가 좋지 않습니다.");
                } else if (Get_Internet(MainActivity.this) == 2) { //데이터 연결
                    tv_loadMsg.setText("데이터 연결상태가 좋지 않습니다.");
                } else if (Get_Internet(MainActivity.this) == 3) { //데이터 연결
                    tv_loadMsg.setText("데이터 연결상태가 좋지 않습니다.");
                }
                tv_loadPer.setVisibility(View.VISIBLE);
            } else {
                if (!isAniLoaded) {
                    fadeOutAndHideLinearLayout(ll_loading);
                }
            }
        }, 8000); //8500
        final Handler handler4 = new Handler();
        handler4.postDelayed(() -> {
            if (!isWebLoaded) {
                if (Get_Internet(MainActivity.this) == 0) { //연결되지 않았다면
                    tv_loadMsg.setText("인터넷에 연결되지 않았습니다.\n잠시후 다시 접속해주세요");
                } else if (Get_Internet(MainActivity.this) == 1) { //wifi 연결
                    tv_loadMsg.setText("wifi 연결상태가 좋지 않습니다.\n잠시후 다시 접속해주세요");
                } else if (Get_Internet(MainActivity.this) == 2) { //데이터 연결
                    tv_loadMsg.setText("데이터 연결상태가 좋지 않습니다.\n잠시후 다시 접속해주세요");
                } else if (Get_Internet(MainActivity.this) == 3) { //데이터 연결
                    tv_loadMsg.setText("데이터 연결상태가 좋지 않습니다.\n잠시후 다시 접속해주세요");
                }
                tv_loadPer.setVisibility(View.VISIBLE);
            } else {
                if (!isAniLoaded) {
                    fadeOutAndHideLinearLayout(ll_loading);
                }
            }
            //지연시키길 원하는 밀리초 뒤에 동작
        }, 15000); //8500
    }

    private void fadeOutAndHideLinearLayout(final LinearLayout linearLayout) {
        isAniLoaded = true;
        isStartAction = false;
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                linearLayout.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        linearLayout.startAnimation(fadeOut);
    }

    private void fadeInAndShowImageView(final ImageView iv) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(600);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                iv.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        iv.startAnimation(fadeIn);
    }



/*Get_Internet
: 인터넷 연결환경에 대해 체크한다.
0을 리턴할 경우, 인터넷 연결끊김
1을 리턴할 경우, 와이파이 연결상태
2를 연결할 경우, 인터넷 연결상태*/


    public static int Get_Internet(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Log.e(TAG, "Get_Internet: type : " + activeNetwork.getType());
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return 1;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return 2;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX) {
                return 3;
            }
        }
        return 0;
    }

    private void getHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }


}
