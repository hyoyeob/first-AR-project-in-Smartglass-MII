package com.supertester.PBV_MII.project_v2.Class;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.supertester.PBV_MII.project_v2.Database.Contacts.ItemContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.ItemStatusContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.OrderStatusContact;
import com.supertester.PBV_MII.project_v2.Database.DBAdapter;
import com.supertester.PBV_MII.project_v2.R;
import com.supertester.PBV_MII.project_v2.VoiceService.MyService;
import org.ksoap2.serialization.SoapObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class Std_Method extends Application {
    private int now_view;
    private String REMEMBER_ID;
    public String ITEM = "http://r3mpwdisp.got.volvo.net:8145/XMII/SOAPRunner/CEMII/04_MaterialSupply/Picking/Transaction/getPickingItemListTrx";
    public String NAMESPACE = "http://www.sap.com/xMII";
    public String SOAP_METHOD = "XacuteRequest";
    public String SOAP_ACTION = "http://www.sap.com/xMII/XacuteRequest";
    public boolean voice_now=false;
    public boolean voice_flag=false;
    public boolean user_flag=false;
    public boolean toggle_flag=false;
    public int voice_count=0;

    HashMap<Integer, Object> map = new HashMap<>();
    ArrayList<String> arr_total;
    ArrayList<String> arr_next;
    ArrayList<String> arr_pre;
    ArrayList<String> arr_enter;
    ArrayList<String> arr_cancel;

    @Override
    public void onCreate() {
        super.onCreate();
        Ready_dictionary();
    }


    public void Set_voice_stat(ImageView voice_stat){
        Log.e("log_voice_flag", voice_flag+"");
        if(voice_flag&&isServiceRunning()) voice_stat.setImageResource(R.drawable.ic_volume_up_black_24dp);
        else voice_stat.setImageResource(R.drawable.ic_volume_off_black_24dp);

    }

    public String load_time(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    public void voice_control(){
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        if (isServiceRunning()) {
            stopService(intent);
            voice_flag=false;
            PrintToastMessage("Voice OFF");
        }else{
            startService(intent);
            PrintToastMessage("Voice ON");
            voice_flag=true;
        }
    }

    public void Key_down(){
        new Thread(
                () -> {
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
                    new Instrumentation().sendKeySync(event);
                    KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT);
                    new Instrumentation().sendKeySync(event2);
                }).start();
    }

    public void Key_up(){
        new Thread(
                () -> {
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
                    new Instrumentation().sendKeySync(event);
                    KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT);
                    new Instrumentation().sendKeySync(event2);
                }).start();
    }

    public void timer(){
        toggle_flag=true;
        new Handler().postDelayed(() -> toggle_flag = false, 1500);
    }


    public void Voice_timer(){
        voice_now=true;
        new Handler().postDelayed(() -> voice_now = false, 3000);
    }


    public void key_voice_control( int keyCode) {
        if(!toggle_flag){
            switch(keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP :
                    if (!voice_flag&&!isServiceRunning()){
                        voice_control();
                    }else if(voice_flag){
                        user_flag=true;
                        voice_control();
                    }
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN :
                    if (!isServiceRunning()) {
                        voice_control();
                    } else {
                        user_flag=true;
                        voice_control();
                    }
                    break;
            }
            timer();
        }
    }

    public boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (MyService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }


    public void Key_enter(){
        new Thread(
                () -> {
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);
                    new Instrumentation().sendKeySync(event);
                    KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER);
                    new Instrumentation().sendKeySync(event2);
                }).start();

    }

    public void Key_cancel(){
        new Thread(
                () -> {
                    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
                    new Instrumentation().sendKeySync(event);
                    KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK);
                    new Instrumentation().sendKeySync(event2);
                }).start();
    }

    public void PrintToastMessage(String msg){
        Toast toast=Toast.makeText(this,"", Toast.LENGTH_SHORT);
        TextView textView=new TextView(this);

        toast.setGravity(Gravity.CENTER, 0, 0);
        textView.setText(msg);
        textView.setBackgroundColor(Color.parseColor("#EE6969"));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setPadding(12,4,12,4);
        toast.setView(textView);

        toast.show();
    }

    public void Key_control(String key){
        switch (getKey(map, key)){
            case 0:
                PrintToastMessage("잘못된 단어");
                break;
            case 1:
                if(voice_now){
                    PrintToastMessage("너무 빠름");
                    break;
                }
                else{
                    Key_enter();
                    Voice_timer();
                    break;
                }
            case 2:
                Key_up();
                break;
            case 3:
                Key_down();
                break;
            case 4:
                Key_cancel();
                break;
        }
    }

    /**
     * 저장한 현재 view 위치를 불러옴
     */
    public void share_load(){
        SharedPreferences prefs = getSharedPreferences("test",MODE_PRIVATE);
        int result = prefs.getInt("NOW",1217);
        Log.e("log_share",result+"");
        now_view=result;
    }


    /**
     * 현재 뷰 위치 영구 저장
     */
    public void share_preferences(){
        SharedPreferences pref = getSharedPreferences("test",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("NOW",now_view);
        editor.apply();
    }

    public String getRememberID(){
        return REMEMBER_ID;
    }

    public void setREMEMBER_ID(String REMEMBER_ID) {
        this.REMEMBER_ID = REMEMBER_ID;
    }

    /**
     * 저장한 현재 아이디 값 불러옴
     */
    public void InitRememberID(){
        SharedPreferences prefs_id = getSharedPreferences("id",MODE_PRIVATE);
        String result = prefs_id.getString("ID","");
        Log.e("log_GetRememberID",result+"");
        REMEMBER_ID=result;
    }


    /**
     * 현재 아이디 기록 위치 영구 저장
     */
    public void ID_Preference(){
        SharedPreferences pref_id = getSharedPreferences("id",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref_id.edit();
        editor.putString("ID",REMEMBER_ID);
        editor.apply();
    }


    /**
     * 현재 뷰위치 읽을 수 있도록 변수에 값 저장
     * @param now: 현재 뷰 위치
     */
    public void set_now(int now){
        now_view=now;
        Log.e("log_method","now_view: "+now_view);
    }

    /**
     * 현재 뷰위치에따른 sw뷰 설정
     * @param linear_h : 세로 설정
     */
    public void set_view(LinearLayout linear_h){
        if(now_view==0) linear_h.setGravity(Gravity.TOP|Gravity.START);
        else if(now_view==1) linear_h.setGravity(Gravity.TOP|Gravity.END);
        else if(now_view==2) linear_h.setGravity(Gravity.BOTTOM|Gravity.START);
        else if(now_view==3) linear_h.setGravity(Gravity.BOTTOM|Gravity.END);

    }


    public int get_now_view(){
        return now_view;
    }

    /**
     * OrderStatusTable 의 모든 값 출력.
     */
    public void OrderStatusPrint (DBAdapter order_status_dbAdapter){
        ArrayList<OrderStatusContact> asd = order_status_dbAdapter.getAllContacts(new OrderStatusContact());
        for(int i=0; i<asd.size(); i++)
        {
            for(String str : asd.get(i).getProperties()){
                Log.e("ItemContact" + i + " 번째", str);
            }
        }
    }


    public String getItemAUFNR(SoapObject countryDetails){
        String result ="";
        Object property = countryDetails.getProperty(0);
        if (property instanceof SoapObject) {
            SoapObject countryObj = (SoapObject) property;
            result = countryObj.getProperty("AUFNR").toString();
        }
        return result;
    }

    private void ItemPrint(ItemContact itemContact, DBAdapter item_dbAdapter){
        Log.e("ItemPrint ", "START");
        Log.e("itemContact", itemContact.getProperties() + "");
        ArrayList<ItemContact> asd = item_dbAdapter.getAllContacts(new ItemContact());

        for(int i=0; i<asd.size(); i++){
            for(String str : asd.get(i).getProperties()){
                Log.e("ItemContact "+i + " 번째", str);
            }
        }
    }


    /**
     *         DB - ItemContact - 모든 값Insert
     *         많은 양의 데이터가 들어오고 속도 향상과 중간에 빠지는 값이 없도록 하기 위해서
     *         트랜잭션을 한번만 일어나도록 한다.
     *         db.beginTransaction(); 시작이고
     *         마무리는 아래의 두가지 중 한가지로 처리되게 만든다.
     *         db.setTransactionSuccessfull();
     *         db.endTransaction();
     *         try~ finally에서는 db를 이용하지 못한다.
     *         왜냐하면 트랜잭션을 진행중이기 때문에 다른 트랜잭션이 끼어들지 못하기 때문임.
     *         여튼 코드 진행 이후에 사용할것
     */
    public void ItemSetData(SoapObject countryDetails, DBAdapter item_dbAdapter, String DATEo){
        SQLiteDatabase db = item_dbAdapter.mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for (int i = 0; i < countryDetails.getPropertyCount(); i++) {
                Object property = countryDetails.getProperty(i);
                if (property instanceof SoapObject) {
                    SoapObject countryObj = (SoapObject) property;
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(
                            countryObj.getProperty("AUFNR").toString(),
                            countryObj.getProperty("PICK_SEQ").toString(),
                            countryObj.getProperty("LINE").toString(),
                            countryObj.getProperty("MATNR").toString(),
                            countryObj.getProperty("TAKT").toString(),
                            countryObj.getProperty("MAKTX").toString(),
                            countryObj.getProperty("BOX_NO").toString(),
                            countryObj.getProperty("OPERATION").toString(),
                            countryObj.getProperty("LAMPOS").toString(),
                            countryObj.getProperty("QTY").toString(),
                            countryObj.getProperty("TOT_QTY").toString(),
                            countryObj.getProperty("BLOCK_GRP").toString(),
                            countryObj.getProperty("STATUS").toString(),
                            String.valueOf(i),
                            DATEo
                    ));
                    item_dbAdapter.OneTimeInsert(db, data);
                }
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }db.close();
    }


    /**
     * ItemContact 테이블에서 해당 AUFNR에 대한 데이터가 DB에 존재하는지 체크.
     * 있다면 DB에서 바로 값을 참조해옴.
     * 없다면 DB에 값 저장후 참조.
     */
    public void SoapToArraylist_item(ArrayList Result, int load_index, DBAdapter item_dbAdapter, String DATEo, ItemStatusContact itemStatusContact, DBAdapter item_status_dbAdapter) {
        try{
            SoapObject countryDetails = (SoapObject) Result.get(load_index);
            ItemSetData(countryDetails, item_dbAdapter, DATEo);
            ItemStatusInput(getItemAUFNR(countryDetails), itemStatusContact, item_status_dbAdapter);
        }catch(Exception e){
            Log.e("log_std", "Item insert error");
            e.printStackTrace();
        }
    }

    public void ItemStatusInput(String aufnr, ItemStatusContact itemStatusContact, DBAdapter item_status_dbAdapter){
        ArrayList<String> input_value = new ArrayList<>(Arrays.asList(aufnr, "0", "0", "0"));
        itemStatusContact.setProperties(input_value);
        item_status_dbAdapter.addContact();
    }

    private void Ready_dictionary(){
        Resources res = getResources();
        String[] word_next=res.getStringArray(R.array.word_next);
        String[] word_pre=res.getStringArray(R.array.word_pre);
        String[] word_enter=res.getStringArray(R.array.word_enter);
        String[] word_cancel=res.getStringArray(R.array.word_cancel);

        arr_next = new ArrayList<>();
        arr_pre = new ArrayList<>();
        arr_enter = new ArrayList<>();
        arr_cancel = new ArrayList<>();
        arr_total = new ArrayList<>();

        Collections.addAll(arr_next, word_next);
        Collections.addAll(arr_pre, word_pre);
        Collections.addAll(arr_enter, word_enter);
        Collections.addAll(arr_cancel, word_cancel);

        map.put(1,arr_enter);
        map.put(2,arr_next);
        map.put(3,arr_pre);
        map.put(4,arr_cancel);
    }

    public static int getKey(HashMap<Integer, Object> m, Object value) {
        Set set= m.keySet();
        for (Object aSet : set) {
            Integer key = (Integer) aSet;
            if (((ArrayList) Objects.requireNonNull(m.get(key))).contains(value)) return key;
        }
        return 0;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}


