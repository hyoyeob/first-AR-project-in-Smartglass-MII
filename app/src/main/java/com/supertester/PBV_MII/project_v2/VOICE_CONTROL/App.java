package com.supertester.PBV_MII.project_v2.VOICE_CONTROL;

import android.app.Application;
import android.util.Log;

import com.sac.speech.Logger;
import com.sac.speech.Speech;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}
