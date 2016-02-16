package com.troop.freedcam.utils;

import android.hardware.Camera;
import android.os.Environment;

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


    public static String ShutterValues = "Auto,1/100000,1/90000,1/75000,1/50000,1/45000,1/30000,1/20000,1/12000,1/10000"+
            ",1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/80,1/70,1/60"+
            ",1/50,1/40,1/35,1/30,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,0.8"+
            ",1.0,1.2,1.4,1.5,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0"+
            ",15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28.0,29.0"+
            ",30.0,31.0,32.0,33.0,35.0,36.0,37.0,38.0,39.0,40.0,41.0,42.0,43.0,44,45.0,46.0"+
            ",47.0,48.0,49.0,50.0,51.0,52.0,53.0,54.0,55.0,56.0,57.0,58.0,59.0,60.0,120.0,240.0";

    public static String[] ShutterValuesArray()
    {
        return ShutterValues.split(",");
    }

    public static String[] getSupportedShutterValues(int minMillisec, int maxMiliisec)
    {
        final String[] allvalues = ShutterValuesArray();
        boolean foundmin = false, foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("Auto");
        for (int i = 1; i< allvalues.length; i++ )
        {
            String s = allvalues[i];

            float a;
            if (s.contains("/")) {
                String split[] = s.split("/");
                a =(Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f);
            }
            else
                a = (Float.parseFloat(s)*1000000f);

            if (a>= minMillisec && a <= maxMiliisec)
                tmp.add(s);
            if (a >= minMillisec && !foundmin)
            {
                foundmin = true;
            }
            if (a > maxMiliisec && !foundmax)
            {
                foundmax = true;
            }
            if (foundmax && foundmin)
                break;

        }
        return tmp.toArray(new String[tmp.size()]);
    }

    public static long getMilliSecondStringFromShutterString(String shuttervalue)
    {
        float a;
        if (shuttervalue.contains("/")) {
            String split[] = shuttervalue.split("/");
            a =(Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f);
        }
        else
            a = (Float.parseFloat(shuttervalue)*1000000f);
        return  (long)a;
    }

   /* public static String getMicro(double value)
    {
        double Oneth = value *1000;

        double QTR = Oneth / 100 * 25;

        String ExpoString = "1/"+String.valueOf(QTR);

        String[] Shutter = ShutterValues.split(",");

        String newSupport;

        if(ShutterValues.contains(ExpoString))
        {
            newSupport = ShutterValues.indexOf()
        }

        return
    }*/

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

    public static String VoLP = "Vol+";
    public static String VoLM = "Vol-";
    public static String Hook = "Hook";


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
        if(DeviceUtils.isSonyM5_MTK())
            return "/storage/sdcard1";
        else
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
