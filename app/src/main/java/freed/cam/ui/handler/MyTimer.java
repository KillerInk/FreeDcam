package freed.cam.ui.handler;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

/**
 * Created by troop on 13.10.13.
 */
public class MyTimer
{
    private final TextView textView;
    private Integer secondsDone = 0;
    private Integer fps = 30;
    private boolean stop;

    private final int interval = 1000/fps; // 1 Second
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable()
    {
        public void run()
        {
            secondsDone++;
            int hour = secondsDone / (3600 * fps);
            int min = secondsDone / (60*fps) % 60;
            int sec = secondsDone / fps % 60;
            int frms = secondsDone % fps;
            textView.setText(String.format("%02d:%02d:%02d.%02d", hour, min, sec,frms));
            if(!stop)
                handler.postDelayed(runnable, interval);
        }
    };

    public MyTimer(TextView textView)
    {
        this.textView = textView;
    }


    public  void Start()
    {
        stop = false;
        handler.postDelayed(runnable, interval);
    }

    public void Stop()
    {
        stop = true;
        secondsDone = 0;
        textView.setText(String.format("%d:%02d:%02d", 0, 0, 0));
    }


}
