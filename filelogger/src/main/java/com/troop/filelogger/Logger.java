package com.troop.filelogger;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * Created by Ingo on 06.03.2016.
 */
public class Logger
{
    private static FileLogger fileLogger;
    private static String TAG = Logger.class.getSimpleName();

    public static void d(String TAG,String msg)
    {
        Logger.d(TAG,msg);
        if (fileLogger != null)
            fileLogger.WriteLogDebug(TAG,msg);
    }

    public static void e(String TAG,String msg)
    {
        Logger.e(TAG, msg);
        if (fileLogger != null)
            fileLogger.WriteLogErrorDebug(TAG, msg);
    }

    public static void w(String TAG,String msg)
    {
        Logger.w(TAG, msg);
        if (fileLogger != null)
            fileLogger.WriteLogDebug(TAG, msg);
    }
    public static void v(String TAG,String msg)
    {
        Logger.v(TAG, msg);
        if (fileLogger != null)
            fileLogger.WriteLogDebug(TAG, msg);
    }

    public static void StartLogging()
    {
        fileLogger = new FileLogger();
    }

    public static void StopLogging()
    {
        fileLogger.Destroy();
    }

    private static class FileLogger
    {
        private FileWriter outputStream;
        private BufferedWriter writer;
        private HandlerThread backgroundThread;
        private Handler backgroundHandler;

        public FileLogger()
        {
            backgroundThread = new HandlerThread(TAG);
            backgroundThread.start();
            backgroundHandler = new Handler(backgroundThread.getLooper());
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/FreeDcam/" + "DEBUG/"+ Build.MODEL + "_" + DateFormat.format("yyyy-MM-dd_hh.mm.ss", new Date().getTime()) + ".txt");
            try {
                outputStream = new FileWriter(file);
                writer = new BufferedWriter(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void Destroy()
        {

            if (outputStream != null)
            {
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            backgroundThread.quit();
        }

        public void WriteLogDebug(final String TAG,final String msg)
        {
            backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        writer.write(TAG + ": DEBUG: " + msg);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        public void WriteLogErrorDebug(final String TAG,final String msg)
        {
            backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        writer.write(TAG + ": ERROR: " + msg);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
