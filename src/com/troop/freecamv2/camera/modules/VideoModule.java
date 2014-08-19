package com.troop.freecamv2.camera.modules;

import com.troop.freecamv2.camera.BaseCameraHolder;

import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecamv2.ui.AppSettingsManager;

/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractModule
{
    public final String TAG = "freecam.VideoModule";

    public VideoModule(BaseCameraHolder cameraHandler, SoundPlayer soundPlayer, AppSettingsManager Settings) {
        super(cameraHandler, soundPlayer, Settings);
        name  = "VideoModule";
    }


//I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
        if (!isWorking)
            startRecording();
        else
            stopRecording();

    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }
//I_Module END


    private void startRecording()
    {
        //TODO add recording logic
    }

    private void stopRecording()
    {
        //TODO add stop recording logic
    }
}
