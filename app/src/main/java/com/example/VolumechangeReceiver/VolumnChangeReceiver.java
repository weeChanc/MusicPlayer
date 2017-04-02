package com.example.VolumechangeReceiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class VolumnChangeReceiver extends BroadcastReceiver {

    static AlertDialog alertDialog = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Fuck you your volumn is tooooooooo big");
        dialog.setMessage("Fucking Fucking Fucking");
        dialog.setPositiveButton("ensure", null);


        if (alertDialog == null)

            if (intent.getExtras().getInt("android.media.EXTRA_VOLUME_STREAM_VALUE") > (int) (max * 0.5))
                alertDialog = dialog.show();
            else {
                alertDialog = null;
            }
    }
}
