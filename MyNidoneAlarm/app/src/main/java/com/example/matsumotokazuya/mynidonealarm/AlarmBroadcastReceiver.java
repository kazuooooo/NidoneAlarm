package com.example.matsumotokazuya.mynidonealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by matsumotokazuya on 2015/09/13.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context,Intent intent){
        //toastで受け取り確認
        Toast.makeText(context,"Received",Toast.LENGTH_LONG).show();
    }
}
