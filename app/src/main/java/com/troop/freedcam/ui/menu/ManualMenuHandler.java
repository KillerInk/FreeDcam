package com.troop.freedcam.ui.menu;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.camera.parameters.manual.ShutterManualParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.sonyapi.parameters.manual.ExposureTimeSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;

import java.util.ArrayList;

/**
 * Created by troop on 01.09.2014.
 */
public class ManualMenuHandler implements SeekBar.OnSeekBarChangeListener, I_ParametersLoaded, AbstractManualParameter.I_ParameterEvent
{
    private final MainActivity_v2 activity;
    private final AppSettingsManager appSettingsManager;
    private final AbstractCameraUiWrapper cameraUiWrapper;
    private final SeekBar manualSeekbar;
    private final LinearLayout manualMenu;
    TextView seekbarText;
    ManualMenuItem currentItem;
    AbstractParameterHandler parametersHandler;

    int realMin;
    int realMax;
    int realCurrent;

    ArrayList<ManualMenuItem> manualItems;

    public ManualMenuHandler(MainActivity_v2 activity, AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
        parametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        manualSeekbar = (SeekBar)activity.seekbarLayout.findViewById(R.id.seekBar_manual);
        seekbarText = (TextView)activity.seekbarLayout.findViewById(R.id.textView_seekbar);
        manualSeekbar.setOnSeekBarChangeListener(this);
        manualMenu = activity.manualSettingsLayout;
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
            if (!item.name.equals(name)) {
                item.DisableItem();
                item.manualParameter.removeEventListner(this);
            }
            else
            {
                item.manualParameter.addEventListner(this);
                currentItem = item;
                item.EnableItem();
                //currentItem.manualParameter.RestartPreview();
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
            //setTextValue(value + realMin);
        }
        else
        {

            if (currentItem.name.equals(activity.getString(R.string.manualmenu_shutter)))
            {
                try
                {
                    currentItem.manualParameter.SetValue(value);
                    Toast.makeText(activity, "Manual Shutter set to:" + value, Toast.LENGTH_LONG);
                }
                catch (Exception ex)
                {
                    Toast.makeText(activity, "Error Set Manual Shutter", Toast.LENGTH_LONG);
                }

                //cameraUiWrapper.camParametersHandler.SetParametersToCamera();
                if(currentItem.manualParameter instanceof ShutterManualParameter) {
                    ShutterManualParameter shutterManualParameter = (ShutterManualParameter) currentItem.manualParameter;
                    setTextValue(shutterManualParameter.GetStringValue());
                }
                if(currentItem.manualParameter instanceof ExposureTimeSony) {
                    ExposureTimeSony shutterManualParameter = (ExposureTimeSony) currentItem.manualParameter;
                    setTextValue(shutterManualParameter.GetStringValue());
                }
                //cameraUiWrapper.cameraHolder.StartPreview();
            }
            else
            {
                currentItem.manualParameter.SetValue(value);
                setTextValue(value + realMin);
            }
        }

    }

    private void setTextValue(int value)
    {
        seekbarText.setText(currentItem.name + ": " + value);
    }
    private void setTextValue(String value)
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
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    public void Incrase()
    {
        if (currentItem != null && manualSeekbar.getProgress() + 1 <= manualSeekbar.getMax())
        {
            setSeekbarProgress(manualSeekbar.getProgress() + 1);
            setValueToParameters(manualSeekbar.getProgress());
        }
    }

    public void Decrase()
    {
        if (currentItem != null && manualSeekbar.getProgress() - 1 >= 0)
        {
            setSeekbarProgress(manualSeekbar.getProgress() - 1);
            setValueToParameters(manualSeekbar.getProgress());
        }
    }

    @Override
    public void ParametersLoaded()
    {
        manualMenu.removeAllViews();
        manualItems.clear();
        if (parametersHandler.ManualBrightness != null && parametersHandler.ManualBrightness.IsSupported())
        {
            ManualMenuItem brightnes = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_brightness), this, parametersHandler.ManualBrightness);
            addToLists(brightnes);
        }
        if (parametersHandler.ManualContrast != null && parametersHandler.ManualContrast.IsSupported())
        {
            ManualMenuItem contrast = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_contrast), this, parametersHandler.ManualContrast);
            addToLists(contrast);
        }
        if (parametersHandler.ManualConvergence != null && parametersHandler.ManualConvergence.IsSupported())
        {
            ManualMenuItem convergence = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_convergence), this, parametersHandler.ManualConvergence);
            addToLists(convergence);
        }
        if (parametersHandler.ManualExposure != null && parametersHandler.ManualExposure.IsSupported())
        {
            ManualMenuItem exposure = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_exposure), this, parametersHandler.ManualExposure);
            addToLists(exposure);
        }
        if (parametersHandler.ManualFocus !=null && parametersHandler.ManualFocus.IsSupported())
        {
            ManualMenuItem focus = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_focus), this,parametersHandler.ManualFocus);
            addToLists(focus);
        }
        if (parametersHandler.ManualSaturation != null && parametersHandler.ManualSaturation.IsSupported())
        {
            ManualMenuItem satu = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_saturation), this, parametersHandler.ManualSaturation);
            addToLists(satu);
        }
        if (parametersHandler.ManualSharpness != null && parametersHandler.ManualSharpness.IsSupported())
        {
            ManualMenuItem sharp = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_sharpness), this, parametersHandler.ManualSharpness);
            addToLists(sharp);
        }
        if (parametersHandler.ManualShutter != null && parametersHandler.ManualShutter.IsSupported())
        {
            ManualMenuItem shutter = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_shutter), this, parametersHandler.ManualShutter);
            addToLists(shutter);
        }
        if (parametersHandler.ISOManual != null && parametersHandler.ISOManual.IsSupported())
        {
            ManualMenuItem iso = new ManualMenuItem(activity, "iso" , this, parametersHandler.ISOManual);
            addToLists(iso);
        }
        if (parametersHandler.Zoom != null && parametersHandler.Zoom.IsSupported())
        {
            ManualMenuItem zoom = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_zoom), this, parametersHandler.Zoom);
            addToLists(zoom);
            zoom.EnableItem();
            DisableOtherItems(zoom.name);
        }

    }

    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onMaxValueChanged(int max) {

    }

    @Override
    public void onMinValueChanged(int min) {

    }

    @Override
    public void onCurrentValueChanged(int current)
    {
        setTextValue(current + realMin);
    }
}
