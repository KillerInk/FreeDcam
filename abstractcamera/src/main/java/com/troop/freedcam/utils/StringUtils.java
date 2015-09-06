package com.troop.freedcam.utils;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ingo on 04.10.2014.
 */
public class StringUtils
{
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String BayerMipiBGGR()
    {
        return "bayer-mipi-10bggr";
    }

    public static String BayerMipiGRBG()
    {
        return "bayer-mipi-10grbg";
    }

    public static String BayerQcomGRBG()
    {
        return "bayer-qcom-10grbg";
    }

    final public static String ON = "ON";
    final public static String OFF = "OFF";


    public static String[] getStringArrayFromCameraSizes(List<Camera.Size> sizes)
    {
        List<String> stringList = new ArrayList<String>();

        for (int i = 0; i < sizes.size(); i++)
            stringList.add(sizes.get(i).width + "x" + sizes.get(i).height);

        return stringList.toArray(new String[sizes.size()]);
    }

    public static String TAG = "freedcam.";

    public static String GetPlatform()
    {
        Process p = null;
        String board_platform = "";
        try {
            p = new ProcessBuilder("/system/bin/getprop", "ro.board.platform").redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null){
                board_platform = line;
            }
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return board_platform;
    }

    public static String GetExternalSDCARD()
    {
        return System.getenv("SECONDARY_STORAGE");
    }

    public static String GetInternalSDCARD()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String TrimmFloatString(String toTrim)
    {
        return String.format("%01.4f", Float.parseFloat(toTrim));
    }

    public static String freedcamFolder = "/DCIM/FreeDcam/";
    public static String DCIMFolder = "/DCIM/";

    public static String getFilePath(boolean externalSd, String fileEnding)
    {
        final StringBuilder builder = new StringBuilder();
        if (externalSd)
            builder.append(GetExternalSDCARD());
        else
            builder.append(GetInternalSDCARD());
        builder.append(freedcamFolder);

        if (fileEnding.equals(".jpg") || fileEnding.equals(".dng") || fileEnding.equals(".jps"))
            builder.append(File.separator).append("IMG_");
        if (fileEnding.equals(".mp4"))
            builder.append(File.separator).append("MOV_");
        Date date = new Date();
        builder.append((new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss")).format(date));
        builder.append(fileEnding);
        return builder.toString();
    }

    public static String getFilePathHDR(boolean externalSd, String fileEnding, int hdrcount)
    {
        final StringBuilder builder = new StringBuilder();
        if (externalSd)
            builder.append(GetExternalSDCARD());
        else
            builder.append(GetInternalSDCARD());
        builder.append(freedcamFolder);
        if (fileEnding.equals(".jpg") || fileEnding.equals(".dng") || fileEnding.equals(".jps"))
            builder.append(File.separator).append("IMG_");
        if (fileEnding.equals(".mp4"))
            builder.append(File.separator).append("MOV_");
        Date date = new Date();
        builder.append((new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss")).format(date));
        builder.append("_HDR" + hdrcount);
        builder.append(fileEnding);
        return builder.toString();
    }

    public static String getFilePathBurst(boolean externalSd, String fileEnding, int hdrcount)
    {
        final StringBuilder builder = new StringBuilder();
        if (externalSd)
            builder.append(GetExternalSDCARD());
        else
            builder.append(GetInternalSDCARD());
        builder.append(freedcamFolder);
        if (fileEnding.equals(".jpg") || fileEnding.equals(".dng") || fileEnding.equals(".jps"))
            builder.append(File.separator).append("IMG_");
        if (fileEnding.equals(".mp4"))
            builder.append(File.separator).append("MOV_");
        Date date = new Date();
        builder.append((new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss")).format(date));
        builder.append("_BURST" + hdrcount);
        builder.append(fileEnding);
        return builder.toString();
    }
}
