package com.troop.freedcam.ui.menu.themes.classic;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.menu.themes.R;


/**
 * Created by Ingo on 25.12.2014.
 */
public class WorkHandler  implements AbstractModuleHandler.I_worker
{
    ProgressBar spinner;
    View activity;

    private static String TAG = WorkHandler.class.getSimpleName();

    public WorkHandler(View mainActivity_v2)
    {
        this.spinner = (ProgressBar) mainActivity_v2.findViewById(R.id.loadingspinner);
        this.activity = mainActivity_v2;
        HideSpinner();
    }

    public void HideSpinner()
    {
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onWorkStarted()
    {
        activity.post(new Runnable() {
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
                        HideSpinner();
                }
            });

    }
}
