package com.troop.freecamv2.ui.switches;

import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.ModuleHandler;
import com.troop.freecamv2.ui.AppSettingsManager;
import com.troop.freecamv2.ui.MainActivity_v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    ListView listView;

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
                moduleHandler.SetModule(modules.get(value));
                appSettingsManager.SetCurrentModule(modules.get(value));
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
}
