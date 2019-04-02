package com.supertester.PBV_MII.project_v2.Async;

import android.os.AsyncTask;
import android.util.Log;

import com.supertester.PBV_MII.project_v2.Database.Item;
import com.supertester.PBV_MII.project_v2.Database.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CallRemote_endpicking extends AsyncTask<String, String, SoapObject> {
    private static final String SOAP_ACTION = "http://www.sap.com/xMII/XacuteRequest"; //웹에서 확인하면 함수 설명이 나옴(namespace + soap_method)
    private static final String SOAP_METHOD = "XacuteRequest"; //호출되는 함수의 이름
    private static final String NAMESPACE = "http://www.sap.com/xMII";  //웹서비스 만들 때 기재
    private static final String URL = "http://r3mpwdisp.got.volvo.net:8145/XMII/SOAPRunner/CEMII/04_MaterialSupply/Picking/Transaction/EndPickingProcessTrx";
    private User user = new User();

    protected SoapObject doInBackground(String... params) {
        String pick;
        String id;
        String pw;
        SoapObject countryDetails = null;

        pick = params[0];
        id = user.getID();
        pw = user.getPW();

        SoapObject input_params = new SoapObject(NAMESPACE, "InputParams");
        SoapObject filter_sequence = new SoapObject(NAMESPACE, "InputSequence");
        SoapObject request = new SoapObject(NAMESPACE, SOAP_METHOD);

        filter_sequence.addProperty("ACT", "");
        filter_sequence.addProperty("PIC_SEQ", pick);
        filter_sequence.addProperty("Plant", "1000");

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
        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);   //웹서비스 호출(soap action 변수 사용)
            countryDetails = (SoapObject) envelope.getResponse();
            Log.e("log_end Result", "End: " + countryDetails);
        } catch (Exception e) {
            Log.e("log_end error Result", "Error: " + e.getMessage());
        }
        return countryDetails;
    }
}
