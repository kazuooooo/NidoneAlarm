package com.example.matsumotokazuya.mynidonealarm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import java.util.Calendar;

import android.app.PendingIntent;
import android.app.AlarmManager;
import android.widget.Toast;


public class AlarmHome extends AppCompatActivity{
    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private TextView countText;
    private Button timerButton;
    private boolean isTimerProcessing = false;
    private int minutes = 0;
    private int seconds = 0;
    private Handler mHandler = new Handler();
    public static SoundPool mSoundPool;
    public static int mSoundId;
    private EditText timerSettingText;
    private SurfaceView surface;
    public static int alarmID;


    //DataStore設定値
    private SharedPreferences dataStore;
    private boolean d_isAlarmSetting;
    private int d_dekiAlarmHour;
    private int d_dekiAlarmMinutes;
    private int d_yabaAlarmHour;
    private int d_yabaAlarmMinutes;
    private HashMap<String,Boolean> DOWMap;

    //NotificationManager
    private NotificationManager notificationManager;

    //二度寝ボタン
    private Button[] nidoneButtons;
    private Button nidoneButtonHalf;
    private Button nidoneButtonQuarter;
    private Button nidoneButtonOneEighth;
    private Button nidoneTimeResetButton;

    //dekiとyabaのCalendar値
    Calendar dekiCalendarDate;
    Calendar yabaCalendarDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_home);
        //タイマーのテキストビューを取得
        countText = (TextView)findViewById(R.id.timer);

        //二度寝ボタン
        nidoneButtonHalf = (Button)findViewById(R.id.NidoneHalfButton);
        nidoneButtonQuarter = (Button)findViewById(R.id.NidoneQuarterButton);
        nidoneButtonOneEighth = (Button)findViewById(R.id.NidoneOneEighth);
        nidoneButtons = new Button[]{nidoneButtonHalf,nidoneButtonQuarter,nidoneButtonOneEighth};
        nidoneTimeResetButton = (Button)findViewById(R.id.NidoneTimeResetButton);

        //テスト
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        mSoundId = mSoundPool.load(getApplicationContext(),R.raw.se_maoudamashii_chime14,0);

        notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    protected void onResume(){
        super.onResume();

        //dataStore
        ReadDataStore();
        //初回起動じゃなければアラームを設
        if(d_isAlarmSetting){
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

    public void OnPushAwakeButton(View view){
        StopAlarm();
        ResetTimer();
    }


    public void StopAlarm(){
        LogUtil.LogString("Stop Alarm");
        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //notificationManager.cancel(0);
    }

    public void StartTimer(View v){
        //タイマーの値を取得
        String timerText = ((Button)v).getText().toString();
        if(timerText.length() == 0){
            LogUtil.LogString("timer null");
            ResetTimer();
            return;
        }
        int timerSettingMinutes = Integer.parseInt(timerText);
        //タイマーフラグをOn
        isTimerProcessing = true;
        //内部的な時間にも設定
        minutes = timerSettingMinutes;
        seconds = minutes * 60;
        //タイマーインスタンスを作成
        this.mainTimer = new Timer();
        //タスククラスインスタンスを作成
        this.mainTimerTask = new MainTimerTask();
        //タイマースケジュールを設定
        this.mainTimer.schedule(mainTimerTask, 1000, 1000);
        //二度寝再設定ボタン表示
        nidoneTimeResetButton.setVisibility(View.VISIBLE);
    }

    private void ResetTimer(){
        if(isTimerProcessing) {
            //完了処理
            mainTimer.cancel();
        }
        //テキスト表示リセット
        countText.setText("00:00");
        //FlagOff
        isTimerProcessing = false;
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
                        OnTimerEnd();
                    }
                }
            });
        }
    }



    private void SetAlarms(){

        LogUtil.LogString("d_hour"+d_yabaAlarmHour+"d_minutes"+d_yabaAlarmMinutes);
        //設定時間と今の時間を比較してアラームが鳴るのが今日か明日かを決定
        boolean isTommorow = GetTodayOrTommorowByTime(d_yabaAlarmHour, d_yabaAlarmMinutes);
        //設定時間の曜日を取得
        String dayOfWeek = GetDayOfWeekByTime(isTommorow);
        //調べた曜日にアラームが設定されているか確認
        if(DOWMap.get(dayOfWeek)){
            Calendar cal = Calendar.getInstance();
            if(isTommorow){
                cal.add(Calendar.DATE,1);
            }

            cal.set(Calendar.HOUR_OF_DAY,d_yabaAlarmHour);
            cal.set(Calendar.MINUTE,d_yabaAlarmMinutes);
            SetAlarmByDate(cal);
        }else{
            LogUtil.LogString(dayOfWeek+"day is not setting");
        }
    }



    private void SetAlarmByDate(Calendar settingCal){
        //時間をセット
        Calendar calendar = settingCal;
        //秒は0にしとく
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        //アラームをセットする
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
        LogUtil.LogString("set alarm" + CalendarUtil.GetCalendarInfo(calendar));
        Toast.makeText(getApplicationContext(),"SetAlarm",Toast.LENGTH_LONG).show();
        //tmp
        yabaCalendarDate = settingCal;
    }

    private void OnTimerEnd(){
        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
        sendBroadcast(intent);
        ResetTimer();
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
        if (calendar.get(Calendar.HOUR_OF_DAY) > d_yabaAlarmHour) {
            isTommorow = true;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == d_yabaAlarmHour) {
            if (calendar.get(Calendar.MINUTE) > d_yabaAlarmMinutes) {
                isTommorow = true;
            }
        }
        LogUtil.LogString("istommorow" + isTommorow);
        return  isTommorow;
    }

    private void ReadDataStore(){
        dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        d_isAlarmSetting = dataStore.getBoolean("isAlarmSet", false);
        d_dekiAlarmHour = dataStore.getInt("dekiAlarmTimeHour", -1);
        d_dekiAlarmMinutes = dataStore.getInt("dekiAlarmTimeMinutes",-1);
        d_yabaAlarmHour = dataStore.getInt("yabaAlarmTimeHour",-1);
        d_yabaAlarmMinutes = dataStore.getInt("yabaAlarmTimeMinutes",-1);
        LogUtil.LogString("dhour"+d_dekiAlarmHour+"dminute"+d_dekiAlarmMinutes);
        LogUtil.LogString("yalarmhour"+d_yabaAlarmHour+"yabaalarmMinutes"+d_yabaAlarmMinutes);
        //map情報を読み込み
        try {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            DOWMap = (HashMap)ois.readObject();
            LogUtil.LogString(DOWMap.toString());
        }catch (Exception e){

        }
    }

    //アラームが鳴っているときはタップでアラームを止める
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.LogString("gettouch");
        //TODO:まとめ
        //Notificationmanagerを使って今アクティブなActivitiを取得
        //StatusBarNotification[] nots = notificationManager.getActiveNotifications();
        //今回はアラームしか使ってないので何かあれば停止
//        for(StatusBarNotification not:nots){
//            LogUtil.LogString("stopalarm");
//            StopAlarm();
//        }
        StopAlarm();
        return super.onTouchEvent(event);
    }

    public void SetNidoneButtonsTime(View v){
        int[] times = NidoneCaliculator.CalcNidoneTimes(yabaCalendarDate);
        for (int i = 0;i<=2;i++) {
            nidoneButtons[i].setText(ParseUtil.ParseIntToString(times[i]));
        }
    }
}
