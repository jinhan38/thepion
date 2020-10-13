package kr.co.thepion.www;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingTextChange extends AppCompatActivity {

    private static final String TAG = "LoadingTextChange";
    private TextSwitcher textSwitcher;
    private String[] loading_content;

    private int count = 0;
    private TextView textSwitcherItem;
    private View loadingPageWrap;

    public void init(View view, TextSwitcher textSwitcher) {

        Log.e(TAG, "init: ");
        loadingPageWrap = view;
        loadingPageWrap.setVisibility(View.GONE);
        this.textSwitcher = textSwitcher;

        loading_content = new String[]
                {
                        "데이터를 불러오는 중입니다",
                        "조금만 기다려주세요",
                        "합리적인 금융사 경쟁입찰 방식",
                        "높은 한도의 주택담보대출, LTV 80% ↑"
                };


//        textSwitcher.setFactory(() -> {
//            textSwitcherItem = findViewById(R.id.switcher_item);
//            return textSwitcherItem;
//        });

    }

    public void show() {
        Log.e(TAG, "show: ");
        Handler handler = new Handler();

        loadingPageWrap.setVisibility(View.VISIBLE);

        new Thread(() -> {

            if (count > loading_content.length - 1) {
                count = 0;
            }
            textSwitcher.setText(loading_content[count]);
            count += 1;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (count > loading_content.length - 1) {
//                    count = 0;
//                }
//                handler.postDelayed(this, 1500);
//                textSwitcher.setText(loading_content[count]);
//                count += 1;
//            }
//        });


    }

    public void hide() {
        Log.e(TAG, "hide: ");
        loadingPageWrap.setVisibility(View.GONE);
    }


//        Handler handler = new Handler();
//        handler.post(() -> {
//
//            if (count > loading_content.length - 1) {
//                count = 0;
//            }
//            textSwitcher.setText(loading_content[count]);
//            count += 1;
//
//        });


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: 로딩페이지 ");
    }
}
