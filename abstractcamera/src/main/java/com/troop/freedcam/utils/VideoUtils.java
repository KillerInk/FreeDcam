package com.troop.freedcam.utils;

/**
 * Created by GeorgeKiarie on 1/24/2016.
 */
public class VideoUtils {

    public static int[] getVideoRes(String Q)
    {
        int x= 0;
        int y = 0;

        switch (Q)
        {
            case "DCI":
                x = 4096;
                y = 2160;
                break;

            case "UHD":
                x = 3840;
                y = 2160;
                break;

            case "FHD":
                x = 1920;
                y = 1080;
                break;
            case "HD":
                x = 1280;
                y = 720;
                break;
            case "qHD":
                x = 960;
                y = 540;
                break;
            case "WVGA":
                x = 800;
                y = 480;
                break;
            case "VGA":
                x = 640;
                y = 480;
                break;

        }
        return new int[]{x,y};
    }


    public static int getVideoBitrate()
    {
        int Qv = 0;
        switch ("Low") {
            case "Extreme":
                Qv = 150000000;
                break;
            case "High":
                Qv = 80000000;
                break;
            case "Medium":
                Qv = 40000000;
                break;
            case "Low":
                Qv = 20000000;
                break;

        }
        return Qv;
    }

    public static int getAudioBitrate()
    {
        int Qv = 0;
        switch ("Extreme") {
            case "Extreme":
                Qv = 384000;
                break;
            case "High":
                Qv = 192000;
                break;
            case "Medium":
                Qv = 156000;
                break;
            case "Low":
                Qv = 96000;
                break;
        }

        return Qv;
    }

    public static int getAudioSample()
    {
        int Qv = 0;
        switch ("Medium") {
            case "Extreme":
                Qv = 96000;
                break;
            case "High":
                Qv = 48000;
                break;
            case "Medium":
                Qv = 44000;
                break;
            case "Low":
                Qv = 22000;
                break;
        }

        return Qv;
    }

    public static int getAudioChannels(String Q)
    {
        int Qv = 0;
        switch (Q) {
            case "Mono":
                Qv = 1;
                break;
            case "Stereo":
                Qv = 2;
                break;

        }

        return Qv;
    }

}
