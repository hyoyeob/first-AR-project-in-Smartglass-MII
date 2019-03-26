package com.supertester.PBV_MII.project_v2.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
    Adapter에서 Helper 클래스로 어떻게 값들을 전달하는지 잘 모르겠음.
    그렇기 때문에 현재 HelperImpl 생성자를 통해서 DB 생성의 역할을 하고
    그 이외에는

    Creating Tables
    Craeting Table 있으나, 사용하지 않고, DBAdapter에서 테이블 생성.

    Upgrading database
    Upgrade 하고 싶으나, Adapter 클래스에서 해당 Helper 클래스로 어떻게 테이블 이름을 가져오는지 모르기에 보류.
 */
    public class HelperImpl extends SQLiteOpenHelper{
        private static String TAG = "DataBaseHelper"; //Logcat에 출력할 태그이름
        //디바이스 장치에서 데이터베이스의 경로
        private static String DB_PATH = "";
        private static String DB_NAME ="YourDbName"; // 데이터베이스 이름
        private SQLiteDatabase mDataBase;

    HelperImpl(Context context, String db_name, SQLiteDatabase.CursorFactory factory, int db_version) {
            super(context, db_name, factory, db_version);
            Log.e("DB 생성", "START");

        }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
