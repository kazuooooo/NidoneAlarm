package com.example.matsumotokazuya.mynidonealarm;

import java.util.Calendar;

/**
 * Created by matsumotokazuya on 2015/10/04.
 */
public class NidoneCaliculator {
    //二度寝時間を計算して返す
    public static int[] CalcNidoneTimes(Calendar yabaCalendar){

        //結果の配列
        int[] result = new int[]{0,0,0};
        //現在時刻を取得
        Calendar currentC = Calendar.getInstance();
        LogUtil.LogString("currentC"+CalendarUtil.GetCalendarInfo(currentC)+"yaba"+CalendarUtil.GetCalendarInfo(yabaCalendar));
        //秒で差分を取得
        int difSec = (int)((yabaCalendar.getTimeInMillis() - currentC.getTimeInMillis())/1000);
        //値を計算して代入
        int half = SecondToMinutes(difSec/2);
        int quarter = SecondToMinutes(difSec/4);
        int oneEighth = SecondToMinutes(difSec/8);

        LogUtil.LogString("half"+half+"quarter"+quarter+"oneEighth"+oneEighth);

        result[0]=half;
        result[1]=quarter;
        result[2]=oneEighth;

        return result;
    }

    private static int SecondToMinutes(int second){
        int minutes = (second/60)+1;
        minutes = 5*(minutes/5);
        return minutes;
    }
}
