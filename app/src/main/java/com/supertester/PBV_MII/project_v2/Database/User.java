package com.supertester.PBV_MII.project_v2.Database;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class User implements Serializable {
    private String LINE;
    private String PLANT;
    private String ZONE;
    private String TAKT;
    private String ID;
    private String PW;
    private String USER;
    private String GETTIME;
    private String REALTIME;
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.KOREA);

    public String getGETTIME() {
        return GETTIME;
    }

    public void setGETTIME(String GETTIME) {
        this.GETTIME = GETTIME;
    }

    public String getREALTIME() {
        return REALTIME;
    }

    public void setREALTIME(String REALTIME) {
        this.REALTIME = REALTIME;
    }

    public String getID() {
        return ID;
    }

    public String getLINE() {
        return LINE;
    }

    public String getPLANT() {
        return PLANT;
    }

    public String getPW() {
        return PW;
    }

    public String getTAKT() {
        return TAKT;
    }

    public String getUSER() {
        return USER;
    }

    public String getZONE() {
        return ZONE;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setLINE(String LINE) {
        this.LINE = LINE;
    }

    public void setPLANT(String PLANT) {
        this.PLANT = PLANT;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }

    public void setTAKT(String TAKT) {
        this.TAKT = TAKT;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public void setZONE(String ZONE) {
        this.ZONE = ZONE;
    }
}
