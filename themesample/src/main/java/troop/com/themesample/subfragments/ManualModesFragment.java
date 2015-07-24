package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.R;
import troop.com.themesample.views.ManualItem;

/**
 * Created by Ingo on 24.07.2015.
 */
public class ManualModesFragment extends AbstractFragment implements I_ParametersLoaded
{
    ManualItem contrast;
    ManualItem burst;
    ManualItem brightnes;
    ManualItem cct;
    ManualItem convergence;
    ManualItem exposure;
    ManualItem fx;
    ManualItem focus;
    ManualItem saturation;
    ManualItem sharp;
    ManualItem shutter;
    ManualItem iso;
    ManualItem zoom;
    ManualItem fnumber;
    ManualItem skintone;
    ManualItem programShift;

    //Holds the Fragment Status if its loaded
    boolean isLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.manual_modes_fragment, container, false);

        contrast = (ManualItem)view.findViewById(R.id.manual_contrast);
        contrast.SetStuff(appSettingsManager, AppSettingsManager.MCONTRAST);

        burst = (ManualItem)view.findViewById(R.id.manual_burst);
        burst.SetStuff(appSettingsManager, "");

        brightnes = (ManualItem)view.findViewById(R.id.manual_brightness);
        brightnes.SetStuff(appSettingsManager, AppSettingsManager.MBRIGHTNESS);

        cct = (ManualItem)view.findViewById(R.id.manual_cct);
        cct.SetStuff(appSettingsManager, "");

        convergence = (ManualItem)view.findViewById(R.id.manual_convergence);
        convergence.SetStuff(appSettingsManager, AppSettingsManager.MCONVERGENCE);

        exposure = (ManualItem)view.findViewById(R.id.manual_exposure);
        exposure.SetStuff(appSettingsManager, AppSettingsManager.MEXPOSURE);

        fx = (ManualItem)view.findViewById(R.id.manual_fx);
        fx.SetStuff(appSettingsManager, "");

        focus = (ManualItem)view.findViewById(R.id.manual_mf);
        focus.SetStuff(appSettingsManager, AppSettingsManager.MF);

        saturation = (ManualItem)view.findViewById(R.id.manual_saturation);
        saturation.SetStuff(appSettingsManager, AppSettingsManager.MSATURATION);

        sharp = (ManualItem)view.findViewById(R.id.manual_sharpness);
        sharp.SetStuff(appSettingsManager, AppSettingsManager.MSHARPNESS);

        shutter = (ManualItem)view.findViewById(R.id.manual_shutter);
        shutter.SetStuff(appSettingsManager, AppSettingsManager.MSHUTTERSPEED);

        iso = (ManualItem)view.findViewById(R.id.manual_iso);
        iso.SetStuff(appSettingsManager, AppSettingsManager.MISO);

        zoom = (ManualItem)view.findViewById(R.id.manual_zoom);
        zoom.SetStuff(appSettingsManager, "");

        fnumber = (ManualItem)view.findViewById(R.id.manual_aperture);
        fnumber.SetStuff(appSettingsManager, "");

        skintone = (ManualItem)view.findViewById(R.id.manual_skintone);
        skintone.SetStuff(appSettingsManager, "");

        programShift = (ManualItem)view.findViewById(R.id.manual_program_shift);
        programShift.SetStuff(appSettingsManager, "");

        if (wrapper != null)
            setWrapper();
        this.isLoaded = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper) {
        super.SetCameraUIWrapper(wrapper);
        wrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);


    }

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity) {
        super.SetStuff(appSettingsManager, i_activity);
    }

    @Override
    public void ParametersLoaded() {
        if (wrapper != null)
            setWrapper();
    }

    private void setWrapper()
    {
        contrast.SetAbstractManualParameter(wrapper.camParametersHandler.ManualContrast);
        burst.SetAbstractManualParameter(wrapper.camParametersHandler.Burst);
        brightnes.SetAbstractManualParameter(wrapper.camParametersHandler.ManualBrightness);
        cct.SetAbstractManualParameter(wrapper.camParametersHandler.CCT);
        convergence.SetAbstractManualParameter(wrapper.camParametersHandler.ManualConvergence);
        exposure.SetAbstractManualParameter(wrapper.camParametersHandler.ManualExposure);
        fx.SetAbstractManualParameter(wrapper.camParametersHandler.FX);
        focus.SetAbstractManualParameter(wrapper.camParametersHandler.ManualFocus);
        saturation.SetAbstractManualParameter(wrapper.camParametersHandler.ManualSaturation);
        sharp.SetAbstractManualParameter(wrapper.camParametersHandler.ManualSharpness);
        shutter.SetAbstractManualParameter(wrapper.camParametersHandler.ManualShutter);
        iso.SetAbstractManualParameter(wrapper.camParametersHandler.ISOManual);
        zoom.SetAbstractManualParameter(wrapper.camParametersHandler.Zoom);
        fnumber.SetAbstractManualParameter(wrapper.camParametersHandler.ManualFNumber);
        skintone.SetAbstractManualParameter(wrapper.camParametersHandler.Skintone);
        programShift.SetAbstractManualParameter(wrapper.camParametersHandler.ProgramShift);
    }
}
