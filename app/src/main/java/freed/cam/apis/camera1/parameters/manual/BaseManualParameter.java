/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.camera1.parameters.manual;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;
import freed.utils.Log;

/**
 * Created by troop on 17.08.2014.
 */
public class BaseManualParameter extends AbstractParameter
{

    private final String TAG = BaseManualParameter.class.getSimpleName();
    /**
     * Holds the list of Supported parameters
     */
    protected Parameters  parameters;
    /*
     * The name of the current key_value to get like brightness
     */
    protected String key_value;


    private int default_value;
    public void Set_Default_Value(int val){
        default_value = val; Log.d(TAG, "set default to:" + val);}
    public int Get_Default_Value(){return default_value;}

    public void ResetToDefault()
    {
        if (isSupported)
        {
            Log.d(TAG,"Reset Back from:" + currentInt + " to:" + default_value);
            SetValue(default_value, true);
            fireIntValueChanged(default_value);
        }
    }

    public BaseManualParameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode)
    {
        super(cameraUiWrapper,settingMode);
        this.parameters = parameters;
        SettingMode mode = (SettingMode) SettingsManager.get(key);
        key_value = mode.getKEY();
        currentString = mode.get();
        stringvalues = mode.getValues();
        isSupported = mode.isSupported();
        isVisible = isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public void setValue(int valueToset, boolean setToCamera)
    {
        currentInt = valueToset;
        Log.d(TAG, "set " + key_value + " to " + valueToset);
        if(stringvalues == null || stringvalues.length == 0)
            return;
        settingMode.set(String.valueOf(valueToset));
        parameters.set(key_value, stringvalues[valueToset]);
        fireIntValueChanged(valueToset);
        fireStringValueChanged(stringvalues[valueToset]);
        try
        {
            Log.d(TAG,"SetValue " + key_value);
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }
    }


    public ParameterEvents GetPicFormatListner()
    {
        return picformatListner;
    }

    private final ParameterEvents picformatListner = new ParameterEvents()
    {

        @Override
        public void onIsSupportedChanged(boolean value) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean value) {

        }

        @Override
        public void onIntValueChanged(int current) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onStringValueChanged(String val) {
            if (val.equals(cameraUiWrapper.getResString(R.string.jpeg_)) && isSupported)
            {
                isVisible = true;
                fireIsSupportedChanged(true);
            }
            else {
                isVisible = false;
                fireIsSupportedChanged(false);
                ResetToDefault();
            }
        }
    };

    public ModuleChangedEvent GetModuleListner()
    {
        return moduleListner;
    }

    private final ModuleChangedEvent moduleListner =new ModuleChangedEvent() {
        @Override
        public void onModuleChanged(String module)
        {
            if (module.equals(cameraUiWrapper.getResString(R.string.module_video)) && isSupported)
                fireIsSupportedChanged(true);
            else if (module.equals(cameraUiWrapper.getResString(R.string.module_picture))
                    || module.equals(cameraUiWrapper.getResString(R.string.module_interval))
                    || module.equals(cameraUiWrapper.getResString(R.string.module_hdr)))
            {
                fireIsSupportedChanged(isVisible);
            }
        }
    };
}
