package com.troop.freedcam.ui.handler;

import android.view.View;
import android.widget.ProgressBar;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.MainActivity_v2;

/**
 * Created by Ingo on 25.12.2014.
 */
public class WorkHandler  implements AbstractModuleHandler.I_worker
{
    ProgressBar spinner;
    MainActivity_v2 activity;

    public WorkHandler(MainActivity_v2 mainActivity_v2)
    {
        this.spinner = (ProgressBar) mainActivity_v2.findViewById(R.id.loadingspinner);
        this.activity = mainActivity_v2;
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onWorkStarted() {
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onWorkFinished(boolean finished)
    {
        if (finished)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    spinner.setVisibility(View.GONE);
                }
            });

    }
}
