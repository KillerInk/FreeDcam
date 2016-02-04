package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;

import com.lge.media.CamcorderProfileEx;

/**
 * Created by troop on 04.02.2016.
 */
public class VideoMediaProfile
{
    public int audioBitRate;
    public int audioChannels;
    public int audioCodec;
    public int audioSampleRate;
    public int duration;
    public int fileFormat;
    public int quality;
    public int videoBitRate;
    public int videoCodec;
    public int videoFrameHeight;
    public int videoFrameRate;
    public int videoFrameWidth;

    public String ProfileName;
    public VideoMode Mode;

    public enum VideoMode
    {
        Normal,
        Highspeed,
        Timelapse,
    }

    public VideoMediaProfile(CamcorderProfileEx ex, String ProfileName, VideoMode mode)
    {
        this.audioBitRate = ex.audioBitRate;
        this.audioChannels = ex.audioChannels;
        this.audioCodec = ex.audioCodec;
        this.audioSampleRate = ex.audioSampleRate;
        this.duration = ex.duration;
        this.fileFormat = ex.fileFormat;
        this.quality = ex.quality;
        this.videoBitRate = ex.videoBitRate;
        this.videoCodec = ex.videoCodec;
        this.videoFrameRate = ex.videoFrameRate;
        this.videoFrameHeight = ex.videoFrameHeight;
        this.videoFrameWidth = ex.videoFrameWidth;
        this.ProfileName = ProfileName;
        this.Mode = mode;
    }

    public VideoMediaProfile(CamcorderProfile ex,String ProfileName, VideoMode mode)
    {
        this.audioBitRate = ex.audioBitRate;
        this.audioChannels = ex.audioChannels;
        this.audioCodec = ex.audioCodec;
        this.audioSampleRate = ex.audioSampleRate;
        this.duration = ex.duration;
        this.fileFormat = ex.fileFormat;
        this.quality = ex.quality;
        this.videoBitRate = ex.videoBitRate;
        this.videoCodec = ex.videoCodec;
        this.videoFrameRate = ex.videoFrameRate;
        this.videoFrameHeight = ex.videoFrameHeight;
        this.videoFrameWidth = ex.videoFrameWidth;
        this.ProfileName = ProfileName;
        this.Mode = mode;
    }
}
