package freed.utils;

import android.text.format.DateFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

import freed.FreedApplication;

/**
 * Created by troop on 08.03.2017.
 */

public class Log
{
    private static FileLog fileLog;
    private static boolean isinit = false;

    public Log()
    {
        if (isinit) {
            android.util.Log.d("Log", "is already init");
            return;
        }
        if(fileLog == null) {
            android.util.Log.d("Log", "is created");
            fileLog = new FileLog();
            isinit = true;
        }
    }

    public static void destroy()
    {
        if (fileLog != null)
            fileLog.destroy();
        fileLog = null;
    }

    public static void flush()
    {
        if (fileLog != null)
            fileLog.flush();
    }

    public static boolean isLogToFileEnable()
    {
        return fileLog != null;
    }

    public static void v(String TAG, String msg)
    {
        if (fileLog != null && msg != null)
            fileLog.writeString("V:" + DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime())+ ":" + TAG + ":" + msg);
        android.util.Log.v(TAG,msg);
    }

    public static void d(String TAG, String msg)
    {
        if (fileLog != null && msg != null)
            fileLog.writeString("D:" + DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime()) + ":" + TAG + ":" + msg);
        android.util.Log.d(TAG,msg);
    }

    public static void i(String TAG, String msg)
    {
        if (fileLog != null && msg != null)
            fileLog.writeString("I:" + DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime())+ ":" + TAG + ":" + msg);
        android.util.Log.d(TAG,msg);
    }

    public static void w(String TAG, String msg)
    {
        if (fileLog != null && msg != null)
            fileLog.writeString("W:" + DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime())+ ":" + TAG + ":" + msg);
        android.util.Log.d(TAG,msg);
    }

    public static void e(String TAG, String msg)
    {
        if (fileLog != null && msg != null)
            fileLog.writeString("E:" +DateFormat.format("hh.mm.ss", Calendar.getInstance().getTime())+ ":" + TAG + ":" + msg);
        android.util.Log.d(TAG,msg);
    }

    public static void WriteEx(Throwable ex)
    {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        if (fileLog != null)
            fileLog.writeString(errors.toString());
        ex.printStackTrace();
    }

    private class FileLog
    {
        private BufferedWriter outwriter;
        private FileWriter fileWriter;
        private Thread.UncaughtExceptionHandler defaultUncaughtExHandler;
        public FileLog()
        {
            defaultUncaughtExHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
            try {
                File outfile = new File(FreedApplication.getContext().getExternalFilesDir(null)+ "/log.txt");
                outfile.createNewFile();
                if (!outfile.getParentFile().exists()) {
                    outfile.getParentFile().mkdirs();
                }

                if (!outfile.exists())
                    outfile.createNewFile();
                fileWriter = new FileWriter(outfile,true);
                outwriter = new BufferedWriter(fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        public void writeString(String msg)
        {
            if (outwriter != null) {
                try {
                    outwriter.write(msg);
                    outwriter.newLine();
                    outwriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        public void flush()
        {
            try {
                outwriter.flush();
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        public void destroy()
        {
            try {
                outwriter.flush();
                fileWriter.flush();
                outwriter.close();
                fileWriter.close();
                outwriter = null;
                fileWriter =null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
            Log.WriteEx(e);
            defaultUncaughtExHandler.uncaughtException(t,e);
            }
        };
    }

}
