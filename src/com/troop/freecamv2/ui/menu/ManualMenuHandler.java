package com.troop.freecamv2.ui.menu;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.parameters.CamParametersHandler;
import com.troop.freecamv2.camera.parameters.I_ParametersLoaded;
import com.troop.freecamv2.camera.parameters.manual.BaseManualParameter;
import com.troop.freecamv2.ui.AppSettingsManager;
import com.troop.freecamv2.ui.MainActivity_v2;

import java.util.ArrayList;

/**
 * Created by troop on 01.09.2014.
 */
public class ManualMenuHandler implements SeekBar.OnSeekBarChangeListener, I_ParametersLoaded
{
    private final MainActivity_v2 activity;
    private final AppSettingsManager appSettingsManager;
    private final CameraUiWrapper cameraUiWrapper;
    private final SeekBar manualSeekbar;
    private final LinearLayout manualMenu;
    TextView seekbarText;
    ManualMenuItem currentItem;
    CamParametersHandler parametersHandler;

    int realMin;
    int realMax;
    int realCurrent;

    ArrayList<ManualMenuItem> manualItems;

    public ManualMenuHandler(MainActivity_v2 activity, CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
        parametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        manualSeekbar = (SeekBar)activity.findViewById(R.id.seekBar_manual);
        seekbarText = (TextView)activity.findViewById(R.id.textView_seekbar);
        manualSeekbar.setOnSeekBarChangeListener(this);
        manualMenu = (LinearLayout)activity.findViewById(R.id.v2_manual_menu);
        manualItems = new ArrayList<ManualMenuItem>();




    }

    private void addToLists(ManualMenuItem item)
    {
        manualItems.add(item);
        manualMenu.addView(item);
    }

    public void DisableOtherItems(String name)
    {
        for(ManualMenuItem item : manualItems)
        {
            if (!item.name.equals(name))
                item.DisableItem();
            else
            {
                currentItem = item;
                int min = item.manualParameter.GetMinValue();
                int max = item.manualParameter.GetMaxValue();
                setSeekbar_Min_Max(min, max);
                setSeekbarProgress(item.manualParameter.GetValue());
                setTextValue(item.manualParameter.GetValue());
            }
        }
    }

    private void setSeekbar_Min_Max(int min, int max)
    {
        realMin = min;
        realMax = max;
        if (min <0)
        {
            int m = max + min * -1;
            manualSeekbar.setMax(m);
        }
        else
            manualSeekbar.setMax(realMax);

    }

    private void setSeekbarProgress(int value)
    {
        if (realMin < 0)
        {
            manualSeekbar.setProgress(value - realMin);

        }
        else
        {
            manualSeekbar.setProgress(value);
        }
    }

    private void setValueToParameters(int value)
    {
        if (realMin < 0)
        {
            currentItem.manualParameter.SetValue(value + realMin);
            cameraUiWrapper.camParametersHandler.SetParametersToCamera();
            setTextValue(value + realMin);
        }
        else {
            currentItem.manualParameter.SetValue(value);
            cameraUiWrapper.camParametersHandler.SetParametersToCamera();
            setTextValue(value);
        }

    }

    private void setTextValue(int value)
    {
        seekbarText.setText(currentItem.name + ": " + value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (fromUser && currentItem != null)
        {
            setValueToParameters(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void ParametersLoaded()
    {
        manualMenu.removeAllViews();
        manualItems.clear();
        if (parametersHandler.ManualBrightness.IsSupported())
        {
            ManualMenuItem brightnes = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_brightness), this, parametersHandler.ManualBrightness);
            addToLists(brightnes);
        }
        if (parametersHandler.ManualContrast.IsSupported())
        {
            ManualMenuItem contrast = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_contrast), this, parametersHandler.ManualContrast);
            addToLists(contrast);
        }
        if (parametersHandler.ManualConvergence.IsSupported())
        {
            ManualMenuItem convergence = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_convergence), this, parametersHandler.ManualConvergence);
            addToLists(convergence);
        }
        if (parametersHandler.ManualExposure.IsSupported())
        {
            ManualMenuItem exposure = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_brightness), this, parametersHandler.ManualExposure);
            addToLists(exposure);
        }
        if (parametersHandler.ManualFocus.IsSupported())
        {
            ManualMenuItem focus = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_focus), this,parametersHandler.ManualFocus);
            addToLists(focus);
        }
        if (parametersHandler.ManualSaturation.IsSupported())
        {
            ManualMenuItem satu = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_saturation), this, parametersHandler.ManualSaturation);
            addToLists(satu);
        }
        if (parametersHandler.ManualSharpness.IsSupported())
        {
            ManualMenuItem sharp = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_sharpness), this, parametersHandler.ManualSharpness);
            addToLists(sharp);
        }
        if (parametersHandler.ManualShutter.IsSupported())
        {
            ManualMenuItem shutter = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_shutter), this, parametersHandler.ManualShutter);
            addToLists(shutter);
        }
        if (parametersHandler.Zoom.IsSupported())
        {
            ManualMenuItem zoom = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_zoom), this, parametersHandler.Zoom);
            addToLists(zoom);
            zoom.EnableItem();
            DisableOtherItems(zoom.name);
        }

    }
}
