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
    private static Logger.FileLogger fileLogger;
    private static final String TAG = Logger.class.getSimpleName();

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

    public static void DUMPLOGTOFILE()
    {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/FreeDcam/" + "CRASH/" + Build.MODEL + "_" + DateFormat.format("yyyy-MM-dd_hh.mm.ss", new Date().getTime()) + ".txt");
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
            try {
                Runtime.getRuntime().exec(
                        "logcat  -d -f " + f.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void LogUncaughtEX(Throwable throwable)
    {
        boolean logwasnull =false;
        if (logwasnull = fileLogger == null)
        {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/FreeDcam/" + "CRASH/" + Build.MODEL + "_" + DateFormat.format("yyyy-MM-dd_hh.mm.ss", new Date().getTime()) + ".txt");
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            Logger.fileLogger = new Logger.FileLogger(f);
        }
        Logger.fileLogger.WriteEx(throwable);
        if (logwasnull)
            Logger.fileLogger.Destroy();
    }

    private static class FileLogger
    {
        private FileWriter outputStream;
        private BufferedWriter writer;
        private final File file;

        public FileLogger()
        {
            this.file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/FreeDcam/" + "DEBUG/"+ Build.MODEL + "_" + DateFormat.format("yyyy_MM_dd_hh_mm_ss", new Date().getTime()) + ".txt");
            try {
                this.outputStream = new FileWriter(this.file);
                this.writer = new BufferedWriter(this.outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public FileLogger(File file)
        {
            this.file = file;
            try {
                this.outputStream = new FileWriter(file);
                this.writer = new BufferedWriter(this.outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void Destroy()
        {

            if (this.outputStream != null)
            {
                try {
                    this.writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    this.outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    this.writer.close();
                    this.writer = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    this.outputStream.close();
                    this.outputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void WriteLogDebug(String TAG, String msg)
        {
            try {
                String b = DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime()) +
                        ":(D) " +
                        TAG + ":" +
                        msg;
                this.writer.write(b);
                this.writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void WriteLogErrorDebug(String TAG, String msg)
        {
            try {
                String b = DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime()) +
                        ":(E) " +
                        TAG +
                        msg;
                this.writer.write(b);
                this.writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void WriteEx(Throwable ex)
        {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            try {
                this.writer.write(errors.toString());
            } catch (IOException e) {
                if (this.file != null)
                {
                    try {
                        FileWriter fr = new FileWriter(this.file);
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
