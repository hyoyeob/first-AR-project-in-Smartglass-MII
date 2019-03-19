package com.supertester.PBV_MII.project_v2.ACT;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.supertester.PBV_MII.project_v2.ASYNC.CallRemote_order;
import com.supertester.PBV_MII.project_v2.CLASS.Std_Method;
import com.supertester.PBV_MII.project_v2.DB.Contacts.ItemContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.ItemStatusContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.OrderContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.OrderStatusContact;
import com.supertester.PBV_MII.project_v2.DB.DBAdapter;
import com.supertester.PBV_MII.project_v2.R;
import com.supertester.PBV_MII.project_v2.VOICE_CONTROL.MyService;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * cr,tv 옮김
 */

public class OrderActivity extends Activity implements Serializable {
    private String LINEo = null;
    private String ZONEo = null;
    private String PLANTo = null;
    private String DATEo = null;        //order table status - date
    private String IDo = null;
    private String PWo = null;
    private String TAKTo = null;
    private String realtime = null;
    private int status = 0;             // order table status - ORDER_STATUS
    int load_index = -1;
    int putindex;
    boolean enter_flag = true;
    boolean b;
    boolean getorder;
    boolean key_flag;
    boolean load_flag = false;
    Context context;
    String check = "V";
    BackgroundThread backgroundThread;
    int order_count = 0;
    int Start = 0;

    SoapObject s = null;
    TextView index;
    TextView Percent;
    TextView[] matnr;
    TextView[] seq;
    TextView[] sernr;
    TextView[] back;
    TextView[] chk;

    ArrayList AUFNR;
    ArrayList test_status;
    ArrayList DCN;
    ArrayList LINE;
    ArrayList<String> MATNR;
    ArrayList<String> SEQ;
    ArrayList<String> SERNR;
    ArrayList STATUS;
    ArrayList<String> YMII_BACK;
    ArrayList PICK_SEQ;
    ArrayList TAKT;
    ArrayList IZONE;
    ArrayList Result;
    ArrayList Result_item;
    ArrayList<String> load_stat;

    ArrayList<OrderContact> order_contact_data;
    OrderStatusContact orderStatusContact = new OrderStatusContact();
    OrderContact orderContact = new OrderContact();
    ItemStatusContact itemStatusContact = new ItemStatusContact();
    ItemContact itemContact = new ItemContact();
    MyHandler myHandler = new MyHandler(this);

    DBAdapter order_status_dbAdapter;
    DBAdapter order_dbAdapter;
    DBAdapter item_status_dbAdapter;
    DBAdapter item_dbAdapter;
    Std_Method app;
    LinearLayout linear_h;
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


