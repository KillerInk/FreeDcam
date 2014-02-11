package com.troop.freecam.manager;

import android.content.Context;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;

import com.troop.freecam.R;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by troop on 18.10.13.
 */
public class SoundPlayer implements  MediaPlayer.OnCompletionListener
{
    //MediaPlayer player;
    Context context;
    private SoundPool soundPool;
    boolean loaded = false;
    int shutterID;
    int focusID;

    private Handler handler = new Handler();

    public SoundPlayer(Context context)
    {
        this.context = context;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });
        shutterID = soundPool.load(context, R.raw.camerashutter, 1);
        focusID = soundPool.load(context, R.raw.camerafocus, 1);
    }

    public void PlayShutter()
    {
        handler.post(playShutter);
        /*player = MediaPlayer.create(context, R.raw.camerashutter);
        player.setOnCompletionListener(this);
        player.start();*/
    }

    Runnable playShutter = new Runnable() {
        @Override
        public void run() {
            if (true /*!DeviceUtils.isEvo3d()*/)
            {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                float actualVolume = (float) audioManager
                        .getStreamVolume(AudioManager.STREAM_RING);
                float maxVolume = (float) audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_RING);
                float volume = actualVolume / maxVolume;
                // Is the sound loaded already?
                if (loaded)
                    soundPool.play(shutterID, volume, volume, 1, 0, 1f);
            }
        }
    };

    public void PlayFocus()
    {
        /*player = MediaPlayer.create(context, R.raw.camerafocus);
        player.setOnCompletionListener(this);
        player.start();*/
        handler.post(playFocus);

    }

    Runnable playFocus = new Runnable() {
        @Override
        public void run() {
            if (true /*!DeviceUtils.isEvo3d()*/)
            {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                float actualVolume = (float) audioManager
                        .getStreamVolume(AudioManager.STREAM_RING);
                float maxVolume = (float) audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_RING);
                float volume = actualVolume / maxVolume;
                //soundPool.setVolume(focusID,1,1);
                // Is the sound loaded already?
                if (loaded)
                    soundPool.play(focusID, volume, volume, 1, 0, 1f);
            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        /*mp.release();
        mp = null;*/
    }
}
