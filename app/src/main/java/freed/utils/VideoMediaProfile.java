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

package freed.utils;

import android.media.CamcorderProfile;

import freed.cam.apis.sonyremote.sonystuff.XmlElement;

/**
 * Created by troop on 04.02.2016.
 * Thats based on CamcorderProfile http://developer.android.com/reference/android/media/CamcorderProfile.html
 */
public class VideoMediaProfile
{
    private static final String TAG = VideoMediaProfile.class.getSimpleName();
    //The target audio output bit rate in bits per second
    public int audioBitRate;
    //The number of audio channels used for the audio track
    public int audioChannels;
    //The audio encoder being used for the audio track.
    public int audioCodec;
    //The audio sampling rate used for the audio track
    public int audioSampleRate;
    //Default recording duration in seconds before the session is terminated.
    public int duration;
    //The file output format of the camcorder profile see MediaRecorder.OutputFormat
    private final int fileFormat;
    //The quality level of the camcorder profile
    private final int quality;
    public int videoBitRate;
    public int videoCodec;
    public int videoFrameHeight;
    public int videoFrameRate;
    public int videoFrameWidth;

    public String ProfileName;
    public VideoMode Mode;

    public boolean isAudioActive;

    public enum VideoMode
    {
        Normal,
        Highspeed,
        Timelapse,
        SlowMO,
    }

    public VideoMediaProfile(CamcorderProfile ex,String ProfileName, VideoMode mode, boolean isAudioActive)
    {
        audioBitRate = ex.audioBitRate;
        audioChannels = ex.audioChannels;
        audioCodec = ex.audioCodec;
        audioSampleRate = ex.audioSampleRate;
        duration = ex.duration;
        fileFormat = ex.fileFormat;
        quality = ex.quality;
        videoBitRate = ex.videoBitRate;
        videoCodec = ex.videoCodec;
        videoFrameRate = ex.videoFrameRate;
        videoFrameHeight = ex.videoFrameHeight;
        videoFrameWidth = ex.videoFrameWidth;
        this.ProfileName = ProfileName;
        Mode = mode;
        this.isAudioActive = isAudioActive;
        Log.d(TAG, "ProfileName:"+ ProfileName+ "Duration:"+ duration +"FileFormat:"+ fileFormat +"Quality:"+ quality);
        Log.d(TAG, "ABR:"+ audioBitRate +"AChannels:"+ audioChannels +"Acodec:"+ audioCodec +"AsampleRate"+ audioSampleRate +"audio_active:" + isAudioActive);
        Log.d(TAG,"VBitrate:"+ videoBitRate +"VCodec:"+ videoCodec +"VFrameRate:"+ videoFrameRate +"VWidth:"+ videoFrameWidth +"Vheight:"+ videoFrameHeight);
    }

    public VideoMediaProfile(int v1, int v2, int v3, int v4, int v5, int v6, int v7, int v8, int v9, int v10, int v11, int v12, String ProfileName, VideoMode mode, boolean isAudioActive)
    {
        audioBitRate = v1;
        audioChannels = v2;
        audioCodec = v3;
        audioSampleRate = v4;
        duration = v5;
        fileFormat = v6;
        quality = v7;
        videoBitRate = v8;
        videoCodec = v9;
        videoFrameRate = v10;
        videoFrameHeight = v11;
        videoFrameWidth = v12;
        this.ProfileName = ProfileName;
        Mode = mode;
        this.isAudioActive = isAudioActive;
        Log.d(TAG, "ProfileName:"+ ProfileName+ "Duration:"+ duration +"FileFormat:"+ fileFormat +"Quality:"+ quality);
        Log.d(TAG, "ABR:"+ audioBitRate +"AChannels:"+ audioChannels +"Acodec:"+ audioCodec +"AsampleRate"+ audioSampleRate +"audio_active:" + isAudioActive);
        Log.d(TAG,"VBitrate:"+ videoBitRate +"VCodec:"+ videoCodec +"VFrameRate:"+ videoFrameRate +"VWidth:"+ videoFrameWidth +"Vheight:"+ videoFrameHeight);
    }

