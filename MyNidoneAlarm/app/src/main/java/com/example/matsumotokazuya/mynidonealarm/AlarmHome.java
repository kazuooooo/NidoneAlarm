package com.example.matsumotokazuya.mynidonealarm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import java.util.Calendar;
import java.util.logging.Logger;

import android.app.PendingIntent;
import android.app.AlarmManager;
import android.widget.Toast;


public class AlarmHome extends AppCompatActivity {
    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private TextView countText;
    private Button timerButton;
    private String timerButtonText;
    private int minutes = 0;
    private int seconds = 0;
    private Handler mHandler = new Handler();
    public static SoundPool mSoundPool;
    public static int mSoundId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_home);
        //タイマーのテキストビューを取得
        countText = (TextView)findViewById(R.id.timer);
        //タイマーのボタン
        timerButton =(Button)findViewById(R.id.TestButton);
        //テスト
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        mSoundId = mSoundPool.load(getApplicationContext(),R.raw.se_maoudamashii_chime14,0);
        LogUtil.LogString("OnCreateCall");
        //AlarmTest();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("my", "onresume");
        //タイマーのボタンに反映
        timerButton.setText((CharSequence) String.valueOf(SettingValues.settingTime));
        //内部的な時間にも設定
        minutes =SettingValues.settingTime;
        seconds = minutes*60;
        //初回起動じゃなければアラームを設定
        if(SettingValues.isAlarmSet){
        SetAlarms();
            LogUtil.LogString("Call SetAlarms");
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void OnSetting(View view){
        Log.d("check", "this is check");
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }

    public void OnStartTimer(View view){

        //タイマーインスタンスを作成
        this.mainTimer = new Timer();
        //タスククラスインスタンスを作成
        this.mainTimerTask = new MainTimerTask();

        //タイマースケジュールを設定
        this.mainTimer.schedule(mainTimerTask, 1000, 1000);

    }

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post(new Runnable() {
                public void run() {

                    //1秒引く
                    seconds -= 1;
                    //秒を分に変換
                    int currentMinute = (int) seconds / 60;
                    int currentSeconds = seconds - 60 * currentMinute;
                    String minuteString = String.valueOf(currentMinute);
                    String secondString = String.valueOf(currentSeconds);
                    if (currentMinute < 10) {
                        minuteString = "0" + minuteString;
                    }
                    if (currentSeconds < 10) {
                        secondString = "0" + secondString;
                    }
                    //画面にカウントを表示
                    countText.setText(minuteString + ":" + secondString);
                    if (seconds == 0) {
                        //即アラーム発生
                        Calendar cal = Calendar.getInstance();
                        SetAlarmByDate(cal);
                        mainTimer.cancel();
                    }
                }
            });
        }
    }



    private void SetAlarms(){

        //設定時間と今の時間を比較してアラームが鳴るのが今日か明日かを決定
        boolean isTommorow = GetTodayOrTommorowByTime(SettingValues.alarmTimeHour, SettingValues.alarmTimeMinutes);
        //設定時間の曜日を取得
        String dayOfWeek = GetDayOfWeekByTime(isTommorow);
        //調べた曜日にアラームが設定されているか確認
        if(SettingValues.daycheckMap.get(dayOfWeek)){
            Calendar cal = Calendar.getInstance();
            if(isTommorow){
                cal.add(Calendar.DATE,1);
            }
            cal.set(Calendar.HOUR_OF_DAY,SettingValues.alarmTimeHour);
            cal.set(Calendar.MINUTE,SettingValues.alarmTimeMinutes);
            SetAlarmByDate(cal);
        }else{
            LogUtil.LogString(dayOfWeek+"day is not setting");
        }
    }


    public void AlarmTest(){
        //時間をセット
//        Calendar calendar = Calendar.getInstance();
//        //Calendarを使って現在の時間をミリ秒で取得
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        //5秒後に設定
//        calendar.add(Calendar.SECOND,5);
//
//        Intent intent = new Intent(getApplicationContext(),AlarmBroadcastReceiver.class);
//        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
//
//        //アラームをセットする
//        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
//        am.set(AlarmManager.RTC,calendar.getTimeInMillis(),pending);
//        LogUtil.LogString("calendar cont"+calendar.toString());
//        LogUtil.LogString("set alarm"+CalendarUtil.GetCalendarInfo(calendar));
//
//        Toast.makeText(getApplicationContext(),"SetAlarm",Toast.LENGTH_LONG).show();
//        LogUtil.LogString("END Alarm TEST");
    }

    private void SetAlarmByDate(Calendar settingCal){
        //時間をセット
        Calendar calendar = settingCal;
        //1秒ずらす
        calendar.add(Calendar.SECOND,1);

        Intent intent = new Intent(getApplicationContext(),AlarmBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        //アラームをセットする
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, calendar.getTimeInMillis(), pending);
        LogUtil.LogString(("current time"+CalendarUtil.GetCalendarInfo(Calendar.getInstance())));
        LogUtil.LogString("set alarm" + CalendarUtil.GetCalendarInfo(calendar));
        Toast.makeText(getApplicationContext(),"SetAlarm",Toast.LENGTH_LONG).show();
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
        if (calendar.get(Calendar.HOUR_OF_DAY) > SettingValues.alarmTimeHour) {
            isTommorow = true;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == SettingValues.alarmTimeHour) {
            if (calendar.get(Calendar.MINUTE) > SettingValues.alarmTimeMinutes) {
                isTommorow = true;
            }
        }
        LogUtil.LogString("istommorow"+isTommorow);
        return  isTommorow;
    }

    public  void onPushStopButton(View view){
       NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
