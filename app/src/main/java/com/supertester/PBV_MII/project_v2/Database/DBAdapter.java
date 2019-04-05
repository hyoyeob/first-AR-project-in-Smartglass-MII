package com.supertester.PBV_MII.project_v2.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DBAdapter<Contact extends com.supertester.PBV_MII.project_v2.Database.Contact> implements DBAdapterInterface<Contact> {

    private static final int ORDER_TABLE = 0;
    private static final int ITEM_TABLE = 1;
    private static final int ORDER_STATUS_TABLE = 2;
    private static final int ITEM_STATUS_TABLE = 3;

    private Class<Contact> temp_contact;
    private Contact contact = null;
    public HelperImpl mDBHelper;
    private String TABLE_NAME;

    int column_count = 0;

    public DBAdapter(Context context, Contact contact) {
        String DB_NAME = "ASD";
        Init(context, contact);
        int DB_VERSION = 1;
        //mDBHelper 값들.
        SQLiteDatabase.CursorFactory FACTORY = null;
        mDBHelper = new HelperImpl(context, DB_NAME, FACTORY, DB_VERSION);
        CreateTable();
    }

    //OrderStatusContact
    //1. 해당 날짜에 따른 필드값 조회.
    //2. 필드값 없을 경우 DB 저장 이후 DB에서 값 꺼내서 참조.

    //OrderContact
    //1. DATE에 따른 OrderContact 조회.
    //2. 필드값 존재시 OrderContact값 바로 참조.
    //2. 필드값 없을 경우 DB 저장 이후 DB에서 값 꺼내서 참조.

    //ItemStatusContact
    //1. 해당 AUFNR에 따른 필드값 조회.
    //...

    //ItemContact
    //1. AUFNR에 따른 ItemContact 조회.
    //2. 필드값 존재시 ItemContact에서 값 바로 참조.
    //2. 필드값 없을 경우 DB에 저장 이후 DB에서 값 꺼내서 참조.


    //인자 2개인 생성자는 CreateTable을 항상 불러오는데, 이것이 에러를 유발하기에 새로운 생성자를 만듬.
    //기본적으로 false 값을 줌.
//    public DBAdapter(Context context, Contact contact, boolean status) {
//        Init(context, contact);
//        mDBHelper = new HelperImpl(context, DB_NAME, FACTORY, DB_VERSION);
//    }

    private void Init(Context context, Contact contact) {
        this.contact = contact;
        this.TABLE_NAME = this.contact.getTable_name();
    }

    @Override
    public void CreateTable() {
        Log.e("CreateTable", "START");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        try {
            String CREATE_CONTACTS_TABLE = contact.getCREATE_CONTACTS_TABLE();

            Log.e("QWEQWE", CREATE_CONTACTS_TABLE);

            db.execSQL(CREATE_CONTACTS_TABLE);
            db.close();
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
        }
    }


    @Override
    public void DropTable() {
        Log.e("DropTable", "START");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        try {

            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            db.close();
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
        }
    }

    //함수를 호출하기 전과 후에는
    //db.beginTransaction();
    //db.setTransactionSuccessful();
    //db.endTransaction();
    //3가지를 꼭 호출해야함
    @Override
    public void OneTimeInsert(SQLiteDatabase db, ArrayList<String> data) {
        try {

            ContentValues cv = new ContentValues();
            for (int i = 0; i < contact.getLength(); i++) {
                cv.put(contact.getProperty_name(i), data.get(i));
            }
            db.insert(TABLE_NAME, null, cv);
        } catch (SQLiteConstraintException e) {


        } catch (Exception e) {
        }
    }

    @Override
    public void addContact() {
        Log.e("addContact", "START");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();

            ArrayList<String> n = contact.getProperties();
            for (int i = 0; i < n.size(); i++) {
                values.put(contact.getProperty_name(i), contact.getProperty(i));
            }
            // Inserting Row
            db.insert(TABLE_NAME, null, values);
            db.close(); // Closing database connection
        } catch (SQLiteConstraintException e) {
            db.close(); // Closing database connection
            e.printStackTrace();
        } catch (Exception e) {
            db.close(); // Closing database connection
            e.printStackTrace();
        }
    }

    public int getConditionCount(Contact temp, String location_name, String location_value) {
        Log.e("getConditionContacts", "START");
        int result;
        String selectQuery;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        if (location_name.equals("DATE")) {
            selectQuery = "SELECT count(*) FROM " + TABLE_NAME + " WHERE " + location_name + " = '" + location_value + "'";
        } else {
            selectQuery = "SELECT DISTINCT count(*) FROM " + TABLE_NAME + " WHERE " + location_name + " = '" + location_value + "'";
        }
        Log.e("log_date", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                return Integer.parseInt(cursor.getString(0));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            cursor.close();
            db.close();
            e.printStackTrace();
            return -1;
        }

        result = cursor.getCount();
        db.close();
        cursor.close();
        return result;
    }

    public ArrayList<Contact> getConditionContacts(Contact temp, String location_name, String location_value) {
        Log.e("getConditionContacts", "START");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();
        String selectQuery;
        if (location_name.equals("DATE")) {
            selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + location_name + " = '" + location_value + "'";
        } else {
            selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME + " WHERE " + location_name + " = '" + location_value + "' ORDER BY LAMPOS ASC";
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("getConditionContacts", selectQuery);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ArrayList<String> str = new ArrayList<>();
                    for (int j = 0; j < contact.getLength(); j++) {
                        str.add(cursor.getString(j));
                    }
                    Contact c = (Contact) temp.getClass().newInstance();
                    c.setProperties(str);
                    contacts.add(c);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return contacts;
        } catch (Exception e) {
            cursor.close();
            db.close();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isEmpty(String location, String data) {
//        Log.e("isEmpty", "START");

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " Where " + location + " = " + "'" + data + "'";

//        Log.e("ASDASD", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    //파라미터 Contact temp 은 제네릭 변수값의 인스턴스를 돕기 위해서 존재.
    @Override
    public ArrayList<Contact> getAllContacts(Contact temp) {
        String selectQuery;
        Log.e("getAllContacts", "START");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();
        if (TABLE_NAME.equals("OrderContact")) {
            selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY SEQ";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_NAME;
        }
        Log.e("log_load_query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    ArrayList<String> str = new ArrayList<>();
                    for (int j = 0; j < contact.getLength(); j++) {
                        str.add(cursor.getString(j));
                    }
                    Contact c = (Contact) temp.getClass().newInstance();
                    c.setProperties(str);
                    contacts.add(c);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return contacts;
        } catch (Exception e) {
            cursor.close();
            db.close();
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getContactBoxNumber(String field, String location_name, String location_value) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String selectQuery = "SELECT DISTINCT " + field + " FROM " + TABLE_NAME
                + " WHERE " + location_name + " = '" + location_value + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            ArrayList<String> c = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    c.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return c;
        } catch (Exception e) {
            cursor.close();
            db.close();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Contact getContact(Contact temp, String location_name, String location_value) {

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME
                + " WHERE " + location_name + " = '" + location_value + "'";


        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("log_load_getContact", selectQuery + "");

        try {
            Contact c = (Contact) temp.getClass().newInstance();

            if (cursor.moveToFirst()) {
                ArrayList<String> str = new ArrayList<>();
                for (int j = 0; j < contact.getLength(); j++) {
                    str.add(cursor.getString(j));
                }
                c.setProperties(str);
            }
            cursor.close();
            db.close();
            return c;
        } catch (Exception e) {
            cursor.close();
            db.close();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateContact(String location_name, String location_value) {
        Log.e("updateContact", "START");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            for (int i = 0; i < contact.getLength(); i++) {
                values.put(contact.getProperty_name(i), contact.getProperty(i));
            }

            db.update(contact.getTable_name(),
                    values,
                    location_name + " = ?",
                    new String[]{location_value});
            db.close();
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
        }
    }

    @Override
    public void deleteContact(String location, String data) {
        Log.e("deleteContact", "START");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String query = "";
        try {
            if (TABLE_NAME.equals("ItemStatusContact") || TABLE_NAME.equals("ItemContact")) {
                query = "DELETE FROM " + TABLE_NAME +
                        " WHERE AUFNR = (SELECT AUFNR FROM OrderContact WHERE DATE = '" + data + "')";
                Log.e("log_date_del", TABLE_NAME + " " + query);
                db.execSQL(query);
            } else if (TABLE_NAME.equals("OrderContact") || TABLE_NAME.equals("OrderStatusContact")) {
                query = "DELETE FROM " + TABLE_NAME
                        + " WHERE DATE = '" + data + "'";
                Log.e("log_date_del", TABLE_NAME + " " + query);
                db.execSQL(query);
            }
            Log.e("log_date_del", query);
            db.close();
        } catch (Exception e) {
            Log.e("log_date_del", "fail");
            db.close();
        }
    }

    public void deleteContact_not(String data) {
        Log.e("deleteContact", "START");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            if (TABLE_NAME.equals("ItemStatusContact") || TABLE_NAME.equals("ItemContact")) {
                String query = "DELETE FROM " + TABLE_NAME +
                        " WHERE AUFNR = (SELECT AUFNR FROM OrderContact WHERE NOT DATE = '" + data + "')";
                Log.e("log_date_del", TABLE_NAME + " " + query);
                db.execSQL(query);
            } else if (TABLE_NAME.equals("OrderContact") || TABLE_NAME.equals("OrderStatusContact")) {
                String query = "DELETE FROM " + TABLE_NAME
                        + " WHERE NOT DATE = '" + data + "'";
                Log.e("log_date_del", TABLE_NAME + " " + query);
                db.execSQL(query);
            }
            db.close();
        } catch (Exception e) {
            Log.e("log_date_del", "fail");
            db.close();
        }
    }
}