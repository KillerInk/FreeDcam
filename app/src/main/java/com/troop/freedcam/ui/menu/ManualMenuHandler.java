package com.troop.freedcam.ui.menu;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.sonyapi.parameters.manual.BaseManualParameterSony;
import com.troop.freedcam.sonyapi.parameters.manual.ZoomManualSony;
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

    boolean seekbarVisible = false;

    int realMin;
    int realMax;
    int realCurrent;

    ArrayList<ManualMenuItem> manualItems;

    ManualMenuItem burst;
    ManualMenuItem brightnes;
    ManualMenuItem cct;
    ManualMenuItem contrast;
    ManualMenuItem convergence;
    ManualMenuItem exposure;
    ManualMenuItem fx;
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

        burst = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_burst), this);
        addToLists(burst);

        cct = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_cct), this);
        addToLists(cct);

        contrast = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_contrast), this);
        addToLists(contrast);
        convergence = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_convergence), this);
        addToLists(convergence);
        exposure = new ManualMenuItem(activity, activity.getString(R.string.manualmenu_exposure), this);
        addToLists(exposure);
        focus = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_focus), this);
        addToLists(focus);

        fx = new ManualMenuItem(activity,activity.getString(R.string.manualmenu_fx), this);
        addToLists(fx);

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


                }
                else
                {
                    if (currentItem == item)
                    {
                        if (seekbarVisible)
                            hideSeekbar();
                        item.DisableItem();
                        item.manualParameter.removeEventListner(this);
                        currentItem = null;
                    }
                    else
                    {
                        if (!seekbarVisible)
                            showSeekbar();
                        item.manualParameter.addEventListner(this);

                        currentItem = item;
                        item.EnableItem();
                        //currentItem.manualParameter.RestartPreview();
                        int min = item.manualParameter.GetMinValue();
                        int max = item.manualParameter.GetMaxValue();
                        setSeekbar_Min_Max(min, max);
                        setSeekbarProgress(item.manualParameter.GetValue());
                        if (realMin < 0)
                            setValueToTextBox(manualSeekbar.getProgress() + realMin);
                        else
                            setValueToTextBox(manualSeekbar.getProgress());
                    }
                }
            }
            else
            {
                item.onIsSupportedChanged(false);
            }
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

        if (parametersHandler.Burst != null)
        {
            burst.SetAbstractManualParameter(parametersHandler.Burst);
        }
        else
            burst.onIsSupportedChanged(false);

        if (parametersHandler.CCT != null)
        {
            cct.SetAbstractManualParameter(parametersHandler.CCT);
        }
        else
            cct.onIsSupportedChanged(false);

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
        //defcomg
        if (parametersHandler.FX != null)
        {
            fx.SetAbstractManualParameter(parametersHandler.FX);
        }
        else
            fx.onIsSupportedChanged(false);

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
            /*zoom.EnableItem();

            DisableOtherItems(zoom.name);*/
        }
        else zoom.onIsSupportedChanged(false);
        hideSeekbar();
    }

    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean value)
    {
        if (value && !seekbarVisible)
            showSeekbar();
        else if (!value && seekbarVisible)
            hideSeekbar();

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
        setValueToTextBox(current);
        if (!userIsSeeking && currentItem.name.equals("Zoom"))
        {
            setSeekbarProgress(current);
        }
    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onCurrentStringValueChanged(String value) {

    }

    /**
     * SEEKBARSTUFF##################################
     * SEEKBARSTUFF##################################
     */

    private void showSeekbar()
    {
        manualSeekbar.setVisibility(View.VISIBLE);
        seekbarVisible = true;
    }
    private void hideSeekbar()
    {
        manualSeekbar.setVisibility(View.GONE);
        seekbarVisible = false;
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
        if (currentItem.manualParameter instanceof ZoomManualSony)
        {
            ((ZoomManualSony)currentItem.manualParameter).fromUser = true;
        }
        if (realMin < 0)
            currentItem.SetValue(value + realMin);
        else
            currentItem.SetValue(value);
        //setValueToTextBox(value);
    }

    private void setValueToTextBox(int value) {
        String txt = currentItem.getStringValue(value);
        if (txt != null && !txt.equals("") && !txt.equals("null"))
            setTextValue(txt);
        else
        {
            setTextValue((value) +"");
        }
    }

    private void setTextValue(final String value)
    {
        if (currentItem !=null)
        {
            seekbarText.post(new Runnable() {
                @Override
                public void run()
                {
                    if (currentItem == null || currentItem.name == null)
                        return;
                    seekbarText.setText(currentItem.name + ": " + value);
                }
            });
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (fromUser && currentItem != null)
        {
            if (!(cameraUiWrapper instanceof CameraUiWrapperSony))
                setValueToParameters(seekBar.getProgress());
            if (realMin < 0)
                setValueToTextBox(progress + realMin);
            else
                setValueToTextBox(progress);
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
        if (cameraUiWrapper instanceof CameraUiWrapperSony)
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
