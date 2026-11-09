package com.smartworker.app;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.provider.Settings;
import android.net.Uri;
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SmartWorker:Alarm");
        wl.acquire(10000);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(5000);
            }
        }
        try {
            Uri alarmSound = Settings.System.DEFAULT_ALARM_ALERT_URI;
            MediaPlayer mp = MediaPlayer.create(context, alarmSound);
            if (mp != null) {
                mp.setLooping(false);
                mp.start();
            }
        } catch (Exception e) { }
        Intent openApp = new Intent(context, MainActivity.class);
        openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(openApp);
        wl.release();
    }
}
