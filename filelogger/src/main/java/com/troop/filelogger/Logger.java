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
        Log.d(TAG,msg);
        if (fileLogger != null)
            fileLogger.WriteLogDebug(TAG,msg);
    }

    public static void e(String TAG,String msg)
    {
        Log.e(TAG, msg);
        if (fileLogger != null)
            fileLogger.WriteLogErrorDebug(TAG, msg);
    }

    public static void w(String TAG,String msg)
    {
        Log.w(TAG, msg);
        if (fileLogger != null)
            fileLogger.WriteLogDebug(TAG, msg);
    }
    public static void v(String TAG,String msg)
    {
        Log.v(TAG, msg);
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
        fileLogger = null;
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
                Logger.e(TAG, e.getMessage());
            }

        }

        public void Destroy()
        {

            if (outputStream != null)
            {
                try {
                    writer.flush();
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
                }
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
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
                        StringBuilder b = new StringBuilder();
                        b.append(DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime()));
                        b.append(":(D) ");
                        b.append(TAG);
                        b.append(msg);
                        writer.write(b.toString());
                        writer.newLine();
                    } catch (IOException e) {
                        Logger.e(TAG, e.getMessage());
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
                        StringBuilder b = new StringBuilder();
                        b.append(DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime()));
                        b.append(":(E) ");
                        b.append(TAG);
                        b.append(msg);
                        writer.write(b.toString());
                        writer.newLine();
                    } catch (IOException e) {
                        Logger.e(TAG, e.getMessage());
                    }
                }
            });

        }
    }
}
