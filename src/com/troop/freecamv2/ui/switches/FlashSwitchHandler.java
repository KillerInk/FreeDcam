package com.troop.freecamv2.ui.switches;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.ModuleHandler;
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
    ListView listView;

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
    public void onClick(View v)
    {
        if (!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_VIDEO)) {
            listView = (ListView) activity.findViewById(R.id.listView_popup);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                    R.layout.simpel_list_item_v2, R.id.textView_simple_list_item_v2, cameraUiWrapper.camParametersHandler.FlashMode.GetValues());
            //attach adapter to the listview and fill
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String value = (String) listView.getItemAtPosition(position);
                    cameraUiWrapper.camParametersHandler.FlashMode.SetValue(value);
                    appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, value);
                    textView.setText(value);
                    listView.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            //TODO check if torch is supported
            if (textView.getText().equals("torch"))
            {
                cameraUiWrapper.camParametersHandler.FlashMode.SetValue("off");
                appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "off");
                textView.setText("off");
            }
            else
            {
                cameraUiWrapper.camParametersHandler.FlashMode.SetValue("torch");
                appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "torch");
                textView.setText("torch");
            }

        }
    }

    @Override
    public void ParametersLoaded()
    {
        if (cameraUiWrapper.camParametersHandler.FlashMode.IsSupported())
        {
            textView.setVisibility(View.VISIBLE);
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
        else
        {
            textView.setVisibility(View.GONE);
        }
    }
}