    public VideoMediaProfile(String t) {
        String[] ar = t.split(" ");
        audioBitRate = Integer.parseInt(ar[0]);
        audioChannels = Integer.parseInt(ar[1]);
        audioCodec = Integer.parseInt(ar[2]);
        audioSampleRate = Integer.parseInt(ar[3]);
        duration = Integer.parseInt(ar[4]);
        fileFormat = Integer.parseInt(ar[5]);
        quality = Integer.parseInt(ar[6]);
        videoBitRate = Integer.parseInt(ar[7]);
        videoCodec = Integer.parseInt(ar[8]);
        videoFrameRate = Integer.parseInt(ar[9]);
        videoFrameHeight = Integer.parseInt(ar[10]);
        videoFrameWidth = Integer.parseInt(ar[11]);
        ProfileName = ar[12];
        Mode = VideoMode.valueOf(ar[13]);
        isAudioActive = ar.length == 14 || Boolean.parseBoolean(ar[14]);

        Log.d(TAG, "ProfileName:" + ProfileName + "Duration:" + duration + "FileFormat:" + fileFormat + "Quality:" + quality);
        Log.d(TAG, "ABR:" + audioBitRate + "AChannels:" + audioChannels + "Acodec:" + audioCodec + "AsampleRate" + audioSampleRate + "audio_active:" + isAudioActive);
        Log.d(TAG, "VBitrate:" + videoBitRate + "VCodec:" + videoCodec + "VFrameRate:" + videoFrameRate + "VWidth:" + videoFrameWidth + "Vheight:" + videoFrameHeight);
    }



    public String GetString()
    {
        String b = audioBitRate + " " +
                audioChannels + " " +
                audioCodec + " " +
                audioSampleRate + " " +
                duration + " " +
                fileFormat + " " +
                quality + " " +
                videoBitRate + " " +
                videoCodec + " " +
                videoFrameRate + " " +
                videoFrameHeight + " " +
                videoFrameWidth + " " +
                ProfileName + " " +
                Mode + " " +
                isAudioActive + " ";
        return b;
    }

    public VideoMediaProfile(XmlElement xmlElement)
    {
        ProfileName = xmlElement.getAttribute("name", "");
        audioChannels = xmlElement.findChild("audioChannels").getIntValue(0);
        audioCodec = xmlElement.findChild("audioCodec").getIntValue(0);
        audioSampleRate = xmlElement.findChild("audioSampleRate").getIntValue(0);
        duration = xmlElement.findChild("duration").getIntValue(0);
        fileFormat = xmlElement.findChild("fileFormat").getIntValue(0);
        quality = xmlElement.findChild("quality").getIntValue(0);
        videoBitRate = xmlElement.findChild("videoBitRate").getIntValue(0);
        videoCodec = xmlElement.findChild("videoCodec").getIntValue(0);
        videoFrameRate = xmlElement.findChild("videoFrameRate").getIntValue(0);
        videoFrameHeight = xmlElement.findChild("videoFrameHeight").getIntValue(0);
        videoFrameWidth = xmlElement.findChild("videoFrameWidth").getIntValue(0);
        isAudioActive = xmlElement.findChild("isAudioActive").getBooleanValue();
        Mode = VideoMode.valueOf(xmlElement.findChild("Mode").getValue());
    }
    public String getXmlString()
    {
        String t = new String();
        t += "<mediaprofile name= " +String.valueOf("\"") +String.valueOf(ProfileName) +String.valueOf("\"")  +">" + "\r\n";
        t += "<audioChannels>" + audioChannels + "</audioChannels>" + "\r\n";
        t += "<audioCodec>" + audioCodec + "</audioCodec>" + "\r\n";
        t += "<audioSampleRate>" + audioSampleRate + "</audioSampleRate>" + "\r\n";
        t += "<duration>" + duration + "</duration>" + "\r\n";
        t += "<fileFormat>" + fileFormat + "</fileFormat>" + "\r\n";
        t += "<quality>" + quality + "</quality>" + "\r\n";
        t += "<videoBitRate>" + videoBitRate + "</videoBitRate>" + "\r\n";
        t += "<videoCodec>" + videoCodec + "</videoCodec>" + "\r\n";
        t += "<videoFrameRate>" + videoFrameRate + "</videoFrameRate>" + "\r\n";
        t += "<videoFrameHeight>" + videoFrameHeight + "</videoFrameHeight>" + "\r\n";
        t += "<videoFrameWidth>" + videoFrameWidth + "</videoFrameWidth>" + "\r\n";
        t += "<isAudioActive>" + isAudioActive + "</isAudioActive>" + "\r\n";
        t += "<Mode>" + Mode.toString() + "</Mode>" + "\r\n";
        t += "</mediaprofile>"  + "\r\n";
        return t;
    }

    public VideoMediaProfile clone()
    {
        return new VideoMediaProfile(audioBitRate, audioChannels, audioCodec, audioSampleRate, duration, fileFormat, quality, videoBitRate, videoCodec, videoFrameRate, videoFrameHeight, videoFrameWidth, ProfileName, Mode, isAudioActive);
    }

}
