package com.troop.freedcam.manager;

import android.os.Build;
import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.Time;

import com.troop.freedcam.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * Created by troop on 09.09.2015.
 */
public class FileLogger
{
    private static boolean LOGTOFILE = false;
    private static boolean isrunning = false;

    public static void StartLogging()
    {
        if (isrunning)
            return;
        LOGTOFILE = true;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                processlog();
            }
        }).start();
    }

    public static void StopLogging()
    {
        LOGTOFILE = false;
    }


    private static void processlog()
    {
        BufferedReader bufferedReader = null;
        FileOutputStream fileOut = null;
        OutputStreamWriter outputstream = null;
        File file = new File(StringUtils.GetInternalSDCARD()+ StringUtils.freedcamFolder+ Build.MODEL + "_" + DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date().getTime()) + ".txt");
        if (!file.getParentFile().exists())
            file.mkdirs();

        try {
            fileOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outputstream = new OutputStreamWriter(fileOut);

        try {
            while (LOGTOFILE)
            {

                Process process = Runtime.getRuntime().exec("logcat -d");
                bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                // Write the string to the file
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    outputstream.write(DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date().getTime())+ ":" +line + "\n");
                }
                Runtime.getRuntime().exec("logcat -c");
            }
        }
        catch (IOException e)
        {
            LOGTOFILE = false;
            try {
                if (outputstream != null)
                    outputstream.flush();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        finally {
            isrunning = false;
            try {
                if (outputstream != null) {
                    outputstream.flush();
                    outputstream.close();
                }
                if (bufferedReader != null)
                    bufferedReader.close();
                if (fileOut != null)
                    fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
