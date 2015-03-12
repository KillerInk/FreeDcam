package com.troop.freedcam.ui.switches;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;

/**
 * Created by troop on 21.08.2014.
 */
public class FlashSwitchHandler implements View.OnClickListener, I_ParametersLoaded, AbstractModeParameter.I_ModeParameterEvent
{
    View activity;
    AbstractCameraUiWrapper cameraUiWrapper;
    TextView textView;
    AppSettingsManager appSettingsManager;
    ListView listView;
    AbstractModeParameter flashmode;
    Fragment fragment;
    private static String TAG = FlashSwitchHandler.class.getSimpleName();

    public FlashSwitchHandler(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;
        this.fragment = fragment;
        textView = (TextView)activity.findViewById(R.id.textView_flashSwitch);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        Log.d(TAG,"Set Camera UI Wrapper");

    }

    @Override
    public void onClick(View v)
    {
        if (!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_VIDEO)) {
            listView = (ListView) fragment.getActivity().findViewById(R.id.listView_popup);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity.getContext(),
                    R.layout.simpel_list_item_v2, R.id.textView_simple_list_item_v2, cameraUiWrapper.camParametersHandler.FlashMode.GetValues());
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
        else
        {
            //TODO check if torch is supported
            if (textView.getText().equals("torch"))
            {
                cameraUiWrapper.camParametersHandler.FlashMode.SetValue("off", true);
                appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "off");
                textView.setText("off");
            }
            else
            {
                cameraUiWrapper.camParametersHandler.FlashMode.SetValue("torch", true);
                appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "torch");
                textView.setText("torch");
            }

        }
    }

    @Override
    public void ParametersLoaded()
    {
        if (cameraUiWrapper.camParametersHandler.FlashMode != null)
        {
            flashmode = cameraUiWrapper.camParametersHandler.FlashMode;
            flashmode.addEventListner(this);
            //flashmode.BackgroundIsSupportedChanged(true);
        }
        Log.d(TAG,"ParametersLoaded");
        activity.post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.camParametersHandler.FlashMode != null) {
                    textView.setVisibility(View.VISIBLE);
                    String appSet = appSettingsManager.getString(AppSettingsManager.SETTING_FLASHMODE);
                    String para = cameraUiWrapper.camParametersHandler.FlashMode.GetValue();
                    if (appSet.equals("")) {
                        appSet = cameraUiWrapper.camParametersHandler.FlashMode.GetValue();
                        appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, para);
                    }
                    if (!appSet.equals(para))
                        cameraUiWrapper.camParametersHandler.FlashMode.SetValue(appSet, true);


                    textView.setText(appSet);
                } else {
                    textView.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onValueChanged(final String val)
    {
        Log.d(TAG,"on Value Changed: " + val);
        activity.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(val);
            }
        });

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported)
    {
        ParametersLoaded();
    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }
}
