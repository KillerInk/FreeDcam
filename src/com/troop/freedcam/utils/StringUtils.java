package com.troop.freedcam.utils;

import android.hardware.Camera;

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
}
