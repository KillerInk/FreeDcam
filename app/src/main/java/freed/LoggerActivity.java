package freed;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.troop.freedcam.R;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.ui.handler.MediaScannerManager;

/**
 * Created by troop on 01.12.2016.
 */

public class LoggerActivity extends FragmentActivity
{
    private Thread.UncaughtExceptionHandler defaultEXhandler;

    private final String EX_MESSAGE = "exmsg";
    private final String TAG = LoggerActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent in = getIntent();
        final String msg = in.getStringExtra(EX_MESSAGE);
        if (msg == null || msg.equals("")) {

            //Get default handler for uncaught exceptions. to let fc app as it should
            defaultEXhandler = Thread.getDefaultUncaughtExceptionHandler();
            //set up own ex handler to have a change to catch the fc bevor app dies
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, final Throwable e) {
                    //yeahaw app crash print ex to logger
                    e.printStackTrace();
                    if (thread != Looper.getMainLooper().getThread())
                        startCrashWindow(Log.getStackTraceString(e));
                    else
                        startCrashWindow(Log.getStackTraceString(e));

                    //set back default exhandler and let app die
                    //Thread.setDefaultUncaughtExceptionHandler(defaultEXhandler);
                    //defaultEXhandler.uncaughtException(thread, e);
                }
            });

            startFreedcam();
            finish();
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.logger_activity);
            TextView msg_txtview = (TextView)findViewById(R.id.textview_crashlog);
            msg_txtview.setMovementMethod(new ScrollingMovementMethod());
            msg_txtview.setText(msg);
            Log.e(TAG, msg);
            Button button_restartfreedcam = (Button)findViewById(R.id.button_restart);
            button_restartfreedcam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startFreedcam();
                    finish();
                }
            });

            final Button dumolog = (Button)findViewById(R.id.button_dumpLog);
            dumolog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DUMPLOGTOFILE();
                }
            });
        }
    }

    private void startCrashWindow(String msg)
    {
        Intent intent = new Intent(getApplicationContext(), LoggerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EX_MESSAGE, msg);
        getApplicationContext().startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void startFreedcam()
    {
        Intent intent = new Intent(this, ActivityFreeDcamMain.class);
        startActivity(intent);
    }

    private void DUMPLOGTOFILE()
    {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/FreeDcam/" + "CRASH/" + Build.MODEL + "_" + DateFormat.format("yyyy-MM-dd_hh.mm.ss", new Date().getTime()) + ".txt");
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        try {
            String[] command = { "logcat", "-t", "500", "-f", f.getAbsolutePath() };
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(),"File saved to :" + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
        MediaScannerManager.ScanMedia(getApplicationContext(),f);
    }
}
