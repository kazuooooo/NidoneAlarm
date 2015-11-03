package com.example.matsumotokazuya.mynidonealarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by matsumotokazuya on 2015/09/13.
 */



public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static  NotificationManager notificationManager;
    private SharedPreferences dataStore;
    private SharedPreferences.Editor dataEditor;
    @Override
    public void onReceive(Context context,Intent intent){
        LogUtil.LogString("CallNotify");

        int bid = intent.getIntExtra("intentId",0);
        String alarmType = intent.getStringExtra("alarmType");
        // RecieverからMainActivityを起動させる
        Intent intent2 = new Intent(context, AlarmHome.class);
        //音を設定
        Uri path = Uri.parse("android.resource://com.example.matsumotokazuya.mynidonealarm/" + R.raw.se_maoudamashii_chime14);
        switch (alarmType){
            case "DEKI":
                path = Uri.parse("android.resource://com.example.matsumotokazuya.mynidonealarm/" + R.raw.jingle);
                break;
            case "TIMER":
                path = Uri.parse("android.resource://com.example.matsumotokazuya.mynidonealarm/" + R.raw.transition);
                break;
        }
        AlarmHome.isAlarmRinging = true;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, bid, intent2, 0);

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("時間です")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("TestAlarm2")
                .setContentText("時間になりました")
                        // 音、バイブレート、LEDで通知
                .setDefaults(Notification.DEFAULT_VIBRATE)
                        // 通知をタップした時にMainActivityを立ち上げる
                .setContentIntent(pendingIntent)
                .setSound(path)
                .build();
        notification.flags = Notification.FLAG_INSISTENT;

        //画面をon
        //参考　http://techbooster.jpn.org/andriod/application/4429/

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "Your App Tag");
        wakelock.acquire();
        wakelock.release();

        // ロック外したらメインアクティビティが起動する
        Intent appopenintent = new Intent(context, AlarmHome.class);
        appopenintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(appopenintent);

        // 古い通知を削除
        notificationManager.cancelAll();
        // 通知
        notificationManager.notify(R.string.app_name, notification);
    }
}
