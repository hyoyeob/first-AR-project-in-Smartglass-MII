package com.supertester.PBV_MII.project_v2.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supertester.PBV_MII.project_v2.Class.Std_Method;
import com.supertester.PBV_MII.project_v2.R;

public class SetViewActivity extends Activity {
    int now = 0;

    RelativeLayout rv;
    TextView tv;
    ImageView voice_stat;
    String leftUp = "왼쪽 시력이 좋으며 아래를 자주볼 때";
    String leftDown = "왼쪽 시력이 좋으며 위쪽을 자주볼 때";
    String rightUp = "오른쪽 시력이 좋으며 아래를 자주볼 때";
    String rightDown = "오른쪽 시력이 좋으며 위쪽을 자주볼 때";

    Std_Method app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_set);
        UI_control();
        rv = findViewById(R.id.layout);
        tv = findViewById(R.id.sample_text);
        rv.setBackgroundResource(R.drawable.left_up);
        tv.setText(leftUp);

        voice_stat = findViewById(R.id.voice_status);
        app.Set_voice_stat(voice_stat);
    }

    private void set_view_control(int index) {
        now += index;
        if (now < 0) {
            now -= index;
        } else if (now > 3) {
            now -= index;
        }
        if (now == 0) {
            rv.setBackgroundResource(R.drawable.left_up);
            tv.setText(leftUp);
        } else if (now == 1) {
            rv.setBackgroundResource(R.drawable.right_up);
            tv.setText(rightUp);
        } else if (now == 2) {
            rv.setBackgroundResource(R.drawable.left_down);
            tv.setText(leftDown);
        } else if (now == 3) {
            rv.setBackgroundResource(R.drawable.right_down);
            tv.setText(rightDown);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                set_view_control(-1);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                set_view_control(-2);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                set_view_control(2);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                set_view_control(1);
                break;
            case KeyEvent.KEYCODE_SPACE:
                Intent intent2 = new Intent(SetViewActivity.this, MainActivity.class);
                intent2.putExtra("now", now);
                setResult(7777, intent2);
                finish();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Intent intent1 = new Intent(SetViewActivity.this, MainActivity.class);
                intent1.putExtra("now", now);
                setResult(7777, intent1);
                finish();
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.e("log_test", "back!!");
                break;
        }
        app.key_voice_control(keyCode);
        new Handler().postDelayed(() -> app.Set_voice_stat(voice_stat), 500);
        return super.onKeyUp(keyCode, event);
    }

    public void UI_control() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        app = (Std_Method) getApplicationContext();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("log_flow", "restart");
        UI_control();
        app.Set_voice_stat(voice_stat);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("log_flow", "destroy");
    }
}
