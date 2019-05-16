package com.supertester.PBV_MII.project_v2.Class;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmWakeLock {
    private static final String TAG = "myapp:wake_lock";
    private static PowerManager.WakeLock mwl;

    public static void wakeLock(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        Log.e("log_voc_wake", "relase sCpuWakeLock = " + mwl);
        mwl = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE | PowerManager.FULL_WAKE_LOCK, TAG);
        mwl.acquire(5000);
        Toast.makeText(context, "Welcome back, Gentleman.", Toast.LENGTH_LONG).show();
        try {
            mwl.release();
        } catch (Exception ignored) {
        }
    }

    public static void releaseWakeLock(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        Log.e("log_voc_release", "relase sCpuWakeLock = " + mwl);
        mwl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mwl.release();
        Toast.makeText(context, "Thanks, Gentleman.", Toast.LENGTH_LONG).show();
        try {
            mwl.release();
        } catch (Exception ignored) {
        }
    }
}
