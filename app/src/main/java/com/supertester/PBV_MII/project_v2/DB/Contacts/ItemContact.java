package com.supertester.PBV_MII.project_v2.DB.Contacts;


import com.supertester.PBV_MII.project_v2.DB.Contact;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemContact extends Contact {

//    private int NO;
    private String AUFNR = "AUFNR"; // 함수의 종속자
    private String PICK_SEQ = "PICK_SEQ";
    private String LINE = "LINE";
    private String MATNR = "MATNR";
    private String TAKT = "TAKT";
    private String MAKTX = "MAKTX";
    private String BOX_NO = "BOX_NO";
    private String OPERATION = "OPERATION";
    private String LAMPOS = "LAMPOS";
    private String QTY = "QTY";
    private String TOT_QTY = "TOT_QTY";
    private String BLOCK_GRP = "BLOCK_GRP";
    private String STATUS = "STATUS";
    private int NO = 0; // 함수의 결정자

    private ArrayList<String> property_name = new ArrayList<String>(Arrays.asList(
            AUFNR, PICK_SEQ, LINE, MATNR, TAKT, MAKTX, BOX_NO, OPERATION, LAMPOS, QTY, TOT_QTY, BLOCK_GRP, STATUS,"NO"));
    private String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS ItemContact("
            +AUFNR +" VARCHAR(100) , "
            +PICK_SEQ +" VARCHAR(100), "
            +LINE + " VARCHAR(100),"
            +MATNR + " VARCHAR(100),"
            +TAKT + " VARCHAR(100),"
            +MAKTX + " VARCHAR(100),"
            +BOX_NO + " VARCHAR(100),"
            +OPERATION + " VARCHAR(100),"
            +LAMPOS + " VARCHAR(100),"
            +QTY + " VARCHAR(100),"
            +TOT_QTY + " VARCHAR(100),"
            +BLOCK_GRP + " VARCHAR(100),"
            +STATUS + " VARCHAR(100),"
            +"NO INT(10) NOT NULL , "
            +"foreign key(AUFNR) references OrderContact(AUFNR)"
            +")";


    public int length = property_name.size();

    public ItemContact() {
        super();
        super.table_name = "ItemContact";
        super.setCREATE_CONTACTS_TABLE(CREATE_CONTACTS_TABLE);
        super.setProperty_name(property_name);
        super.setLength(this.length);
    }



    public void setProperties(ArrayList<String> data, int no){
        data.add(String.valueOf(no));
        super.setProperties(data);
        this.NO = no;

        AUFNR = data.get(0);
        PICK_SEQ = data.get(1);
        LINE = data.get(2);
        MATNR = data.get(3);
        TAKT = data.get(4);
        MAKTX = data.get(5);
        BOX_NO = data.get(6);
        OPERATION = data.get(7);
        LAMPOS = data.get(8);
        QTY = data.get(9);
        TOT_QTY = data.get(10);
        BLOCK_GRP = data.get(11);
        STATUS = data.get(12);
    }

    public void setProperties(ArrayList<String> data){
        super.setProperties(data);

        AUFNR = data.get(0);
        PICK_SEQ = data.get(1);
        LINE = data.get(2);
        MATNR = data.get(3);
        TAKT = data.get(4);
        MAKTX = data.get(5);
        BOX_NO = data.get(6);
        OPERATION = data.get(7);
        LAMPOS = data.get(8);
        QTY = data.get(9);
        TOT_QTY = data.get(10);
        BLOCK_GRP = data.get(11);
        STATUS = data.get(12);
        NO = Integer.parseInt(data.get(13));
    }
    public int getNO(){ return NO; }
    public String getAUFNR(){ return AUFNR; }
    public String getPICK_SEQ(){ return PICK_SEQ; }
    public String getLINE(){ return LINE; }
    public String getMATNR(){ return MATNR; }
    public String getTAKT(){ return TAKT; }
    public String getMAKTX(){ return MAKTX; }
    public String getBOX_NO(){ return BOX_NO; }
    public String getOPERATION(){ return OPERATION; }
    public String getLAMPOS(){ return LAMPOS; }
    public String getQTY(){ return QTY; }
    public String getTOT_QTY(){ return TOT_QTY; }
    public String getBLOCK_GRP(){ return BLOCK_GRP; }
    public String getSTATUS(){ return STATUS; }

}