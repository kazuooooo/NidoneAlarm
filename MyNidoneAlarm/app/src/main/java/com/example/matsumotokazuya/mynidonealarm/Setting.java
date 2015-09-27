package com.example.matsumotokazuya.mynidonealarm;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.util.HashMap;

public class Setting extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private EditText timerSettingText;
    private SharedPreferences dataStore;
    private SharedPreferences.Editor dataEditor;
    private Switch isAlarmSettingSwitch;
    private TimePicker settingTimePicker;
    private TimePicker settingTimePickerChild;
    private HashMap<String,Boolean> DOWMap;
    private HashMap<String,CheckBox> DOWCheckBox;
    private String[] woddays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        timerSettingText = (EditText)findViewById(R.id.SettingTime);
        isAlarmSettingSwitch = (Switch)findViewById(R.id.isAlarmActive);
        //リスナー設定
        LogUtil.LogString("setonchangedlistenaer");
        isAlarmSettingSwitch.setOnCheckedChangeListener(this);
        settingTimePicker = (TimePicker)findViewById(R.id.timePickerChild);
        settingTimePickerChild = (TimePicker)findViewById(R.id.timePickerChild);

        dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        dataEditor = dataStore.edit();
        //曜日checkboxを配列で取得
        DOWCheckBox = new HashMap<String,CheckBox>();
        DOWCheckBox.put("Mon",(CheckBox) findViewById(R.id.checkMon));
        DOWCheckBox.put("Tue",(CheckBox) findViewById(R.id.checkTue));
        DOWCheckBox.put("Wed",(CheckBox) findViewById(R.id.checkWed));
        DOWCheckBox.put("Thu",(CheckBox) findViewById(R.id.checkThu));
        DOWCheckBox.put("Fri",(CheckBox) findViewById(R.id.checkFri));
        DOWCheckBox.put("Sat",(CheckBox) findViewById(R.id.checkSat));
        DOWCheckBox.put("Sun",(CheckBox) findViewById(R.id.checkSun));
        woddays = new String[]{"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
    }

    @Override
    protected void onResume(){
        super.onResume();
        //設定してある値をUIに反映
        RefrectSettingData();

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
        SaveSettingData();
    }

    private void SaveSettingData(){
        //アラームのOn/Off
        boolean isAlarmSet = isAlarmSettingSwitch.isChecked();
        dataEditor.putBoolean("isAlarmSet", isAlarmSet);
        //Timerの設定値を保存
        Log.d("Destroy", "onpause call");
        int alarmTimeHour = settingTimePicker.getCurrentHour();
        dataEditor.putInt("alarmTimeHour", alarmTimeHour);
        int alarmTimeMinutes = settingTimePicker.getCurrentMinute();
        dataEditor.putInt("alarmTimeMinutes", alarmTimeMinutes);

        //曜日のチェック情報を保存
        //HashMapのsaveはObjectOutputStreamに書き換えたい　http://stackoverflow.com/questions/7944601/saving-a-hash-map-into-shared-preferences
        SaveDOWChecks();
        dataEditor.commit();
    }

    private void SaveDOWChecks(){
        HashMap<String,Boolean> maps = new HashMap<String,Boolean>();
        for (String dow:woddays) {
            maps.put(dow, DOWCheckBox.get(dow).isChecked());
        }
        //ファイルにhasmapの情報を書き出し
        File file = new File(getDir("data", MODE_PRIVATE), "map");
        try {
            LogUtil.LogString("try");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(maps);
            outputStream.flush();
            outputStream.close();
        }catch (Exception ex){
            LogUtil.LogString("catch" + ex.toString());
        }
    }


    private void RefrectSettingData(){
        //設定値を読み込み
        boolean d_isAlarmSetting = dataStore.getBoolean("isAlarmSet", false);
        int d_alarmHour = dataStore.getInt("alarmTimeHour", 0);
        int d_alarmMinutes = dataStore.getInt("alarmTimeMinutes", 0);

        //画面に反映
        isAlarmSettingSwitch.setChecked(d_isAlarmSetting);
        settingTimePicker.setCurrentHour(d_alarmHour);
        settingTimePicker.setCurrentMinute(d_alarmMinutes);

        //曜日部分
        try {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            DOWMap = (HashMap)ois.readObject();
            for (String dow:woddays) {
                CheckBoxReflect(dow);
            }
        }catch (Exception e){
            LogUtil.LogString("catch" + e.toString());
        }
    }

    private void CheckBoxReflect(String dow){
        boolean isCheck = DOWMap.get(dow);
        DOWCheckBox.get(dow).setChecked(isCheck);
    }

    //アラーム設定が変更されたときにその他の設定項目のOn/Offを切り替え
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ChangeSettingState(isChecked);
    }

    private void ChangeSettingState(boolean canSet){
            settingTimePicker.setEnabled(canSet);
            settingTimePickerChild.setEnabled(canSet);
            for (String dow:woddays) {
                DOWCheckBox.get(dow).setEnabled(canSet);
            }
    }
}
