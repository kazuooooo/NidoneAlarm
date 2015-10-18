package com.example.matsumotokazuya.mynidonealarm;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.util.HashMap;

public class Setting extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, TimePickerDialog.OnTimeSetListener {
    private SharedPreferences dataStore;
    private SharedPreferences.Editor dataEditor;
    private Switch isAlarmSettingSwitch;
    private HashMap<String,Boolean> DOWMap;
    private HashMap<String,CheckBox> DOWCheckBox;
    private String[] woddays;
    private FragmentActivity fragmentActivity;
    private TextView timeTextD;
    private int dekiHour;
    private int dekiMinutes;
    private TextView timeTextY;
    private int yabaHour;
    private int yabaMinutes;
    private static enum TimePickerSettingState{
        DEKISETTING,
        YABASETTING,
        NONE
    }
    public static TimePickerSettingState tpState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        isAlarmSettingSwitch = (Switch)findViewById(R.id.isAlarmActive);
        //リスナー設定
        LogUtil.LogString("setonchangedlistenaer");
        isAlarmSettingSwitch.setOnCheckedChangeListener(this);

        //設定時間テキスト
        timeTextD = (TextView)findViewById(R.id.TimeTextD);
        timeTextY = (TextView)findViewById(R.id.TimeTextY);


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
    //OnPauseでアラームを設定
    @Override
    public void onPause(){
        super.onPause();
        SaveSettingData();
    }

    private void SaveSettingData(){
        //アラームのOn/Off
        boolean isAlarmSet = isAlarmSettingSwitch.isChecked();
        dataEditor.putBoolean("isAlarmSet", isAlarmSet);

        //曜日のチェック情報を保存
        //HashMapのsaveはObjectOutputStreamに書き換えたい　http://stackoverflow.com/questions/7944601/saving-a-hash-map-into-shared-preferences
        SaveDOWChecks();
        dataEditor.commit();

        LogUtil.LogString("Call AlarmSetting!!");
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


    @Override
    public void onTimeSet(TimePicker view, int hour, int minutes) {

        LogUtil.LogString("tpState" + tpState.toString());
        switch (tpState){
            case DEKISETTING:
                dataEditor.putInt("dekiAlarmTimeHour", hour);
                dataEditor.putInt("dekiAlarmTimeMinutes", minutes);
                dataEditor.commit();
                LogUtil.LogString("save deki"+"hour"+hour+"minutes"+minutes);
                SetTextTime(timeTextD, hour, minutes);
                break;
            case YABASETTING:
                dataEditor.putInt("yabaAlarmTimeHour", hour);
                dataEditor.putInt("yabaAlarmTimeMinutes", minutes);
                dataEditor.commit();
                LogUtil.LogString("save yaba" + "hour" + hour + "minutes" + minutes);
                SetTextTime(timeTextY, hour, minutes);
                break;
        }
        tpState = TimePickerSettingState.NONE;
    }

    public void showDekiTimePickerDialog(View v) {
        tpState = TimePickerSettingState.DEKISETTING;
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "dekitimePicker");
    }

    public void showYabaTimePickerDialog(View v) {
        tpState = TimePickerSettingState.YABASETTING;
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "yabatimePicker");
    }

    private void SetTextTime(TextView v,int hour,int minutes){
        String hourString = ParseUtil.ParseIntToString(hour);
        String minuteString = ParseUtil.ParseIntToString(minutes);
        if(hour<10){
            hourString = "0"+hourString;
        }
        if(minutes<10){
            minuteString = "0"+minuteString;
        }
        v.setText(hourString+":"+minuteString);
    }

    private void RefrectSettingData(){
        //設定値を読み込み
        boolean d_isAlarmSetting = dataStore.getBoolean("isAlarmSet", false);
        dekiHour = dataStore.getInt("dekiAlarmTimeHour", 0);
        dekiMinutes = dataStore.getInt("dekiAlarmTimeMinutes", 0);
        yabaHour = dataStore.getInt("yabaAlarmTimeHour",0);
        yabaMinutes = dataStore.getInt("yabaAlarmTimeMinutes",0);

        //画面に反映
        isAlarmSettingSwitch.setChecked(d_isAlarmSetting);
        ChangeSettingState(d_isAlarmSetting);
        SetTextTime(timeTextD, dekiHour, dekiMinutes);
        SetTextTime(timeTextY,yabaHour,yabaMinutes);

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
            //入力可否
            timeTextY.setEnabled(canSet);
            timeTextD.setEnabled(canSet);
            for (String dow:woddays) {
                DOWCheckBox.get(dow).setEnabled(canSet);
            }
            //Alpha変更
        if(canSet) {
            timeTextY.setAlpha(1);
            timeTextD.setAlpha(1);
            for (String dow:woddays) {
                DOWCheckBox.get(dow).setAlpha(1);
            }
        }else{
            timeTextY.setAlpha(0.7f);
            timeTextD.setAlpha(0.7f);
            for (String dow:woddays) {
                DOWCheckBox.get(dow).setAlpha(0.7f);
            }
        }
    }
}
