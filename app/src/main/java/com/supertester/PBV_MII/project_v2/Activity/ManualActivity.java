package com.supertester.PBV_MII.project_v2.Activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.supertester.PBV_MII.project_v2.R;


public class ManualActivity extends AppCompatActivity {
    WebView mWebView;
    TextView index;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        UI_control();

        // 웹뷰 셋팅
        mWebView = findViewById(R.id.webView);//xml 자바코드 연결
        index = findViewById(R.id.index);
        String myUrl = "file:///android_asset/www/pbv_manual_ko.html";
        mWebView.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
        mWebView.loadUrl(myUrl);//웹뷰 실행
        mWebView.setBackgroundColor(Color.parseColor("#000000"));
        mWebView.setWebChromeClient(new WebChromeClient());//웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        mWebView.setWebViewClient(new WebViewClientClass());//새창열기 없이 웹뷰 내에서 다시 열기//페이지 이동 원활히 하기위해 사용

        index.bringToFront();
        new Handler().postDelayed(this::SetIndexView, 300);
    }

    private void SetIndexView() {
        String indexView = ((mWebView.getScrollY() / 600) + 1) + "/" + ((mWebView.getContentHeight() / 600) + 1);
        index.setText(indexView);
        Log.e("log_scroll", indexView);
    }

    public void UI_control() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//뒤로가기 버튼 이벤트
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {//웹뷰에서 뒤로가기 버튼을 누르면 뒤로가짐
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("getkey", event.getKeyCode() + "");
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT://bt300 제스쳐 시도
                mWebView.setScrollY(mWebView.getScrollY() - 300);
                if (mWebView.getScrollY() < 0)
                    mWebView.setScrollY(0);
                break;
            case KeyEvent.KEYCODE_DPAD_UP://bt300 제스쳐 시도
                mWebView.setScrollY(mWebView.getScrollY() - 600);
                if (mWebView.getScrollY() < 0)
                    mWebView.setScrollY(0);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mWebView.setScrollY(mWebView.getScrollY() + 600);
                if (mWebView.getScrollY() > mWebView.getContentHeight())
                    mWebView.setScrollY(mWebView.getScrollY() - 600);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mWebView.setScrollY(mWebView.getScrollY() + 300);
                if (mWebView.getScrollY() > mWebView.getContentHeight())
                    mWebView.setScrollY(mWebView.getScrollY() - 300);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            case KeyEvent.KEYCODE_SPACE:
                break;
            case KeyEvent.KEYCODE_BACK:
                break;
        }
        mWebView.clearFocus();
        SetIndexView();
        return super.onKeyUp(keyCode, event);
    }

    private class WebViewClientClass extends WebViewClient {//페이지 이동
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("check URL", url);
            view.loadUrl(url);
            return true;
        }
    }
}
