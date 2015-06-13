package troop.com.themesample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.views.ThumbView;
import troop.com.themesample.views.UiSettingsChild;

/**
 * Created by troop on 09.06.2015.
 */
public class SampleThemeFragment extends AbstractFragment
{
    UiSettingsChild flash;
    UiSettingsChild iso;
    UiSettingsChild autoexposure;
    UiSettingsChild whitebalance;
    UiSettingsChild focus;
    UiSettingsChild night;
    UiSettingsChild format;

    ThumbView thumbView;

    AbstractCameraUiWrapper abstractCameraUiWrapper;
    LinearLayout left_cameraUI_holder;
    View view;
    I_Activity i_activity;
    AppSettingsManager appSettingsManager;

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity) {
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.abstractCameraUiWrapper = wrapper;
        if (view != null)
        {
            flash.SetParameter(abstractCameraUiWrapper.camParametersHandler.FlashMode);
            iso.SetParameter(abstractCameraUiWrapper.camParametersHandler.IsoMode);
            autoexposure.SetParameter(abstractCameraUiWrapper.camParametersHandler.ExposureMode);
            whitebalance.SetParameter(abstractCameraUiWrapper.camParametersHandler.WhiteBalanceMode);
            focus.SetParameter(abstractCameraUiWrapper.camParametersHandler.FocusMode);
            night.SetParameter(abstractCameraUiWrapper.camParametersHandler.NightMode);
            thumbView.INIT(i_activity,abstractCameraUiWrapper);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.cameraui, container, false);
        this.left_cameraUI_holder = (LinearLayout)left_cameraUI_holder.findViewById(R.id.left_ui_holder);
        this.flash = (UiSettingsChild)view.findViewById(R.id.Flash);
        this.iso = (UiSettingsChild)view.findViewById(R.id.Iso);
        this.autoexposure =(UiSettingsChild)view.findViewById(R.id.Ae);
        this.whitebalance = (UiSettingsChild)view.findViewById(R.id.wb);
        this.focus = (UiSettingsChild)view.findViewById(R.id.focus);
        this.night = (UiSettingsChild)view.findViewById(R.id.focus);
        this.format = (UiSettingsChild)view.findViewById(R.id.format);
        this.thumbView = (ThumbView)view.findViewById(R.id.thumbview);

        return view;
    }

}
