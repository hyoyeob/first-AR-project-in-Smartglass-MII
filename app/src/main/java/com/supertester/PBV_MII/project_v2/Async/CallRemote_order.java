package com.supertester.PBV_MII.project_v2.Async;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CallRemote_order extends AsyncTask<String, String, SoapObject> {
    private static final String SOAP_ACTION = "http://www.sap.com/xMII/XacuteRequest"; //웹에서 확인하면 함수 설명이 나옴(namespace + soap_method)
    private static final String SOAP_METHOD = "XacuteRequest"; //호출되는 함수의 이름
    private static final String NAMESPACE = "http://www.sap.com/xMII";  //웹서비스 만들 때 기재
    private static final String URL = "http://r3mpwdisp.got.volvo.net:8145/XMII/SOAPRunner/CEMII/04_MaterialSupply/Picking/Transaction/getPickingOrderListTrx";



    protected SoapObject doInBackground(String... params) {
        String line;
        String plant;
        String zone ;
        String date;
        String id;
        String pw;
        String takt;
        String status;
        SoapObject countryDetails;

        line = params[0];
        plant = params[1];
        zone = params[2];
        date = params[3];
        id = params[4];
        pw = params[5];
        takt = params[6];
        status = params[7];

        try {
            SoapObject input_params = new SoapObject(NAMESPACE, "InputParams");
            SoapObject filter_sequence = new SoapObject(NAMESPACE, "InputSequence");
            SoapObject request = new SoapObject(NAMESPACE, SOAP_METHOD);

            filter_sequence.addProperty("DATE", date);
            filter_sequence.addProperty("Line", line);
            filter_sequence.addProperty("Plant", plant);
            filter_sequence.addProperty("Zone", zone);
            filter_sequence.addProperty("Takt", takt);
            filter_sequence.addProperty("Period", "0");
            filter_sequence.addProperty("Status",status);

            input_params.addSoapObject(filter_sequence);

            request.addProperty("LoginName", id);
            request.addProperty("LoginPassword", pw);
            request.addProperty("InputParams", filter_sequence);

            /////////웹서비스 호출 준비
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 3000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call(SOAP_ACTION, envelope);   //웹서비스 호출(soap action 변수 사용)

            countryDetails = (SoapObject) envelope.getResponse();
            Log.e("log_order_info",""+countryDetails);

        } catch (Exception e) { //네트워크 x, 기타 등등...
            Log.e("log_order error Result", e.getMessage());
            return null;
        }
        return countryDetails;
    }
}
