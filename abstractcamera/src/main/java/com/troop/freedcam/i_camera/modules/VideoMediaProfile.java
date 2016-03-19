package com.troop.freedcam.i_camera.modules;

import android.media.CamcorderProfile;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.utils.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

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
    public int fileFormat;
    //The quality level of the camcorder profile
    public int quality;
    public int videoBitRate;
    public int videoCodec;
    public int videoFrameHeight;
    public int videoFrameRate;
    public int videoFrameWidth;

    public String ProfileName;
    public VideoMode Mode;

    public boolean isAudioActive = false;

    public enum VideoMode
    {
        Normal,
        Highspeed,
        Timelapse,
    }

    public VideoMediaProfile(CamcorderProfile ex,String ProfileName, VideoMode mode, boolean isAudioActive)
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
        this.isAudioActive = isAudioActive;
        Logger.d(TAG, "ProfileName:"+ ProfileName+ "Duration:"+duration +"FileFormat:"+fileFormat+"Quality:"+quality);
        Logger.d(TAG, "ABR:"+audioBitRate +"AChannels:"+audioChannels+"Acodec:"+audioCodec +"AsampleRate"+audioSampleRate+"audio_active:" + isAudioActive);
        Logger.d(TAG,"VBitrate:"+videoBitRate+"VCodec:"+videoCodec+"VFrameRate:"+videoFrameRate+"VWidth:"+videoFrameWidth+"Vheight:"+videoFrameHeight);
    }

    public VideoMediaProfile(int v1,int v2, int v3,int v4,int v5, int v6, int v7, int v8, int v9, int v10,int v11, int v12, String ProfileName, VideoMode mode, boolean isAudioActive)
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
        this.isAudioActive = isAudioActive;
        Logger.d(TAG, "ProfileName:"+ ProfileName+ "Duration:"+duration +"FileFormat:"+fileFormat+"Quality:"+quality);
        Logger.d(TAG, "ABR:"+audioBitRate +"AChannels:"+audioChannels+"Acodec:"+audioCodec +"AsampleRate"+audioSampleRate+"audio_active:" + isAudioActive);
        Logger.d(TAG,"VBitrate:"+videoBitRate+"VCodec:"+videoCodec+"VFrameRate:"+videoFrameRate+"VWidth:"+videoFrameWidth+"Vheight:"+videoFrameHeight);
    }

    public VideoMediaProfile(String t)
    {
        String[] ar = t.split(" ");
        this.audioBitRate =     Integer.parseInt(ar[0]);
        this.audioChannels =    Integer.parseInt(ar[1]);
        this.audioCodec =       Integer.parseInt(ar[2]);
        this.audioSampleRate =  Integer.parseInt(ar[3]);
        this.duration =         Integer.parseInt(ar[4]);
        this.fileFormat =       Integer.parseInt(ar[5]);
        this.quality =          Integer.parseInt(ar[6]);
        this.videoBitRate =     Integer.parseInt(ar[7]);
        this.videoCodec =       Integer.parseInt(ar[8]);
        this.videoFrameRate =   Integer.parseInt(ar[9]);
        this.videoFrameHeight = Integer.parseInt(ar[10]);
        this.videoFrameWidth =  Integer.parseInt(ar[11]);
        this.ProfileName = ar[12];
        this.Mode = VideoMode.valueOf(ar[13]);
        if (ar.length == 14)
            this.isAudioActive = true;
        else
            this.isAudioActive = Boolean.parseBoolean(ar[14]);

        Logger.d(TAG, "ProfileName:"+ ProfileName+ "Duration:"+duration +"FileFormat:"+fileFormat+"Quality:"+quality);
        Logger.d(TAG, "ABR:"+audioBitRate +"AChannels:"+audioChannels+"Acodec:"+audioCodec +"AsampleRate"+audioSampleRate+"audio_active:" + isAudioActive);
        Logger.d(TAG,"VBitrate:"+videoBitRate+"VCodec:"+videoCodec+"VFrameRate:"+videoFrameRate+"VWidth:"+videoFrameWidth+"Vheight:"+videoFrameHeight);
    }

    public String GetString()
    {
        StringBuilder b = new StringBuilder();
        b.append(audioBitRate +" ");
        b.append(audioChannels +" ");
        b.append(audioCodec +" ");
        b.append(audioSampleRate +" ");
        b.append(duration +" ");
        b.append(fileFormat +" ");
        b.append(quality +" ");
        b.append(videoBitRate +" ");
        b.append(videoCodec +" ");
        b.append(videoFrameRate +" ");
        b.append(videoFrameHeight +" ");
        b.append(videoFrameWidth +" ");
        b.append(ProfileName +" ");
        b.append(Mode.toString()+ " ");
        b.append(isAudioActive + " ");
        return b.toString();
    }

    public VideoMediaProfile clone()
    {
        return new VideoMediaProfile(audioBitRate,audioChannels, audioCodec, audioSampleRate, duration, fileFormat,quality,videoBitRate,videoCodec,videoFrameRate, videoFrameHeight,videoFrameWidth, ProfileName, Mode, isAudioActive);
    }


    final public static String MEDIAPROFILESPATH = StringUtils.GetInternalSDCARD()+StringUtils.freedcamFolder+"CustomMediaProfiles.txt";

    public static void loadCustomProfiles(HashMap<String, VideoMediaProfile> list) throws IOException
    {
        File mprof = new File(MEDIAPROFILESPATH);
        if(mprof.exists())
        {
            Logger.d(TAG, "CustomMediaProfile exists loading....");
            BufferedReader br = new BufferedReader(new FileReader(mprof));
            String line;

            while ((line = br.readLine()) != null)
            {
                if (!line.startsWith("#")) {
                    VideoMediaProfile m = new VideoMediaProfile(line);
                    list.put(m.ProfileName, m);
                }
            }
            br.close();
        }
        else
            Logger.d(TAG, "No CustomMediaProfiles found");

    }

    public static void saveCustomProfiles(HashMap<String, VideoMediaProfile> list)
    {
        File mprof = new File(MEDIAPROFILESPATH);
        try {
            if (!mprof.getParentFile().exists())
                mprof.getParentFile().mkdirs();
            mprof.createNewFile();
            Logger.d(TAG,"wrote MediaProfiles to txt");
        } catch (IOException e) {
            Logger.exception(e);
        }
        if(mprof.exists()) {
            try
            {
                BufferedWriter br = new BufferedWriter(new FileWriter(mprof));
                br.write("#audiobitrate audiochannels audioCodec audiosamplerate duration fileFormat quality videoBitrate videoCodec videoFrameRate videoFrameHeight videoFrameWidth ProfileName RecordMode isAudioActive \n");
                for (VideoMediaProfile profile : list.values())
                    br.write(profile.GetString() +"\n");
                br.close();
            } catch (IOException e) {
            }
        }
    }
}
