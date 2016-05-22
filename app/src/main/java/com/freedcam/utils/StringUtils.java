package com.freedcam.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ingo on 04.10.2014.
 */
public class StringUtils
{

    final public static String ON = "ON";
    final public static String OFF = "OFF";

    public static String VoLP = "Vol+";
    public static String VoLM = "Vol-";
    public static String Hook = "Hook";

    private StringUtils(){}

    public static String GetExternalSDCARD() throws NullPointerException
    {
        return System.getenv("SECONDARY_STORAGE");
    }

    public static String GetInternalSDCARD()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    public static String TrimmFloatString4Places(String toTrim)
    {
        return String.format("%01.4f", Float.parseFloat(toTrim));
    }

    public static String freedcamFolder = "/DCIM/FreeDcam/";
    public static String DCIMFolder = "/DCIM/";

    public static String GetFreeDcamConfigFolder = GetInternalSDCARD()+freedcamFolder+"config/";
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
        builder.append(getStringDatePAttern().format(new Date()))
        .append(fileEnding);
        return builder.toString();
    }

    public static String GetDCIMFolder(boolean external)
    {
        final StringBuilder builder = new StringBuilder();
        if (external)
            builder.append(GetExternalSDCARD());
        else
            builder.append(GetInternalSDCARD());
        builder.append(DCIMFolder);
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
        builder.append(getStringDatePAttern().format(new Date()))
        .append("_HDR" + hdrcount)
        .append(fileEnding);
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
        builder.append(getStringDatePAttern().format(date));
        builder.append("_BURST" + hdrcount);
        builder.append(fileEnding);
        return builder.toString();
    }

    public static SimpleDateFormat getStringDatePAttern()
    {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    }


    public class FileEnding
    {
        final public static String RAW = "raw";
        final public static String DNG = "dng";
        final public static String JPG = "jpg";
        final public static String JPS = "jps";
        final public static String BAYER = "bayer";
        final public static String MP4 = "mp4";

        public String GetWithDot()
        {
            return "."+ FileEnding.BAYER;
        }
    }

}
