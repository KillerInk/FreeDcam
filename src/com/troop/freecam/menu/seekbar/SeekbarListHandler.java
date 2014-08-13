package com.troop.freecam.menu.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.LandscapeSeekbarControl;
import com.troop.freecam.enums.E_ManualSeekbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 11.08.2014.
 */
public class SeekbarListHandler extends LinearLayout
{
    CameraManager cameraManager;
    MainActivity activity;

    public ManualBrightnessSeekbar manualBrightnessSeekbar;
    public ManualExposureSeekbar manualExposureSeekbar;
    public ManualContrastSeekbar manualContrastSeekbar;
    public ManualSharpnessSeekbar manualSharpnessSeekbar;
    public ManualSaturationSeekbar manualSaturationSeekbar;
    public ManualConvergenceSeekbar manualConvergenceSeekbar;
    public ManualFocusSeekbar manualFocusSeekbar;
    public ManualShutterSeekbar manualShutterSeekbar;
    public ZoomSeekbar zoomSeekbar;

    LandscapeSeekbarControl selectedSeekbar;

    List<LandscapeSeekbarControl> seekbarControls;


    public SeekbarListHandler(Context context) {
        super(context);
    }

    public SeekbarListHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekbarListHandler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Init(CameraManager cameraManager, MainActivity activity)
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seekbars_list, this);
        this.cameraManager = cameraManager;
        this.activity = activity;

        seekbarControls = new ArrayList<LandscapeSeekbarControl>();

        manualBrightnessSeekbar = (ManualBrightnessSeekbar) findViewById(R.id.brightness_seekbar);
        manualBrightnessSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(manualBrightnessSeekbar);

        manualExposureSeekbar = (ManualExposureSeekbar) findViewById(R.id.exposure_seekbar);
        manualExposureSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(manualExposureSeekbar);

        manualContrastSeekbar = (ManualContrastSeekbar)findViewById(R.id.contrast_seekbar);
        manualContrastSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(manualContrastSeekbar);

        manualSharpnessSeekbar = (ManualSharpnessSeekbar)findViewById(R.id.sharpness_seekbar);
        manualSharpnessSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(manualSharpnessSeekbar);

        manualSaturationSeekbar = (ManualSaturationSeekbar)findViewById(R.id.saturation_seekbar);
        manualSaturationSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(manualSaturationSeekbar);

        manualConvergenceSeekbar = (ManualConvergenceSeekbar)findViewById(R.id.convergence_seekbar);
        manualConvergenceSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(manualConvergenceSeekbar);

        manualFocusSeekbar = (ManualFocusSeekbar)findViewById(R.id.focus_seekbar);
        manualFocusSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(manualFocusSeekbar);

        manualShutterSeekbar = (ManualShutterSeekbar)findViewById(R.id.shutter_seekbar);
        manualShutterSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(manualShutterSeekbar);

        zoomSeekbar = (ZoomSeekbar)findViewById(R.id.zoom_seekbar);
        zoomSeekbar.SetCameraManager(cameraManager);
        seekbarControls.add(zoomSeekbar);

        hideAll();
    }

    public void Update()
    {
        if (cameraManager.parametersManager.getSupportBrightness()) {
            manualBrightnessSeekbar.SetMinMaxValues(cameraManager.parametersManager.Brightness.GetMinValue(), cameraManager.parametersManager.Brightness.GetMaxValue());
            manualBrightnessSeekbar.SetCurrentValue(cameraManager.parametersManager.Brightness.Get());
        }
        if (cameraManager.parametersManager.getSupportExposureMode()) {
            manualExposureSeekbar.SetMinMaxValues(cameraManager.parametersManager.manualExposure.getMin(), cameraManager.parametersManager.manualExposure.getMax());
            manualExposureSeekbar.SetCurrentValue(cameraManager.parametersManager.manualExposure.getValue());
        }
        if (cameraManager.parametersManager.getSupportContrast())
        {
            manualContrastSeekbar.SetMinMaxValues(cameraManager.parametersManager.manualContrast.getMin(), cameraManager.parametersManager.manualContrast.getMax());
            manualContrastSeekbar.SetCurrentValue(cameraManager.parametersManager.manualContrast.getValue());
        }
        if (cameraManager.parametersManager.getSupportSharpness())
        {
            //TODO: add get min value to manualSharpness
            manualSharpnessSeekbar.SetMinMaxValues(0, cameraManager.parametersManager.manualSharpness.getMax());
            manualSharpnessSeekbar.SetCurrentValue(cameraManager.parametersManager.manualSharpness.getValue());
        }
        if (cameraManager.parametersManager.getSupportSaturation())
        {
            //TODO: add min value to manualSaturation
            manualSaturationSeekbar.SetMinMaxValues(0, cameraManager.parametersManager.manualSaturation.getMax());
            manualSaturationSeekbar.SetCurrentValue(cameraManager.parametersManager.manualSaturation.getValue());
        }

        if (cameraManager.parametersManager.getSupportManualConvergence())
        {
            manualConvergenceSeekbar.SetMinMaxValues(cameraManager.parametersManager.manualConvergence.getMin(), cameraManager.parametersManager.manualConvergence.getMax());
            manualConvergenceSeekbar.SetCurrentValue(cameraManager.parametersManager.manualConvergence.get());
        }
        if (cameraManager.parametersManager.getSupportManualFocus())
        {
            manualFocusSeekbar.SetMinMaxValues(cameraManager.parametersManager.manualFocus.getMin(), cameraManager.parametersManager.manualFocus.getMax());
            manualFocusSeekbar.SetCurrentValue(cameraManager.parametersManager.manualFocus.getValue());
        }
        if (cameraManager.parametersManager.getSupportManualShutter())
        {
            //TODO: add min value for shutter
            manualShutterSeekbar.SetMinMaxValues(0, cameraManager.parametersManager.manualShutter.getMax());
            manualShutterSeekbar.SetCurrentValue(cameraManager.parametersManager.manualShutter.getValue());
        }
        if (cameraManager.parametersManager.getParameters().isZoomSupported())
        {
            zoomSeekbar.SetMinMaxValues(1, zoomSeekbar.getMaxZoomValue());
            zoomSeekbar.SetCurrentValue(cameraManager.parametersManager.getParameters().getZoom());
        }
    }

    public void setVisibility(E_ManualSeekbar e_manualSeekbar, boolean show)
    {
        for (int i = 0; i<seekbarControls.size(); i++)
        {
            if (seekbarControls.get(i).GetManualSeekBarEnum() == e_manualSeekbar)
                if (show) {
                    seekbarControls.get(i).setVisibility(VISIBLE);
                    selectedSeekbar = seekbarControls.get(i);
                }
                else {
                    seekbarControls.get(i).setVisibility(GONE);
                    selectedSeekbar = zoomSeekbar;
                }
            else
                seekbarControls.get(i).setVisibility(GONE);
        }
    }

    private void hideAll()
    {
        for (int i = 0; i<seekbarControls.size(); i++)
        {
            seekbarControls.get(i).setVisibility(GONE);
            selectedSeekbar = zoomSeekbar;
        }
    }

    public boolean OnKeyEvent(KeyEvent keyEvent)
    {
        int key = keyEvent.getKeyCode();
        if (selectedSeekbar != null) {
            if (key == KeyEvent.KEYCODE_VOLUME_UP) {
                selectedSeekbar.SetCurrentValue(selectedSeekbar.GetCurrentValue() + 1);
                return true;
            }
            //zoom out
            else if (key == KeyEvent.KEYCODE_VOLUME_DOWN) {
                selectedSeekbar.SetCurrentValue(selectedSeekbar.GetCurrentValue() - 1);
                return true;
            }
        }
        return false;
    }
}
