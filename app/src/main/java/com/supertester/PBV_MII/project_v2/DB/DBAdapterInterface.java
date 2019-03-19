package com.supertester.PBV_MII.project_v2.DB;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public interface DBAdapterInterface<Contact> {

    //생성자 2개
    boolean CreateTable();
    boolean DropTable();
    int OneTimeInsert(SQLiteDatabase db, ArrayList<String> data);
    int addContact();
    boolean isEmpty(String location, String data);
    ArrayList<Contact> getAllContacts(Contact temp);

    Contact getContact(Contact temp, String location_name, String location_value);
    boolean updateContact(String location, String data);
    boolean deleteContact(String location, String data);
}
