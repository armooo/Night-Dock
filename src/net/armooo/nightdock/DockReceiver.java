package net.armooo.nightdock;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.media.AudioManager;
import  android.preference.Preference;

public class DockReceiver extends BroadcastReceiver {

    public void onReceive (Context context, Intent intent) {

        int dock_state = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
        if (dock_state == Intent.EXTRA_DOCK_STATE_DESK) {
            //intent.setClass(context, DockReceiver.class);
            //context.startService(intent);

            AudioManager audio_mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            audio_mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);


        } else if (dock_state == Intent.EXTRA_DOCK_STATE_UNDOCKED){

            AudioManager audio_mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            audio_mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audio_mgr.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);

        }
    }

}
