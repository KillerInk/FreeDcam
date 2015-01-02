package com.troop.freedcam.ui.menu;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.camera.parameters.manual.ShutterManualParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.sonyapi.parameters.manual.BaseManualParameterSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;

import java.util.ArrayList;

/**
 * Created by troop on 01.09.2014.
 */
public class ManualMenuHandler implements SeekBar.OnSeekBarChangeListener, I_ParametersLoaded, AbstractManualParameter.I_ManualParameterEvent
{
    private final MainActivity_v2 activity;
    private final AppSettingsManager appSettingsManager;
    private AbstractCameraUiWrapper cameraUiWrapper;
    private final SeekBar manualSeekbar;
    public final LinearLayout manualMenu;
    TextView seekbarText;
    ManualMenuItem currentItem;
    AbstractParameterHandler parametersHandler;
    boolean userIsSeeking= false;
    int current = 0;

    int realMin;
    int realMax;
    int realCurrent;

    ArrayList<ManualMenuItem> manualItems;

    ManualMenuItem brightnes;
    ManualMenuItem contrast;
    ManualMenuItem convergence;
    ManualMenuItem exposure;
    ManualMenuItem focus;
    ManualMenuItem saturation;
    ManualMenuItem sharp;
    ManualMenuItem shutter;
    ManualMenuItem iso;
    ManualMenuItem zoom;
    ManualMenuItem fnumber;

    public ManualMenuHandler(MainActivity_v2 activity, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;
        manualSeekbar = (SeekBar)activity.seekbarLayout.findViewById(R.id.seekBar_manual);
        seekbarText = (TextView)activity.seekbarLayout.findViewById(R.id.textView_seekbar);
        manualSeekbar.setOnSeekBarChangeListener(this);
        manualMenu = activity.manualSettingsLayout;
        manualItems = new ArrayList<ManualMenuItem>();

        brightnes = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_brightness), this);
        addToLists(brightnes);
        contrast = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_contrast), this);
        addToLists(contrast);
        convergence = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_convergence), this);
        addToLists(convergence);
        exposure = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_exposure), this);
        addToLists(exposure);
        focus = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_focus), this);
        addToLists(focus);
        saturation = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_saturation), this);
        addToLists(saturation);
        sharp = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_sharpness), this);
        addToLists(sharp);
        shutter = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_shutter), this);
        addToLists(shutter);
        iso = new ManualMenuItem(activity, "iso" , this);
        addToLists(iso);
        zoom = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_zoom), this);
        addToLists(zoom);
        fnumber = new ManualMenuItem(activity, "FNumber",this);
        addToLists(fnumber);

    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
        parametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
    }

    private void addToLists(ManualMenuItem item)
    {
        manualItems.add(item);
    }

    public void DisableOtherItems(String name)
    {
        for(ManualMenuItem item : manualItems)
        {
            if (item.manualParameter != null)
            {
                if (!item.name.equals(name)) {
                    item.DisableItem();
                    item.manualParameter.removeEventListner(this);

                } else {
                    item.manualParameter.addEventListner(this);

                    currentItem = item;
                    item.EnableItem();
                    //currentItem.manualParameter.RestartPreview();
                    int min = item.manualParameter.GetMinValue();
                    int max = item.manualParameter.GetMaxValue();
                    setSeekbar_Min_Max(min, max);
                    setSeekbarProgress(item.manualParameter.GetValue());
                    seekbarText.setText(currentItem.getStringValue(manualSeekbar.getProgress()));
                }
            }
            else
                item.onIsSupportedChanged(false);
        }
    }

    @Override
    public void ParametersLoaded()
    {
        if (parametersHandler.ManualBrightness != null)
        {
            brightnes.SetAbstractManualParameter(parametersHandler.ManualBrightness);
        }
        else
            brightnes.onIsSupportedChanged(false);
        if (parametersHandler.ManualContrast != null)
        {
            contrast.SetAbstractManualParameter(parametersHandler.ManualContrast);
        }
        else contrast.onIsSupportedChanged(false);
        if (parametersHandler.ManualConvergence != null)
        {
            convergence.SetAbstractManualParameter(parametersHandler.ManualConvergence);
        }
        else convergence.onIsSupportedChanged(false);
        if (parametersHandler.ManualExposure != null)
        {
            exposure.SetAbstractManualParameter(parametersHandler.ManualExposure);
        }
        else exposure.onIsSupportedChanged(false);
        if (parametersHandler.ManualFocus !=null)
        {
            focus.SetAbstractManualParameter(parametersHandler.ManualFocus);
        }
        else focus.onIsSupportedChanged(false);
        if (parametersHandler.ManualSaturation != null)
        {
            saturation.SetAbstractManualParameter(parametersHandler.ManualSaturation);
        }
        else saturation.onIsSupportedChanged(false);
        if (parametersHandler.ManualSharpness != null)
        {
            sharp.SetAbstractManualParameter(parametersHandler.ManualSharpness);
        }
        else sharp.onIsSupportedChanged(false);
        if (parametersHandler.ManualShutter != null)
        {
            shutter.SetAbstractManualParameter(parametersHandler.ManualShutter);
        }
        else shutter.onIsSupportedChanged(false);
        if (parametersHandler.ISOManual != null)
        {
            iso.SetAbstractManualParameter(parametersHandler.ISOManual);
        }
        else iso.onIsSupportedChanged(false);
        if (parametersHandler.ManualFNumber != null)
        {
            fnumber.SetAbstractManualParameter(parametersHandler.ManualFNumber);
        }
        else fnumber.onIsSupportedChanged(false);
        if (parametersHandler.Zoom != null)
        {
            zoom.SetAbstractManualParameter(parametersHandler.Zoom);
            zoom.EnableItem();
            zoom.onIsSetSupportedChanged(true);
            DisableOtherItems(zoom.name);
        }
        else zoom.onIsSupportedChanged(false);

    }

    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean value) {

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
        setTextValue(currentItem.GetStringValue());
        if (!userIsSeeking && currentItem.name.equals("Zoom"))
        {
            setSeekbarProgress(current);
        }
    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    /**
     * SEEKBARSTUFF##################################
     * SEEKBARSTUFF##################################
     */

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
            currentItem.SetValue(value + realMin);
            //setTextValue(value + realMin);
        }
        else
        {
            currentItem.SetValue(value);
            String txt = currentItem.GetStringValue();
            if (txt != null && !txt.equals(""))
                setTextValue(txt);
            else
            {
                setTextValue(value + realMin);
            }

        }

    }

    private void setTextValue(int value)
    {
        seekbarText.setText(currentItem.name + ": " + value);

    }
    private void setTextValue(final String value)
    {
        seekbarText.post(new Runnable() {
            @Override
            public void run() {
                seekbarText.setText(currentItem.name + ": " + value);
            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (fromUser && currentItem != null)
        {
            seekbarText.setText(currentItem.getStringValue(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        userIsSeeking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        userIsSeeking = false;
        setValueToParameters(seekBar.getProgress());
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
}
