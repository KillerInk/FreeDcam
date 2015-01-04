package com.troop.freedcam.ui.switches;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 20.08.2014.
 */
public class ModuleSwitchHandler implements View.OnClickListener, I_ParametersLoaded
{

    MainActivity_v2 activity;
    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    AbstractModuleHandler moduleHandler;
    TextView moduleView;
    ListView listView;

    public ModuleSwitchHandler(MainActivity_v2 activity, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;
        moduleView = (TextView)activity.findViewById(R.id.textView_ModuleSwitch);
        moduleView.setOnClickListener(this);
        moduleView.setVisibility(View.GONE);

    }


    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.moduleHandler = cameraUiWrapper.moduleHandler;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
    }

    //shows the popupmenu
    @Override
    public void onClick(View v)
    {
        listView = (ListView) activity.findViewById(R.id.listView_popup);
        List<String> mods = new ArrayList<String>();
        for (HashMap.Entry<String,AbstractModule> module : cameraUiWrapper.moduleHandler.moduleList.entrySet())
        {
            mods.add(module.getValue().LongName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                R.layout.simpel_list_item_v2, R.id.textView_simple_list_item_v2, mods);
        //attach adapter to the listview and fill
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) listView.getItemAtPosition(position);
                for (HashMap.Entry<String,AbstractModule> module : cameraUiWrapper.moduleHandler.moduleList.entrySet())
                {
                    if (value.equals(module.getValue().LongName()))
                    {
                        appSettingsManager.SetCurrentModule(module.getValue().name);
                        moduleHandler.SetModule(module.getValue().name);

                        moduleView.setText(module.getValue().ShortName());
                        break;
                    }

                }

                listView.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void ParametersLoaded() {
        moduleHandler.SetModule(appSettingsManager.GetCurrentModule());
        moduleView.setText(moduleHandler.GetCurrentModule().ShortName());
        moduleView.setVisibility(View.VISIBLE);
    }
}
