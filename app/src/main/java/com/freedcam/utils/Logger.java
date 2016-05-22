package com.freedcam.utils;

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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
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
        try {
            Log.d(TAG,msg);
            if (fileLogger != null)
                fileLogger.WriteLogDebug(TAG,msg);
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void e(String TAG,String msg)
    {
        try {
            Log.e(TAG, msg);
            if (fileLogger != null)
                fileLogger.WriteLogErrorDebug(TAG, msg);
        }
        catch (NullPointerException ex)
        {ex.printStackTrace();}

    }

    public static void w(String TAG,String msg)
    {
        try {
            Log.w(TAG, msg);
            if (fileLogger != null)
                fileLogger.WriteLogDebug(TAG, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void v(String TAG,String msg)
    {
        try {
            Log.v(TAG, msg);
            if (fileLogger != null)
                fileLogger.WriteLogDebug(TAG, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exception(Throwable ex)
    {
        ex.printStackTrace();
        if (fileLogger != null)
            fileLogger.WriteEx(ex);
    }

    public static void StartLogging()
    {
        fileLogger = new FileLogger();
    }

    public static void StopLogging()
    {
        fileLogger.Destroy();
        fileLogger = null;
    }

    private static class FileLogger
    {
        private FileWriter outputStream;
        private BufferedWriter writer;
        private HandlerThread backgroundThread;
        private Handler backgroundHandler;
        private File file;

        public FileLogger()
        {
            backgroundThread = new HandlerThread(TAG);
            backgroundThread.start();
            backgroundHandler = new Handler(backgroundThread.getLooper());
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/FreeDcam/" + "DEBUG/"+ Build.MODEL + "_" + DateFormat.format("yyyy-MM-dd_hh.mm.ss", new Date().getTime()) + ".txt");
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
                    outputStream.flush();
                } catch (IOException e) {
                    Logger.exception(e);
                }
                try {
                    writer.close();
                    writer = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                    outputStream = null;
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
                        String b = String.valueOf(DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime())) +
                                ":(D) " +
                                TAG + ":" +
                                msg;
                        writer.write(b);
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
                        String b = String.valueOf(DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime())) +
                                ":(E) " +
                                TAG +
                                msg;
                        writer.write(b);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        public void WriteEx(Throwable ex)
        {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            try {
                writer.write(errors.toString());
            } catch (IOException e) {
                if (file != null)
                {
                    try {
                        FileWriter fr = new FileWriter(file);
                        fr.write(errors.toString());
                        fr.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
    }
}
