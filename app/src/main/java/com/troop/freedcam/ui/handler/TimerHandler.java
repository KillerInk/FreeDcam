package com.troop.freedcam.ui.handler;

import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.MainActivity;
import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.manager.MyTimer;

/**
 * Created by troop on 26.11.2014.
 */
public class TimerHandler implements I_ModuleEvent, I_RecorderStateChanged
{
    TextView timerText;

    private final MainActivity mainActivity;
    MyTimer timer;

    public TimerHandler(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        this.timerText = (TextView) mainActivity.findViewById(R.id.textView_RecCounter);
        timer = new MyTimer(timerText);
        timerText.setVisibility(View.GONE);
    }

    @Override
    public String ModuleChanged(final String module)
    {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (module.equals(ModuleHandler.MODULE_VIDEO))
                    timerText.setVisibility(View.VISIBLE);
                else
                    timerText.setVisibility(View.GONE);
            }
        });

        return null;
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
