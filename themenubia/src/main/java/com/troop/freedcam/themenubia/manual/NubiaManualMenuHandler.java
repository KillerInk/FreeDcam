package com.troop.freedcam.themenubia.manual;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.triggertap.seekarc.SeekArc;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.sonyapi.parameters.manual.ZoomManualSony;
import com.troop.freedcam.themenubia.R;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;

/**
 * Created by troop on 01.09.2014.
 */
public class NubiaManualMenuHandler implements SeekBar.OnSeekBarChangeListener, I_ParametersLoaded, AbstractManualParameter.I_ManualParameterEvent
{
    private final View activity;
    private final AppSettingsManager appSettingsManager;
    private AbstractCameraUiWrapper cameraUiWrapper;
    public final LinearLayout manualMenu;
    NubiaManualMenuFragment menuFragment;

    TextView seekbarText;
    NubiaManualMenuItem currentItem;
    AbstractParameterHandler parametersHandler;
    boolean userIsSeeking= false;
    int current = 0;
    final String TAG = NubiaManualMenuHandler.class.getSimpleName();

    boolean seekbarVisible = false;

    int realMin;
    int realMax;
    int realCurrent;

    ArrayList<NubiaManualMenuItem> manualItems;

    NubiaManualMenuItem burst;
    NubiaManualMenuItem brightnes;
    NubiaManualMenuItem cct;
    NubiaManualMenuItem contrast;
    NubiaManualMenuItem convergence;
    NubiaManualMenuItem exposure;
    NubiaManualMenuItem fx;
    NubiaManualMenuItem focus;
    NubiaManualMenuItem saturation;
    NubiaManualMenuItem sharp;
    NubiaManualMenuItem shutter;
    NubiaManualMenuItem iso;
    NubiaManualMenuItem zoom;
    NubiaManualMenuItem fnumber;
    HandlerThread thread;
    Handler handler;

    private SeekArc mSeekArc;


    public NubiaManualMenuHandler(View activity, AppSettingsManager appSettingsManager, NubiaManualMenuFragment fragment)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;

        mSeekArc = (SeekArc) activity.findViewById(R.id.seekArc);
        thread = new HandlerThread("seekbarThread");
        thread.start();
        handler = new Handler(thread.getLooper());


        seekbarText = (TextView)activity.findViewById(R.id.textView_seekbar);
       // manualSeekbar.setOnSeekBarChangeListener(this);
        Typeface font;

