package com.supertester.PBV_MII.project_v2.Database.Contacts;


import com.supertester.PBV_MII.project_v2.Database.Contact;

import java.util.ArrayList;
import java.util.Arrays;

/*
    당장에 AUFNR 값은 참조키로 OrderContact의 AUFNR 값을 참조한다.
    ItemStatusContact에서 AUFNR 값은 고유값으로 작용한다.
    이는 기본키가 아니어서 AUFNR값이 중복되어서 들어올 수도 있으니 주의해서 Insert, Update 하도록 하자.
 */
public class ItemStatusContact extends Contact {

    private String AUFNR = "AUFNR";
    private String ITEM_STATUS = "ITEM_STATUS";
    private String SUCCESS_NUMBER = "SUCCESS_NUMBER";
    private String ITEM_QTY = "ITEM_QTY";


    public ItemStatusContact() {
        super();
        super.table_name = "ItemStatusContact";
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS ItemStatusContact("
                + AUFNR + " VARCHAR(100) , "
                + ITEM_STATUS + " VARCHAR(100), "
                + SUCCESS_NUMBER + " VARCHAR(100),"
                + ITEM_QTY + " VARCHAR(100),"
                + "FOREIGN KEY(AUFNR) REFERENCES OrderContact(AUFNR)"
                + ")";
        super.setCREATE_CONTACTS_TABLE(CREATE_CONTACTS_TABLE);
        ArrayList<String> property_name = new ArrayList<>(Arrays.asList(AUFNR, ITEM_STATUS, SUCCESS_NUMBER, ITEM_QTY));
        super.setProperty_name(property_name);
        int length = property_name.size();
        super.setLength(length);
    }

    public void setProperties(ArrayList<String> data) {
        super.setProperties(data);

        AUFNR = data.get(0);
        ITEM_STATUS = data.get(1);
        SUCCESS_NUMBER = data.get(2);
        ITEM_QTY = data.get(3);
    }

    public String getAUFNR() {
        return AUFNR;
    }

}