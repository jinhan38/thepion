package kr.co.thepion.www;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import static kr.co.thepion.www.MainActivity.mainActivity;

/**
 * Created by Administrator on 2017-08-03.
 */

public class AndroidBridge {
    private static final String TAG = "AndroidBridge";
    private final Handler handler = new Handler();
    private final Handler handler2 = new Handler();
    private final Handler handler3 = new Handler();
    private final Handler handler4 = new Handler();
    private WebView mWebView;
    private Integer curNum;
    private int bridgeConnectNum = 0;
    private boolean fromJavaScriptCheck = false;

    // 생성자
    // 따로 사용할일 없으면 이거 안만들고 위의 변수도 안만들어도 됨.
    public AndroidBridge(WebView mWebView, Integer curNum) {
        this.mWebView = mWebView;
        this.curNum = curNum;
        Log.e(TAG, "AndroidBridge: 브릿지 연결 curNum : " + curNum);
    }


    //쉐어드 프리퍼런스로 로그인 값 저장
    //자동로그인된상태면 진행중 페이지로 보내고
    //아니면 로그인 시키고 페이지로 보내기기

    // 안드로이드 접속
    @JavascriptInterface
    public void connectAndroidApp() { // 토큰값을 호출하면 아래 함수로 값을 전송한다.
        handler3.post(new Runnable() {
            public void run() {
                Log.e(TAG, "run: connectAndroidApp ");
//                Log.e(TAG, "run: token : " + Util.TOKEN );
                String strVersion = Util.VERSION.replaceAll("ver", "").replaceAll("\\.", "").trim();
                mWebView.loadUrl("javascript:setAndroidAppToken('" + Util.TOKEN + "')"); //토큰값 입력
                mWebView.loadUrl("javascript:setAndroidAppVersion('" + strVersion + "')"); //버전값 전송
//                bridgeConnectNum++;
                if (UserInfo.isGetPushClick() && SharedPreference.getAutologin().equals("y")) {
                    if (SharedPreference.getAutologin().equals("y")) {
                        Log.e(TAG, "run: 푸시 유알엘 " + Util.APP_PACKAGENAME + UserInfo.getPushUrl());
                        mWebView.loadUrl(Util.APP_PACKAGENAME + UserInfo.getPushUrl());
                        UserInfo.setGetPushClick(false);
                    }
                }

            }
        });
    }


    //본인인증 팝업 닫았을 때 호출하는 메소드
//    @JavascriptInterface
//    public void whenPopupisClosed(String url) {
//        handler.post(() -> {
//
//            Log.e(TAG, "whenPopupisClosed: ");
////            mWebView.loadUrl("javascript:identityVerificationClose('" + url + "')");
//        });
//    }

    @JavascriptInterface
    public void isLoginConfirm(final String isLogin, final String level, final String autoLogin) {
        handler3.post(new Runnable() {
            public void run() {
                Log.e(TAG, "run: isLoginConfirm 진입 : " + isLogin);

                if (isLogin.equals("y")) {

                    SharedPreference.setIsLogin(isLogin);
                    SharedPreference.setLevel(level);
                    SharedPreference.setAutologin(autoLogin);
                    Log.e(TAG, "run: isLogin : " + isLogin + " ,level : " + level + " ,autoLogin : " + autoLogin);
                    fromJavaScriptCheck = true;
                }
            }
        });

    }

    /**
     * 로딩페이지 호출
     */
    @JavascriptInterface
    public void androidLoadingPageShow() {
        handler.post(new Runnable() {
            public void run() {
                mWebView.loadUrl("javascript:androidLoadingPageShow()");
                //웹페이지에서 로딩이 끝나면 알아서 로딩 페이지를 닫아버린다.
                Log.e(TAG, "androidLoadingPageShow: ");
            }
        });

    }

    /**
     * 로딩페이지 닫기
     */
    @JavascriptInterface
    public void androidLoadingPageHide() {
        handler.post(new Runnable() {
            public void run() {
                mWebView.loadUrl("javascript:androidLoadingPageHide()");
                Log.e(TAG, "androidLoadingPageHide: ");
            }
        });

    }


//    @JavascriptInterface
//    public void androidAppVersionCheck() {
//        handler.post(new Runnable() {
//            public void run() {
//                String appVersion = Util.VERSION.replaceAll("\\.", "").replace("ver ", "");
//                mWebView.loadUrl("javascript:androidAppVersionCheck('" + appVersion + "')");
//                Log.e(TAG, "run: androidAppVersionCheck() : " + appVersion);
//            }
//        });
//    }

    // 창닫기
    @JavascriptInterface
    public void webViewLogoutCallBack() { // must be final
        Log.e(TAG, "webViewLogoutCallBack: ");
        handler.post(new Runnable() {
            public void run() {
                fromJavaScriptCheck = false;
            }
        });
    }

    // 창닫기
    @JavascriptInterface
    public void activityClose() { // must be final
        handler.post(new Runnable() {
            public void run() {

                Log.e("HybridApp", "데이터 요청");
                String test = "닫기버튼이 눌렸습니다.";
                Log.e("HybridApp", test);
                Log.e("activityClose", test);

                if (curNum == 1) {
                    PopViewActivity.openerActivity.onBackPressed();
                } else if (curNum == 2) {
                    PopMoreViewActivity.openerMoreActivity.onBackPressed();
                } else if (curNum == 3) {
                    PopMoreMoreViewActivity.openerMoreMoreActivity.onBackPressed();
                } else if (curNum == 0) {
                    mainActivity.onBackPressed();
                }
            }
        });
    }