        switch (appSettingsManager.GetTheme())
        {
            case "Ambient": case "Nubia":
                font = Typeface.createFromAsset(appSettingsManager.context.getAssets(),"fonts/arial.ttf");
                seekbarText.setTypeface(font);
            break;
            case "Minimal":
                font = Typeface.createFromAsset(appSettingsManager.context.getAssets(), "fonts/BRADHITC.TTF");
                seekbarText.setTypeface(font);
                break;
            case "Material":
                font = Typeface.createFromAsset(appSettingsManager.context.getAssets(), "fonts/BOOKOS.TTF");
                seekbarText.setTypeface(font);
                break;
        }


        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                userIsSeeking = false;
                if (cameraUiWrapper instanceof CameraUiWrapperSony)
                    setValueToParameters(mSeekArc.getProgres());
                if (!(cameraUiWrapper instanceof CameraUiWrapperSony) && currentItem.name.equals("Shutter"))
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setValueToParameters(mSeekArc.getProgres());
                        }
                    });
                }

            }
            @Override
            public void onStartTrackingTouch(SeekArc seekArc)
            {
                userIsSeeking = true;
            }


            @Override
            public void onProgressChanged(SeekArc seekArc, final int progress,
                                          boolean fromUser) {


                // seekbarText.setText(String.valueOf(progress));
                if (fromUser && currentItem != null)
                {
                    seekbarText.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!(cameraUiWrapper instanceof CameraUiWrapperSony) && !currentItem.name.equals("Shutter"))
                            {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setValueToParameters(mSeekArc.getProgres());
                                        seekbarText.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (realMin < 0) {
                                                    setValueToTextBox(progress + realMin);
                                                } else {
                                                    setValueToTextBox(progress);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        manualMenu = (LinearLayout)activity.findViewById(R.id.v2_manual_menu);
        this.menuFragment = fragment;

        manualItems = new ArrayList<NubiaManualMenuItem>();

        brightnes = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_brightness), this,appSettingsManager);
        addToLists(brightnes);

        burst = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_burst), this,appSettingsManager);
        addToLists(burst);

        cct = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_cct), this,appSettingsManager);
        addToLists(cct);

        contrast = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_contrast), this,appSettingsManager);
        addToLists(contrast);
        convergence = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_convergence), this,appSettingsManager);
        addToLists(convergence);
        exposure = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_exposure), this,appSettingsManager);
        addToLists(exposure);
        focus = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_focus), this,appSettingsManager);
        addToLists(focus);

        fx = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_fx), this,appSettingsManager);
        addToLists(fx);

        saturation = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_saturation), this,appSettingsManager);
        addToLists(saturation);
        sharp = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_sharpness), this,appSettingsManager);
        addToLists(sharp);
        shutter = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_shutter), this,appSettingsManager);
        addToLists(shutter);
        iso = new NubiaManualMenuItem(activity.getContext(), "iso" , this,appSettingsManager);
        addToLists(iso);
        zoom = new NubiaManualMenuItem(activity.getContext(), fragment.getString(R.string.manualmenu_zoom), this,appSettingsManager);
        addToLists(zoom);
        fnumber = new NubiaManualMenuItem(activity.getContext(), "FNumber",this,appSettingsManager);
        addToLists(fnumber);


    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.parametersHandler = cameraUiWrapper.camParametersHandler;
        parametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        ParametersLoaded();
    }

    private void addToLists(NubiaManualMenuItem item)
    {
        manualItems.add(item);
    }

    public void DisableOtherItems(String name)
    {
        for(NubiaManualMenuItem item : manualItems)
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
                        if (realMin < 0) {
                            setValueToTextBox(mSeekArc.getProgres() + realMin);

                        }
                        else
                            setValueToTextBox(mSeekArc.getProgres());
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
        Log.d(TAG, "Brightness");
        if (parametersHandler.ManualBrightness != null)
        {

            brightnes.SetAbstractManualParameter(parametersHandler.ManualBrightness);
        }
        else
            brightnes.onIsSupportedChanged(false);
        Log.d(TAG, "Burst");
        if (parametersHandler.Burst != null)
        {
            burst.SetAbstractManualParameter(parametersHandler.Burst);
        }
        else
            burst.onIsSupportedChanged(false);

        Log.d(TAG, "cct");
        if (parametersHandler.CCT != null)
        {
            cct.SetAbstractManualParameter(parametersHandler.CCT);
        }
        else
            cct.onIsSupportedChanged(false);

        Log.d(TAG, "Contrast");
        if (parametersHandler.ManualContrast != null)
        {
            contrast.SetAbstractManualParameter(parametersHandler.ManualContrast);
        }
        else contrast.onIsSupportedChanged(false);
        Log.d(TAG, "Convergence");
        if (parametersHandler.ManualConvergence != null)
        {
            convergence.SetAbstractManualParameter(parametersHandler.ManualConvergence);
        }
        else convergence.onIsSupportedChanged(false);
        Log.d(TAG, "Exposure");
        if (parametersHandler.ManualExposure != null)
        {
            exposure.SetAbstractManualParameter(parametersHandler.ManualExposure);
        }
        else exposure.onIsSupportedChanged(false);
        Log.d(TAG, "ManualFocus");
        if (parametersHandler.ManualFocus !=null)
        {
            focus.SetAbstractManualParameter(parametersHandler.ManualFocus);
        }
        else focus.onIsSupportedChanged(false);
        //defcomg
        Log.d(TAG, "FX");
        if (parametersHandler.FX != null)
        {
            fx.SetAbstractManualParameter(parametersHandler.FX);
        }
        else
            fx.onIsSupportedChanged(false);

        Log.d(TAG, "Saturation");
        if (parametersHandler.ManualSaturation != null)
        {
            saturation.SetAbstractManualParameter(parametersHandler.ManualSaturation);
        }
        else saturation.onIsSupportedChanged(false);
        Log.d(TAG, "Sharpness");
        if (parametersHandler.ManualSharpness != null)
        {
            sharp.SetAbstractManualParameter(parametersHandler.ManualSharpness);
        }
        else sharp.onIsSupportedChanged(false);
        Log.d(TAG, "Shutter");
        if (parametersHandler.ManualShutter != null)
        {
            shutter.SetAbstractManualParameter(parametersHandler.ManualShutter);
        }
        else shutter.onIsSupportedChanged(false);
        Log.d(TAG, "Iso");
        if (parametersHandler.ISOManual != null)
        {
            iso.SetAbstractManualParameter(parametersHandler.ISOManual);
        }
        else iso.onIsSupportedChanged(false);
        Log.d(TAG, "Fnumber");
        if (parametersHandler.ManualFNumber != null)
        {
            fnumber.SetAbstractManualParameter(parametersHandler.ManualFNumber);
        }
        else fnumber.onIsSupportedChanged(false);
        Log.d(TAG, "Zoom");
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
      //  manualSeekbar.setVisibility(View.VISIBLE);
        mSeekArc.setVisibility(View.VISIBLE);
        seekbarVisible = true;
        seekbarText.setVisibility(View.VISIBLE);
    }
    private void hideSeekbar()
    {

        mSeekArc.setVisibility(View.GONE);
        seekbarVisible = false;
        seekbarText.setVisibility(View.GONE);
    }

    private void setSeekbar_Min_Max(int min, int max)
    {
        realMin = min;
        realMax = max;
        if (min <0)
        {
            int m = max + min * -1;

            mSeekArc.setmMax(m);
        }
        else {

            mSeekArc.setmMax(realMax);
        }

    }

    private void setSeekbarProgress(int value)
    {
        if (realMin < 0)
        {

            mSeekArc.setProgress(value -realMin);


        }
        else
        {

            mSeekArc.setProgress(value);
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
            if (currentItem == null || currentItem.name == null)
                return;
            seekbarText.setText(currentItem.name + ": " + value);
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
}
