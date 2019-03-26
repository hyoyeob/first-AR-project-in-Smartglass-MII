package com.supertester.PBV_MII.project_v2.Database.Contacts;


import com.supertester.PBV_MII.project_v2.Database.Contact;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderStatusContact extends Contact {

    private String DATE = "DATE";
    private String LOAD_TIME = "LOAD_TIME";
    private String ORDER_STATUS = "ORDER_STATUS";
    private String SUCCESS_NUMBER = "SUCCESS_NUMBER";
    private String ORDER_QTY = "ORDER_QTY";

    public OrderStatusContact() {
        super();
        super.table_name = "OrderStatusContact";
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS OrderStatusContact("
                + DATE + " VARCHAR(100) PRIMARY KEY, "
                + ORDER_STATUS + " VARCHAR(100), "
                + SUCCESS_NUMBER + " VARCHAR(100),"
                + ORDER_QTY + " VARCHAR(100),"
                + LOAD_TIME + " VARCHAR(100))";
        super.setCREATE_CONTACTS_TABLE(CREATE_CONTACTS_TABLE);
        ArrayList<String> property_name = new ArrayList<>(Arrays.asList(DATE, ORDER_STATUS, SUCCESS_NUMBER, ORDER_QTY, LOAD_TIME));
        super.setProperty_name(property_name);
        int length = property_name.size();
        super.setLength(length);
    }

    public void setProperties(ArrayList<String> data){
        super.setProperties(data);

        DATE = data.get(0);
        ORDER_STATUS = data.get(1);
        SUCCESS_NUMBER = data.get(2);
        ORDER_QTY = data.get(3);
        LOAD_TIME = data.get(4);
    }

    public String getDate(){ return DATE; }

}