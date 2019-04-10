package com.supertester.PBV_MII.project_v2.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sac.speech.Speech;
import com.supertester.PBV_MII.project_v2.Class.Std_Method;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.supertester.PBV_MII.project_v2.Database.Contacts.ItemContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.ItemStatusContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.OrderContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.OrderStatusContact;
import com.supertester.PBV_MII.project_v2.Database.DBAdapter;
import com.supertester.PBV_MII.project_v2.Database.User;
import com.supertester.PBV_MII.project_v2.R;
import com.supertester.PBV_MII.project_v2.VoiceService.Constants;
import com.supertester.PBV_MII.project_v2.VoiceService.MyService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Date;

public class MainActivity extends Activity {
    User userInfo = new User();
    Std_Method app;
    int arrow_pos = 0;
    String user[];

    TextView NW_stat;
    TextView Date;
    TextView Manual;
    ImageView arrow1;
    ImageView arrow2;
    ImageView arrow3;
    ImageView arrow4;
    ImageView arrow5;
    ImageView voice_stat;
    LinearLayout linear_h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        voice_set();
        ready();
        UserInIt();

//        JsonParse("A293155");
    }

    private void voice_set() {
        enableAutoStart();
        app.voice_control();
        voice_stat = findViewById(R.id.voice_status);
        app.Set_voice_stat(voice_stat);
    }

    private void enableAutoStart() {
        for (Intent intent : Constants.AUTO_START_INTENTS) {
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                try {
                    for (Intent intent1 : Constants.AUTO_START_INTENTS)
                        if (getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY)
                                != null) {
                            startActivity(intent1);
                            break;
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void UI_control() {
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        app = (Std_Method) getApplicationContext();
        linear_h = findViewById(R.id.layout_h);
        app.share_load();
        app.set_view(linear_h);
        app.InitRememberID();
        Log.e("log_test_id", "" + app.getRememberID());
    }

    private void Init() {
        UI_control();
        NW_stat = findViewById(R.id.nw_stat);
        Date = findViewById(R.id.date);
        Manual = findViewById(R.id.manual);
        arrow1 = findViewById(R.id.arrow1);
        arrow2 = findViewById(R.id.arrow2);
        arrow3 = findViewById(R.id.arrow3);
        arrow4 = findViewById(R.id.arrow4);
        arrow5 = findViewById(R.id.arrow5);
        arrow2.setVisibility(View.INVISIBLE);
        arrow3.setVisibility(View.INVISIBLE);
        arrow4.setVisibility(View.INVISIBLE);
        arrow5.setVisibility(View.INVISIBLE);
        user = new String[5];
        for (int i = 0; i < 5; i++) user[i] = "";
    }

    private void ready() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean b = networkInfo != null;

        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        userInfo.setGETTIME(userInfo.simpleDateFormat.format(date));
        Date.setText(userInfo.getGETTIME());
        if (b) NW_stat.setText(R.string.Connected);
        else NW_stat.setText(R.string.Disconnected);
    }

    private void UserInIt() {
        userInfo.setUSER(user[0]);
        userInfo.setPLANT(user[1]);
        userInfo.setTAKT(user[2]);
        userInfo.setID(user[3]);
        userInfo.setPW(user[4]);
    }

    private String[] JsonParse(String json) { //json A293155
        String USER;
        String PLANT;
        String TAKT;
        String ID;
        String PW;
        String[] user_data = new String[5];
        try {
            JSONObject jsonObject = new JSONObject(getString(R.string.UserInfo));
            JSONArray jsonArray = jsonObject.getJSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                USER = jObject.getString("USER");
                PLANT = jObject.getString("PLANT");
                TAKT = jObject.getString("TAKT");
                ID = jObject.getString("ID");
                PW = jObject.getString("PW");

                user_data[0] = USER;
                user_data[1] = PLANT;
                user_data[2] = TAKT;
                user_data[3] = ID;
                user_data[4] = PW;

                userInfo.LOGIN_STATUS = jsonObject.has(json);

                userInfo.setUSER(user_data[0]);
                userInfo.setPLANT(user_data[1]);
                userInfo.setTAKT(user_data[2]);
                userInfo.setID(user_data[3]);
                userInfo.setPW(user_data[4]);
                userInfo.logUSERINFO();
            }
        } catch (Exception e) {
            user_data[0] = "";
            user_data[1] = "";
            user_data[2] = "";
            user_data[3] = "";
            user_data[4] = "";
            e.printStackTrace();
        }
        return user_data;
    }

    private void start_menu_function() {
        if (arrow_pos == 0) {
            if (userInfo.LOGIN_STATUS) {
                app.PrintToastMessage("Connected " + userInfo.getUSER());
//                Intent intent1 = new Intent(MainActivity.this, DateSetActivity.class);
                Intent intent1 = new Intent(MainActivity.this, SetMenuActivity.class);
                intent1.putExtra("userInfo", userInfo);
                startActivity(intent1);
            } else {
                IntentIntegrator qrScan = new IntentIntegrator(this);
                if (app.get_now_view() != 2) qrScan.setCaptureActivity(CustomScannerActivity.class);
                qrScan.initiateScan();
            }
        } else if (arrow_pos == 1) {
            if (!app.toggle_flag) {
                app.voice_control();
                app.timer();
            }
        } else if (arrow_pos == 2) {
            Intent intent = new Intent(this, SetViewActivity.class);
            Bundle b = new Bundle();
            intent.putExtra("bundle", b);
            startActivityForResult(intent, 1);
        } else if (arrow_pos == 3) {
            Intent intent = new Intent(this, ManualActivity.class);
            startActivity(intent);
        } else if (arrow_pos == 4) {
            Dialog();
        }
    }

    public void Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Data");
        builder.setMessage("Do you want to reset the data table?\nUncompleted work records may disappear.");
        builder.setNegativeButton("No",
                (dialog, which) -> {
                });
        builder.setPositiveButton("Reset",
                (dialog, which) -> {
                    InitAdapter();
                    userInfo.LOGIN_STATUS = false;
                    app.PrintToastMessage("Data initialization completed");
                });
        builder.show();
        UI_control();
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

    private void key_control() {
        if (arrow_pos == 0) {
            arrow1.setVisibility(View.VISIBLE);
            arrow2.setVisibility(View.INVISIBLE);
            arrow3.setVisibility(View.INVISIBLE);
            arrow4.setVisibility(View.INVISIBLE);
            arrow5.setVisibility(View.INVISIBLE);
        } else if (arrow_pos == 1) {
            arrow1.setVisibility(View.INVISIBLE);
            arrow2.setVisibility(View.VISIBLE);
            arrow3.setVisibility(View.INVISIBLE);
            arrow4.setVisibility(View.INVISIBLE);
            arrow5.setVisibility(View.INVISIBLE);
        } else if (arrow_pos == 2) {
            arrow1.setVisibility(View.INVISIBLE);
            arrow2.setVisibility(View.INVISIBLE);
            arrow3.setVisibility(View.VISIBLE);
            arrow4.setVisibility(View.INVISIBLE);
            arrow5.setVisibility(View.INVISIBLE);
        } else if (arrow_pos == 3) {
            arrow1.setVisibility(View.INVISIBLE);
            arrow2.setVisibility(View.INVISIBLE);
            arrow3.setVisibility(View.INVISIBLE);
            arrow4.setVisibility(View.VISIBLE);
            arrow5.setVisibility(View.INVISIBLE);
        } else if (arrow_pos == 4) {
            arrow1.setVisibility(View.INVISIBLE);
            arrow2.setVisibility(View.INVISIBLE);
            arrow3.setVisibility(View.INVISIBLE);
            arrow4.setVisibility(View.INVISIBLE);
            arrow5.setVisibility(View.VISIBLE);
        }
    }

    private void key_down() {
        if (arrow_pos > 0) {
            arrow_pos -= 1;
        }
        key_control();
    }

    private void key_up() {
        if (arrow_pos < 4) {
            arrow_pos += 1;
        }
        key_control();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("getkey", event.getKeyCode() + "");
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("getkey", event.getKeyCode() + "");
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT://bt300 제스쳐 시도
                key_down();
                break;
            case KeyEvent.KEYCODE_DPAD_UP://bt300 제스쳐 시도
                key_down();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                key_up();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                key_up();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                start_menu_function();
                break;
            case KeyEvent.KEYCODE_SPACE:
                start_menu_function();
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
            finish();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        switch (request) {
            case 1:
                if (result == RESULT_OK) {
                    String edt;
                    edt = data.getStringExtra("gettime");
                    Date.setText(edt);
                    userInfo.setGETTIME(edt);
                } else if (result == 7777) {
                    int now_view;
                    now_view = data.getIntExtra("now", 1217);
                    app.set_now(now_view);
                    app.share_preferences();
                    app.set_view(linear_h);
                }
                break;
            default:
                IntentResult results = IntentIntegrator.parseActivityResult(request, result, data);//데이터 결과 문자
                if (results != null) {
                    user = JsonParse(results.getContents());
                    UserInIt();
                    if (userInfo.LOGIN_STATUS) {
                        if(!app.getRememberID().equals(userInfo.getUSER())){
                            InitAdapter();
                            app.setREMEMBER_ID(userInfo.getUSER());
                        }
//                        Intent intent1 = new Intent(MainActivity.this, DateSetActivity.class);
                        Intent intent1 = new Intent(MainActivity.this, SetMenuActivity.class);
                        intent1.putExtra("userInfo", userInfo);
                        startActivity(intent1);
                    } else {
                        app.PrintToastMessage("Not Connected to MII");
                    }
                } else {
                    super.onActivityResult(request, result, data);
                }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        UI_control();
        app.Set_voice_stat(voice_stat);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (app.isServiceRunning()) {
            if (Speech.getInstance().isListening()) {
                Speech.getInstance().stopListening();
            }
            stopService(new Intent(this, MyService.class));
            app.voice_flag = false;
        }
    }
}

