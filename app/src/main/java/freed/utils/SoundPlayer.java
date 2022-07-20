package freed.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.troop.freedcam.R;

public class SoundPlayer {
    private final SoundPool soundPool;
    private final int streams = 1;
    private final int shutterid;

    public SoundPlayer(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(streams)
                    .build();
        } else {
            soundPool = new SoundPool(streams, AudioManager.STREAM_MUSIC, 0);
        }
        shutterid = soundPool.load(context, R.raw.shutter_01,0);
    }

    public void play()
    {
        soundPool.play(shutterid,1,1,1,0,1);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        soundPool.release();
    }
}
