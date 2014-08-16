package com.troop.freecam.camera;

import com.troop.freecam.camera.modules.ModuleHandler;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecam.surfaces.CamPreview;

/**
 * Created by troop on 16.08.2014.
 */
public class CameraUiWrapper
{
    private CamPreview preview;
    private ModuleHandler moduleHandler;
    private BaseCameraHolder cameraHolder;
    AppSettingsManager appSettingsManager;
    SoundPlayer soundPlayer;


    public CameraUiWrapper(CamPreview preview, AppSettingsManager appSettingsManager, SoundPlayer soundPlayer)
    {
        this.preview = preview;
        this.soundPlayer = soundPlayer;
        this.appSettingsManager = appSettingsManager;
        cameraHolder = new BaseCameraHolder();
        moduleHandler = new ModuleHandler(cameraHolder, appSettingsManager, soundPlayer);
    }
}
