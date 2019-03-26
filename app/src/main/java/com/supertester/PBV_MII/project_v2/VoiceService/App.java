package com.supertester.PBV_MII.project_v2.VoiceService;

import android.app.Application;

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
