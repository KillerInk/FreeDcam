package com.troop.freedcam.utils;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
// TODO Auto-generated catch block
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
}
