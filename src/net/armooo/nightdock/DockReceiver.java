package net.armooo.nightdock;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import android.util.Log;

import com.nullwire.trace.ExceptionHandler;

public class DockReceiver extends BroadcastReceiver {

    private static final String TAG = "DockReceiver";

    private String OLD_NOTIFICATION_VOLUME = "old_note_volume";
    private String OLD_NOTIFICATION_VIBRATE = "old_note_vibrate";
    private String OLD_RINGER_VOLUME = "old_ring_volume";
    private String OLD_RINGER_VIBRATE = "old_ring_vibrate";
    private String OLD_NOTIF_USE_RING = "old_notif_use_ring";
    private String OLD_NOTIF_LED = "old_notif_led";

    private SharedPreferences prefs;
    private AudioManager audio_mgr;
    private Context context;

    public void onReceive (Context context, Intent intent) {
        this.context = context;
        ExceptionHandler.register(context);

        Log.v(TAG, "got intent");

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int dock_state = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
        if (dock_state == Intent.EXTRA_DOCK_STATE_DESK) {
            Log.v(TAG, "in a desk dock");
            audio_mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            saveSettings();
            updateSettings();
            Editor editer = prefs.edit();
            editer.putBoolean("docked", true);
            editer.commit();

        } else if (dock_state == Intent.EXTRA_DOCK_STATE_UNDOCKED){
            Log.v(TAG, "undocking");
            if (getPref("docked")){
                Log.v(TAG, "puting setting back");
                audio_mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                restoreSettings();
                Editor editer = prefs.edit();
                editer.putBoolean("docked", false);
                editer.commit();
            } else {
                Log.v(TAG, "not puting setting back");
            }
        }
    }

    private void saveSettings() {
        Log.v(TAG, "saving settings");
        Editor editer = prefs.edit();

        try {
            editer.putInt(OLD_NOTIF_USE_RING,
                Settings.System.getInt(
                    context.getContentResolver(),
                    "notifications_use_ring_volume"
                )
            );
        } catch (Settings.SettingNotFoundException e) {
            editer.putInt(OLD_NOTIF_USE_RING, 0);
        }

        try {
            editer.putInt(OLD_NOTIF_LED,
                Settings.System.getInt(
                    context.getContentResolver(),
                    "notification_light_pulse"
                )
            );
        } catch (Settings.SettingNotFoundException e) {
            editer.putInt(OLD_NOTIF_LED, 1);
        }

        editer.putInt(OLD_NOTIFICATION_VOLUME,
            audio_mgr.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
        editer.putInt(OLD_NOTIFICATION_VIBRATE,
            audio_mgr.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION));

        editer.putInt(OLD_RINGER_VOLUME,
            audio_mgr.getStreamVolume(AudioManager.STREAM_RING));
        editer.putInt(OLD_RINGER_VIBRATE,
            audio_mgr.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER));

        editer.commit();
    }

    private void updateSettings() {
        Log.v(TAG, "seting dock settings");
        Settings.System.putInt(
            context.getContentResolver(),
            "notifications_use_ring_volume",
            0);

        if (getPref("notification_led")) {
            Settings.System.putInt(
                context.getContentResolver(),
                "notification_light_pulse",
                0);
        }

        if (getPref("notification_sound")) {
            audio_mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        }
        if (getPref("notification_vibrate")) {
            audio_mgr.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
                AudioManager.VIBRATE_SETTING_OFF);
        }

        if (getPref("ringer_sound")) {
            audio_mgr.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        }
        if (getPref("ringer_vibrate")) {
            audio_mgr.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                AudioManager.VIBRATE_SETTING_OFF);
        }
    }

    private void restoreSettings() {
        Log.v(TAG, "puting settings back");

        if (getPref("notification_led")) {
            Settings.System.putInt(
                context.getContentResolver(),
                "notification_light_pulse",
                prefs.getInt(OLD_NOTIF_LED, 1));
        }

        if (getPref("notification_sound")) {
            audio_mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                prefs.getInt(OLD_NOTIFICATION_VOLUME, 0), 0);
        }
        if (getPref("notification_vibrate")) {
            audio_mgr.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
                prefs.getInt(OLD_NOTIFICATION_VIBRATE, 0));
        }

        if (getPref("ringer_sound")) {
            audio_mgr.setStreamVolume(AudioManager.STREAM_RING,
                prefs.getInt(OLD_RINGER_VOLUME, 0), 0);
        }
        if (getPref("ringer_vibrate")) {
            audio_mgr.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                prefs.getInt(OLD_RINGER_VIBRATE, 0));
        }

        Settings.System.putInt(
            context.getContentResolver(),
            "notifications_use_ring_volume",
            prefs.getInt(OLD_NOTIF_USE_RING, 0)
        );

        Editor editer = prefs.edit();
        editer.remove(OLD_NOTIFICATION_VOLUME);
        editer.remove(OLD_NOTIFICATION_VIBRATE);

        editer.remove(OLD_RINGER_VOLUME);
        editer.remove(OLD_RINGER_VIBRATE);

        editer.remove(OLD_NOTIF_USE_RING);
        editer.commit();
    }

    private boolean getPref(String key){
        boolean default_ = false;
        if ("notification_sound".equals(key) || "notification_vibrate".equals(key)) {
            default_ = true;
        }
        return prefs.getBoolean(key, default_);
    }
}
