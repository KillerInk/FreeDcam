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

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingKeys;

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
        if(parameters.get(FreedApplication.getStringFromRessources(R.string.morpho_hht)) != null
                && parameters.get(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr)) != null) {
            setViewState(ViewState.Visible);
            //cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).addEventListner(this);
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_))) {
            parameters.set(FreedApplication.getStringFromRessources(R.string.morpho_hdr), FreedApplication.getStringFromRessources(R.string.false_));
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HDRMode).fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
            parameters.set("capture-burst-exposures","-10,0,10");
            parameters.set(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr), FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_aebracket));
            parameters.set(FreedApplication.getStringFromRessources(R.string.morpho_hht), FreedApplication.getStringFromRessources(R.string.true_));
        } else {
            parameters.set(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr), FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_aebracket));
            parameters.set(FreedApplication.getStringFromRessources(R.string.morpho_hht), FreedApplication.getStringFromRessources(R.string.false_));
        }
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public String GetStringValue()
    {
        if (parameters.get(FreedApplication.getStringFromRessources(R.string.morpho_hht)).equals(FreedApplication.getStringFromRessources(R.string.true_))
                && parameters.get(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr)).equals(FreedApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_off)))
            return FreedApplication.getStringFromRessources(R.string.on_);
        else
            return FreedApplication.getStringFromRessources(R.string.off_);
    }

    @Override
    public String[] getStringValues()
    {
       return new String[] {FreedApplication.getStringFromRessources(R.string.off_),FreedApplication.getStringFromRessources(R.string.on_)};
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_video))|| curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_hdr)))
            Hide();
        else
        {
            if (format.contains(FreedApplication.getStringFromRessources(R.string.jpeg_))) {
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
        if (val.contains(FreedApplication.getStringFromRessources(R.string.jpeg_))&&!visible &&!curmodule.equals(FreedApplication.getStringFromRessources(R.string.module_hdr)))
            Show();

        else if (!val.contains(FreedApplication.getStringFromRessources(R.string.jpeg_))&& visible) {
            Hide();
        }
    }

    private void Hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(FreedApplication.getStringFromRessources(R.string.off_),true);
        fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
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
