package com.supertester.PBV_MII.project_v2.DB;

import java.util.ArrayList;

public class Contact {
    protected ArrayList<String> property;
    protected ArrayList<String> property_name;

    public String table_name;
    private String CREATE_CONTACTS_TABLE;

    private int length = 0;
    public Contact(){
        table_name = new String();
        CREATE_CONTACTS_TABLE = new String();
    }

    public void setProperties(ArrayList<String> data){
        property = data;
    }

    public void setProperty_name(ArrayList<String> data){ property_name = data; }

    public void setCREATE_CONTACTS_TABLE(String data){ CREATE_CONTACTS_TABLE = data; }


    public String getCREATE_CONTACTS_TABLE(){ return CREATE_CONTACTS_TABLE; }

    public String getTable_name(){return table_name;}

    public String getProperty(int i){return property.get(i);}

    public ArrayList<String> getProperties(){return property;}

    public ArrayList<String> getProperties_name(){ return property_name; }

    public String getProperty_name(int i){ return property_name.get(i); }

    public void setLength(int length){ this.length = length; }

    public int getLength(){ return length;}
}
