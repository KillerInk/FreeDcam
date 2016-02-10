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

    public VideoMediaProfile(int v1,int v2, int v3,int v4,int v5, int v6, int v7, int v8, int v9, int v10,int v11, int v12, String ProfileName, VideoMode mode)
    {
        this.audioBitRate = v1;
        this.audioChannels = v2;
        this.audioCodec = v3;
        this.audioSampleRate = v4;
        this.duration = v5;
        this.fileFormat = v6;
        this.quality = v7;
        this.videoBitRate = v8;
        this.videoCodec = v9;
        this.videoFrameRate = v10;
        this.videoFrameHeight = v11;
        this.videoFrameWidth = v12;
        this.ProfileName = ProfileName;
        this.Mode = mode;
    }

    public VideoMediaProfile clone()
    {
        return new VideoMediaProfile(audioBitRate,audioChannels, audioCodec, audioSampleRate, duration, fileFormat,quality,videoBitRate,videoCodec,videoFrameRate, videoFrameHeight,videoFrameWidth, ProfileName, Mode);
    }
}
