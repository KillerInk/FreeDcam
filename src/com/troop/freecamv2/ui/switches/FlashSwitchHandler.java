package com.troop.freecamv2.ui.switches;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.parameters.I_ParametersLoaded;
import com.troop.freecamv2.ui.AppSettingsManager;
import com.troop.freecamv2.ui.MainActivity_v2;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class FlashSwitchHandler implements View.OnClickListener, I_ParametersLoaded
{
    MainActivity_v2 activity;
    CameraUiWrapper cameraUiWrapper;
    TextView textView;
    AppSettingsManager appSettingsManager;

    public FlashSwitchHandler(MainActivity_v2 activity, CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        textView = (TextView)activity.findViewById(R.id.textView_flashSwitch);
        textView.setOnClickListener(this);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
    }

    @Override
    public void onClick(View v) {
        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.moduleSwitch_placeholder));

        for (String o : cameraUiWrapper.camParametersHandler.FlashMode.GetValues())
        {
            popupMenu.getMenu().add((CharSequence) o);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                cameraUiWrapper.camParametersHandler.FlashMode.SetValue(tmp);
                appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, tmp);
                textView.setText(tmp);
                return true;
            }
        });

        popupMenu.show();
    }

    @Override
    public void ParametersLoaded()
    {
        String appSet = appSettingsManager.getString(AppSettingsManager.SETTING_FLASHMODE);
        String para = cameraUiWrapper.camParametersHandler.FlashMode.GetValue();
        if (appSet.equals("")) {
            appSet = cameraUiWrapper.camParametersHandler.FlashMode.GetValue();
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, para);
        }
        if (!appSet.equals(para))
            cameraUiWrapper.camParametersHandler.FlashMode.SetValue(appSet);


        textView.setText(appSet);
    }
}
