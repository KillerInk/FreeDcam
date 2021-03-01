package freed.cam.apis.camera2;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class ExtFlashTrigger
{
    private byte[] trigger_signal;
    private AudioTrack audioplayer;
    private boolean preCaptureRunning = false;

    public ExtFlashTrigger()
    {
        //create flash trigger signal
        //i dont know how it have to look like
        trigger_signal = new byte[2];
        trigger_signal[0] = (byte)255;
        trigger_signal[1] = (byte)255;

        int bufsize = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT);
        audioplayer = new AudioTrack(AudioManager.STREAM_MUSIC,
                8000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT, bufsize,
                AudioTrack.MODE_STREAM);
    }

    public void triggerFlash()
    {
        send(trigger_signal);
    }

    /**
     * send a continouse signal while
     */
    public void preCaptureFlash()
    {
        preCaptureRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (preCaptureRunning)
                    triggerFlash();
            }
        }).start();
    }

    public void stopPreCapture()
    {
        preCaptureRunning = false;
    }

    private void send(byte[] bytes_pkg) {

        audioplayer.play();
        audioplayer.write(bytes_pkg, 0, bytes_pkg.length);
    }

    public void close()
    {
        preCaptureRunning = false;
        if (audioplayer != null)
            audioplayer.stop();
        audioplayer = null;
    }

}
