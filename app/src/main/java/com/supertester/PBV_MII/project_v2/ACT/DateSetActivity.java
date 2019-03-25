package com.supertester.PBV_MII.project_v2.ACT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.supertester.PBV_MII.project_v2.CLASS.Std_Method;
import com.supertester.PBV_MII.project_v2.DB.User;
import com.supertester.PBV_MII.project_v2.R;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateSetActivity extends Activity {
    TextView DATE;
    boolean b;
    boolean key_flag;
    Std_Method app;
    ImageView voice_stat;
    User userInfo = new User();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ready();
    }

    public void UI_control() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        app = (Std_Method) getApplicationContext();
        LinearLayout linear_h = findViewById(R.id.layout_h);
        app.share_load();
        app.set_view(linear_h);
    }

    private void net_check() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        b = networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void ready() {
        UI_control();
        voice_stat = findViewById(R.id.voice_status);
        DATE = findViewById(R.id.gettime);
        app.Set_voice_stat(voice_stat);
        userInfo = (User) getIntent().getSerializableExtra("userInfo");
        userInfo.setREALTIME(userInfo.getGETTIME());

        date_control(0);
        Log.e("log_user", userInfo.getUSER() + " ");
    }

    private void date_control(int i) {
        try {
            Date date = userInfo.simpleDateFormat.parse(userInfo.getGETTIME());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, i);
            userInfo.setGETTIME(userInfo.simpleDateFormat.format(cal.getTime()));
            DATE.setText(userInfo.getGETTIME());
            Log.e("log_now time", userInfo.getGETTIME());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void enter_key() {
        net_check();
        if (userInfo.getUSER().equals("A293155") || userInfo.getUSER().equals("A246087")) {
            try {
                Intent intent1 = new Intent(this, OrderActivity.class);
                intent1.putExtra("userInfo", userInfo);
                startActivity(intent1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!b && !userInfo.getGETTIME().equals(userInfo.getREALTIME())) {
            app.PrintToastMessage("Login failed.\nCheck your Network.");
        } else {
            app.PrintToastMessage("Login failed.\nCheck your ID.");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (key_flag) {
                try {
                    date_control(-1);
                    Thread.sleep(150);
                } catch (InterruptedException ignore) {
                }
            }
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (key_flag) {
                try {
                    date_control(1);
                    Thread.sleep(150);
                } catch (InterruptedException ignore) {
                }
            }
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
        Log.e("getkey", String.valueOf(event.getKeyCode()));
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT://bt300 제스쳐 시도
                if (!key_flag) {
                    date_control(-1);
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP://bt300 제스쳐 시도
                if (!key_flag) {
                    date_control(1);
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!key_flag) {
                    date_control(-1);
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!key_flag) {
                    date_control(1);
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_SPACE:
                enter_key();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                enter_key();
                break;
            case KeyEvent.KEYCODE_BACK:
                break;
        }
        app.key_voice_control(keyCode);
        new Handler().postDelayed(() -> app.Set_voice_stat(voice_stat), 500);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            key_flag = true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            key_flag = true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UI_control();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        UI_control();
        app.Set_voice_stat(voice_stat);
    }
}
