package com.example.matsumotokazuya.mynidonealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by matsumotokazuya on 2015/10/18.
 */
public class NidoneAlarmManager {
    //シングルトン
    private static NidoneAlarmManager   _instance;
    private NidoneAlarmManager()
    {
    }

    public synchronized static NidoneAlarmManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new NidoneAlarmManager();
        }
        return _instance;
    }

    //設定値
    //DataStore設定値
    private SharedPreferences dataStore;
    private SharedPreferences.Editor dataEditor;
    private boolean d_isAlarmSetting;
    private int d_dekiAlarmHour;
    private int d_dekiAlarmMinutes;
    private int d_yabaAlarmHour;
    private int d_yabaAlarmMinutes;
    private int d_intentId;
    private HashMap<String,Boolean> DOWMap;

    Calendar yabaCalendarDate;
    //Context
    Context appContext;
    public void Test(String val){
        LogUtil.LogString("hi this is alarm manager");
    }

    public void SetAlarms(){
        //設定値を読み込み
        ReadDataStore();
        //dalarmは普通に設定
        SetAlarm(d_dekiAlarmHour, d_dekiAlarmMinutes, false, "DEKI");
        //dekiとyabaを比較してyabaの方が時間が前なら１日ずらす
        boolean ytommorow = false;
        if((d_yabaAlarmHour<d_dekiAlarmHour)||(d_yabaAlarmHour==d_dekiAlarmHour && d_yabaAlarmMinutes < d_dekiAlarmMinutes)){
            ytommorow = true;
        }
        SetAlarm(d_yabaAlarmHour, d_yabaAlarmMinutes, ytommorow, "YABA");
    }

    private void SetAlarm(int hour,int minute,boolean ytommorrow,String type) {
        LogUtil.LogString("d_hour" + hour + "d_minutes" + minute);
        //設定時間と今の時間を比較してアラームが鳴るのが今日か明日かを決定
        boolean isTommorow = GetTodayOrTommorowByTime(hour, minute);
        //設定時間の曜日を取得
        String dayOfWeek = GetDayOfWeekByTime(isTommorow);
        //調べた曜日にアラームが設定されているか確認
        if(DOWMap.get(dayOfWeek)){
            Calendar cal = Calendar.getInstance();
            if(isTommorow){
                cal.add(Calendar.DATE,1);
            }
            if(ytommorrow){
                cal.add(Calendar.DATE,1);
            }

            cal.set(Calendar.HOUR_OF_DAY,hour);
            cal.set(Calendar.MINUTE,minute);
            SetAlarmByDate(cal,type);
        }else{
            LogUtil.LogString(dayOfWeek+"day is not setting");
        }
    }

    private void SetAlarmByDate(Calendar settingCal,String type){
        //時間をセット
        Calendar calendar = settingCal;
        //秒は0にしとく
        calendar.set(Calendar.SECOND, 0);
        //context
        appContext = AlarmHome.getAppContext();

        Intent intent = new Intent(appContext, AlarmBroadcastReceiver.class);
        //intentにidを渡す
        intent.putExtra("intentId", GetIntentId());
        intent.putExtra("alarmType", type);
        PendingIntent pending = PendingIntent.getBroadcast(appContext, GetIntentId(), intent, 0);

        //アラームをセットする
        AlarmManager am = (AlarmManager)appContext.getSystemService(appContext.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
        LogUtil.LogString("set alarm" + CalendarUtil.GetCalendarInfo(calendar));
        Toast.makeText(AlarmHome.getAppContext(), "SetAlarm", Toast.LENGTH_LONG).show();
        if(type == "YABA"){
            yabaCalendarDate = settingCal;
        }
    }

    private int GetIntentId(){
        int val = dataStore.getInt("intentId", 0);
        dataEditor.putInt("intentId", val + 1);
        dataEditor.commit();
        LogUtil.LogString("send id");
        return val;
    }

    private String GetDayOfWeekByTime(boolean isTommorow){
        String dayOfWeek = "no";
        Calendar calendar = Calendar.getInstance();
        if(isTommorow){
            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+1);
        }
        int DOWNum = calendar.get(Calendar.DAY_OF_WEEK);
        switch(DOWNum){
            case Calendar.MONDAY:
                dayOfWeek = "Mon";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "Tue";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "Wed";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "Thu";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "Fri";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "Sat";
                break;
            case Calendar.SUNDAY:
                dayOfWeek = "Sun";
                break;
        }
        return  dayOfWeek;
    }
    private boolean GetTodayOrTommorowByTime(int hour,int minutes) {
        Calendar calendar = Calendar.getInstance();
        boolean isTommorow = false;
        if (calendar.get(Calendar.HOUR_OF_DAY) > hour) {
            isTommorow = true;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == hour) {
            if (calendar.get(Calendar.MINUTE) > minutes) {
                isTommorow = true;
            }
        }
        LogUtil.LogString("istommorow" + isTommorow);
        return  isTommorow;
    }

    private void ReadDataStore(){
        appContext = AlarmHome.getAppContext();

        dataStore = appContext.getSharedPreferences("DataStore", appContext.MODE_PRIVATE);
        dataEditor = dataStore.edit();
        d_isAlarmSetting = dataStore.getBoolean("isAlarmSet", false);
        d_dekiAlarmHour = dataStore.getInt("dekiAlarmTimeHour", -1);
        d_dekiAlarmMinutes = dataStore.getInt("dekiAlarmTimeMinutes",-1);
        d_yabaAlarmHour = dataStore.getInt("yabaAlarmTimeHour",-1);
        d_yabaAlarmMinutes = dataStore.getInt("yabaAlarmTimeMinutes",-1);

        //map情報を読み込み
        try {
            File file = new File(appContext.getDir("data", appContext.MODE_PRIVATE), "map");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            DOWMap = (HashMap)ois.readObject();
            LogUtil.LogString(DOWMap.toString());
        }catch (Exception e){

        }
    }

}
