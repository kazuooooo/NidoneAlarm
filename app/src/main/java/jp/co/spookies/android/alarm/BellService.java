package jp.co.spookies.android.alarm;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class BellService extends Service {
	private NotificationManager notificationManager;
	private MediaPlayer player;

	@Override
	public void onCreate() {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		Uri uri = Uri.parse(pref.getString(
				getString(R.string.pref_key_ringtone), ""));
		player = new MediaPlayer();
		try {
			player.setDataSource(this, uri);
			player.setAudioStreamType(AudioManager.STREAM_ALARM);
			player.setLooping(true);
			player.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		notificationManager.cancel(R.string.app_name);
		player.pause();
		player.stop();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent bellIntent = new Intent(this, AlarmActivity.class);
		bellIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(bellIntent);
		Notification notification = new Notification(R.drawable.icon, "",
				System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				bellIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.app_name), "",
				contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notificationManager.notify(R.string.app_name, notification);
		player.start();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
