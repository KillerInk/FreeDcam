package com.troop.freedcam.ui.switches;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 26.09.2014.
 */
public class NightModeSwitchHandler implements View.OnClickListener, I_ParametersLoaded
{
    protected View activity;
    protected AbstractCameraUiWrapper cameraUiWrapper;
    TextView textView;
    protected AppSettingsManager appSettingsManager;
    ListView listView;
    protected Fragment fragment;

    public NightModeSwitchHandler(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        this.activity = activity;

        this.appSettingsManager = appSettingsManager;

        init();
    }

    protected void init()
    {
        textView = (TextView)activity.findViewById(R.id.textView_nightmode);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
    }

    @Override
    public void ParametersLoaded()
    {
        if (textView != null)
        {
            activity.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (cameraUiWrapper.camParametersHandler.NightMode != null && cameraUiWrapper.camParametersHandler.NightMode.IsSupported())
                    {
                        textView.setVisibility(View.VISIBLE);
                        String appSet = appSettingsManager.getString(AppSettingsManager.SETTING_NIGHTEMODE);
                        textView.setText(appSet);
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View v)
    {
        listView = (ListView) fragment.getActivity().findViewById(R.id.listView_popup);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity.getContext(),
                R.layout.simpel_list_item_v2, R.id.textView_simple_list_item_v2, cameraUiWrapper.camParametersHandler.NightMode.GetValues());
        //attach adapter to the listview and fill
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) listView.getItemAtPosition(position);
                cameraUiWrapper.camParametersHandler.FlashMode.SetValue(value, true);
                appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, value);
                textView.setText(value);
                listView.setVisibility(View.GONE);
            }
        });
    }
}
