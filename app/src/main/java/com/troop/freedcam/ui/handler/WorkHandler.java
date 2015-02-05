package com.troop.freedcam.ui.handler;

import android.util.Log;
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

    private static String TAG = WorkHandler.class.getSimpleName();

    public WorkHandler(MainActivity_v2 mainActivity_v2)
    {
        this.spinner = (ProgressBar) mainActivity_v2.findViewById(R.id.loadingspinner);
        this.activity = mainActivity_v2;
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onWorkStarted()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onWorkFinished(final boolean finished)
    {
        Log.d(TAG, "workfinsihed =" + finished);

            spinner.post(new Runnable() {
                @Override
                public void run()
                {
                    if (finished)
                        spinner.setVisibility(View.GONE);
                }
            });

    }
}
