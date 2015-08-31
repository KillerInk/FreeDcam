package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import troop.com.themesample.subfragments.CameraUiFragment;
import troop.com.themesample.views.menu.MenuItemBayerFormat;

/**
 * Created by troop on 07.07.2015.
 */
public class UiSettingsChildFormat extends UiSettingsChild
{
    AbstractCameraUiWrapper cameraUiWrapper;
    Camera1picFormat camera1picFormat;
    public UiSettingsChildFormat(Context context) {
        super(context);
    }

    public UiSettingsChildFormat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UiSettingsChildFormat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);

    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {
        if (cameraUiWrapper instanceof CameraUiWrapper)
        {
            camera1picFormat = new Camera1picFormat(this.getHandler());
            super.SetParameter(camera1picFormat);
            if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals("true")) {
                cameraUiWrapper.camParametersHandler.SetDngActive(true);
                valueText.setText("DNG");
            }
        }
        else {
            camera1picFormat = null;
            super.SetParameter(cameraUiWrapper.camParametersHandler.PictureFormat);
        }
    }

    private class Camera1picFormat extends AbstractModeParameter
    {
        public Camera1picFormat(Handler uiHandler) {
            super(uiHandler);
        }

        @Override
        public boolean IsSupported()
        {
            return DeviceUtils.isCamera1DNGSupportedDevice() || DeviceUtils.isMediaTekDevice() || !(cameraUiWrapper instanceof CameraUiWrapper);
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            if (!DeviceUtils.isMediaTekDevice()) {
                if (valueToSet.equals("DNG")) {
                    cameraUiWrapper.camParametersHandler.SetDngActive(true);
                    cameraUiWrapper.camParametersHandler.PictureFormat.SetValue(appSettingsManager.getString(MenuItemBayerFormat.APPSETTING_BAYERFORMAT), false);
                    appSettingsManager.setString(AppSettingsManager.SETTING_PICTUREFORMAT, appSettingsManager.getString(MenuItemBayerFormat.APPSETTING_BAYERFORMAT));
                    appSettingsManager.setString(AppSettingsManager.SETTING_DNG, true + "");
                } else if (valueToSet.equals("JPEG")) {
                    cameraUiWrapper.camParametersHandler.SetDngActive(false);
                    cameraUiWrapper.camParametersHandler.PictureFormat.SetValue("jpeg", false);
                    appSettingsManager.setString(AppSettingsManager.SETTING_PICTUREFORMAT, "jpeg");
                    appSettingsManager.setString(AppSettingsManager.SETTING_DNG, false + "");
                } else
                    super.SetValue(valueToSet, setToCamera);
            }
            else
            {
                if (valueToSet.equals("DNG")) {
                    cameraUiWrapper.camParametersHandler.SetDngActive(true);
                    appSettingsManager.setString(AppSettingsManager.SETTING_DNG, true + "");
                }
                else if (valueToSet.equals("JPEG"))
                {
                    cameraUiWrapper.camParametersHandler.SetDngActive(false);
                    appSettingsManager.setString(AppSettingsManager.SETTING_DNG, false + "");
                }
            }

        }

        @Override
        public String GetValue()
        {
            if (DeviceUtils.isCamera1DNGSupportedDevice()|| DeviceUtils.isMediaTekDevice())
            {
                String t = appSettingsManager.getString(AppSettingsManager.SETTING_DNG);
                if (t.equals("") || t.equals("false"))
                {
                    return "JPEG";
                }
                else if (t.equals("true"))
                    return "DNG";
            }
            else
                return cameraUiWrapper.camParametersHandler.PictureFormat.GetValue();
            return null;
        }

        @Override
        public String[] GetValues()
        {
            if (DeviceUtils.isCamera1DNGSupportedDevice())
                return new String[]{"DNG", "JPEG"};
            else
                return cameraUiWrapper.camParametersHandler.PictureFormat.GetValues();
        }
    }
}
