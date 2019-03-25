package com.supertester.PBV_MII.project_v2.ACT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.supertester.PBV_MII.project_v2.ASYNC.CallRemote_endpicking;
import com.supertester.PBV_MII.project_v2.CLASS.Std_Method;
import com.supertester.PBV_MII.project_v2.DB.Contacts.ItemContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.ItemStatusContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.OrderContact;
import com.supertester.PBV_MII.project_v2.DB.Contacts.OrderStatusContact;
import com.supertester.PBV_MII.project_v2.DB.DBAdapter;
import com.supertester.PBV_MII.project_v2.DB.Item;
import com.supertester.PBV_MII.project_v2.DB.User;
import com.supertester.PBV_MII.project_v2.R;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ItemActivity extends Activity implements Serializable {
    ItemActivity.BackgroundThread backgroundThread;
    boolean enter_flag = false;
    boolean key_flag;
    boolean item_crash_flag;
    boolean item_crash_flag2;

    int menu_count = 3;
    int order_index;

    TextView aufnr;
    TextView warning;
    TextView count;
    TextView Percent;
    TextView[] matnr_item;
    TextView[] lampos;
    TextView[] qty;
    TextView[] box_no;
    TextView[] index;
    RelativeLayout[] rl;
    ImageView voice_stat;

    ArrayList Result;


    Item item = new Item();
    User user = new User();
    Std_Method app;

    OrderStatusContact orderStatusContact = new OrderStatusContact();
    OrderContact orderContact = new OrderContact();
    ItemStatusContact itemStatusContact = new ItemStatusContact();
    ItemContact itemContact = new ItemContact();

    DBAdapter<OrderStatusContact> order_status_dbAdapter;
    DBAdapter<OrderContact> order_dbAdapter;
    DBAdapter<ItemStatusContact> item_status_dbAdapter;
    DBAdapter<ItemContact> item_dbAdapter;
    LinearLayout linear_h;

    int item_status = 0;
    int item_qty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        InitAdapter();
        ready_to_load();
        InitStatusNumber();
        First_Print();
        XmasReplace();
        backgroundThread = new BackgroundThread();
        backgroundThread.setRunning(true);
        backgroundThread.start();
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


    private void First_Print() {
        for (int i = 0; i < menu_count; i++) {
            if (item_status + menu_count <= item.getITEM_MATNR().size()) {
                key_control_print(item_status + i, 1 + i);
                rl[i + 1].setVisibility(View.VISIBLE);
            } else if (i < item.getITEM_MATNR().size() % menu_count) {
                key_control_print(item_status + i, 1 + i);
                rl[i + 1].setVisibility(View.VISIBLE);
            } else {
                empty_last(i + 1);
                rl[i + 1].setVisibility(View.INVISIBLE);
            }
        }

        aufnr.setText(item.getMATNR_NAME());
        count.setText(getString(R.string.StringInt, "Total:", item.getITEM_MATNR().size()));
        print_N(item_status);  //현재 N인 친구들 모두 출력
        percent();
    }

    private void empty_last(int i) {
        matnr_item[i].setText("");
        lampos[i].setText("");
        index[i].setText("");
        box_no[i].setText("");
        qty[i].setText("");
    }

    private void InitStatusNumber() {
        int item_success;
        int order_success;
        ItemStatusContact tempContact = item_status_dbAdapter.getContact(new ItemStatusContact(), "AUFNR", item.getORDER_NAME());

        item_status = Integer.parseInt(tempContact.getProperty(1));
        Log.e("InitSuccessNumber", item_status + "      ");

        item_qty = Integer.parseInt(item_status_dbAdapter.getContact(new ItemStatusContact(), "AUFNR", item.getORDER_NAME()).getProperty(3));
        item_success = item_dbAdapter.getConditionCount(new ItemContact(), "STATUS", "Y");
        order_success = order_dbAdapter.getConditionCount(new OrderContact(), "STATUS", "Y");
        Log.e("InitSuccessNumber", item_qty + "      " + item_success + "    " + order_success);
    }

    private void InitAdapter() {
        UI_control();
        app = (Std_Method) getApplicationContext();
        voice_stat = findViewById(R.id.voice_status);
        app.Set_voice_stat(voice_stat);
        order_status_dbAdapter = new DBAdapter<>(getApplicationContext(), orderStatusContact);
        order_dbAdapter = new DBAdapter<>(getApplicationContext(), orderContact);
        item_status_dbAdapter = new DBAdapter<>(getApplicationContext(), itemStatusContact);
        item_dbAdapter = new DBAdapter<>(getApplicationContext(), itemContact);
    }

    //ORDER_STATUS 값의 갱신을 위해서 존재.
    //그래서 파라미터가
    private void ItemUpdate(int no, String aufnr, String lampos) {//lampos로
        SQLiteDatabase db = item_dbAdapter.mDBHelper.getWritableDatabase();
        try {
            String query = "UPDATE ItemContact SET STATUS = 'Y' where AUFNR = '" + aufnr + "' AND LAMPOS = '" + lampos + "'";
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        item.getITEM_STATUS().set(no, "Y");
    }

    private void OrderUpdate(String aufnr) {//lampos로
        SQLiteDatabase db = order_dbAdapter.mDBHelper.getWritableDatabase();
        try {
            String query = "UPDATE OrderContact SET STATUS = 'Y' where AUFNR = '" + aufnr + "'";
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    //현재 아이템 스테이터스 이전의 모든 Y가 아닌 값들 출력
    private void print_N(int status) {
        warning.setText("");
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < status; i++) {
            if (!item.getITEM_STATUS().get(i).equals("Y") && index.size() < 5) {
                index.add(i + 1);
                warning.append((i + 1) + " ");
            } else if (!item.getITEM_STATUS().get(i).equals("Y") && index.size() >= 5) {
                index.add(i + 1);
                warning.setText("...");
                for (int j = 5; j > 0; j--) {
                    warning.append(index.get(index.size() - j) + " ");
                }
            }
        }
        Log.e("2. printN ORDER_STA", item.getITEM_STATUS() + "");
    }

    private void WarningCheck(int status) {
        int count = 0;
        for (int i = 0; i < status; i++) {
            if (item.getITEM_STATUS().get(i).equals("Y")) {
                count++;
            }
        }
        Log.e("3. WarningChk ORDER_STA", count + "/" + item.getITEM_MATNR().size() + "  " + item.getITEM_STATUS() + " ");
        if (count == item.getITEM_MATNR().size()) {
            SendSuccessToMII();//다되면 현재오더보냄
        }
    }

    //item_status_dbAdapter.updateContact 에서 contact의 property 값이 계속해서 null로 나와서 그냥 하드코딩함.
    private void ItemStatusUpdate() {
        String convert_value = String.valueOf(item_status);
        itemStatusContact = item_status_dbAdapter.getContact(new ItemStatusContact(), "AUFNR", item.getORDER_NAME());
        try {
            ArrayList<String> str = new ArrayList<>(itemStatusContact.getProperties());
            int location = itemStatusContact.getProperties_name().indexOf("ITEM_STATUS");
            if (location != -1) {
                Log.e("item_log_Convert_value", convert_value);
                str.set(location, convert_value);
                itemStatusContact.setProperties(str);
                SQLiteDatabase db = item_status_dbAdapter.mDBHelper.getWritableDatabase();
                try {
                    ContentValues values = new ContentValues();
                    for (int i = 0; i < itemStatusContact.getLength(); i++) {
                        values.put(itemStatusContact.getProperty_name(i), itemStatusContact.getProperty(i));
                    }
                    db.update(itemStatusContact.getTable_name(),
                            values,
                            "AUFNR = ?",
                            new String[]{itemStatusContact.getAUFNR()});
                    db.close();
                } catch (Exception e) {
                    db.close();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void percent() {//특정 위치 색변환 시키는 함수, 퍼센트 생성
        String percent = "";
        String result_percent;
        int i;
        for (i = 0; i < item.getITEM_MATNR().size(); i++) {      // 퍼센트 초기화
            result_percent = percent.concat("l");
            percent = result_percent;
        }
        SpannableString spercent = new SpannableString(percent);
        for (i = 0; i < item.getITEM_MATNR().size(); i++) {      // 퍼센트 초기화
            if (i < item_status && !item.getITEM_STATUS().get(i).equals("Y")) {
                spercent.setSpan(new ForegroundColorSpan(Color.RED), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (i == item_status) {
                if (item_status + menu_count <= item.getITEM_MATNR().size()) {
                    spercent.setSpan(new ForegroundColorSpan(Color.BLACK), i, i + menu_count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    for (int j = 0; j < item.getITEM_MATNR().size() % menu_count; j++) {
                        spercent.setSpan(new ForegroundColorSpan(Color.BLACK), i, i + j + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } else if (item.getITEM_STATUS().get(i).equals("Y") && (i > (item_status + menu_count - 1) || i < item_status)) {
                spercent.setSpan(new ForegroundColorSpan(Color.GREEN), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        Percent.setText(spercent);
    }

    private void ready_to_load() {
        user = (User) getIntent().getSerializableExtra("userInfo");
        item = (Item) getIntent().getSerializableExtra("item");
        Result = getIntent().getStringArrayListExtra("Result");
        order_index = Objects.requireNonNull(getIntent().getExtras()).getInt("order_index");
        ArrayList<ItemContact> item_contact_data = item_dbAdapter.getConditionContacts(new ItemContact(), "AUFNR", item.getORDER_NAME()); //DB값 호출.

        for (int i = 0; i < item_contact_data.size(); i++) {
            int loop_stat;
            if (i < item_contact_data.size() - 2 && (item_contact_data.get(i).getLAMPOS().equals(item_contact_data.get(i + 1).getLAMPOS()))) {
                if (i < item_contact_data.size() - 3 && (item_contact_data.get(i + 1).getLAMPOS().equals(item_contact_data.get(i + 2).getLAMPOS()))) {
                    if (i < item_contact_data.size() - 4 && (item_contact_data.get(i + 2).getLAMPOS().equals(item_contact_data.get(i + 3).getLAMPOS()))) {
                        if (i < item_contact_data.size() - 5 && (item_contact_data.get(i + 3).getLAMPOS().equals(item_contact_data.get(i + 4).getLAMPOS()))) {
                            loop_stat = 5;
                        } else loop_stat = 4;
                    } else loop_stat = 3;
                } else loop_stat = 2;
            } else loop_stat = 1;

            switch (loop_stat) {
                case 1:
                    lampos_side_check(i, item_contact_data, loop_stat);
                    break;
                case 2:
                    lampos_side_check(i, item_contact_data, loop_stat);
                    i += (loop_stat - 1);
                    break;
                case 3:
                    lampos_side_check(i, item_contact_data, loop_stat);
                    i += (loop_stat - 1);
                    break;
                case 4:
                    lampos_side_check(i, item_contact_data, loop_stat);
                    i += (loop_stat - 1);
                    break;
                case 5:
                    lampos_side_check(i, item_contact_data, loop_stat);
                    i += (loop_stat - 1);
                    break;
            }

            item.getLAMPOS().add(item_contact_data.get(i).getLAMPOS());
            item.getMAKTX().add(item_contact_data.get(i).getMAKTX());
            item.getITEM_MATNR().add(item_contact_data.get(i).getMATNR());
            item.getITEM_STATUS().add(item_contact_data.get(i).getSTATUS());
            item.getTOT_QTY().add(item_contact_data.get(i).getTOT_QTY());
        }

        item_qty = item.getITEM_MATNR().size();
        aufnr = findViewById(R.id.aufnr);
        count = findViewById(R.id.count);
        warning = findViewById(R.id.warning);

        lampos = new TextView[menu_count + 1];
        matnr_item = new TextView[menu_count + 1];
        qty = new TextView[menu_count + 1];
        box_no = new TextView[menu_count + 1];
        index = new TextView[menu_count + 1];
        rl = new RelativeLayout[menu_count + 1];

        for (int i = 1; i < menu_count + 1; i++) {
            int getID;
            getID = getResources().getIdentifier("lampos" + i, "id", getApplicationContext().getPackageName());
            lampos[i] = findViewById(getID);
            getID = getResources().getIdentifier("mat_nr" + i, "id", getApplicationContext().getPackageName());
            matnr_item[i] = findViewById(getID);
            getID = getResources().getIdentifier("rem" + i, "id", getApplicationContext().getPackageName());
            qty[i] = findViewById(getID);
            getID = getResources().getIdentifier("box_no" + i, "id", getApplicationContext().getPackageName());
            box_no[i] = findViewById(getID);
            getID = getResources().getIdentifier("index" + i, "id", getApplicationContext().getPackageName());
            index[i] = findViewById(getID);
            getID = getResources().getIdentifier("rl" + i, "id", getApplicationContext().getPackageName());
            rl[i] = findViewById(getID);
        }

        Percent = findViewById(R.id.percent);
        item_status = 0;
    }

    private void lampos_side_check(int i, ArrayList<ItemContact> item_contact_data, int loop_status) {//램포스를 살펴 연속되는 것 찾아서 플래그 삽입
        ArrayList<String> temp_box = new ArrayList<>();
        ArrayList<Integer> temp_qty = new ArrayList<>();
        int j = 0;

        while (j < loop_status) {
            temp_box.add(item_contact_data.get(i + j).getBOX_NO());
            temp_qty.add(Integer.parseInt(item_contact_data.get(i + j).getQTY()));
            j++;
        }
        item.getBOX_NO().add(temp_box);
        item.getQTY().add(temp_qty);
    }

    private void SendSuccessToMII() { //MII로 완료된 오더 전송, 이전에 논리 필요함
        CallRemote_endpicking ce = new CallRemote_endpicking();
        String pick = item.getPICK_SEQ().get(order_index);
        AsyncTask<String, String, SoapObject> at = ce.execute(pick, user.getID(), user.getPW());
        try {
            SoapObject s;
            s = at.get();
            Log.e("end_picking", s + "");
            OrderUpdate(item.getORDER_NAME());
            enter_flag = true;
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("load_index_item", item.getLOAD_INDEX());  //현재 위치 알려줌
            intent.putExtra("limit_index", item.getLIMIT_INDEX());//다운 다되면 가득참
            intent.putStringArrayListExtra("load_stat", item.getLOAD_STATUS());
            setResult(1111, intent);
            app.PrintToastMessage("Finish task");
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void XmasReplace() {
        for (int i = 0; i < menu_count; i++) {
            if (item_status + menu_count <= item.getITEM_MATNR().size()) {
                xmas_2(item_status + i, 1 + i);
                rl[i + 1].setVisibility(View.VISIBLE);
            } else if (i < item.getITEM_MATNR().size() % menu_count) {
                xmas_2(item_status + i, 1 + i);
                rl[i + 1].setVisibility(View.VISIBLE);
            } else {
                empty_last(i + 1);
                rl[i + 1].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void xmas_2(int item_status, int num) {
        for (int i = 0; i < item.getQTY().get(item_status).size(); i++) {
            if (item.getQTY().get(item_status).size() == 1) {
                if (item.getQTY().get(item_status).get(i) > 9)
                    qty[num].setText(getString(R.string.IntString, item.getQTY().get(item_status).get(i), "\t "));
                else
                    qty[num].setText(getString(R.string.IntString, item.getQTY().get(item_status).get(i), "\t\t "));
                box_no[num].setText(item.getBOX_NO().get(item_status).get(i));
            } else if (i == 0) {//젤처음
                if (item.getQTY().get(item_status).get(i) > 9)
                    qty[num].setText(getString(R.string.IntString, item.getQTY().get(item_status).get(i), "\t  "));
                else
                    qty[num].setText(getString(R.string.IntString, item.getQTY().get(item_status).get(i), "\t\t  "));
                box_no[num].setText(getString(R.string.TwoString, item.getBOX_NO().get(item_status).get(i), "\t  "));
            } else if (i == item.getQTY().get(item_status).size() - 1) {
                if (item.getQTY().get(item_status).get(i) > 9)
                    qty[num].append(item.getQTY().get(item_status).get(i) + "");
                else
                    qty[num].append(item.getQTY().get(item_status).get(i) + "\t");
                box_no[num].append(item.getBOX_NO().get(item_status).get(i));
            } else {
                if (item.getQTY().get(item_status).get(i) > 9)
                    qty[num].append(item.getQTY().get(item_status).get(i) + "\t ");
                else
                    qty[num].append(item.getQTY().get(item_status).get(i) + "\t\t ");
                box_no[num].append(item.getBOX_NO().get(item_status).get(i) + "\t ");
            }
        }
    }

    private void key_control(int i) {
        item_crash_flag = true;//트루에서 로드못하게 해야함
        item_status = item_status + i;
        if (!item_crash_flag2) {
            ItemStatusUpdate();
        }
        item_crash_flag = false;
        print_N(item_status);  //현재 N인 친구들 모두 출력

        for (int j = 0; j < menu_count; j++) {
            if (item_status + menu_count <= item.getITEM_MATNR().size()) {
                key_control_print(item_status + j, 1 + j);
                rl[j + 1].setVisibility(View.VISIBLE);
            } else if (j < item.getITEM_MATNR().size() % menu_count) {
                key_control_print(item_status + j, 1 + j);
                rl[j + 1].setVisibility(View.VISIBLE);
            } else {
                empty_last(j + 1);
                rl[j + 1].setVisibility(View.INVISIBLE);
            }
        }
        percent();
        XmasReplace();
    }

    private void key_control_print(int item_status, int num) {
        matnr_item[num].setText(item.getITEM_MATNR().get(item_status));
        if (item.getLAMPOS().get(item_status).length() == 5)
            lampos[num].setText(item.getLAMPOS().get(item_status));
        else lampos[num].setText(item.getMAKTX().get(item_status));
        index[num].setText(String.valueOf(item_status + 1));

        BoxColorSet(item_status, num);
    }

    private void BoxColorSet(int item_status, int num) {
        if (item_status % 2 == 1) {
            matnr_item[num].setBackgroundResource(R.drawable.border4);
            lampos[num].setBackgroundResource(R.drawable.border4);
            qty[num].setBackgroundResource(R.drawable.border4);
            box_no[num].setBackgroundResource(R.drawable.border4);
        } else {
            matnr_item[num].setBackgroundResource(R.drawable.border1);
            lampos[num].setBackgroundResource(R.drawable.border1);
            qty[num].setBackgroundResource(R.drawable.border1);
            box_no[num].setBackgroundResource(R.drawable.border1);
        }
    }


    private void key_down_left() {
        if (key_flag) {
            try {
                if (item_status > 0) {
                    key_control(-menu_count);
                    Thread.sleep(200);
                }
            } catch (InterruptedException ignore) {
            }
        }
    }

    private void key_down_right() {
        if (key_flag) {
            try {
                if (item_status + menu_count < item.getITEM_MATNR().size()) {
                    key_control(menu_count);
                    Thread.sleep(200);
                }
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("getkey", event.getKeyCode() + "");
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            event.startTracking();
            key_down_left();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            event.startTracking();
            key_down_right();
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


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:    //status down key
                if (!key_flag) {
                    if (item_status > 0) {
                        key_control(-menu_count);
                    }
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!key_flag) {
                    if (item_status + menu_count < item.getITEM_MATNR().size()) {
                        key_control(menu_count);
                    }
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN: //다른 기능 넣을 수 있으면 넣기
                if (!key_flag) {
                    if (item_status + menu_count < item.getITEM_MATNR().size()) {
                        key_control(menu_count);
                    }
                } else {
                    key_flag = false;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:    //status down key
                callDialog();
                break;
            case KeyEvent.KEYCODE_SPACE:
                key_enter();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER: //작업 완료.
                key_enter();
                break;
            case KeyEvent.KEYCODE_BACK:
                enter_flag = true;
                Intent intent = new Intent(this, OrderActivity.class);
                intent.putExtra("load_index_item", item.getLOAD_INDEX());  //현재 위치 알려줌
                intent.putExtra("limit_index", item.getLIMIT_INDEX());//다운 다되면 가득참
                intent.putStringArrayListExtra("load_stat", item.getLOAD_STATUS());
                setResult(1111, intent);
                finish();
                break;
        }
        app.key_voice_control(keyCode);
        new Handler().postDelayed(() -> app.Set_voice_stat(voice_stat), 500);
        return super.onKeyUp(keyCode, event);
    }

    private void key_enter() {
        if (item_status + menu_count < item.getITEM_MATNR().size()) { //여유있을때
            if (!item.getITEM_STATUS().get(item_status).equals("Y")) {
                item_crash_flag = true;//트루에서 로드못하게 해야함
                for (int i = 0; i < menu_count; i++) {
                    ItemUpdate(item_status + i, item.getORDER_NAME(), item.getLAMPOS().get(item_status + i));  //아이템 스테이터스 Y로
                }
                key_control(menu_count);     //UI 동작
                WarningCheck(item_status);
            } else {
                key_control(menu_count);     //UI 동작
            }
        } else if (item_status + menu_count == item.getITEM_MATNR().size()) { //딱맞을때
            item_crash_flag = true;//트루에서 로드못하게 해야함
            for (int i = 0; i < menu_count; i++) {
                ItemUpdate(item_status + i, item.getORDER_NAME(), item.getLAMPOS().get(item_status + i));  //아이템 스테이터스 Y로
            }
            item_crash_flag = false;//트루에서 로드못하게 해야함
            print_N(item_status);  //현재 N인 친구들 모두 출력
            WarningCheck(item_status + menu_count);
        } else {
            item_crash_flag = true;//트루에서 로드못하게 해야함
            for (int i = 0; i < item.getITEM_MATNR().size() % menu_count; i++) {
                ItemUpdate(item_status + i, item.getORDER_NAME(), item.getLAMPOS().get(item_status + i));  //아이템 스테이터스 Y로
            }
            item_crash_flag = false;//트루에서 로드못하게 해야함
            print_N(item_status);  //현재 N인 친구들 모두 출력
            WarningCheck(item_status + item.getITEM_MATNR().size() % menu_count);
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            key_flag = true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            key_flag = true;
        }
        return super.onKeyLongPress(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        UI_control();
        app.Set_voice_stat(voice_stat);
        backgroundThread = new BackgroundThread();
        backgroundThread.setRunning(true);
        backgroundThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("log_voc_set_flow", "item_destroy");
        boolean retry = true;
        UI_control();
        backgroundThread.setRunning(false);
        while (retry) {
            try {
                backgroundThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void callDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("List of boxes required");
        ArrayList<String> message;
        message = item_dbAdapter.getContactBoxNumber("BOX_NO", "AUFNR", getIntent().getStringExtra("AUFNR"));
        Collections.sort(message);
        builder.setMessage(message.toString());
        builder.setPositiveButton("예",
                (dialog, which) -> UI_control());
        AlertDialog alert = builder.create();
        alert.show();
        Objects.requireNonNull(alert.getWindow()).getAttributes();
        TextView textView = alert.findViewById(android.R.id.message);
        textView.setTextSize(22);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UI_control();
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
                    if (enter_flag) {
                        backgroundThread.setRunning(false);
                        break breakOut;
                    }
                    SoapObject countryDetails;
                    Log.e("log_load ", "1. 배열 인덱스: " + item.getLOAD_INDEX());
                    SoapObject input_params = new SoapObject(app.NAMESPACE, "InputParams");
                    SoapObject filter_sequence = new SoapObject(app.NAMESPACE, "InputSequence");
                    SoapObject request = new SoapObject(app.NAMESPACE, app.SOAP_METHOD);
                    filter_sequence.addProperty("OrderNo", item.getLOAD_ORDER());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Line", user.getLINE());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Plant", user.getPLANT());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Zone", user.getZONE());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("Takt", user.getTAKT());//전달 파라미터(변수명 값 입력해야함)
                    filter_sequence.addProperty("PIC_SEQ", item.getLOAD_SEQ());//전달 파라미터(변수명 값 입력해야함)
                    input_params.addSoapObject(filter_sequence);//전달 파라미터(변수명 값 입력해야함)
                    request.addProperty("LoginName", "XCEMII01");//전달 파라미터(변수명 값 입력해야함)
                    request.addProperty("LoginPassword", "init1234");//전달 파라미터(변수명 값 입력해야함)
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
                        if (!item_crash_flag) {
                            item_crash_flag2 = true;
                            Result.set(item.getLOAD_INDEX(), countryDetails);
                            item.getLOAD_STATUS().set(item.getLOAD_INDEX(), "V");
                            Log.e("log_load", "load_index: " + item.getLOAD_INDEX() + "/ date: " + user.getGETTIME());
                            app.SoapToArraylist_item(Result, item.getLOAD_INDEX(), item_dbAdapter, user.getGETTIME(), itemStatusContact, item_status_dbAdapter);
                            item_crash_flag2 = false;
                        } else {
                            Result.set(item.getLOAD_INDEX(), "");
                            item.setLOAD_INDEX(item.getLOAD_INDEX() - 1);
                        }
                    } catch (Exception e) {
                        item.getLOAD_STATUS().set(item.getLOAD_INDEX(), "0");
                        Result.set(item.getLOAD_INDEX(), "");
                        item.setLOAD_INDEX(item.getLOAD_INDEX() - 1);
                        e.printStackTrace();
                    }
                    item.setLOAD_INDEX(item.getLOAD_INDEX() + 1);
                }
                item.setLIMIT_INDEX(item.getLOAD_INDEX());
            }
        }
    }
}