    @JavascriptInterface
    public void callMarketDownloadForManager() {
        handler.post(new Runnable() {
            public void run() {
                try {
                    mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Util.APP_PACKAGENAME_FOR_MANAGER)));
                } catch (Exception e) {
                    mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Util.APP_PACKAGENAME_FOR_MANAGER)));
                }
            }
        });
    }

    @JavascriptInterface
    public void callMarketDownload() {
        handler.post(new Runnable() {
            public void run() {
                try {
                    mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Util.APP_PACKAGENAME)));
                } catch (Exception e) {
                    mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Util.APP_PACKAGENAME)));
                }
            }
        });
    }


    @JavascriptInterface
    public void activityCloseSendParam(final String str) {
        handler.post(new Runnable() {
            public void run() {
                Log.e("HybridApp", "데이터 요청");
                String test = "닫기버튼이 눌렸습니다.";
                Log.e("HybridApp", test);
                Log.e("activityClose", test);

                if (curNum == 1) {
                    Log.e("activityCloseSendParam", str);
                    mainActivity.callJavaScript(str);
                    PopViewActivity.openerActivity.onBackPressed();
                } else if (curNum == 2) {
                    PopMoreViewActivity.openerMoreActivity.onBackPressed();
                } else if (curNum == 3) {
                    PopMoreMoreViewActivity.openerMoreMoreActivity.onBackPressed();
                } else if (curNum == 0) {
                    mainActivity.onBackPressed();
                }
            }
        });
    }


    @JavascriptInterface
    public void activityCloseParentRefresh(final String url) { // must be final
        handler.post(new Runnable() {
            public void run() {
                Log.e("HybridApp", "데이터 요청");
                String test = "닫기버튼이 눌렸습니다.";
                Log.e("HybridApp", test);

                //밑에창에(부모웹뷰에) showLoading()

                if (curNum == 1) {
                    Log.e("curNum", 1 + ", url : " + url);
                    mainActivity.mWebView.loadUrl(url);
                    PopViewActivity.openerActivity.finish();
                } else if (curNum == 2) {
                    Log.e("curNum", 2 + ", url : " + url);
                    PopViewActivity.openerActivity.pWebView.loadUrl(url);
                    PopMoreViewActivity.openerMoreActivity.finish();
                } else if (curNum == 3) {
                    PopViewActivity.openerActivity.pWebView.loadUrl(url);
                    PopMoreViewActivity.openerMoreActivity.finish();
                    PopMoreMoreViewActivity.openerMoreMoreActivity.finish();
                } else if (curNum == 0) {
                    mainActivity.mWebView.loadUrl("javascript:showLoading()");
                    mainActivity.mWebView.loadUrl(url);

                    Log.e("curNum", 0 + ", url : " + url);
                    //mainActivity.onBackPressed();
                }
            }
        });
    }


    // 토큰 생성하기
    @JavascriptInterface
    public void callTokenValue(final String num, final String id, final String level) { // 토큰값을 호출하면 아래 함수로 값을 전송한다.
        handler2.post(new Runnable() {
            public void run() {
                Util.NUM = num;
                Util.MID = id;
                Util.MEMBER_LEVEL = level;
                Util.isLogin = true;
                activityClose();
                Log.e("loginVal", Util.NUM + Util.MID + Util.MEMBER_LEVEL);
            }
        });
    }


    private void pushRedirect() {
        if (SharedPreference.getLevel().equals("111")) {
            if (!UserInfo.getAhNum().equals("null")) {
                mWebView.loadUrl("javascript:sendAhNum('" + UserInfo.getAhNum() + "')");
            }
            mWebView.loadUrl("javascript:sendLoanNum('" + UserInfo.getLoanNum() + "')");
            mWebView.loadUrl("javascript:appCMemberPushRedirect('" + UserInfo.getType() + "')");
            UserInfo.setGetPushClick(false);
        } else if (SharedPreference.getLevel().equals("11")) {
            mWebView.loadUrl("javascript:sendLoanNum('" + UserInfo.getLoanNum() + "')");
            mWebView.loadUrl("javascript:appManagerPushRedirect('" + UserInfo.getType() + "')");
            UserInfo.setGetPushClick(false);
        }
    }

    @JavascriptInterface
    public void savePhoneInWebView(final String name, final String phone, final String isManager) {
        handler4.post(new Runnable() {
            public void run() {
                Log.e("savePhoneInWebView", "name : " + name);
                Log.e("savePhoneInWebView", "name : " + phone);
                Log.e("savePhoneInWebView", "name : " + isManager);
                String rename = name;
                if (isManager.equals("y")) {
                    rename += "(PION 매니저)";
                } else {
                    rename += "(PION 고객)";
                }
                Util.savePhone(rename, phone);
            }
        });
    }

    @JavascriptInterface
    public void callInWebView(final String phone) {
        handler4.post(new Runnable() {
            public void run() {
                Util.callInWebView(phone);
            }
        });
    }


    //웹 자바스크립트에서 호출
//    @JavascriptInterface
//    public void onDownloadStart(String url, String fileName) {
//
//        Log.e("WebView", "clicked!");
//        Toast toast = new Toast(mainActivity);
//        //다운로드 모듈
//        Util.DOWNLOAD_FILE_NAME = fileName;
//        toast.makeText(mainActivity, "파일다운로드 중입니다.", Toast.LENGTH_LONG).show();
//        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String strDir = file.getAbsolutePath();
//
//        DownloadManager.getInstance().setSavePath(strDir + "/ARTEUM"); // 저장하려는 경로 지정.
//        DownloadManager.getInstance().setDownloadUrl("https://www.arteum.co.kr" + url);
//        Log.e("WebView", "true");
//
//    }

//    public String getVersionInfo(Context context) {
//        String version = null;
//        try {
//            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            version = i.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//        }
//        return version;
//    }


}

