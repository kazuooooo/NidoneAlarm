package com.example.matsumotokazuya.mynidonealarm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by matsumotokazuya on 2015/10/03.
 */
public class TimePick extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        return  new TimePickerDialog(getActivity(),(Setting)getActivity(),hour,minutes,true);
    }

   @Override
    public void onTimeSet(android.widget.TimePicker view,int hour,int minutes){

   }

}
