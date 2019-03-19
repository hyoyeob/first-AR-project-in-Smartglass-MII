package com.supertester.PBV_MII.project_v2.ACT;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import com.supertester.PBV_MII.project_v2.CLASS.ShutdownConfigAdminReceiver;
import com.supertester.PBV_MII.project_v2.CLASS.Std_Method;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.supertester.PBV_MII.project_v2.DB.Contacts.ItemContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.ItemStatusContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.OrderContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.OrderStatusContact;
import com.supertester.PBV_MII.project_v2.DB.DBAdapter;
import com.supertester.PBV_MII.project_v2.R;
import com.supertester.PBV_MII.project_v2.VOICE_CONTROL.Constants;
import com.supertester.PBV_MII.project_v2.VOICE_CONTROL.MyService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * qrcode, edt, b, now_view옮김
 * @SuppressLint("SetTextI18n")삭제
 *
 */
public class MainActivity extends Activity {
    Long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

    Std_Method app;
    int arrow_pos=0;

    String LINE="E21";
    String PLANT="1000";
    String ZONE="ECM1";
    String TAKT="AE04";
    String id="XCEMII01";
    String pw="init1234";
    String user=null;
    String gettime = sdf.format(date);

    TextView NW_stat;
    TextView Date;
//    TextView ID_tv;
    ImageView arrow1;
    ImageView arrow2;
    ImageView arrow3;
    ImageView arrow4;
    ImageView voice_stat;
    LinearLayout linear_h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ready();
        voice_set();
        Log.e("log_test_cpu", getCpuTemperature()+"");
    }

    private void voice_set(){
        enableAutoStart();
        app.voice_control();
        voice_stat = findViewById(R.id.voice_status);
        app.Set_voice_stat(voice_stat);

//        new Handler().postDelayed(()->{
//        },1500);
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

    private float getCpuTemperature() {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();

            float temperature = Float.parseFloat(line) / 1000.0f;
            Log.e("log_test_cpu", temperature+"  111111");
            return temperature;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private void Permission(){
        app.devicePolicyManager = (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(), ShutdownConfigAdminReceiver.class);
        if(!app.devicePolicyManager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, 0);
        }
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void UI_control(){
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        app=(Std_Method) getApplicationContext();
        linear_h = findViewById(R.id.layout_h);
        app.share_load();
        app.set_view(linear_h);
    }

    private void ready(){
        UI_control();
//        Permission();
        NW_stat = findViewById(R.id.nw_stat);
        Date = findViewById(R.id.date);
//        ID_tv = findViewById(R.id.id);
        arrow1 = findViewById(R.id.arrow1);
        arrow2 = findViewById(R.id.arrow2);
        arrow3 = findViewById(R.id.arrow3);
        arrow4 = findViewById(R.id.arrow4);
        arrow2.setVisibility(View.INVISIBLE);
        arrow3.setVisibility(View.INVISIBLE);
        arrow4.setVisibility(View.INVISIBLE);
        Date.setText(gettime);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Boolean b = networkInfo != null && networkInfo.isConnectedOrConnecting();
//        ID_tv.setText(user);
        if(b)NW_stat.setText("Connected");
        else NW_stat.setText("Not connected");
        Date.setText(gettime);
    }

    private void start_menu_function(){
        if(arrow_pos==0){
            if (user == null || !user.equals("A293155")) {
                IntentIntegrator qrScan = new IntentIntegrator(this);
                qrScan.setOrientationLocked(false);
                if(app.get_now_view()!=2) qrScan.setCaptureActivity(CustomScannerActivity.class);
                qrScan.initiateScan();
            }else if(user.equals("A293155")){
                app.PrintToastMessage("Connected "+id);
                Intent intent1 = new Intent(MainActivity.this, DateSetActivity.class);
                intent1.putExtra("puttime", gettime);
                intent1.putExtra("PLANTo", PLANT);
                intent1.putExtra("LINEo", LINE);
                intent1.putExtra("ZONEo", ZONE);
                intent1.putExtra("DATEo", gettime);
                intent1.putExtra("TAKTo", TAKT);
                intent1.putExtra("IDo", id);
                intent1.putExtra("PWo", pw);
                intent1.putExtra("user", user);
                Log.e("log login test",id+" / "+pw);
                startActivity(intent1);
            }
        }else if(arrow_pos==1) {
            if(!app.toggle_flag){
                app.voice_control();
                app.timer();
            }
        }else if(arrow_pos==2) {
            Intent intent = new Intent(this, SetViewActivity.class);
            Bundle b = new Bundle();
            intent.putExtra("bundle", b);
            startActivityForResult(intent, 1);
        }else if(arrow_pos==3) {
            Dialog();
        }
    }

    public void Dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Data");
        builder.setMessage("Do you want to reset the data table?\nUncompleted work records may disappear.");
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setPositiveButton("Reset",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        InitAdapter();
                        app.PrintToastMessage("Data initialization completed");
                    }
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

        order_status_dbAdapter = new DBAdapter(getApplicationContext(), orderStatusContact);
        order_dbAdapter = new DBAdapter(getApplicationContext(), orderContact);
        item_status_dbAdapter = new DBAdapter(getApplicationContext(), itemStatusContact);
        item_dbAdapter = new DBAdapter(getApplicationContext(), itemContact);

        order_status_dbAdapter.DropTable();
        order_status_dbAdapter.CreateTable();

        item_status_dbAdapter.DropTable();
        item_status_dbAdapter.CreateTable();

        item_dbAdapter.DropTable();
        item_dbAdapter.CreateTable();

        order_dbAdapter.DropTable();
        order_dbAdapter.CreateTable();
    }

    private void key_control(){
        if(arrow_pos==0){
            arrow1.setVisibility(View.VISIBLE);
            arrow2.setVisibility(View.INVISIBLE);
            arrow3.setVisibility(View.INVISIBLE);
            arrow4.setVisibility(View.INVISIBLE);
        }else if(arrow_pos==1){
            arrow1.setVisibility(View.INVISIBLE);
            arrow2.setVisibility(View.VISIBLE);
            arrow3.setVisibility(View.INVISIBLE);
            arrow4.setVisibility(View.INVISIBLE);
        }else if(arrow_pos==2){
            arrow1.setVisibility(View.INVISIBLE);
            arrow2.setVisibility(View.INVISIBLE);
            arrow3.setVisibility(View.VISIBLE);
            arrow4.setVisibility(View.INVISIBLE);
        }else if(arrow_pos==3){
            arrow1.setVisibility(View.INVISIBLE);
            arrow2.setVisibility(View.INVISIBLE);
            arrow3.setVisibility(View.INVISIBLE);
            arrow4.setVisibility(View.VISIBLE);
        }

    }

    private void key_down(){
        if(arrow_pos>0){
            arrow_pos-=1;
        }
        key_control();
    }

    private void key_up(){
        if(arrow_pos<3){
            arrow_pos+=1;
        }
        key_control();
    }


    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event ) {
        Log.e("getkey",event.getKeyCode()+"");
        if( keyCode == KeyEvent.KEYCODE_DPAD_LEFT||keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            event.startTracking();
            return true;
        }else if( keyCode == KeyEvent.KEYCODE_DPAD_RIGHT||keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            event.startTracking();
            return true;
        }else if( keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) {
            event.startTracking();
            return true;
        }else if( keyCode == KeyEvent.KEYCODE_VOLUME_UP ||keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {
            event.startTracking();
            return true;
        }return super.onKeyDown( keyCode, event );
    }



    @Override
    public boolean onKeyUp( int keyCode, KeyEvent event ) {
        Log.e("getkey",event.getKeyCode()+"");
        Log.e("key pressed", String.valueOf(event.getKeyCode()));
        switch(keyCode) {
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
        new Handler().postDelayed(()->{
            app.Set_voice_stat(voice_stat);
        },500);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress( int keyCode, KeyEvent event ) {
        if( keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) {
            finish();
            return true;
        }return super.onKeyLongPress( keyCode, event );
    }


    @Override
    protected void onActivityResult(int request, int result, Intent data){
        Log.e("log_act", request+"   "+result);
        switch (request) {
            case 1:
                if (result == RESULT_OK) {
                    String edt;
                    Log.e("log_test", result + " " + RESULT_OK);
                    edt = data.getStringExtra("gettime");
                    Date.setText(edt);
                    gettime = edt;
                    Log.e("GETTIME", gettime);
                }else if (result == 7777) {
                    int now_view;
                    now_view = data.getIntExtra("now",1217);
                    app.set_now(now_view);
                    app.share_preferences();
                    app.set_view(linear_h);
            }break;
            default:
                IntentResult results = IntentIntegrator.parseActivityResult(request, result, data);//데이터 결과 문자
                if (results != null) {
                    user = results.getContents();
                    if(user==null|| !user.equals("A293155")){
                        app.PrintToastMessage("Not Connected to MII");
                    }else {
                        Log.e("login","not elsesese");
                        Intent intent1 = new Intent(MainActivity.this, DateSetActivity.class);
//                            ID_tv.setText(user);
                        intent1.putExtra("puttime", gettime);
                        intent1.putExtra("PLANTo", PLANT);
                        intent1.putExtra("LINEo", LINE);
                        intent1.putExtra("ZONEo", ZONE);
                        intent1.putExtra("DATEo", gettime);
                        intent1.putExtra("TAKTo", TAKT);
                        intent1.putExtra("IDo", id);
                        intent1.putExtra("PWo", pw);
                        intent1.putExtra("user", user);
                        startActivity(intent1);
                    }
                }else{
                    super.onActivityResult(request, result, data);
                }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("log_flow","restart");
        UI_control();
        app.Set_voice_stat(voice_stat);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(app.isServiceRunning()){
            if (Speech.getInstance().isListening()){
                Speech.getInstance().stopListening();
                Log.e("log_voc_set_flow","stop listening!!");
            }
            stopService(new Intent(this, MyService.class));
            app.voice_flag=false;
            Log.e("log_voc_set_flow","destroy");
        }
    }
}

