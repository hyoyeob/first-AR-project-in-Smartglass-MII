package com.example.vopl1.vuzixproject3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {
    private MyVoiceControl mVC;
    private RequestQueue mQueue;
    private int number = 0;
    String text;
    private IntentIntegrator qrScan;
    Activity activity = this;


    TextView content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);

        content = (TextView)findViewById(R.id.content);

        mVC = new MyVoiceControl(this, activity){
            @Override
            public void onRecognition(String word){
                Log.e("onRecognition", word);
                ((TextView)findViewById(R.id.voice)).setText(word);

                if(word.equals("focus")) {
                    mVC.off();
                    mVC.on();
                    number=0;
                    qrScan = new IntentIntegrator(activity);
                    qrScan.setOrientationLocked(false);
                    qrScan.initiateScan();
                }
                if(word.equals("next")) {
                    number++;
                    jsonParse(text);
                }
                if(word.equals("complete")) {
                    number= -1;
                    ((TextView)findViewById(R.id.content)).setText("");
                }
                if(word.equals("previous")) {
                    if(number!=0){
                        number--;
                    }
                    jsonParse(text);
                }
                if(word.equals("voice on")) {
                    ((TextView)findViewById(R.id.content)).setText("Voice on");
                }
                if(word.equals("voice off")) {
                    ((TextView)findViewById(R.id.content)).setText("Voice off");
                }
                if(word.equals("1")) {
                    number=0;
                    jsonParse(text);
                }
                if(word.equals("2")) {
                    number=1;
                    jsonParse(text);
                }
                if(word.equals("3")) {
                    number=2;
                    jsonParse(text);
                }
                if(word.equals("4")) {
                    number=3;
                    jsonParse(text);
                }
                if(word.equals("5")) {
                    number=4;
                    jsonParse(text);
                }
            }
        };
        mVC.on();
        restrictWords();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVC != null) {
             mVC.off();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mVC.on();

    }


    @Override
    protected void onResume() {
        super.onResume();
       if (mVC != null) {
            mVC.on();
      }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);//데이터 결과 문자
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(this, "Scan complete", Toast.LENGTH_SHORT).show();

                text = result.getContents();
                jsonParse(text);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void restrictWords() {
        // Create array of words to restrict to
        String wordList[] = { "focus","next", "previous","complete", "voice on", "voice off", "go home", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        mVC.setWordlist(wordList);
    }

    public void jsonParse(String text) {

        String ip = "http://192.168.43.235";
        String port = ":3000/";
        String url = ip + port + text;
        Log.e("ASDASDASD", url);
        Log.e("ASDASDASD", url);
        Log.e("ASDASDASD", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Item");

                            if(number==0){
                                JSONObject num = jsonArray.getJSONObject(number);
                                String index = num.getString("식별번호");
                                String name = num.getString("이름");
                                String ea = num.getString("개수");
                                String line = num.getString("Line");
                                ((TextView)findViewById(R.id.content)).setText("식별번호: " + index + "\n이름: "+name + "\n개수:" +ea + "\nLine: " + line);
                            }
                            else if(number==1){
                                JSONObject num = jsonArray.getJSONObject(number);
                                String summ = num.getString("요약");
                                ((TextView)findViewById(R.id.content)).setText("요약: "+summ);
                            }
                            else if(number==2){
                                JSONObject num = jsonArray.getJSONObject(number);
                                String intro = num.getString("서론");
                                ((TextView)findViewById(R.id.content)).setText("서론: "+intro);
                            }
                            else if(number==3){
                                JSONObject num = jsonArray.getJSONObject(number);
                                String point = num.getString("본론");
                                ((TextView)findViewById(R.id.content)).setText("본론: "+point);
                            }
                            else if(number==4){
                                JSONObject num = jsonArray.getJSONObject(number);
                                String conclusion = num.getString("결론");
                                ((TextView)findViewById(R.id.content)).setText("결론: "+conclusion);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

}
