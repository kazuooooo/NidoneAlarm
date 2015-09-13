package com.example.matsumotokazuya.mynidonealarm;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;

public class Setting extends AppCompatActivity {
    private EditText timerSettingText;
    private SharedPreferences dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        timerSettingText = (EditText)findViewById(R.id.SettingTime);
        dataStore = getSharedPreferences("DataStore",MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
@Override
    public void onPause(){
    super.onPause();
    //Timerの設定値を保存
    Log.d("Destroy","onpause call");
    SettingValues.settingTime = Integer.parseInt(timerSettingText.getText().toString());
    SettingValues.alarmTimeHour = ((TimePicker)findViewById(R.id.timePicker)).getCurrentHour();
    SettingValues.alarmTimeMinutes = ((TimePicker)findViewById(R.id.timePicker)).getCurrentMinute();
    LogUtil.LogInt(SettingValues.alarmTimeHour);
    LogUtil.LogInt(SettingValues.alarmTimeMinutes);
    }
}
