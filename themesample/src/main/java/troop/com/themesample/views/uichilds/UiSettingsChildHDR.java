package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by GeorgeKiarie on 1/29/2016.
 */
public class UiSettingsChildHDR extends UiSettingsChild
{
    AbstractCameraUiWrapper cameraUiWrapper;
    HDRLogic hdrLogic;
    boolean isPictureModule = true;

    public UiSettingsChildHDR(Context context) {
        super(context);
    }

    public UiSettingsChildHDR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UiSettingsChildHDR(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;

        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());

    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {
        if (cameraUiWrapper instanceof CameraUiWrapper )
        {
            hdrLogic = new HDRLogic(this.getHandler());

            super.SetParameter(hdrLogic);
        }
        else {
            hdrLogic = null;
            super.SetParameter(cameraUiWrapper.camParametersHandler.HDR_State);
        }
        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());
    }

    @Override
    public String ModuleChanged(String module)
    {
        if (module.equals(AbstractModuleHandler.MODULE_PICTURE))
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
        return module;
    }

    @Override
    public void ParametersLoaded()
    {
        sendLog("Parameters Loaded");
        if (parameter != null && parameter.IsSupported() &&
                (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_PICTURE)))
        {
            setTextToTextBox(parameter);
            onIsSupportedChanged(true);
        }
        else
            onIsSupportedChanged(false);
    }

    private class HDRLogic extends AbstractModeParameter
    {
        public HDRLogic(Handler uiHandler) {
            super(uiHandler);
        }

        @Override
        public boolean IsSupported() {
            if (cameraUiWrapper instanceof CameraUiWrapper) {


                if (isPictureModule && !(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("bayer") ||
                        appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("raw"))) {

                    try {
                        if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Scene() || ((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Auto())
                            return true;
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }


            }

                return false;
        }


        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            if (cameraUiWrapper instanceof CameraUiWrapper) {
                if(DeviceUtils.isXiaomiMI3W() || DeviceUtils.isXiaomiMI4W()) {
                    if(valueToSet.equals("on"))
                    {
                        ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("capture-burst-exposures", "-10,0,10");
                        ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("morpho-hdr", "true");
                        ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("ae-bracket-hdr", "AE-Bracket");
                    }
                    else
                    {
                        ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("morpho-hdr", "false");
                        ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("ae-bracket-hdr", "off");
                    }
                }
                else if(DeviceUtils.isLG_G3() || DeviceUtils.isG2())
                {
                    switch (valueToSet)
                    {
                        case "on":
                            ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("hdr-mode", "1");
                            break;
                        case "off":
                            ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("hdr-mode", "0");
                            break;
                        case "auto":
                            ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("hdr-mode", "2");
                    }

                }
                else {
                    switch (valueToSet) {
                        case "off":

                            if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Scene())
                                ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("scene-mode", "auto");
                            if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Auto())
                                ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("auto-hdr-enable", "disable");
                            break;
                        case "on":
                            if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Scene())
                                ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("scene-mode", "on");
                            if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Auto())
                                ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("auto-hdr-enable", "disable");
                            break;
                        case "auto":
                            if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Scene())
                                ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("scene-mode", "asd");
                            if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Auto())
                                ((CamParametersHandler) cameraUiWrapper.camParametersHandler).setHDR("auto-hdr-enable", "enable");


                    }
                }
            }

        }

        @Override
        public String GetValue()
        {
            return "off";

        }

        @Override
        public String[] GetValues()
        {
            List<String> hdrVals =  new ArrayList<>();
            hdrVals.add("off");
            if (cameraUiWrapper instanceof CameraUiWrapper) {
            if(DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W())
            {
                hdrVals.add("on");
            }
            else if(DeviceUtils.isG2() || DeviceUtils.isLG_G3()) {
                hdrVals.add("on");
                hdrVals.add("auto");
            }
                else  {
                    if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Scene())
                        hdrVals.add("on");
                    if (((CamParametersHandler) cameraUiWrapper.camParametersHandler).HDR_supported_Auto())
                        hdrVals.add("auto");
                }
            }

            return hdrVals.toArray(new String[hdrVals.size()]);


        }
    }
}