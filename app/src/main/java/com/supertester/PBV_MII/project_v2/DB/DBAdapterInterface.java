package com.supertester.PBV_MII.project_v2.DB;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public interface DBAdapterInterface<Contact> {

    //생성자 2개
    void CreateTable();

    void DropTable();

    void OneTimeInsert(SQLiteDatabase db, ArrayList<String> data);

    void addContact();
    boolean isEmpty(String location, String data);
    ArrayList<Contact> getAllContacts(Contact temp);

    Contact getContact(Contact temp, String location_name, String location_value);

    void updateContact(String location, String data);

    void deleteContact(String location, String data);
}
