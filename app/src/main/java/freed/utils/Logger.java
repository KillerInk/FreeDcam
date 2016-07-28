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

package freed.utils;

import android.os.Build;
import android.os.Environment;
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
        throwable.printStackTrace();
        boolean logwasnull =false;
        if (logwasnull = fileLogger == null)
        {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/FreeDcam/" + "CRASH/" + Build.MODEL + "_" + DateFormat.format("yyyy-MM-dd_hh.mm.ss", new Date().getTime()) + ".txt");
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            fileLogger = new FileLogger(f);
        }
        fileLogger.WriteEx(throwable);
        if (logwasnull)
            fileLogger.Destroy();
    }

    private static class FileLogger
    {
        private FileWriter outputStream;
        private BufferedWriter writer;
        private final File file;

        public FileLogger()
        {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/FreeDcam/" + "DEBUG/"+ Build.MODEL + "_" + DateFormat.format("yyyy_MM_dd_hh_mm_ss", new Date().getTime()) + ".txt");
            try {
                outputStream = new FileWriter(file);
                writer = new BufferedWriter(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public FileLogger(File file)
        {
            this.file = file;
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
                    e.printStackTrace();
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

        }

        public void WriteLogDebug(String TAG, String msg)
        {
            if (writer == null)
                return;
            try {
                String b = DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime()) +
                        ":(D) " +
                        TAG + ":" +
                        msg;
                writer.write(b);
                writer.newLine();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }

        public void WriteLogErrorDebug(String TAG, String msg)
        {
            if (writer == null)
                return;
            try {
                String b = DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime()) +
                        ":(E) " +
                        TAG +
                        msg;
                writer.write(b);
                writer.newLine();
            } catch (IOException e) {
                //e.printStackTrace();
            }
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
                        //e1.printStackTrace();
                    }
                }
                //e.printStackTrace();
            }
        }
    }
}
