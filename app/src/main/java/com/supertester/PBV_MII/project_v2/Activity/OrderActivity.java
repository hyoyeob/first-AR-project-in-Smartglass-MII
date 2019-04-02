package com.supertester.PBV_MII.project_v2.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supertester.PBV_MII.project_v2.Async.CallRemote_order;
import com.supertester.PBV_MII.project_v2.Class.Std_Method;
import com.supertester.PBV_MII.project_v2.Database.Contacts.ItemContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.ItemStatusContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.OrderContact;
import com.supertester.PBV_MII.project_v2.Database.Contacts.OrderStatusContact;
import com.supertester.PBV_MII.project_v2.Database.DBAdapter;
import com.supertester.PBV_MII.project_v2.Database.Item;
import com.supertester.PBV_MII.project_v2.Database.User;
import com.supertester.PBV_MII.project_v2.R;
import com.supertester.PBV_MII.project_v2.VoiceService.MyService;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class OrderActivity extends Activity implements Serializable {
    boolean enter_flag = true;
    boolean b;
    boolean getorder;
    boolean key_flag;
    boolean load_flag = false;
    BackgroundThread backgroundThread;
    int order_count = 0;
    int order_index = 0;
    int Start = 0;

    SoapObject s = null;
    TextView index;
    TextView Percent;
    TextView[] matnr;
    TextView[] seq;
    TextView[] sernr;
    TextView[] back;
    TextView[] chk;
    ArrayList Result;
    ArrayList Result_item;

    User userInfo = new User();
    Item item = new Item();

    ArrayList<String> test_status;

    ArrayList<OrderContact> order_contact_data;
    OrderStatusContact orderStatusContact = new OrderStatusContact();
    OrderContact orderContact = new OrderContact();
    ItemStatusContact itemStatusContact = new ItemStatusContact();
    ItemContact itemContact = new ItemContact();
    MyHandler myHandler = new MyHandler(this);

    DBAdapter<OrderStatusContact> order_status_dbAdapter;
    DBAdapter<OrderContact> order_dbAdapter;
    DBAdapter<ItemStatusContact> item_status_dbAdapter;
    DBAdapter<ItemContact> item_dbAdapter;
    Std_Method app;
    ImageView voice_stat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initDB();
        init();
        getOrder();
        batteryLevel();
        error_test();
        check_load_time();

        backgroundThread = new BackgroundThread();
        backgroundThread.setRunning(true);
        backgroundThread.start();
    }


    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    public void batteryLevel() {
        if (isServiceRunning()) {
            BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);
                    int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int level = -1;
                    if (rawlevel >= 0 && scale > 0) {
                        level = (rawlevel * 100) / scale;
                    }
                    battery_init(level);
                }
            };
            IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        }
    }

    public void battery_init(int level) {
        int hour;
        double minute;
        int hour2;
        double minute2;
        hour = level / 20;
        hour2 = level / 15;
        minute = (level * 2.8) - (hour * 60);
        minute2 = (level * 3.78) - (hour2 * 60);
        int m1 = (int) minute;
        int m2 = (int) minute2;
        int sub = (int) ((level * 3.78) - (level * 2.8));
        if (level < 25) {
            Dialog(level, hour, m1, hour2, m2, sub);
        }
    }

    public void Dialog(int level, int hour, int minute, int hour2, int minute2, int sub) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Battery Warning!");
        builder.setMessage("The battery is running low.(" + level + "%)\nService shutdown can increase usage time.\nKeep On: " + hour + "h " + minute + "m Available" + "\nTo end: " + hour2 + "h " + minute2 + "m Available(" + sub + "m more available)\nQuit voice service?");
        builder.setNegativeButton("No",
                (dialog, which) -> app.PrintToastMessage("Battery discharge \nafter " + hour + "h " + minute + "m"));
        builder.setPositiveButton("Yes",
                (dialog, which) -> {
                    stopService(new Intent(this, MyService.class));
                    app.PrintToastMessage("Terminate Voice Services\nAvailable for " + hour2 + "h " + minute2 + "m");
                });
        builder.show();
    }


    private void percent() {//특정 위치 색변환 시키는 함수, 퍼센트 생성
        String percent = "";
        String result_percent;
        int i;
        for (i = 0; i < item.getAUFNR().size(); i++) {      // 퍼센트 초기화
            result_percent = percent.concat("l");
            percent = result_percent;
        }
        Log.e("log_order_percent", order_index + "/ " + item.getORDER_STATUS().size() + "");
        SpannableString spercent = new SpannableString(percent);
        for (i = 0; i < item.getAUFNR().size(); i++) {     // 퍼센트 초기화
            if (i < order_index && !item.getORDER_STATUS().get(i).equals("Y")) {
                spercent.setSpan(new ForegroundColorSpan(Color.RED), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (i == order_index) {
                spercent.setSpan(new ForegroundColorSpan(Color.BLACK), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (item.getORDER_STATUS().get(i).equals("Y")) {
                spercent.setSpan(new ForegroundColorSpan(Color.GREEN), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        Percent.setText(spercent);
    }

    private void error_test() {
        if (!getorder) {
            enter_flag = true;
            if (userInfo.getGETTIME().equals(userInfo.getREALTIME())) {
                Delete_table_not(userInfo.getREALTIME());
            }
            err_dialog();
        } else {
            enter_flag = false;
            setUI();
            percent();
        }
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
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String wifi_name = "FactoryWireless";     //wifi바뀌면 이거 바꾸면댐
        String insert_wifi = "\"" + wifi_name + "\"";
        if (networkInfo.isConnected()) {
            WifiManager wifim = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifim.getConnectionInfo();
            String ssid = info.getSSID();
            Log.e("log_wifi", ssid + " " + insert_wifi);
            b = ssid.equals(insert_wifi);
        } else {
            b = false;
        }
        Log.e("log_net", "network connect " + b);
    }

    private void Delete_table_not(String del_DATE) {//DATE 가져 오는것까지 해야함,
        Log.e("log_date", "살리는 날짜: " + del_DATE + " 삭제하는 날짜: " + userInfo.getGETTIME());
        if (!order_status_dbAdapter.isEmpty("DATE", del_DATE)) { //DB에 값이 있다면...
            try {
                item_status_dbAdapter.deleteContact_not(del_DATE);
                item_dbAdapter.deleteContact_not(del_DATE);
                order_dbAdapter.deleteContact_not(del_DATE);
                order_status_dbAdapter.deleteContact_not(del_DATE);
                Log.e("log_date", del_DATE + " 해당 날짜 외의 테이블 삭제 완료");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            order_status_dbAdapter.DropTable();
            order_status_dbAdapter.CreateTable();

            item_status_dbAdapter.DropTable();
            item_status_dbAdapter.CreateTable();

            item_dbAdapter.DropTable();
            item_dbAdapter.CreateTable();

            order_dbAdapter.DropTable();
            order_dbAdapter.CreateTable();
            Log.e("log_date", " 모든 날짜의 테이블 삭제 완료");
        }
    }

    private void Delete_table(String del_DATE) {//DATE 가져 오는것까지 해야함,
        if (!order_status_dbAdapter.isEmpty("DATE", del_DATE)) { //DB에 값이 있다면...
            try {
                item_status_dbAdapter.deleteContact("DATE", del_DATE);
                item_dbAdapter.deleteContact("DATE", del_DATE);
                order_dbAdapter.deleteContact("DATE", del_DATE);
                order_status_dbAdapter.deleteContact("DATE", del_DATE);
                Log.e("log_date", del_DATE + " 해당 날짜의 테이블 삭제 완료");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getDownloadCount() {
        SQLiteDatabase db = item_dbAdapter.mDBHelper.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        for (int i = 0; i < item.getAUFNR().size(); i++) {
            if (item_status_dbAdapter.isEmpty("AUFNR", String.valueOf(item.getAUFNR().get(i)))) {
                break;
            }
            String selectQuery = "SELECT AUFNR FROM " + itemContact.getTable_name() + " WHERE AUFNR = '" + item.getAUFNR().get(i) + "'";
            cursor = db.rawQuery(selectQuery, null);
            try {
                if (cursor.moveToFirst()) {
                    result++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cursor != null)
            cursor.close();
        db.close();
        return result;
    }

    private void setUI() {
        Print_First(seq, item.getORDER_SEQ());
        Print_First(sernr, item.getSERNR());
        Print_First(matnr, item.getORDER_MATNR());
        Print_First(back, item.getYMII_BACK());
        Print_First(chk, item.getLOAD_STATUS());
        print_clean(back);
        index.setText(getString(R.string.OrderIndex, (order_index + 1), item.getORDER_MATNR().size()));
    }

    private void CallDB(int size) {
        order_contact_data = OrderGetData(); //DB값 호출.
        for (int i = 0; i < order_contact_data.size(); i++)
            order_contact_data.get(i).setProperties(order_contact_data.get(i).getProperties());
        order_count = size;
        if (item.getAUFNR().size() != size) addOrderVariable(order_contact_data); // 변수에 값 저장.
    }

    private void SetLoadIndex(int size) {
        getorder = size > 4;
        item.setLOAD_INDEX(getDownloadCount());
        for (int i = 0; i < item.getLOAD_INDEX(); i++) {
            item.getLOAD_STATUS().set(i, getString(R.string.Check));
        }
    }

    private void getOrder() {
        try {
            CallRemote_order cr = new CallRemote_order();
            AsyncTask<String, String, SoapObject> at = cr.execute("");
            s = at.get();   //TODO 위에 파라미터 줄이기
            int size = order_dbAdapter.getConditionCount(orderContact, "DATE", userInfo.getREALTIME());
            if (!order_dbAdapter.isEmpty("DATE", userInfo.getGETTIME()) && userInfo.getREALTIME().equals(userInfo.getGETTIME())) { //DATE에 해당하는 필드값이 존재하므로 OrderList가 있다.
                if (!b || s == null) { //네트워크가 끊어진 경우. == 예외처리.
                    CallDB(size);
                    SetLoadIndex(size);
                } else {
                    SoapObject countryDetails = s;
                    CallDB(size);
                    if (check_order_err(countryDetails)) OrderSetData_update(countryDetails);
                    SetLoadIndex(size);
                }
            } else { //DB에 값이 없는 경우.
                if (!b || s == null) { //네트워크가 끊어진 경우. == 예외처리.
                    enter_flag = true;
                    getorder = false;
                } else {
                    OrderStatusUpdate("ORDER_QTY", String.valueOf(s.getPropertyCount()));
                    SoapToArraylist();
                    item.setLOAD_INDEX(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            enter_flag = true;
            finish();
        }
    }

    private void InitAdapter() {
        order_status_dbAdapter = new DBAdapter<>(getApplicationContext(), orderStatusContact);
        order_dbAdapter = new DBAdapter<>(getApplicationContext(), orderContact);
        item_status_dbAdapter = new DBAdapter<>(getApplicationContext(), itemStatusContact);
        item_dbAdapter = new DBAdapter<>(getApplicationContext(), itemContact);
    }

    //DB - OrderStatusTable- DATE 삽입
    //디비에 새로운 필드값 생성과 동시에 초기화 하는 과정.
    private void OrderStatusInput(String date, String order_status, String success_number, String order_qty, String load_date) {
        ArrayList<String> data = new ArrayList<>(Arrays.asList(date, order_status, success_number, order_qty, load_date));
        orderStatusContact.setProperties(data);
        order_status_dbAdapter.addContact();
    }

    private ArrayList<OrderContact> OrderGetData() {
        return order_dbAdapter.getAllContacts(orderContact);
    }

    private void addOrderVariable(ArrayList<OrderContact> order_contact_data) {
        for (int i = Start; i < order_count; i++) { //여기바뀌면 출력바뀜
            item.AUFNR.add(order_contact_data.get(i).getAUFNR());
            item.DCN.add(order_contact_data.get(i).getDCN());
            item.LINE.add(order_contact_data.get(i).getLINE());
            item.ORDER_MATNR.add(order_contact_data.get(i).getMATNR());
            item.ORDER_SEQ.add(order_contact_data.get(i).getSEQ());
            item.SERNR.add(order_contact_data.get(i).getSERNR());
            item.ORDER_STATUS.add(order_contact_data.get(i).getSTATUS());
            item.YMII_BACK.add(order_contact_data.get(i).getYMII_BACK());
            item.PICK_SEQ.add(order_contact_data.get(i).getPICK_SEQ());
            item.TAKT.add(order_contact_data.get(i).getTAKT());
            item.ZONE.add(order_contact_data.get(i).getIZONE());
            item.LOAD_STATUS.add("");
            Result.add("");
            Result_item.add("");
        }
    }

    private void OrderSetData(SoapObject countryDetails) {
        //DB - OrderTable - 모든 값Insert
        //많은 양의 데이터가 들어오고 속도 향상과 중간에 빠지는 값이 없도록 하기 위해서
        //트랜잭션을 한번만 일어나도록 한다.
        //db.beginTransaction(); 시작이고
        //마무리는 아래의 두가지 중 한가지로 처리되게 만든다.
        //db.setTransactionSuccessfull();
        //db.endTransaction();
        //try~ finally에서는 db를 이용하지 못한다.
        //왜냐하면 트랜잭션을 진행중이기 때문에 다른 트랜잭션이 끼어들지 못하기 때문임.

        test_status = new ArrayList<>(countryDetails.getPropertyCount());
        SQLiteDatabase db = order_dbAdapter.mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < countryDetails.getPropertyCount(); i++) {
                Object property = countryDetails.getProperty(i);
                if (property instanceof SoapObject) {
                    SoapObject countryObj = (SoapObject) property;
                    //DB Insert
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(
                            countryObj.getProperty("AUFNR").toString(),
                            userInfo.getGETTIME(),
                            userInfo.getPLANT(),
                            countryObj.getProperty("LINE").toString(),
                            userInfo.getZONE(),
                            countryObj.getProperty("DCN").toString(),
                            countryObj.getProperty("MATNR").toString(),
                            countryObj.getProperty("SEQ").toString(),
                            countryObj.getProperty("SERNR").toString(),
                            countryObj.getProperty("STATUS").toString(),
                            countryObj.getProperty("YMII_BACK").toString(),
                            countryObj.getProperty("PICK_SEQ").toString(),
                            countryObj.getProperty("TAKT").toString(),
                            countryObj.getProperty("IZONE").toString()
                    ));
                    order_dbAdapter.OneTimeInsert(db, data);
                    String Status = countryObj.getProperty("STATUS").toString();
                    test_status.add(Status);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    private void OrderSetData_update(SoapObject countryDetails) {
        test_status = new ArrayList<>(countryDetails.getPropertyCount());
        for (int i = 0; i < countryDetails.getPropertyCount(); i++) {
            Object property = countryDetails.getProperty(i);
            if (property instanceof SoapObject) {
                SoapObject countryObj = (SoapObject) property;
                String Status = countryObj.getProperty("STATUS").toString();
                test_status.add(Status);
            }
        }
        item.ORDER_STATUS = test_status;
    }


    private void OrderStatusUpdate(String convert_name, String convert_value) {
        OrderStatusContact temp = order_status_dbAdapter.getContact(new OrderStatusContact(), "DATE", userInfo.getGETTIME());
        ArrayList<String> data = temp.getProperties();
        orderStatusContact.setProperties(data);
        try {
            ArrayList<String> str = new ArrayList<>(orderStatusContact.getProperties());
            int location = orderStatusContact.getProperties_name().indexOf(convert_name);
            if (location != -1) {
                Log.e("log_test_Convert_value", convert_value);
                str.set(location, convert_value);
                orderStatusContact.setProperties(str);
                order_status_dbAdapter.updateContact(orderStatusContact.getProperty_name(0), orderStatusContact.getProperty(0));//OrderStatus의 0번째가 DATE로 기본키이기 때문에 이렇게 명시적으로 사용함.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Result_item = new ArrayList();
        Result = new ArrayList();
        seq = new TextView[5];
        sernr = new TextView[5];
        matnr = new TextView[5];
        back = new TextView[5];
        chk = new TextView[5];
        TextView task_date = findViewById(R.id.task_date);
        index = findViewById(R.id.index);
        Percent = findViewById(R.id.percent);
        task_date.setText(userInfo.getGETTIME());

        for (int i = 0; i < 5; i++) {
            int getID;
            getID = getResources().getIdentifier("seq" + i, "id", getApplicationContext().getPackageName());
            seq[i] = findViewById(getID);
            getID = getResources().getIdentifier("sernr" + i, "id", getApplicationContext().getPackageName());
            sernr[i] = findViewById(getID);
            getID = getResources().getIdentifier("matnr" + i, "id", getApplicationContext().getPackageName());
            matnr[i] = findViewById(getID);
            getID = getResources().getIdentifier("back" + i, "id", getApplicationContext().getPackageName());
            back[i] = findViewById(getID);
            getID = getResources().getIdentifier("chk" + i, "id", getApplicationContext().getPackageName());
            chk[i] = findViewById(getID);
        }
    }

    private void table_init() {
        Delete_table_not(userInfo.getREALTIME());
        net_check();
    }


    private void initDB() {
        UI_control();
        InitAdapter();

        enter_flag = true;
        userInfo = (User) getIntent().getSerializableExtra("userInfo");

        voice_stat = findViewById(R.id.voice_status);
        app.Set_voice_stat(voice_stat);

        table_init();

        if (order_status_dbAdapter.isEmpty("DATE", userInfo.getGETTIME())) { //DB에 값이 없다면...
            String date = userInfo.getGETTIME();
            String order_status = "0";
            String success_number = "0";
            String order_qty = "0";
            String load_date = "0";
            OrderStatusInput(date, order_status, success_number, order_qty, load_date);
        } else { // DB에 값있다면
            Log.e("OrderStatus 필드 조회", "실패 or 값이 존재.");
        }
        order_index = Integer.parseInt(order_status_dbAdapter.getContact(new OrderStatusContact(), "DATE", userInfo.getGETTIME()).getProperty(1));
        item.setORDER_INDEX(order_index);
    }


    private boolean check_order_err(SoapObject countryDetails) {
        Object property = countryDetails.getProperty(0);
        SoapObject countryObj = (SoapObject) property;
        int count = countryObj.getPropertyCount();
        if (count == 1) {
            Log.e("log_order result", "false 값 에러");
            return false;
        } else {
            Log.e("log_order result", "true 값 정상");
            return true;
        }
    }

    //오더 리스트
    public void SoapToArraylist() {
        try {
            SoapObject countryDetails = s;      //all
            if (!order_dbAdapter.isEmpty("DATE", userInfo.getGETTIME()))
                Delete_table(userInfo.getGETTIME()); //존재유무
            if (check_order_err(countryDetails)) {
                OrderSetData(countryDetails);
                Date A;
                Date B;
                A = userInfo.simpleDateFormat.parse(userInfo.getREALTIME());
                B = userInfo.simpleDateFormat.parse(userInfo.getGETTIME());
                int compare = A.compareTo(B); //리얼이랑 현재 비교
                order_contact_data = OrderGetData(); //DB값 호출.
                if (compare >= 0) {  //과거 기록 조회
                    order_count = order_dbAdapter.getConditionCount(new OrderContact(), "DATE", userInfo.getGETTIME());//조회날짜 갯수
                    for (int i = 0; i < order_contact_data.size(); i++)
                        order_contact_data.get(i).setProperties(order_contact_data.get(i).getProperties());
                    addOrderVariable(order_contact_data); // 변수에 값 저장.
                } else { //미래 기록 조회
                    order_count = order_contact_data.size();
                    Start = order_dbAdapter.getConditionCount(new OrderContact(), "DATE", userInfo.getREALTIME());//조회날짜 갯수
                    for (int i = 0; i < order_contact_data.size(); i++)
                        order_contact_data.get(i).setProperties(order_contact_data.get(i).getProperties());
                    addOrderVariable(order_contact_data); // 변수에 값 저장.
                }
                item.ORDER_STATUS = test_status;
                getorder = item.getAUFNR().size() > 4;
            } else
                getorder = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void print_clean(TextView[] tv) {
        int i;
        for (i = -2; i < 3; i++) {
            int temp = order_index;
            temp += i;
            if (temp >= 0 && temp < item.getORDER_STATUS().size() && item.getORDER_STATUS().get(temp).equals("Y")) {
                tv[i + 2].setBackgroundColor(Color.parseColor("#FEFF52"));
            } else {
                tv[i + 2].setBackgroundColor(Color.parseColor("#7EE8C6"));
            }
        }
    }

    private void err_dialog() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        String err_Net = "The network is not working. Connect to 'FactoryWireless'.";
        String err_not_exist = "[" + userInfo.getGETTIME() + "] data does not exist!";
        String err_message;
        alertdialog.setTitle("Not Found");
        if (!b) {
            err_message = err_Net;
        } else {
            err_message = err_not_exist;
        }
        alertdialog.setMessage(err_message).setPositiveButton("Return",
                (dialog, which) -> OrderActivity.this.finish());
        AlertDialog alertDialog = alertdialog.create();
        alertDialog.show();
    }

    private void check_load_time() {     //테스트만 하면댐 월요일
        String load_date;
        TextView tv = findViewById(R.id.load_date);
        load_date = order_status_dbAdapter.getContact(new OrderStatusContact(), "DATE", userInfo.getGETTIME()).getProperty(4);
        if (load_date.equals("0")) {
            OrderStatusUpdate("LOAD_TIME", app.load_time());
            load_date = order_status_dbAdapter.getContact(new OrderStatusContact(), "DATE", userInfo.getGETTIME()).getProperty(4);
            tv.setText(getString(R.string.TwoString, "loading from ", load_date));
        } else if (item.getLOAD_INDEX() == item.getAUFNR().size() && item.getLOAD_INDEX() != 0) {      //동작키에 넣어야할듯?
            if (load_flag) {
                OrderStatusUpdate("LOAD_TIME", app.load_time());
                load_flag = false;
            }
            tv.setText(getString(R.string.TwoString, "End loading ", load_date));
        } else {
            tv.setText(getString(R.string.TwoString, "loading from ", load_date));
        }
    }

    public void Print_First(TextView[] tv, ArrayList<String> arr) {
        if (order_index == 0) {
            tv[0].setText("");
            tv[1].setText("");
            tv[2].setText(arr.get(order_index));
            tv[3].setText(arr.get(order_index + 1));
            tv[4].setText(arr.get(order_index + 2));
        } else if (order_index == 1) {
            tv[0].setText("");
            tv[1].setText(arr.get(order_index - 1));
            tv[2].setText(arr.get(order_index));
            tv[3].setText(arr.get(order_index + 1));
            tv[4].setText(arr.get(order_index + 2));
        } else if (order_index > 1 && order_index < item.getAUFNR().size() - 2) {
            tv[0].setText(arr.get(order_index - 2));
            tv[1].setText(arr.get(order_index - 1));
            tv[2].setText(arr.get(order_index));
            tv[3].setText(arr.get(order_index + 1));
            tv[4].setText(arr.get(order_index + 2));
        } else if (order_index == item.getAUFNR().size() - 2) {
            tv[0].setText(arr.get(order_index - 2));
            tv[1].setText(arr.get(order_index - 1));
            tv[2].setText(arr.get(order_index));
            tv[3].setText(arr.get(order_index + 1));
            tv[4].setText("");
        } else {
            tv[0].setText(arr.get(order_index - 2));
            tv[1].setText(arr.get(order_index - 1));
            tv[2].setText(arr.get(order_index));
            tv[3].setText("");
            tv[4].setText("");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("getkey", event.getKeyCode() + "");
        check_load_time();
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (key_flag) {
                try {
                    key_down();
                    Thread.sleep(300);
                } catch (InterruptedException ignore) {
                }
            }
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (key_flag) {
                try {
                    key_up();
                    Thread.sleep(300);
                } catch (InterruptedException ignore) {
                }
            }
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            event.startTracking();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void key_down() {
        if (order_index > 0) {    //상태 감소
            order_index -= 1;
            item.setORDER_INDEX(order_index);
            //UPDATE ORDER_STATUS 값
            String convert_value = String.valueOf(order_index);
            OrderStatusUpdate("ORDER_STATUS", convert_value);
            app.OrderStatusPrint(order_status_dbAdapter);
            for (int i = 4; i > 0; i--) {
                seq[i].setText(seq[i - 1].getText());
                sernr[i].setText(sernr[i - 1].getText());
                matnr[i].setText(matnr[i - 1].getText());
                back[i].setText(back[i - 1].getText());
            }
            if (order_index - 1 > 0) {
                seq[0].setText(item.getORDER_SEQ().get(order_index - 2));
                sernr[0].setText(item.getSERNR().get(order_index - 2));
                matnr[0].setText(item.getORDER_MATNR().get(order_index - 2));
                back[0].setText(item.getYMII_BACK().get(order_index - 2));
            } else {
                seq[0].setText("");
                sernr[0].setText("");
                matnr[0].setText("");
                back[0].setText("");
            }
        }
        Print_First(chk, item.getLOAD_STATUS());
        print_clean(back);
        percent();
        index.setText(getString(R.string.OrderIndex, (order_index + 1), item.getORDER_MATNR().size()));
    }

    private void key_up() {
        if (order_index + 1 < item.getORDER_MATNR().size()) {
            order_index += 1;
            item.setORDER_INDEX(order_index);
            //UPDATE ORDER_STATUS 값
            String convert_value = order_index + "";
            OrderStatusUpdate("ORDER_STATUS", convert_value);
            app.OrderStatusPrint(order_status_dbAdapter);
            for (int i = 0; i < 4; i++) {
                seq[i].setText(seq[i + 1].getText());
                sernr[i].setText(sernr[i + 1].getText());
                matnr[i].setText(matnr[i + 1].getText());
                back[i].setText(back[i + 1].getText());
            }
            if (order_index + 2 < item.getORDER_MATNR().size()) {
                seq[4].setText(item.getORDER_SEQ().get(order_index + 2));
                sernr[4].setText(item.getSERNR().get(order_index + 2));
                matnr[4].setText(item.getORDER_MATNR().get(order_index + 2));
                back[4].setText(item.getYMII_BACK().get(order_index + 2));
            } else {
                seq[4].setText("");
                sernr[4].setText("");
                matnr[4].setText("");
                back[4].setText("");
            }
        }
        Print_First(chk, item.getLOAD_STATUS());
        print_clean(back);
        percent();
        index.setText(getString(R.string.OrderIndex, (order_index + 1), item.getORDER_MATNR().size()));
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!key_flag) {
                    key_down();
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!key_flag) {
                    key_down();
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!key_flag) {
                    key_up();
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!key_flag) {
                    key_up();
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_HOME:
                break;
            case KeyEvent.KEYCODE_BACK:
                enter_flag = true;
                if (!userInfo.getGETTIME().equals(userInfo.getREALTIME())) {
                    Delete_table_not(userInfo.getREALTIME());
                }
                finish();
                break;
            case KeyEvent.KEYCODE_SPACE:
                enter_key();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                enter_key();
                break;
        }
        app.key_voice_control(keyCode);
        new Handler().postDelayed(() -> app.Set_voice_stat(voice_stat), 500);
        return super.onKeyUp(keyCode, event);
    }


    private void enter_key() {
        String temp = item.getLOAD_STATUS().get(order_index);
        if (temp.equals(getString(R.string.Check))) {
            enter_flag = true;
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra("userInfo", userInfo);
            intent.putExtra("item", item);
            intent.putExtra("order_index", order_index);
            intent.putExtra("Result", Result_item);
            startActivityForResult(intent, 1);
        } else {
            app.PrintToastMessage("Not yet loaded");
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            key_flag = true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            key_flag = true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        enter_flag = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        UI_control();
        app.Set_voice_stat(voice_stat);
        if (userInfo.getGETTIME().equals(userInfo.getREALTIME())) {
            getOrder();
            print_clean(back);
        }
        if (item.getLOAD_INDEX() < item.getAUFNR().size() && item.getLIMIT_INDEX() != item.getAUFNR().size()) {
            backgroundThread = new BackgroundThread();
            backgroundThread.setRunning(true);
            backgroundThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        enter_flag = true;
        boolean retry = true;
        backgroundThread.setRunning(false);
        while (retry) {
            try {
                backgroundThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        UI_control();
        if (userInfo.getGETTIME().equals(userInfo.getREALTIME())) {
            Delete_table_not(userInfo.getREALTIME());
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        switch (request) {
            case 1:
                if (result == 1111) {
                    UI_control();
                    item.setLOAD_INDEX(data.getIntExtra("load_index_item", 3333));//현재 위치 알려줌
                    item.setLIMIT_INDEX(data.getIntExtra("limit_index", 4444));//다운다됐는지 확인
                    item.LOAD_STATUS = data.getStringArrayListExtra("load_stat");
                    if (item.getLIMIT_INDEX() >= item.getAUFNR().size() - 1) {   //다 다운받았을경우
                        enter_flag = true;
                        backgroundThread.setRunning(false);
                    } else {
                        enter_flag = false;
                    }
                }
                break;
        }
    }

    private void handleMessage(Message msg) {
        Print_First(chk, item.getLOAD_STATUS());
        Log.e("log_voc_thread_test", "handleMessage" + Thread.currentThread().getName());
    }

    public class BackgroundThread extends Thread {
        boolean running = false;

        void setRunning(boolean b) {
            running = b;
        }

        @Override
        public void run() {
            breakOut:
            while (running) {
                while (item.getLOAD_INDEX() < item.getAUFNR().size() && item.getLIMIT_INDEX() != item.getAUFNR().size()) {
                    if (item.getLIMIT_INDEX() == item.getAUFNR().size() || enter_flag) {
                        backgroundThread.setRunning(false);
                        break breakOut;
                    }
                    SoapObject countryDetails;
                    Log.e("log_load ", "1. 배열 인덱스: " + item.getLOAD_INDEX());
                    SoapObject input_params = new SoapObject(app.NAMESPACE, "InputParams");
                    SoapObject filter_sequence = new SoapObject(app.NAMESPACE, "InputSequence");
                    SoapObject request = new SoapObject(app.NAMESPACE, app.SOAP_METHOD);
                    filter_sequence.addProperty("OrderNo", item.getLOAD_ORDER());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Line", userInfo.getLINE());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Plant", userInfo.getPLANT());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Zone", userInfo.getZONE());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Takt", userInfo.getTAKT());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("PIC_SEQ", item.getLOAD_SEQ());//전달 파라미터(변수명 값 입력해야함)
                    input_params.addSoapObject(filter_sequence);//전달 파라미터(변수명 값 입력해야함)
                    request.addProperty("LoginName", userInfo.getID());//전달 파라미터(변수명 값 입력해야함)
                    request.addProperty("LoginPassword", userInfo.getPW());//전달 파라미터(변수명 값 입력해야함)
                    request.addProperty("InputParams", filter_sequence);//전달 파라미터(변수명 값 입력해야함)

                    //////웹서비스 호출 준비
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = true;
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(app.ITEM, 3000);
                    androidHttpTransport.debug = true;
                    try {
                        androidHttpTransport.call(app.SOAP_ACTION, envelope);
                        countryDetails = (SoapObject) envelope.getResponse();
                        Result.set(item.getLOAD_INDEX(), countryDetails);
                        item.getLOAD_STATUS().set(item.getLOAD_INDEX(), getString(R.string.Check));
                        Log.e("log_load", "load_index: " + item.getLOAD_INDEX() + "/ date: " + userInfo.getGETTIME());
                        app.SoapToArraylist_item(Result, item.getLOAD_INDEX(), item_dbAdapter, userInfo.getGETTIME(), itemStatusContact, item_status_dbAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("log_load ", "catch index: " + item.getLOAD_INDEX());
                        Result.set(item.getLOAD_INDEX(), "");
                        item.setLOAD_INDEX(item.getLOAD_INDEX() - 1);
                    }
                    myHandler.sendMessage(myHandler.obtainMessage());
                    if (item.getLOAD_INDEX() == (item.getAUFNR().size() - 1)) {
                        load_flag = true;
                    }
                    item.setLOAD_INDEX(item.getLOAD_INDEX() + 1);
                }
                item.setLIMIT_INDEX(item.getLOAD_INDEX());
            }
        }
    }

    private static class MyHandler extends Handler {
        // 핸들러 객체 만들기
        private final WeakReference<OrderActivity> mActivity;

        private MyHandler(OrderActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            OrderActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}


