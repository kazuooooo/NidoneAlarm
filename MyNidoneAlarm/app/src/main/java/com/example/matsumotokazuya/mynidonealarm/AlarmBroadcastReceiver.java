package com.example.matsumotokazuya.mynidonealarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by matsumotokazuya on 2015/09/13.
 */



public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context,Intent intent){
        //効かない多分onReceive内でできることが限られている
        //LogUtil.LogString("playAlarm!!!")
// intentID (requestCode) を取り出す
        int bid = intent.getIntExtra("intentId",0);
        // RecieverからMainActivityを起動させる
        Intent intent2 = new Intent(context, AlarmHome.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, bid, intent2, 0);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("時間です")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("TestAlarm2")
                .setContentText("時間になりました")
                        // 音、バイブレート、LEDで通知
                .setDefaults(Notification.DEFAULT_ALL)
                        // 通知をタップした時にMainActivityを立ち上げる
                .setContentIntent(pendingIntent)
                .build();

        // 古い通知を削除
        notificationManager.cancelAll();
        // 通知
        notificationManager.notify(R.string.app_name, notification);
    }

    private void TEST(){
        LogUtil.LogString("test");
    }
}
