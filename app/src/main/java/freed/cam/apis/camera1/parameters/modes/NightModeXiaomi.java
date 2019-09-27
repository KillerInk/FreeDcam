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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 10.06.2016.
 */
public class NightModeXiaomi extends BaseModeParameter implements ParameterEvents
{
    final String TAG = NightModeZTE.class.getSimpleName();
    private boolean visible = true;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public NightModeXiaomi(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper,SettingKeys.NightMode);
        if(parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.morpho_hht)) != null
                && parameters.get(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr)) != null) {
            setViewState(ViewState.Visible);
            //cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).addEventListner(this);
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_))) {
            parameters.set(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.morpho_hdr), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.false_));
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HDRMode).fireStringValueChanged(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_));
            parameters.set("capture-burst-exposures","-10,0,10");
            parameters.set(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr), SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr_values_aebracket));
            parameters.set(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.morpho_hht), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.true_));
        } else {
            parameters.set(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr), SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr_values_aebracket));
            parameters.set(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.morpho_hht), cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.false_));
        }
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public String GetStringValue()
    {
        if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.morpho_hht)).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.true_))
                && parameters.get(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr)).equals(SettingsManager.getInstance().getResString(R.string.ae_bracket_hdr_values_off)))
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_);
        else
            return cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_);
    }

    @Override
    public String[] getStringValues()
    {
       return new String[] {cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_),cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_)};
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_video))|| curmodule.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_hdr)))
            Hide();
        else
        {
            if (format.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_))) {
                Show();
                setViewState(ViewState.Visible);
            }
        }
    }

    @Override
    public void onViewStateChanged(ViewState value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String val) {
        format = val;
        if (val.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_))&&!visible &&!curmodule.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_hdr)))
            Show();

        else if (!val.contains(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_))&& visible) {
            Hide();
        }
    }

    private void Hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_),true);
        fireStringValueChanged(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_));
        setViewState(ViewState.Hidden);
    }

    private void Show()
    {
        visible = true;
        SetValue(state,true);
        fireStringValueChanged(state);
        setViewState(ViewState.Visible);
    }
}
