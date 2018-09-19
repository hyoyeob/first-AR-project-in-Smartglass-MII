package com.example.vopl1.vuzixproject3;

import android.app.Activity;
import android.content.Context;
import com.vuzix.speech.Constants;
import com.vuzix.speech.VoiceControl;

public class MyVoiceControl extends VoiceControl {
    Context context;

    public MyVoiceControl(Context context, Activity activity) {
        super(context);
        this.context = context;

        addGrammar(Constants.GRAMMAR_CAMERA);
        addGrammar(Constants.GRAMMAR_BASIC);
        addGrammar(Constants.GRAMMAR_MEDIA);
        addGrammar(Constants.GRAMMAR_NAVIGATION);
    }

    @Override
    public void onRecognition(String result) {
    }

    @Override
    public void addGrammar(int i){
        super.addGrammar(i);
    }
}