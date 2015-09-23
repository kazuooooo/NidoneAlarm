package com.example.matsumotokazuya.mynidonealarm;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Setting extends AppCompatActivity {
    private EditText timerSettingText;
    private SharedPreferences dataStore;
    private SharedPreferences.Editor dataEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        timerSettingText = (EditText)findViewById(R.id.SettingTime);
        dataStore = getSharedPreferences("DataStore",MODE_PRIVATE);
        dataEditor = dataStore.edit();
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
    //アラームのOn/Off
    boolean isAlarmSet = ((Switch)findViewById(R.id.isAlarmActive)).isChecked();
    dataEditor.putBoolean("isAlarmSet", isAlarmSet);
    //Timerの設定値を保存
    Log.d("Destroy","onpause call");
    int alarmTimeHour = ((TimePicker)findViewById(R.id.timePicker)).getCurrentHour();
    dataEditor.putInt("alarmTimeHour", alarmTimeHour);
    int alarmTimeMinutes = ((TimePicker)findViewById(R.id.timePicker)).getCurrentMinute();
    dataEditor.putInt("alarmTimeMinutes", alarmTimeMinutes);

    //曜日のチェック情報を保存
    //HashMapのsaveはObjectOutputStreamに書き換えたい　http://stackoverflow.com/questions/7944601/saving-a-hash-map-into-shared-preferences
    SaveDOWChecks();
    dataEditor.commit();
    }

    private void SaveDOWChecks(){
        HashMap<String,Boolean> maps = new HashMap<String,Boolean>();
        maps.put("Mon", ((CheckBox) findViewById(R.id.checkMonday)).isChecked());
        maps.put("Tue",((CheckBox)findViewById(R.id.checkTuesday)).isChecked());
        maps.put("Wed", ((CheckBox) findViewById(R.id.checkWednesday)).isChecked());
        maps.put("Thu",((CheckBox)findViewById(R.id.checkThursday)).isChecked());
        maps.put("Fri",((CheckBox)findViewById(R.id.checkFriday)).isChecked());
        maps.put("Sat", ((CheckBox) findViewById(R.id.checkSaturday)).isChecked());
        maps.put("Sun", ((CheckBox) findViewById(R.id.checkSunday)).isChecked());
        SettingValues.daycheckMap = maps;
    }

//    private void SaveData(String key,){
//
//    }
}