    private boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (MyService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    public void batteryLevel() {
        if(isServiceRunning()){
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

    public void battery_init(int level){
        int hour;
        double minute;
        int hour2;
        double minute2;
        hour = level/20;
        hour2 = level/15;
        minute=(level*2.8)-(hour*60);
        minute2=(level*3.78)-(hour2*60);
        int m1 =(int)minute;
        int m2 =(int)minute2;
        int sub =(int)((level*3.78)-(level*2.8));
        if(level<25){
            Dialog(level, hour, m1, hour2, m2, sub);
        }
    }

    public void Dialog(int level, int hour, int minute, int hour2, int minute2, int sub){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Battery Warning!");
        builder.setMessage("The battery is running low.("+level+"%)\nService shutdown can increase usage time.\nKeep On: "+hour+"h "+minute+"m Available"+"\nTo end: "+hour2+"h "+minute2+"m Available("+sub+"m more available)\nQuit voice service?");
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        app.PrintToastMessage("Battery discharge \nafter "+hour+"h "+minute+"m");
                    }
                });
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        stopService(new Intent(context, MyService.class));
                        app.PrintToastMessage("Terminate Voice Services\nAvailable for "+hour2+"h "+minute2+"m");
                    }
                });
        builder.show();
    }


    private void percent() {//특정 위치 색변환 시키는 함수, 퍼센트 생성
        String percent = "";
        String result_percent;
        int i;
        for (i = 0; i < AUFNR.size(); i++) {      // 퍼센트 초기화
            result_percent = percent.concat("l");
            percent = result_percent;
        }
        Log.e("log_order_percent",STATUS+"");
        SpannableString spercent = new SpannableString(percent);
        for (i = 0; i < AUFNR.size(); i++) {     // 퍼센트 초기화
            if (i < status && !STATUS.get(i).equals("Y")) {
                spercent.setSpan(new ForegroundColorSpan(Color.RED), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (i == status) {
                spercent.setSpan(new ForegroundColorSpan(Color.BLACK), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (STATUS.get(i).equals("Y")) {
                spercent.setSpan(new ForegroundColorSpan(Color.GREEN), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        Percent.setText(spercent);
    }

    private void error_test() {
        if (!getorder) {
            enter_flag = true;
            if (DATEo.equals(realtime)) {
                Delete_table_not(realtime);
                Log.e("log_net", "exit to error");
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
        linear_h = findViewById(R.id.layout_h);
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
        Log.e("log_date", "살리는 날짜: "+del_DATE + " 삭제하는 날짜: " + DATEo);
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
        }else{
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

    private void Delete_table(String data, String del_DATE) {//DATE 가져 오는것까지 해야함,
        Log.e("log_date", del_DATE + " " + DATEo);
        if (!order_status_dbAdapter.isEmpty("DATE", del_DATE)) { //DB에 값이 있다면...
            try {
                item_status_dbAdapter.deleteContact(data, del_DATE);
                item_dbAdapter.deleteContact(data, del_DATE);
                order_dbAdapter.deleteContact(data, del_DATE);
                order_status_dbAdapter.deleteContact(data, del_DATE);
                Log.e("log_date", del_DATE + " 해당 날짜 외의 테이블 삭제 완료");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getDownloadCount() {
        SQLiteDatabase db = item_dbAdapter.mDBHelper.getWritableDatabase();
        Cursor cursor = null;
        int result = 0;

        for (int i = 0; i < AUFNR.size(); i++) {
            if (item_status_dbAdapter.isEmpty("AUFNR", String.valueOf(AUFNR.get(i)))) {
                break;
            }
            String selectQuery = "SELECT AUFNR FROM " + itemContact.getTable_name() + " WHERE AUFNR = '" + AUFNR.get(i) + "'";
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
        Print_First(seq, SEQ);
        Print_First(sernr, SERNR);
        Print_First(matnr, MATNR);
        Print_First(back, YMII_BACK);
        Print_First(chk, load_stat);
        print_clean(back);
        index.setText((status + 1) + "/" + MATNR.size());
    }

    private void getOrder() {
        try {
            CallRemote_order cr = new CallRemote_order();
            AsyncTask<String, String, SoapObject> at = cr.execute(LINEo, PLANTo, ZONEo, DATEo, IDo, PWo, TAKTo, "");
            s = at.get();

            initOrderVariable();
            int size = order_dbAdapter.getConditionCount(new OrderContact(), "DATE", realtime);
            Log.e("log_today_size", size + "");

            if (!order_dbAdapter.isEmpty("DATE", DATEo) && realtime.equals(DATEo)) { //DATE에 해당하는 필드값이 존재하므로 OrderList가 있다.
                if (!b || s == null){ //네트워크가 끊어진 경우. == 예외처리.
                    order_contact_data = OrderGetData(); //DB값 호출.
                    for (int i = 0; i < order_contact_data.size(); i++)
                        order_contact_data.get(i).setProperties(order_contact_data.get(i).getProperties());
                    order_count = size;
                    setOrderVariable(order_contact_data); // 변수에 값 저장.
                    Log.e("log_order1111", STATUS + "");
                    getorder = size > 4;
                    load_index = getDownloadCount();
                    for (int i = 0; i < load_index; i++) {
                        load_stat.set(i, check);
                    }
                }else{
                    SoapObject countryDetails = s;      //all
                    order_contact_data = OrderGetData(); //DB값 호출.
                    for (int i = 0; i < order_contact_data.size(); i++)
                        order_contact_data.get(i).setProperties(order_contact_data.get(i).getProperties());
                    order_count = size;
                    setOrderVariable(order_contact_data); // 변수에 값 저장.
                    if (check_order_err(countryDetails)) OrderSetData_update(countryDetails);
                    Log.e("log_order2222", STATUS + "");
                    getorder = size > 4;
                    load_index = getDownloadCount();
                    for (int i = 0; i < load_index; i++) {
                        load_stat.set(i, check);
                    }
                }

            } else { //DB에 값이 없는 경우.
                if (!b || s == null){ //네트워크가 끊어진 경우. == 예외처리.
                    enter_flag = true;
                    getorder = false;
                }else{
                    OrderStatusUpdate("ORDER_QTY", String.valueOf(s.getPropertyCount()));
                    SoapToArraylist();
                    load_index = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            enter_flag = true;
            finish();
        }
    }

    private void InitAdapter() {
        order_status_dbAdapter = new DBAdapter(getApplicationContext(), orderStatusContact);
        order_dbAdapter = new DBAdapter(getApplicationContext(), orderContact);
        item_status_dbAdapter = new DBAdapter(getApplicationContext(), itemStatusContact);
        item_dbAdapter = new DBAdapter(getApplicationContext(), itemContact);

        /**
         * 하단 주석 전부 풀면 실행 시마다 테이블 초기화
         */
//        order_status_dbAdapter.DropTable();
//        order_status_dbAdapter.CreateTable();
//
//        item_status_dbAdapter.DropTable();
//        item_status_dbAdapter.CreateTable();
//
//        item_dbAdapter.DropTable();
//        item_dbAdapter.CreateTable();
//
//        order_dbAdapter.DropTable();
//        order_dbAdapter.CreateTable();
    }

    //DB - OrderStatusTable- DATE 삽입
    //디비에 새로운 필드값 생성과 동시에 초기화 하는 과정.
    private void OrderStatusInput(String date, String order_status, String success_number, String order_qty, String load_date) {
        ArrayList<String> data = new ArrayList<String>(Arrays.asList(date, order_status, success_number, order_qty, load_date));
        orderStatusContact.setProperties(data);
        order_status_dbAdapter.addContact();
    }

    private ArrayList<OrderContact> OrderGetData() {
        return order_dbAdapter.getAllContacts(new OrderContact());
    }

    private void initOrderVariable() {
        AUFNR = new ArrayList();
        DCN = new ArrayList();
        LINE = new ArrayList();
        MATNR = new ArrayList();
        SEQ = new ArrayList();
        SERNR = new ArrayList();
        STATUS = new ArrayList();
        YMII_BACK = new ArrayList();
        PICK_SEQ = new ArrayList();
        TAKT = new ArrayList();
        IZONE = new ArrayList();
    }

    private void setOrderVariable(ArrayList<OrderContact> order_contact_data) {
        Log.e("flow", order_count + "");
//        for(int i=order_count; i<order_contact_data.size(); i++){ //여기바뀌면 출력바뀜
        for (int i = Start; i < order_count; i++) { //여기바뀌면 출력바뀜
            AUFNR.add(order_contact_data.get(i).getAUFNR());
            DCN.add(order_contact_data.get(i).getDCN());
            LINE.add(order_contact_data.get(i).getLINE());
            MATNR.add(order_contact_data.get(i).getMATNR());
            SEQ.add(order_contact_data.get(i).getSEQ());
            SERNR.add(order_contact_data.get(i).getSERNR());
            STATUS.add(order_contact_data.get(i).getSTATUS());
            YMII_BACK.add(order_contact_data.get(i).getYMII_BACK());
            PICK_SEQ.add(order_contact_data.get(i).getPICK_SEQ());
            TAKT.add(order_contact_data.get(i).getTAKT());
            IZONE.add(order_contact_data.get(i).getIZONE());
            load_stat.add("");
            Result.add("");
            Result_item.add("");
        }
    }

    private void setOrderVariable2(ArrayList<OrderContact> order_contact_data) {
        Log.e("flow", order_count + "");
//        for(int i=order_count; i<order_contact_data.size(); i++){ //여기바뀌면 출력바뀜
        for (int i = Start; i < order_count; i++) { //여기바뀌면 출력바뀜
            STATUS.set(i, order_contact_data.get(i).getSTATUS());
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

        test_status = new ArrayList(countryDetails.getPropertyCount());
        SQLiteDatabase db = order_dbAdapter.mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < countryDetails.getPropertyCount(); i++) {
                Object property = countryDetails.getProperty(i);
                if (property instanceof SoapObject) {
                    SoapObject countryObj = (SoapObject) property;
                    //DB Insert
                    ArrayList<String> data = new ArrayList<String>(Arrays.asList(
                            countryObj.getProperty("AUFNR").toString(),
                            DATEo,
                            PLANTo,
                            countryObj.getProperty("LINE").toString(),
                            ZONEo,
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
        test_status = new ArrayList(countryDetails.getPropertyCount());
        try {
            for (int i = 0; i < countryDetails.getPropertyCount(); i++) {
                Object property = countryDetails.getProperty(i);
                if (property instanceof SoapObject) {
                    SoapObject countryObj = (SoapObject) property;
                    String Status = countryObj.getProperty("STATUS").toString();
                    test_status.add(Status);
                }
            }
        } finally {
        }
        STATUS=test_status;
    }


    private void OrderStatusUpdate(String convert_name, String convert_value) {
        OrderStatusContact temp = (OrderStatusContact) order_status_dbAdapter.getContact(new OrderStatusContact(), "DATE", DATEo);
        ArrayList<String> data = temp.getProperties();
        orderStatusContact.setProperties(data);

        try {
            ArrayList<String> str = new ArrayList<>(orderStatusContact.getProperties());
            int location = orderStatusContact.getProperties_name().indexOf(convert_name);
            if (location != -1) {
                Log.e("log_indexOf", str.indexOf(convert_name) + "");
                Log.e("log_test_Convert_value", convert_value);
                str.set(location, convert_value);
                orderStatusContact.setProperties(str);
                order_status_dbAdapter.updateContact(orderStatusContact.getProperty_name(0), orderStatusContact.getProperty(0));//OrderStatus의 0번째가 DATE로 기본키이기 때문에 이렇게 명시적으로 사용함.
            }
        } catch (Exception e) {
            Log.e("ASD", e + "");
        }
    }

    private void init() {
        Result = new ArrayList();
        load_stat = new ArrayList();
        Result_item = new ArrayList();
        context = this;
        seq = new TextView[5];
        sernr = new TextView[5];
        matnr = new TextView[5];
        back = new TextView[5];
        chk = new TextView[5];
        TextView task_date = findViewById(R.id.task_date);
        index = findViewById(R.id.index);
        Percent = findViewById(R.id.percent);
        task_date.setText(DATEo);

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

    private void table_init(){
        Delete_table_not(realtime);
        net_check();
    }


    private void initDB() {
        UI_control();
        InitAdapter();

        enter_flag = true;
        realtime = getIntent().getExtras().getString("realtime");
        DATEo = getIntent().getExtras().getString("DATEo");
        LINEo = getIntent().getExtras().getString("LINEo");
        PLANTo = getIntent().getExtras().getString("PLANTo");
        ZONEo = getIntent().getExtras().getString("ZONEo");
        TAKTo = getIntent().getExtras().getString("TAKTo");
        IDo = getIntent().getExtras().getString("IDo");
        PWo = getIntent().getExtras().getString("PWo");

        voice_stat = findViewById(R.id.voice_status);
        app.Set_voice_stat(voice_stat);

        table_init();
//        new Handler().postDelayed(()->{
//        },500);

        if (order_status_dbAdapter.isEmpty("DATE", getIntent().getExtras().getString("DATEo"))) { //DB에 값이 없다면...
            String date = getIntent().getExtras().getString("DATEo");
            String order_status = "0";
            String success_number = "0";
            String order_qty = "0";
            String load_date = "0";
            OrderStatusInput(date, order_status, success_number, order_qty, load_date);
        } else { // DB에 값있다면
            Log.e("OrderStatus 필드 조회", "실패 or 값이 존재.");
        }
        status = Integer.parseInt(order_status_dbAdapter.getContact(new OrderStatusContact(), "DATE", DATEo).getProperty(1));
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
            if (!order_dbAdapter.isEmpty("DATE", DATEo)) Delete_table("DATE", DATEo); //존재유무
            if (check_order_err(countryDetails)) {
                OrderSetData(countryDetails);
                Date A;
                Date B;
                A = new SimpleDateFormat("MM-dd-yyyy").parse(realtime);
                B = new SimpleDateFormat("MM-dd-yyyy").parse(DATEo);
                int compare = A.compareTo(B); //리얼이랑 현재 비교
                order_contact_data = OrderGetData(); //DB값 호출.
                if (compare >= 0) {  //과거 기록 조회
                    order_count = order_dbAdapter.getConditionCount(new OrderContact(), "DATE", DATEo);//조회날짜 갯수
                    Log.e("log_today_size 과거 또는 현재", Start + " ~ " + order_count);
                    for (int i = 0; i < order_contact_data.size(); i++)
                        order_contact_data.get(i).setProperties(order_contact_data.get(i).getProperties());
                    setOrderVariable(order_contact_data); // 변수에 값 저장.
                } else { //미래 기록 조회
                    order_count = order_contact_data.size();
                    Start = order_dbAdapter.getConditionCount(new OrderContact(), "DATE", realtime);//조회날짜 갯수
                    Log.e("log_today_size 미래", Start + " ~ " + order_count);
                    for (int i = 0; i < order_contact_data.size(); i++)
                        order_contact_data.get(i).setProperties(order_contact_data.get(i).getProperties());
                    setOrderVariable(order_contact_data); // 변수에 값 저장.
                }
                STATUS=test_status;
                Log.e("log_order3333", STATUS + "");
                getorder = AUFNR.size() > 4;
            }else
                getorder = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void print_clean(TextView[] tv) {
        int i;
        for (i = -2; i < 3; i++) {
            int temp = status;
            temp += i;
            if (temp >= 0 && temp < STATUS.size() && STATUS.get(temp).equals("Y")) {
                tv[i + 2].setBackgroundColor(Color.parseColor("#FEFF52"));
            } else {
                tv[i + 2].setBackgroundColor(Color.parseColor("#7EE8C6"));
            }
        }
    }

    private void err_dialog() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
        Log.e("log_dial", "test");
        String err_Net = "The network is not working. Connect to 'FactoryWireless'.";
        String err_not_exist = "[" + DATEo + "] data does not exist!";
        String err_message = "";
        alertdialog.setTitle("Not Found");
        if (!b) {
            err_message = err_Net;
        } else {
            err_message = err_not_exist;
        }
        alertdialog.setMessage(err_message).setPositiveButton("Return",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrderActivity.this.finish();
                    }
                });
        AlertDialog alertDialog = alertdialog.create();
        alertDialog.show();
    }

    private void check_load_time() {     //테스트만 하면댐 월요일
        String load_date;
        TextView tv = findViewById(R.id.load_date);
        Log.e("log_date start", load_index + "   " + AUFNR.size());
        load_date = order_status_dbAdapter.getContact(new OrderStatusContact(), "DATE", DATEo).getProperty(4);
        if (load_date.equals("0")) {
            OrderStatusUpdate("LOAD_TIME", app.load_time());
            load_date = order_status_dbAdapter.getContact(new OrderStatusContact(), "DATE", DATEo).getProperty(4);
            tv.setText("loading from " + load_date);
        } else if (load_index == AUFNR.size() && load_index != 0) {      //동작키에 넣어야할듯?
            if (load_flag) {
                OrderStatusUpdate("LOAD_TIME", app.load_time());
                load_flag = false;
            }
            tv.setText("End loading " + load_date);
        } else {
            tv.setText("loading from " + load_date);
        }
    }

    public void Print_First(TextView[] tv, ArrayList<String> arr) {
        if (status == 0) {
            tv[0].setText("");
            tv[1].setText("");
            tv[2].setText(arr.get(status));
            tv[3].setText(arr.get(status + 1));
            tv[4].setText(arr.get(status + 2));
        } else if (status == 1) {
            tv[0].setText("");
            tv[1].setText(arr.get(status - 1));
            tv[2].setText(arr.get(status));
            tv[3].setText(arr.get(status + 1));
            tv[4].setText(arr.get(status + 2));
        } else if (status > 1 && status < AUFNR.size() - 2) {
            tv[0].setText(arr.get(status - 2));
            tv[1].setText(arr.get(status - 1));
            tv[2].setText(arr.get(status));
            tv[3].setText(arr.get(status + 1));
            tv[4].setText(arr.get(status + 2));
        } else if (status == AUFNR.size() - 2) {
            tv[0].setText(arr.get(status - 2));
            tv[1].setText(arr.get(status - 1));
            tv[2].setText(arr.get(status));
            tv[3].setText(arr.get(status + 1));
            tv[4].setText("");
        } else {
            tv[0].setText(arr.get(status - 2));
            tv[1].setText(arr.get(status - 1));
            tv[2].setText(arr.get(status));
            tv[3].setText("");
            tv[4].setText("");
        }
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, ItemActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("getkey", event.getKeyCode() + "");
        Log.e("key pressed", String.valueOf(event.getKeyCode()));
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
        if (status > 0) {    //상태 감소
            status -= 1;
            //UPDATE ORDER_STATUS 값
            String convert_name = "ORDER_STATUS";
            String convert_value = String.valueOf(status);
            OrderStatusUpdate(convert_name, convert_value);
            app.OrderStatusPrint(order_status_dbAdapter);
            for (int i = 4; i > 0; i--) {
                seq[i].setText(seq[i - 1].getText());
                sernr[i].setText(sernr[i - 1].getText());
                matnr[i].setText(matnr[i - 1].getText());
                back[i].setText(back[i - 1].getText());
            }
            if (status - 1 > 0) {
                seq[0].setText(SEQ.get(status - 2));
                sernr[0].setText(SERNR.get(status - 2));
                matnr[0].setText(MATNR.get(status - 2));
                back[0].setText(YMII_BACK.get(status - 2));
            } else {
                seq[0].setText("");
                sernr[0].setText("");
                matnr[0].setText("");
                back[0].setText("");
            }
        }
        Print_First(chk, load_stat);
        print_clean(back);
        percent();
        index.setText((status + 1) + "/" + MATNR.size());
    }

    private void key_up() {
        if (status + 1 < MATNR.size()) {
            status += 1;
            //UPDATE ORDER_STATUS 값
            String convert_name = "ORDER_STATUS";
            String convert_value = status + "";
            OrderStatusUpdate(convert_name, convert_value);
            app.OrderStatusPrint(order_status_dbAdapter);
            for (int i = 0; i < 4; i++) {
                seq[i].setText(seq[i + 1].getText());
                sernr[i].setText(sernr[i + 1].getText());
                matnr[i].setText(matnr[i + 1].getText());
                back[i].setText(back[i + 1].getText());
            }
            if (status + 2 < MATNR.size()) {
                seq[4].setText(SEQ.get(status + 2));
                sernr[4].setText(SERNR.get(status + 2));
                matnr[4].setText(MATNR.get(status + 2));
                back[4].setText(YMII_BACK.get(status + 2));
            } else {
                seq[4].setText("");
                sernr[4].setText("");
                matnr[4].setText("");
                back[4].setText("");
            }
        }
        Print_First(chk, load_stat);
        print_clean(back);
        percent();
        index.setText((status + 1) + "/" + MATNR.size());
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!key_flag) {
                    key_down();
                } else {
                    key_flag = false;
                    Log.e("log_thread_test",Thread.currentThread().getName());
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!key_flag) {
                    key_down();
                    Log.e("log_thread_test",Thread.currentThread().getName());
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!key_flag) {
                    key_up();
                    Log.e("log_thread_test",Thread.currentThread().getName());
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!key_flag) {
                    key_up();
                    Log.e("log_thread_test",Thread.currentThread().getName());
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_HOME:
                Log.e("log_key", "key_home");
                break;
            case KeyEvent.KEYCODE_BACK:
                enter_flag = true;
                if (!DATEo.equals(realtime)) {
                    Log.e("log_order", "back to delete!");
                    Delete_table_not(realtime);
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
        new Handler().postDelayed(()->{
            app.Set_voice_stat(voice_stat);
        },500);
        return super.onKeyUp(keyCode, event);
    }


    private void enter_key(){
        String temp = load_stat.get(status);
        if (temp.equals(check)) {
            enter_flag = true;
            Intent intent = new Intent(this, ItemActivity.class);
            Bundle b = new Bundle();
            intent.putExtra("bundle", b);
            intent.putExtra("AUFNR", AUFNR.get(status).toString());
            intent.putExtra("mat", MATNR.get(status));
            intent.putExtra("LINEo", LINEo);
            intent.putExtra("PLANTo", PLANTo);
            intent.putExtra("IZONEo", ZONEo);
            intent.putExtra("TAKTo", TAKTo);
            intent.putExtra("IDo", IDo);
            intent.putExtra("PWo", PWo);
            intent.putExtra("load_index", load_index);
            intent.putExtra("status_order", status);
            intent.putExtra("getindex", putindex);
            intent.putExtra("DATEo", DATEo);
            intent.putStringArrayListExtra("AUFNR_order", AUFNR);
            intent.putStringArrayListExtra("PICK_SEQ", PICK_SEQ);
            intent.putStringArrayListExtra("load_stat", load_stat);
            intent.putStringArrayListExtra("Result", Result_item);
            intent.putExtra("order_qty", order_contact_data.size());
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
        Log.e("log_flow", "order onPause");
        enter_flag = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("log_flow", "order restart");
        UI_control();
        app.Set_voice_stat(voice_stat);
        if (DATEo.equals(realtime)) {
            getOrder();
            print_clean(back);
        }
        if(load_index < AUFNR.size() && putindex != AUFNR.size()){
            Log.e("log_flow", "thread restart "+backgroundThread.isInterrupted());
            backgroundThread = new BackgroundThread();
            backgroundThread.setRunning(true);
            backgroundThread.start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("log_flow", "order_destroy");
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
        if (DATEo.equals(realtime)) {
            Delete_table_not(realtime);
        }
        Log.e("log_flow", "thread destroy "+backgroundThread.isInterrupted());
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        Log.e("log_test_", request + " " + result);
        switch (request) {
            case 1:
                if (result == 1111) {
                    UI_control();
                    load_index = data.getIntExtra("load_index_item", 3333);  //현재 위치 알려줌
                    putindex = data.getIntExtra("putindex", 4444);//다운다됐는지 확인
                    load_stat = data.getStringArrayListExtra("load_stat");
                    if (putindex >= AUFNR.size() - 1) {   //다 다운받았을경우
                        enter_flag = true;
                        backgroundThread.setRunning(false);
                        onDestroy();
                    } else {
                        enter_flag = false;
                    }
                }
                break;
        }
    }

    private void handleMessage(Message msg) {
        Print_First(chk, load_stat);
        Log.e("log_voc_thread_test","handleMessage"+Thread.currentThread().getName());
    }

    public class BackgroundThread extends Thread { //TODO 대체방안 마련해요
        boolean running = false;

        void setRunning(boolean b) {
            running = b;
        }

        @Override
        public void run() {
            breakOut:
            while (running) {
                while (load_index < AUFNR.size() && putindex != AUFNR.size()) {
                    Log.e("log_load_index6", load_index + " interrupt: "+backgroundThread.isInterrupted());
                    if(putindex==AUFNR.size()){
                        backgroundThread.setRunning(false);
                        break breakOut;
                    }else if(enter_flag){
                        backgroundThread.setRunning(false);
                        break breakOut;
                    }
                    SoapObject countryDetails;
                    Log.e("log_load ", "1. 배열 인덱스: " + load_index);
                    Log.e("log_load ", "1. 배열 인덱스 크기: " + (AUFNR.size() - 1));
                    SoapObject input_params = new SoapObject(app.NAMESPACE, "InputParams");
                    SoapObject filter_sequence = new SoapObject(app.NAMESPACE, "InputSequence");
                    SoapObject request = new SoapObject(app.NAMESPACE, app.SOAP_METHOD);
                    filter_sequence.addProperty("OrderNo", AUFNR.get(load_index));//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Line", LINEo);//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Plant", PLANTo);//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Zone", ZONEo);//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Takt", TAKTo);//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("PIC_SEQ", PICK_SEQ.get(load_index));//전달 파라미터(변수명 값 입력해야함)
                    input_params.addSoapObject(filter_sequence);//전달 파라미터(변수명 값 입력해야함)
                    request.addProperty("LoginName", IDo);//전달 파라미터(변수명 값 입력해야함)
                    request.addProperty("LoginPassword", PWo);//전달 파라미터(변수명 값 입력해야함)
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
                        SoapObject hello_s;
                        hello_s = countryDetails;
                        Object property = hello_s.getProperty(0);
                        if (property instanceof SoapObject) {
                            SoapObject countryObj = (SoapObject) property;
                            String auf_test = countryObj.getProperty("AUFNR").toString();
                            if (auf_test == null) {
                                load_stat.set(load_index, "");
                                backgroundThread.setRunning(false);
                                break breakOut;
                            } else {
                                Log.e("log_load", "5b. 내용물 정상임" + auf_test);
                                Result.set(load_index, countryDetails);
                                load_stat.set(load_index, check);
                                app.SoapToArraylist_item(Result, load_index, item_dbAdapter, DATEo, itemStatusContact, item_status_dbAdapter);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("log_load ", "6. 캐치 인덱스: " + load_index);
                        load_stat.set(load_index, "0");
                        Result.set(load_index, "Need Download");
                        load_index--;
                    }
                    myHandler.sendMessage(myHandler.obtainMessage());
                    if (load_index == (AUFNR.size() - 1)) {
                        load_flag = true;
                    }
                    load_index++;
                }
                putindex = load_index;
            }
        }
    }

    private static class MyHandler extends Handler {
        // 핸들러 객체 만들기
        private final WeakReference<OrderActivity> mActivity;

        public MyHandler(OrderActivity activity) {
            mActivity = new WeakReference<OrderActivity>(activity);
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


