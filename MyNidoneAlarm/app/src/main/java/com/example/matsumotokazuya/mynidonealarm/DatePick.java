package com.example.matsumotokazuya.mynidonealarm;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by matsumotokazuya on 2015/10/03.
 */
public class DatePick extends DialogFragment implements DatePickerDialog.OnDateSetListener{

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState){
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get((Calendar.DAY_OF_MONTH));
//
//        return  new DatePickerDialog(getActivity(),(Setting)getActivity(),year,month,day);
//    }

    @Override
    public void onDateSet(android.widget.DatePicker view,int year,int monthOfYer,int dayOfMonth){

    }


}
