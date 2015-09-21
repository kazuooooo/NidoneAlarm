package com.example.matsumotokazuya.mynidonealarm;

import java.util.Calendar;

/**
 * Calendarクラス Utility
 */
public class CalendarUtil {
    //受け取ったカレンダー型の変数を見やすい感じにして返す
    public static String GetCalendarInfo(Calendar cal){
        return cal.get(Calendar.YEAR)+"年"+cal.get(Calendar.MONTH)+"月" +cal.get(Calendar.DAY_OF_MONTH)+"日"+cal.get(Calendar.HOUR_OF_DAY)+"時"+cal.get(Calendar.MINUTE)+"分"+cal.get(Calendar.SECOND)+"秒";
    }
}
