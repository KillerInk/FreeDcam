package com.troop.freedcam.ui.handler;

import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.manager.MyTimer;
import com.troop.freedcam.ui.MainActivity_v2;

/**
 * Created by troop on 26.11.2014.
 */
public class TimerHandler implements I_ModuleEvent, I_RecorderStateChanged
{
    TextView timerText;

    private final MainActivity_v2 mainActivity_v2;
    MyTimer timer;

    public TimerHandler(MainActivity_v2 mainActivity_v2)
    {
        this.mainActivity_v2 = mainActivity_v2;
        this.timerText = (TextView) mainActivity_v2.findViewById(R.id.textView_RecCounter);
        timer = new MyTimer(timerText);
    }

    @Override
    public String ModuleChanged(final String module)
    {
        mainActivity_v2.runOnUiThread(new Runnable() {
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
        }

    }
}
