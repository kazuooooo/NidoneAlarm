package com.example.matsumotokazuya.mynidonealarm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by matsumotokazuya on 2015/10/03.
 */
public class TimePick extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private SharedPreferences dataStore;
    private SharedPreferences.Editor dataEditor;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int hour = 0;
        int minutes = 0;
        dataStore = getContext().getSharedPreferences("DataStore", Context.MODE_PRIVATE);
        dataEditor = dataStore.edit();
        String tag = this.getTag();
        LogUtil.LogString("picker tag"+tag);
        switch (tag){
            case "dekitimePicker":
                hour = dataStore.getInt("dekiAlarmTimeHour", 8);
                minutes = dataStore.getInt("dekiAlarmTimeMinutes", 0);
                break;
            case "yabatimePicker":
                hour = dataStore.getInt("yabaAlarmTimeHour",8);
                minutes= dataStore.getInt("yabaAlarmTimeMinutes",0);
                break;
        }



        return  new TimePickerDialog(getActivity(),(Setting)getActivity(),hour,minutes,true);
    }

   @Override
    public void onTimeSet(android.widget.TimePicker view,int hour,int minutes){

   }

}
