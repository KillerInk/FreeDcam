package com.freedcam.apis.camera1.camera.modules;

import com.freedcam.apis.basecamera.camera.modules.VideoMediaProfile;
import com.lge.media.CamcorderProfileEx;

/**
 * Created by troop on 24.02.2016.
 */
public class VideoMediaProfileLG extends VideoMediaProfile
{
   public VideoMediaProfileLG(CamcorderProfileEx ex, String ProfileName, VideoMode mode, boolean isAudioActive)
    {
        super(ex.audioBitRate,ex.audioChannels,ex.audioCodec,ex.audioSampleRate,ex.duration,ex.fileFormat,ex.quality,ex.videoBitRate,ex.videoCodec,ex.videoFrameRate,ex.videoFrameHeight,ex.videoFrameWidth,ProfileName,mode, isAudioActive);
    }
}
