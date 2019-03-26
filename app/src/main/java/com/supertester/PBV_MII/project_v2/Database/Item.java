package com.supertester.PBV_MII.project_v2.Database;

import java.io.Serializable;
import java.util.ArrayList;

public class Item implements Serializable {
    //ORDER
    public ArrayList<String> AUFNR = new ArrayList<>();
    public ArrayList<String> DCN = new ArrayList<>();
    public ArrayList<String> LINE = new ArrayList<>();
    public ArrayList<String> ORDER_MATNR = new ArrayList<>();
    private ArrayList<String> ITEM_MATNR = new ArrayList<>();
    public ArrayList<String> ORDER_SEQ = new ArrayList<>();
    public ArrayList<String> SERNR = new ArrayList<>();
    public ArrayList<String> YMII_BACK = new ArrayList<>();
    public ArrayList<String> LOAD_STATUS = new ArrayList<>();
    public ArrayList<String> ORDER_STATUS = new ArrayList<>();
    public ArrayList<String> PICK_SEQ = new ArrayList<>();
    public ArrayList<String> TAKT = new ArrayList<>();
    public ArrayList<String> ZONE = new ArrayList<>();
//    public transient ArrayList<SoapObject> SOAP_RESULT = new ArrayList<>();

    //ITEM
    private ArrayList<String> LAMPOS = new ArrayList<>();
    private ArrayList<String> MAKTX = new ArrayList<>();
    private ArrayList<String> TOT_QTY = new ArrayList<>();
    private ArrayList<String> ITEM_STATUS = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> QTY = new ArrayList<>();
    private ArrayList<ArrayList<String>> BOX_NO = new ArrayList<>();

    private int LOAD_INDEX = 0;
    private int ORDER_INDEX = 0;
    private int LIMIT_INDEX;

    public ArrayList<ArrayList<Integer>> getQTY() {
        return QTY;
    }

    public ArrayList<ArrayList<String>> getBOX_NO() {
        return BOX_NO;
    }

    public ArrayList<String> getITEM_STATUS() {
        return ITEM_STATUS;
    }

    public ArrayList<String> getMAKTX() {
        return MAKTX;
    }


    public ArrayList<String> getTOT_QTY() {
        return TOT_QTY;
    }

    public ArrayList<String> getLAMPOS() {
        return LAMPOS;
    }

    public String getLOAD_SEQ() {
        return getPICK_SEQ().get(getLOAD_INDEX());
    }

    public String getLOAD_ORDER() {
        return getAUFNR().get(getLOAD_INDEX());
    }

    public String getORDER_NAME() {
        return getAUFNR().get(getORDER_INDEX());
    }

    private int getORDER_INDEX() {
        return ORDER_INDEX;
    }

    public void setORDER_INDEX(int ORDER_INDEX) {
        this.ORDER_INDEX = ORDER_INDEX;
    }


    public ArrayList<String> getORDER_MATNR() {
        return ORDER_MATNR;
    }

    public ArrayList<String> getITEM_MATNR() {
        return ITEM_MATNR;
    }

    public String getMATNR_NAME() {
        return getORDER_MATNR().get(getORDER_INDEX());
    }

    public int getLIMIT_INDEX() {
        return LIMIT_INDEX;
    }

    public void setLIMIT_INDEX(int LIMIT_INDEX) {
        this.LIMIT_INDEX = LIMIT_INDEX;
    }

    public void setLOAD_INDEX(int LOAD_INDEX) {
        this.LOAD_INDEX = LOAD_INDEX;
    }

    public int getLOAD_INDEX() {
        return LOAD_INDEX;
    }

    public ArrayList<String> getAUFNR() {
        return AUFNR;
    }

    public ArrayList<String> getORDER_SEQ() {
        return ORDER_SEQ;
    }

    public ArrayList<String> getSERNR() {
        return SERNR;
    }

    public ArrayList<String> getYMII_BACK() {
        return YMII_BACK;
    }

    public ArrayList<String> getLOAD_STATUS() {
        return LOAD_STATUS;
    }

    public ArrayList<String> getPICK_SEQ() {
        return PICK_SEQ;
    }

    public ArrayList<String> getORDER_STATUS() {
        return ORDER_STATUS;
    }

}
