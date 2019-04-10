package com.supertester.PBV_MII.project_v2.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.supertester.PBV_MII.project_v2.Class.Std_Method;
import com.supertester.PBV_MII.project_v2.Database.Contacts.ItemContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.ItemStatusContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.OrderContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.OrderStatusContact;
import com.supertester.PBV_MII.project_v2.Database.DBAdapter;
import com.supertester.PBV_MII.project_v2.Database.User;
import com.supertester.PBV_MII.project_v2.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class SetMenuActivity extends Activity {
    int row = 0;
    int z_col = 0;
    int l_col = 0;
    boolean b;
    boolean key_flag;

    TextView DATE;
    TextView tvZONE;
    TextView tvLINE;
    ImageView voice_stat;
    String[] ZONE;
    String[][] LINE;

    User userInfo = new User();

    Std_Method app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_menu);
        JsonParse();
        ready();
        UserInIt();
    }

    private void JsonParse() {
        try {
            JSONObject jsonObjectZ = new JSONObject(getString(R.string.ColumnData));
            JSONObject jsonObjectL = new JSONObject(getString(R.string.LINE));

            JSONArray jsonArrayZ = jsonObjectZ.getJSONArray("ZONE");

            ZONE = new String[jsonArrayZ.length()];
            LINE = new String[jsonArrayZ.length()][];

            for (int i = 0; i < jsonArrayZ.length(); i++) {
                int j = 0;
                ZONE[i] = (String) jsonArrayZ.get(i);
                JSONArray jsonArrayL = jsonObjectL.getJSONArray(ZONE[i]);
                LINE[i] = new String[jsonArrayL.length()];

                while (j < jsonArrayL.length()) {
                    LINE[i][j] = (String) jsonArrayL.get(j);
                    j++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ready() {
        UI_control();
        voice_stat = findViewById(R.id.voice_status);
        DATE = findViewById(R.id.gettime);
        tvZONE = findViewById(R.id.list_zone);
        tvLINE = findViewById(R.id.list_line);
        app.Set_voice_stat(voice_stat);
        userInfo = (User) getIntent().getSerializableExtra("userInfo");
        userInfo.setREALTIME(userInfo.getGETTIME());

        tvZONE.setText(ZONE[0]);
        tvLINE.setText(LINE[0][0]);
        Color_set();
        date_control(0);
    }

    private void date_control(int i) {
        try {
            Date date = userInfo.simpleDateFormat.parse(userInfo.getGETTIME());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, i);
            userInfo.setGETTIME(userInfo.simpleDateFormat.format(cal.getTime()));
            DATE.setText(userInfo.getGETTIME());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void InitAdapter() {
        DBAdapter order_status_dbAdapter;
        DBAdapter order_dbAdapter;
        DBAdapter item_status_dbAdapter;
        DBAdapter item_dbAdapter;
        OrderStatusContact orderStatusContact = new OrderStatusContact();
        OrderContact orderContact = new OrderContact();
        ItemStatusContact itemStatusContact = new ItemStatusContact();
        ItemContact itemContact = new ItemContact();

        order_status_dbAdapter = new DBAdapter<>(getApplicationContext(), orderStatusContact);
        order_dbAdapter = new DBAdapter<>(getApplicationContext(), orderContact);
        item_status_dbAdapter = new DBAdapter<>(getApplicationContext(), itemStatusContact);
        item_dbAdapter = new DBAdapter<>(getApplicationContext(), itemContact);

        order_status_dbAdapter.DropTable();
        item_status_dbAdapter.DropTable();
        item_dbAdapter.DropTable();
        order_dbAdapter.DropTable();

        order_status_dbAdapter.CreateTable();
        item_status_dbAdapter.CreateTable();
        item_dbAdapter.CreateTable();
        order_dbAdapter.CreateTable();
    }

    private void enter_key() {
        net_check();
        if (userInfo.LOGIN_STATUS) {
            if (!app.getRememberOption().equals(userInfo.getLINE())) {
                InitAdapter();
                app.setREMEMBEROption(userInfo.getLINE());
            }
            if (userInfo.getLINE().equals("E50") && !userInfo.getGETTIME().equals(userInfo.getREALTIME()))
                app.PrintToastMessage("E50 line only displays today's data.");
            else {
                try {
                    Intent intent1 = new Intent(this, OrderActivity.class);
                    intent1.putExtra("userInfo", userInfo);
                    startActivity(intent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (!b && !userInfo.getGETTIME().equals(userInfo.getREALTIME())) {
            app.PrintToastMessage("Login failed.\nCheck your Network.");
        } else {
            app.PrintToastMessage("Login failed.\nCheck your ID.");
        }
    }
    //

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

    private void Shift_Row(int i) { //행 이동 후 칼럼 이동 시 이전 작업 기억할 필요있음(첨부터 다시 칼럼탐색하던지)
        row += i;
        if (row < 0 || row > 2) row -= i;
        Color_set();
    }

    private void Color_set() {
        if (row == 0) {
            tvZONE.setBackgroundColor(Color.parseColor("#F9FF93"));
            tvLINE.setBackgroundColor(Color.parseColor("#F4FFC4"));
            DATE.setBackgroundColor(Color.parseColor("#F4FFC4"));
        } else if (row == 1) {
            tvZONE.setBackgroundColor(Color.parseColor("#F4FFC4"));
            tvLINE.setBackgroundColor(Color.parseColor("#F9FF93"));
            DATE.setBackgroundColor(Color.parseColor("#F4FFC4"));
        } else {
            tvZONE.setBackgroundColor(Color.parseColor("#F4FFC4"));
            tvLINE.setBackgroundColor(Color.parseColor("#F4FFC4"));
            DATE.setBackgroundColor(Color.parseColor("#F9FF93"));
        }
    }

    private void UserInIt() {
        userInfo.setLINE(LINE[z_col][l_col]);
        userInfo.setZONE(ZONE[z_col]);
        userInfo.logUSERINFO();
    }

    private void Shift_Column(int i) {
        switch (row) {
            case 0: //zone
                z_col += i;
                if (z_col < 0 || z_col > ZONE.length - 1) z_col -= i;
                else {
                    l_col = 0;
                    tvZONE.setText(ZONE[z_col]);
                    tvLINE.setText(LINE[z_col][0]);
                }
                break;
            case 1: //line
                l_col += i;
                if (l_col < 0 || l_col > LINE[z_col].length - 1) l_col -= i;
                else tvLINE.setText(LINE[z_col][l_col]);
                break;
            case 2: //date
                date_control(i);
                break;
        }
        UserInIt();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("getkey", String.valueOf(event.getKeyCode()));
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT://bt300 제스쳐 시도
                if (!key_flag) {
                    Shift_Column(-1);
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP://bt300 제스쳐 시도
                if (!key_flag) {
                    Shift_Row(-1);
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!key_flag) {
                    Shift_Row(1);
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!key_flag) {
                    Shift_Column(1);
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

    public void UI_control() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        app = (Std_Method) getApplicationContext();
        LinearLayout linear_h = findViewById(R.id.layout_h);
        app.share_load();
        app.set_view(linear_h);
        app.InitRememberOption();
        Log.e("log_test_option", app.getRememberOption() + "/" + userInfo.getLINE());
    }

    private void net_check() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        b = networkInfo != null && networkInfo.isConnectedOrConnecting();
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
