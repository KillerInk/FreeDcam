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
    public static long MAX_RECORDING_SIZE = 3037822976L;
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

    public long maxRecordingSize;

    public int profile = -1;
    public int level = -1;
    public String encoderName = "Default";

    public boolean videoHdr = false;
    public int opcode = -1;
    public int preview_opcode = -1;

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
        duration = ex.duration *60 *1000; //from min to ms
        maxRecordingSize = 0;
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

    public VideoMediaProfile(int v1,
                             int v2,
                             int v3,
                             int v4,
                             int v5,
                             int v6,
                             int v7,
                             int v8,
                             int v9,
                             int v10,
                             int v11,
                             int v12,
                             long maxRecordingSize,
                             String ProfileName,
                             VideoMode mode,
                             boolean isAudioActive,
                             int profile,
                             int lvl,
                             String encoderName,
                             boolean videohdr,
                             int opcode,
                             int preview_opcode)
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
        this.maxRecordingSize = maxRecordingSize;
        this.profile = profile;
        this.level =lvl;
        this.encoderName = encoderName;
        this.videoHdr = videohdr;
        this.opcode = opcode;
        this.preview_opcode = preview_opcode;
        Log.d(TAG, "ProfileName:"+ ProfileName+ " Duration:"+ duration +" FileFormat:"+ fileFormat +" Quality:"+ quality +" RecSize:" + maxRecordingSize);
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
        if (ar.length == 16)
            maxRecordingSize = Long.parseLong(ar[15]);
        else
            maxRecordingSize = 0;

        Log.d(TAG, "ProfileName:" + ProfileName + "Duration:" + duration + "FileFormat:" + fileFormat + "Quality:" + quality);
        Log.d(TAG, "ABR:" + audioBitRate + "AChannels:" + audioChannels + "Acodec:" + audioCodec + "AsampleRate" + audioSampleRate + "audio_active:" + isAudioActive);
        Log.d(TAG, "VBitrate:" + videoBitRate + "VCodec:" + videoCodec + "VFrameRate:" + videoFrameRate + "VWidth:" + videoFrameWidth + "Vheight:" + videoFrameHeight);
    }



    public String GetString()
    {
        return audioBitRate + " " +
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
                isAudioActive + " " +
                maxRecordingSize;
    }

    public VideoMediaProfile(XmlElement xmlElement)
    {
        ProfileName = xmlElement.getAttribute("name", "");
        audioChannels = xmlElement.findChild("audioChannels").getIntValue(0);
        audioCodec = xmlElement.findChild("audioCodec").getIntValue(0);
        audioBitRate = xmlElement.findChild("audioBitRate").getIntValue(0);
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
        maxRecordingSize = xmlElement.findChild("recordingsize").getLongValue();
        profile = xmlElement.findChild("profile").getIntValue(-1);
        level = xmlElement.findChild("level").getIntValue(-1);
        encoderName = xmlElement.findChild("encodername").getValue();
        videoHdr = xmlElement.findChild("videohdr").getBooleanValue();
        opcode = xmlElement.findChild("opcode").getIntValue(-1);
        preview_opcode = xmlElement.findChild("preview_opcode").getIntValue(-1);
    }
    public String getXmlString()
    {
        String t = "";
        t += "<mediaprofile name= " +String.valueOf("\"") +String.valueOf(ProfileName) +String.valueOf("\"")  +">" + "\r\n";
        t += "<audioChannels>" + audioChannels + "</audioChannels>" + "\r\n";
        t += "<audioCodec>" + audioCodec + "</audioCodec>" + "\r\n";
        t += "<audioBitRate>" + audioBitRate + "</audioBitRate>" + "\r\n";
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
        t += "<recordingsize>" + maxRecordingSize + "</recordingsize>" + "\r\n";
        t += "<profile>" + profile + "</profile>" + "\r\n";
        t += "<level>" + level + "</level>" + "\r\n";
        t += "<encodername>" + encoderName + "</encodername>" + "\r\n";
        t += "<videohdr>" + videoHdr + "</videohdr>" + "\r\n";
        t += "<opcode>" + opcode + "</opcode>" + "\r\n";
        t += "<preview_opcode>" + preview_opcode + "</preview_opcode>" + "\r\n";
        t += "</mediaprofile>"  + "\r\n";
        return t;
    }

    public VideoMediaProfile clone()
    {
        return new VideoMediaProfile(audioBitRate,
                audioChannels,
                audioCodec,
                audioSampleRate,
                duration,
                fileFormat,
                quality,
                videoBitRate,
                videoCodec,
                videoFrameRate,
                videoFrameHeight,
                videoFrameWidth,
                maxRecordingSize,
                ProfileName,
                Mode,
                isAudioActive,
                profile,
                level,
                encoderName,
                videoHdr,
                opcode,
                preview_opcode);
    }

}
