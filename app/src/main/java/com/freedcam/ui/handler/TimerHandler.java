package com.freedcam.ui.handler;

import android.view.View;
import android.widget.TextView;

import com.freedcam.MainActivity;
import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.modules.I_RecorderStateChanged;
import com.troop.freedcam.R.id;

/**
 * Created by troop on 26.11.2014.
 */
public class TimerHandler implements I_ModuleEvent, I_RecorderStateChanged
{
    private TextView timerText;

    private final MainActivity mainActivity;
    private MyTimer timer;

    public TimerHandler(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        timerText = (TextView) mainActivity.findViewById(id.textView_RecCounter);
        timer = new MyTimer(timerText);
        timerText.setVisibility(View.GONE);
    }

    @Override
    public void ModuleChanged(final String module)
    {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (module.equals(KEYS.MODULE_VIDEO))
                    timerText.setVisibility(View.VISIBLE);
                else
                    timerText.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void RecordingStateChanged(int status)
    {
        switch (status) {
            case I_RecorderStateChanged.STATUS_RECORDING_STOP:
                timer.Stop();
                break;
            case I_RecorderStateChanged.STATUS_RECORDING_START :
                timer.Start();
                break;
            default:
                timer.Stop();
                break;
        }

    }
}
