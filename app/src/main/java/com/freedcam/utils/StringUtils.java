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

    public static String GetFreeDcamConfigFolder = GetInternalSDCARD()+ freedcamFolder +"config/";
    public static String getFilePath(boolean externalSd, String fileEnding)
    {
        StringBuilder builder = new StringBuilder();
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
        StringBuilder builder = new StringBuilder();
        if (external)
            builder.append(GetExternalSDCARD());
        else
            builder.append(GetInternalSDCARD());
        builder.append(DCIMFolder);
        return builder.toString();
    }

    public static String getFilePathHDR(boolean externalSd, String fileEnding, int hdrcount)
    {
        StringBuilder builder = new StringBuilder();
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
        StringBuilder builder = new StringBuilder();
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
        public static final String RAW = "raw";
        public static final String DNG = "dng";
        public static final String JPG = "jpg";
        public static final String JPS = "jps";
        public static final String BAYER = "bayer";
        public static final String MP4 = "mp4";

        public String GetWithDot()
        {
            return "."+ BAYER;
        }
    }

}
