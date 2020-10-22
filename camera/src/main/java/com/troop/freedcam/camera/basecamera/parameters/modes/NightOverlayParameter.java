package com.troop.freedcam.camera.basecamera.parameters.modes;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

//import freed.ActivityInterface;

/**
 * Created by Ingo on 24.09.2016.
 */
public class NightOverlayParameter extends AbstractParameter {

    private final String TAG = NightOverlayParameter.class.getSimpleName();
    //private ActivityInterface activityInterface;

    public NightOverlayParameter(CameraControllerInterface cameraControllerInterface)
    {
        super(SettingKeys.NightOverlay);
        //this.activityInterface = cameraControllerInterface.getActivityInterface();
        setViewState(ViewState.Visible);
    }

    @Override
    public String[] getStringValues() {
        return new String[] { ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_), ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_) };
    }

    @Override
    public String GetStringValue() {
        boolean enable = SettingsManager.getGlobal(SettingKeys.NightOverlay).get();
        if (enable)
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_);
        else
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        SettingsManager.getGlobal(SettingKeys.NightOverlay).set(valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)));
        Log.d(TAG, "Nightoverlay :" +SettingsManager.getGlobal(SettingKeys.NightOverlay).get());
        //activityInterface.SetNightOverlay();
        fireStringValueChanged(valueToSet);

    }
}
