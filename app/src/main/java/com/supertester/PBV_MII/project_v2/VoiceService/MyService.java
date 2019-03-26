package com.supertester.PBV_MII.project_v2.VoiceService;

import android.Manifest.permission;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;
import com.supertester.PBV_MII.project_v2.Class.Std_Method;
import com.tbruyelle.rxpermissions.RxPermissions;
import java.util.List;
import java.util.Random;

public class MyService extends Service implements SpeechDelegate, Speech.stopDueToDelay {

    Std_Method sm;
    String voc_result="";

    public static SpeechDelegate delegate;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sm=(Std_Method)getApplicationContext();
        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
            muteBeepSoundOfRecorder();
        } else {
            System.setProperty("rx.unsafe-disable", "True");
            RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) {
                    try {
                        if (Speech.getInstance().isListening()) Speech.getInstance().stopListening();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        Log.e("log_voc_set", "Speech recognition is not available on this device!");

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        Log.e("log_voc_set", "Google voice typing must be enabled!");
                    }
                }
            });
            muteBeepSoundOfRecorder();
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
//        for (String partial : results) {
//        }
    }

    @Override
    public void onSpeechResult(String result) {
        Log.e("log_voc_result",result);
        if (!TextUtils.isEmpty(result)&&!result.equals("아")) {
            voc_result=result;
            sm=(Std_Method)getApplicationContext();
            sm.Key_control(voc_result);
        }
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        sm=(Std_Method)getApplicationContext();

        sm.voice_count++;
        Log.e("log_voc_set_flow","onSpecifiedCommandPronounced 안정  "+sm.voice_count+" / "+  sm.isServiceRunning());
        if (Speech.getInstance().isListening()) {
            muteBeepSoundOfRecorder();
            Speech.getInstance().stopListening();
        } else {
            RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) {
                    try {
                        if (Speech.getInstance().isListening()) Speech.getInstance().stopListening();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        Log.e("log_voc_set", "Speech recognition is not available on this device!");
                    } catch (GoogleVoiceTypingDisabledException exc) {
                        Log.e("log_voc_set", "Google voice typing must be enabled!");
                    }
                }
            });
            muteBeepSoundOfRecorder();
        }
    }

    /**
     * Function to remove the beep sound of voice recognizer.
     */
    private void muteBeepSoundOfRecorder() {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Restarting the service if it is removed.
        PendingIntent service =
                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
                        new Intent(getApplicationContext(), MyService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sm=(Std_Method)getApplicationContext();
        sm.voice_flag=false;
        if (Speech.getInstance().isListening()){
            Speech.getInstance().stopListening();
        }
    }
}