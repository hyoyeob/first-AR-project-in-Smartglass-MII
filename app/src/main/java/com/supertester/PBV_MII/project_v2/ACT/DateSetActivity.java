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
import com.supertester.PBV_MII.project_v2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateSetActivity extends Activity {
    String gettime=null;
    String realtime=null;
    TextView DATE;
    Date date;
    Calendar cal;
    private String LINEo = null;
    private String ZONEo = null;
    private String PLANTo = null;
    private String DATEo = null;
    private String IDo = null;
    private String PWo = null;
    private String MACo = null;
    private String TAKTo = null;
    private String user = null;
    boolean b;
    boolean key_flag;
    Std_Method app;
    LinearLayout linear_h;
    ImageView voice_stat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ready();
    }

    public void UI_control(){
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        app=(Std_Method) getApplicationContext();
        linear_h = findViewById(R.id.layout_h);
        app.share_load();
        app.set_view(linear_h);
    }

    private void net_check(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        b = networkInfo != null && networkInfo.isConnectedOrConnecting();
        Log.e("log_net","network connect "+b);
    }

    private void ready(){
        UI_control();
        voice_stat = findViewById(R.id.voice_status);
        app.Set_voice_stat(voice_stat);
        gettime = getIntent().getStringExtra("puttime");
        realtime = getIntent().getStringExtra("puttime");
        LINEo = getIntent().getExtras().getString("LINEo");
        PLANTo = getIntent().getExtras().getString("PLANTo");
        ZONEo = getIntent().getExtras().getString("ZONEo");
        DATEo = getIntent().getExtras().getString("DATEo");
        IDo = getIntent().getExtras().getString("IDo");
        PWo = getIntent().getExtras().getString("PWo");
        MACo = getIntent().getExtras().getString("MACo");
        TAKTo = getIntent().getExtras().getString("TAKTo");
        user = getIntent().getExtras().getString("user");
        DATE = findViewById(R.id.gettime);
        DATE.setText(gettime);

        try{
            date=new SimpleDateFormat("MM-dd-yyyy").parse(gettime);
            cal = Calendar.getInstance();
            cal.setTime(date);
            gettime = new SimpleDateFormat("MM-dd-yyyy").format(cal.getTime());

            realtime = gettime; //오늘날짜 설정

            DATE.setText(gettime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void date_control(int i){
        try {
            date=new SimpleDateFormat("MM-dd-yyyy").parse(gettime);
            cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, i);
            gettime = new SimpleDateFormat("MM-dd-yyyy").format(cal.getTime());
            DATE.setText(gettime);
            Log.e("log_now time",gettime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event ) {
        Log.e("getkey",event.getKeyCode()+"");
        Log.e("key pressed", String.valueOf(event.getKeyCode()));
        if( keyCode == KeyEvent.KEYCODE_DPAD_LEFT||keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if(key_flag){
                try{
                    date_control(-1);
                    Thread.sleep(150);
                }catch(InterruptedException ignore){}
            }
            event.startTracking();
            return true;
        }else if( keyCode == KeyEvent.KEYCODE_DPAD_RIGHT||keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if(key_flag){
                try{
                    date_control(1);
                    Thread.sleep(150);
                }catch(InterruptedException ignore){}
            }
            event.startTracking();
            return true;
        }else if( keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) {
            event.startTracking();
            return true;
        }return super.onKeyDown( keyCode, event );
    }

    private void enter_key() {
        net_check();
        if(IDo.equals("XCEMII01")&&PWo.equals("init1234")&&user.equals("A293155")){
            try {
                Intent intent1 = new Intent(this, OrderActivity.class);
                intent1.putExtra("DATEo", gettime);
                intent1.putExtra("PLANTo", PLANTo);
                intent1.putExtra("LINEo", LINEo);
                intent1.putExtra("ZONEo", ZONEo);
                intent1.putExtra("DATEo", gettime);
                intent1.putExtra("TAKTo", TAKTo);
                intent1.putExtra("IDo", IDo);
                intent1.putExtra("PWo", PWo);
                intent1.putExtra("realtime", realtime);
                startActivity(intent1);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }else if(!b&&!DATEo.equals(realtime)){
            app.PrintToastMessage("Login failed.\nCheck your Network.");
        }else{
            app.PrintToastMessage("Login failed.\nCheck your ID.");
        }
    }


    @Override
    public boolean onKeyUp( int keyCode, KeyEvent event ) {
        Log.e("getkey",event.getKeyCode()+"");
        Log.e("key pressed", String.valueOf(event.getKeyCode()));
        switch(keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT://bt300 제스쳐 시도
                if(!key_flag){
                    date_control(-1);
                }else {
                    key_flag=false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP://bt300 제스쳐 시도
                if(!key_flag){
                    date_control(1);
                }else {
                    key_flag=false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(!key_flag){
                    date_control(-1);
                }else {
                    key_flag=false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(!key_flag){
                    date_control(1);
                }else {
                    key_flag=false;
                }
                break;
            case KeyEvent.KEYCODE_SPACE:
                enter_key();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                enter_key();
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.e("log_test", "back!!");
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
            return true;
        }else if( keyCode == KeyEvent.KEYCODE_DPAD_RIGHT||keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            key_flag=true;
        }else if( keyCode == KeyEvent.KEYCODE_DPAD_LEFT||keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            key_flag=true;
        }
        return super.onKeyLongPress( keyCode, event );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("log_flow","destroy");
        UI_control();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("log_flow","restart");
        UI_control();
        app.Set_voice_stat(voice_stat);
    }
}
