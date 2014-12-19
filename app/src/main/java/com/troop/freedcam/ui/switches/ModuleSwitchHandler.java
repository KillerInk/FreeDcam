package com.troop.freedcam.ui.switches;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
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
    HashMap<String,String> modules;
    TextView moduleView;
    ListView listView;

    public ModuleSwitchHandler(MainActivity_v2 activity, AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        this.moduleHandler = cameraUiWrapper.moduleHandler;
        modules = new HashMap<String, String>();
        modules.put("Pic", ModuleHandler.MODULE_PICTURE);
        modules.put("LoEx", ModuleHandler.MODULE_LONGEXPO);
        modules.put("Video", ModuleHandler.MODULE_VIDEO);
        modules.put("HDR", ModuleHandler.MODULE_HDR);
        //modules.put("Burst", ModuleHandler.MODULE_BURST);
        moduleView = (TextView)activity.findViewById(R.id.textView_ModuleSwitch);
        moduleView.setOnClickListener(this);

        moduleView.setText(GetKeyFromValue(appSettingsManager.GetCurrentModule()));
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);

    }


    //shows the popupmenu
    @Override
    public void onClick(View v)
    {
        listView = (ListView) activity.findViewById(R.id.listView_popup);
        List<String> mods = new ArrayList<String>();
        for (HashMap.Entry<String,String> o : modules.entrySet())
        {
            mods.add((String) o.getKey());
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
                appSettingsManager.SetCurrentModule(modules.get(value));
                moduleHandler.SetModule(modules.get(value));

                moduleView.setText(value);
                listView.setVisibility(View.GONE);
            }
        });

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

    @Override
    public void ParametersLoaded() {
        moduleHandler.SetModule(appSettingsManager.GetCurrentModule());
    }
}
