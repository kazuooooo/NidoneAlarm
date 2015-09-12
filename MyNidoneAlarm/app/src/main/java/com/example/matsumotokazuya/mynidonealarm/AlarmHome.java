package com.example.matsumotokazuya.mynidonealarm;

import android.content.Intent;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;


public class AlarmHome extends AppCompatActivity {
    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private TextView countText;
    private Button timerButton;
    private String timerButtonText;
    private EditText settingTime;
    private int minutes = 0;
    private int seconds = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_home);
        //タイマーのテキストビューを取得
        countText = (TextView)findViewById(R.id.timer);
        //設定から書いてある時間を取得
        timerButton =(Button)findViewById(R.id.TestButton);
        settingTime = (EditText)findViewById(R.id.SettingTime);
        Log.e("time", settingTime.toString());
        timerButtonText = timerButton.getText().toString();
        minutes = Integer.parseInt(timerButtonText);
        seconds = minutes*60;
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
            mHandler.post( new Runnable() {
                public void run() {

                    //1秒引く
                    seconds -= 1;
                    //秒を分に変換
                    int currentMinute = (int)seconds/60;
                    int currentSeconds = seconds - 60*currentMinute;
                    //画面にカウントを表示
                    countText.setText(String.valueOf(currentMinute)+":"+String.valueOf(currentSeconds));
                    if(minutes == 0){
                        countText.setText("End");
                    }
                }
            });
        }
    }
}
