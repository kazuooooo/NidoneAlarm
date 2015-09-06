package jp.co.spookies.android.alarm;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Config extends PreferenceActivity {
	private static final int REQUEST_CODE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		setVolumeControlStream(AudioManager.STREAM_ALARM);
		Preference prefEnabled = findPreference(getString(R.string.pref_key_enabled));
		prefEnabled
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Boolean enabled = (Boolean) newValue;
						if (enabled) {
							setAlarm();
						} else {
							stopAlarm();
						}
						return true;
					}
				});
		Preference prefTime = findPreference(getString(R.string.pref_key_time));
		prefTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if (getEnabled()) {
					stopAlarm();
					setAlarm();
				}
				return true;
			}
		});
	}

	private void setAlarm() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, BellService.class);
		PendingIntent sender = PendingIntent.getService(this, REQUEST_CODE,
				intent, 0);
		int[] time = TimePickerPreference.parseTime(getTime());
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				getTriggerAtTime(time[0], time[1]), AlarmManager.INTERVAL_DAY,
				sender);
	}

	private void stopAlarm() {
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, BellService.class);
		PendingIntent sender = PendingIntent.getService(this, REQUEST_CODE,
				intent, 0);
		manager.cancel(sender);
	}

	private long getTriggerAtTime(int hour, int minute) {
		Date now = new Date();
		long time = SystemClock.elapsedRealtime();
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(new Date());
		Calendar targetCalendar = Calendar.getInstance();
		targetCalendar.setTime(now);
		targetCalendar.set(Calendar.HOUR_OF_DAY, hour);
		targetCalendar.set(Calendar.MINUTE, minute);
		targetCalendar.set(Calendar.SECOND, 0);
		if (targetCalendar.before(nowCalendar)) {
			targetCalendar.add(Calendar.DATE, 1);
		}
		return time + targetCalendar.getTimeInMillis()
				- nowCalendar.getTimeInMillis();
	}

	private int getTime() {
		return PreferenceManager.getDefaultSharedPreferences(this).getInt(
				getString(R.string.pref_key_time), 0);
	}

	private boolean getEnabled() {
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				getString(R.string.pref_key_enabled), false);
	}
}
