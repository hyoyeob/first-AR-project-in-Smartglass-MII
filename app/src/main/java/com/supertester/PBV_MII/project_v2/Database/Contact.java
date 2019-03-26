package com.supertester.PBV_MII.project_v2.Database;

import java.util.ArrayList;

public class Contact {
    private ArrayList<String> property;
    private ArrayList<String> property_name;

    protected String table_name;
    private String CREATE_CONTACTS_TABLE;

    private int length = 0;
    public Contact(){
        table_name = "";
        CREATE_CONTACTS_TABLE = "";
    }

    public void setProperties(ArrayList<String> data){
        property = data;
    }

    protected void setProperty_name(ArrayList<String> data){ property_name = data; }

    protected void setCREATE_CONTACTS_TABLE(String data){ CREATE_CONTACTS_TABLE = data; }


    String getCREATE_CONTACTS_TABLE(){ return CREATE_CONTACTS_TABLE; }

    public String getTable_name(){return table_name;}

    public String getProperty(int i){return property.get(i);}

    public ArrayList<String> getProperties(){return property;}

    public ArrayList<String> getProperties_name(){ return property_name; }

    public String getProperty_name(int i){ return property_name.get(i); }

    protected void setLength(int length){ this.length = length; }

    public int getLength(){ return length;}
}
