package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 07.07.2015.
 */
public class UiSettingsChildFormat extends UiSettingsChild
{
    AbstractCameraUiWrapper cameraUiWrapper;
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

    private class camera1picFormat extends AbstractModeParameter
    {
        public camera1picFormat(Handler uiHandler) {
            super(uiHandler);
        }

        @Override
        public boolean IsSupported() {
            return DeviceUtils.isCamera1DNGSupportedDevice();
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            super.SetValue(valueToSet, setToCamera);
        }

        @Override
        public String GetValue()
        {
            final String format =cameraUiWrapper.camParametersHandler.PictureFormat.GetValue();
            if (DeviceUtils.isCamera1DNGSupportedDevice())
            {
                if (format.contains("bayer") || format.contains("raw"))
                    return "DNG";
                else
                    return "JPEG";
            }
            else
                return format;
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
