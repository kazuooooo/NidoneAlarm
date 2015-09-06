package jp.co.spookies.android.alarm;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public class AlarmActivity extends Activity {

    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private String time;
    private TextView countText;
    private Button button;
    private int minutes = 0;
    private int seconds = 0;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell);
        setVolumeControlStream(AudioManager.STREAM_ALARM);
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.vibration);
        ImageView clock = (ImageView) findViewById(R.id.clock);
        clock.startAnimation(animation);
        //タイマーのテキストビューを取得
        countText = (TextView)findViewById(R.id.timer);
        //ボタンに書いてある時間を取得
        button = (Button)findViewById(R.id.TestButton);
        time = button.getText().toString();
        minutes = Integer.parseInt(time);
        seconds = minutes*60;
    }

    public void OnStartTimer(View view){

        //タイマーインスタンスを作成
        this.mainTimer = new Timer();
        //タスククラスインスタンスを作成
        this.mainTimerTask = new MainTimerTask();
        //タイマースケジュールを設定
        this.mainTimer.schedule(mainTimerTask,1000,1000);

    }

    //アラームを停止
    public void onClick(View view) {
        ImageView clock = (ImageView) findViewById(R.id.clock);
        clock.clearAnimation();
        stopService(new Intent(this, BellService.class));
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

    public void OnSetting(View view){

    }
}

