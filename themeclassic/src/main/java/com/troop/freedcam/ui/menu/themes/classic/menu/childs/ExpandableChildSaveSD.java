package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;
import android.os.Handler;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

/**
 * Created by Ingo on 24.05.2015.
 */
public class ExpandableChildSaveSD extends ExpandableChild
{
    AbstractCameraUiWrapper cameraUiWrapper;
    public ExpandableChildSaveSD(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        this.parameterHolder = new SdParamter(null);
        valueTextView.setText(parameterHolder.GetValue());
    }

    public  void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
    }

    @Override
    public void setValue(String value)
    {
        if (value.equals(external))
        {
            boolean canWriteExternal = false;
            final String path = StringUtils.GetExternalSDCARD() + StringUtils.freedcamFolder + "/test.t";
            final File f = new File(path);
            try {
                f.createNewFile();
                canWriteExternal = true;
                f.delete();
            }
            catch (Exception ex)
            {
                canWriteExternal =false;
            }
            if (canWriteExternal) {
                appSettingsManager.SetWriteExternal(true);
                valueTextView.setText(external);
            }
            else {
                cameraUiWrapper.onCameraError("Cant write on External SD, pls apply SD fix");
                valueTextView.setText(internal);
            }
        }
        else {
            appSettingsManager.SetWriteExternal(false);
            valueTextView.setText(value);
        }
    }

    final String internal = "Internal";
    final String external ="External";

    public class SdParamter extends AbstractModeParameter
    {

        public SdParamter(Handler uiHandler) {
            super(uiHandler);
        }

        @Override
        public void addEventListner(I_ModeParameterEvent eventListner) {
            super.addEventListner(eventListner);
        }

        @Override
        public void removeEventListner(I_ModeParameterEvent parameterEvent) {
            super.removeEventListner(parameterEvent);
        }

        @Override
        public boolean IsSupported()
        {
            File file = new File(StringUtils.GetExternalSDCARD());
            if (file.exists())
                return true;
            else
                return false;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {

        }

        @Override
        public String GetValue()
        {
            if (appSettingsManager.GetWriteExternal())
                return external;
            else
                return internal;
        }

        @Override
        public String[] GetValues() {
            return new String[] {internal,external};
        }

        @Override
        public void BackgroundValueHasChanged(String value) {
            super.BackgroundValueHasChanged(value);
        }

        @Override
        public void BackgroundValuesHasChanged(String[] value) {
            super.BackgroundValuesHasChanged(value);
        }

        @Override
        public void BackgroundIsSupportedChanged(boolean value) {
            super.BackgroundIsSupportedChanged(value);
        }

        @Override
        public void BackgroundSetIsSupportedHasChanged(boolean value) {
            super.BackgroundSetIsSupportedHasChanged(value);
        }
    }
}
