package com.troop.freecamv2.ui.switches;

import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.ModuleHandler;
import com.troop.freecamv2.ui.AppSettingsManager;
import com.troop.freecamv2.ui.MainActivity_v2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by troop on 20.08.2014.
 */
public class ModuleSwitchHandler implements View.OnClickListener
{

    MainActivity_v2 activity;
    CameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    ModuleHandler moduleHandler;
    HashMap<String,String> modules;
    TextView moduleView;

    public ModuleSwitchHandler(MainActivity_v2 activity, CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        this.moduleHandler = cameraUiWrapper.moduleHandler;
        modules = new HashMap<String, String>();
        modules.put("Pic", ModuleHandler.MODULE_PICTURE);
        modules.put("Video", ModuleHandler.MODULE_VIDEO);
        modules.put("HDR", ModuleHandler.MODULE_HDR);
        modules.put("Burst", ModuleHandler.MODULE_BURST);
        moduleView = (TextView)activity.findViewById(R.id.textView_ModuleSwitch);
        moduleView.setOnClickListener(this);
        moduleHandler.SetModule(appSettingsManager.GetCurrentModule());
        moduleView.setText(GetKeyFromValue(appSettingsManager.GetCurrentModule()));

    }

    @Override
    public void onClick(View v)
    {
        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.moduleSwitch_placeholder));

        for (HashMap.Entry<String,String> o : modules.entrySet())
        {
            popupMenu.getMenu().add((CharSequence) o.getKey());
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                moduleHandler.SetModule(modules.get(tmp));
                appSettingsManager.SetCurrentModule(modules.get(tmp));
                moduleView.setText(tmp);
                return true;
            }
        });

        popupMenu.show();
    }


    private String GetKeyFromValue(String Value)
    {
        if (modules.containsValue(Value))
        {
            for (HashMap.Entry<String,String> o : modules.entrySet())
            {
                if (o.getValue().equals(Value))
                    return o.getKey();
            }
        }
        return  null;
    }
}
