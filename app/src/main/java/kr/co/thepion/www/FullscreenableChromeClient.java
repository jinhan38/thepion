package kr.co.thepion.www;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;


/**
 * 동영상 화면 때문에 추가한 클래스
 */
public class FullscreenableChromeClient extends WebChromeClient {
    private Activity mActivity = null;
    private static final String TAG = "FullscreenableChromeCli";
    public static View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private FrameLayout mFullscreenContainer;
    public static FullscreenableChromeClient fullscreenableChromeClient;
    public static boolean isFullScreen = false;
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public FullscreenableChromeClient(Activity activity) {
        this.mActivity = activity;
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        fullscreenableChromeClient = this;

        Log.e(TAG, "onShowCustomView: context = " + view.getContext()  );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mOriginalOrientation = mActivity.getRequestedOrientation();
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            mFullscreenContainer = new FullscreenHolder(mActivity);
            mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
            decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
            mCustomView = view;
            setFullscreen(true);
            mCustomViewCallback = callback;
//          mActivity.setRequestedOrientation(requestedOrientation);

        }

        super.onShowCustomView(view, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
        this.onShowCustomView(view, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }
        Log.e(TAG, "onHideCustomView:mCustomView context =  " + mCustomView.getContext() );

        setFullscreen(false);
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();
        mActivity.setRequestedOrientation(mOriginalOrientation);


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setFullscreen(boolean enabled) {

        Window win = mActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
            isFullScreen = true;
            mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE  );
            Log.e(TAG, "setFullscreen: " + isFullScreen );
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            isFullScreen = false;
            Log.e(TAG, "setFullscreen: " + isFullScreen );
        }

        win.setAttributes(winParams);
    }


    private static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
        }
        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

}
