package com.supertester.PBV_MII.project_v2.Database.Contacts;


import com.supertester.PBV_MII.project_v2.Database.Contact;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderContact extends Contact {

    private String AUFNR = "AUFNR";
    private String DATE = "DATE";
    private String PLANT = "PLANT";
    private String LINE = "LINE";
    private String ZONE = "ZONE";
    private String DCN = "DCN";
    private String MATNR = "MATNR";
    private String SEQ = "SEQ";
    private String SERNR = "SERNR";
    private String STATUS = "STATUS";
    private String YMII_BACK = "YMII_BACK";
    private String PICK_SEQ = "PICK_SEQ";
    private String TAKT = "TAKT";
    private String IZONE = "IZONE";

    public OrderContact() {
        super();
        super.table_name = "OrderContact";
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS OrderContact("
                + AUFNR + " VARCHAR(100) PRIMARY KEY, "
                + DATE + " VARCHAR(100), "
                + PLANT + " VARCHAR(100),"
                + LINE + " VARCHAR(100),"
                + ZONE + " VARCHAR(100),"
                + DCN + " VARCHAR(100),"
                + MATNR + " VARCHAR(100),"
                + SEQ + " INT(100),"
                + SERNR + " VARCHAR(100),"
                + STATUS + " VARCHAR(100),"
                + YMII_BACK + " VARCHAR(100),"
                + PICK_SEQ + " VARCHAR(100),"
                + TAKT + " VARCHAR(100),"
                + IZONE + " VARCHAR(100),"
                + "constraint date_fk foreign key(date) references OrderStatusContact(date)"
                + ")";
        super.setCREATE_CONTACTS_TABLE(CREATE_CONTACTS_TABLE);
        ArrayList<String> property_name = new ArrayList<>(Arrays.asList(
                AUFNR, DATE, PLANT, LINE, ZONE, DCN, MATNR, SEQ, SERNR, STATUS, YMII_BACK, PICK_SEQ, TAKT, IZONE));
        super.setProperty_name(property_name);
        int length = property_name.size();
        super.setLength(length);
    }

    public void setProperties(ArrayList<String> data){
        super.setProperties(data);

        AUFNR = data.get(0);
        DATE = data.get(1);
        PLANT = data.get(2);
        LINE = data.get(3);
        ZONE = data.get(4);
        DCN = data.get(5);
        MATNR = data.get(6);
        SEQ = data.get(7);
        SERNR = data.get(8);
        STATUS = data.get(9);
        YMII_BACK = data.get(10);
        PICK_SEQ = data.get(11);
        TAKT = data.get(12);
        IZONE = data.get(13);
    }

    public void setProperties2(ArrayList<String> data){
        super.setProperties(data);

        STATUS = data.get(9);
    }



    public String getAUFNR(){ return AUFNR; }
    public String getLINE(){ return LINE; }
    public String getDCN(){ return DCN; }
    public String getMATNR(){ return MATNR; }
    public String getSEQ(){ return SEQ; }
    public String getSERNR(){ return SERNR; }
    public String getSTATUS(){ return STATUS; }
    public String getYMII_BACK(){ return YMII_BACK; }
    public String getPICK_SEQ(){ return PICK_SEQ; }
    public String getTAKT(){ return TAKT; }
    public String getIZONE(){ return IZONE; }

}