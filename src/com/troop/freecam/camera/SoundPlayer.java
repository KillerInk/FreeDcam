package com.troop.freecam.camera;

import android.content.Context;
import android.media.MediaPlayer;

import com.troop.freecam.R;

/**
 * Created by troop on 18.10.13.
 */
public class SoundPlayer implements  MediaPlayer.OnCompletionListener
{
    MediaPlayer player;
    Context context;

    public SoundPlayer(Context context)
    {
        this.context = context;
    }

    public void PlayShutter()
    {
        player = MediaPlayer.create(context, R.raw.camerashutter);
        player.setOnCompletionListener(this);
        player.start();
    }

    public void PlayFocus()
    {
        player = MediaPlayer.create(context, R.raw.camerafocus);
        player.setOnCompletionListener(this);
        player.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        mp.release();
        mp = null;
    }
}
