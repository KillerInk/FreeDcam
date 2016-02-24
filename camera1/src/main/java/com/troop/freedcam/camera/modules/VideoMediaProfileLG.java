package com.troop.freedcam.camera.modules;

import com.lge.media.CamcorderProfileEx;
import com.troop.freedcam.i_camera.modules.VideoMediaProfile;

/**
 * Created by troop on 24.02.2016.
 */
public class VideoMediaProfileLG extends VideoMediaProfile
{
   public VideoMediaProfileLG(CamcorderProfileEx ex, String ProfileName, VideoMode mode)
    {
        super(ex.audioBitRate,ex.audioChannels,ex.audioCodec,ex.audioSampleRate,ex.duration,ex.fileFormat,ex.quality,ex.videoBitRate,ex.videoCodec,ex.videoFrameRate,ex.videoFrameHeight,ex.videoFrameWidth,ProfileName,mode);
    }
}
