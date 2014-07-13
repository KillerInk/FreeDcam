package com.troop.freecam.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.troop.freecam.R;
import com.troop.freecam.camera.PictureCam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.ByteBuffer;

/**
 * Created by George on 6/10/14.
 */
public class EncodeTiff extends Activity {
    PictureCam pictureCam;
    private static Context context;
    private byte[] rb;

    public static void setContext(Context mcontext)
    {
        if (context == null)
            context = mcontext;
    }

    private final Handler handler = new Handler() {

    @Override
    public void handleMessage(Message message)
    {

    }

    };



    public void Encode()
    {
        //new Thread(runnableLoad).start();
        new Thread(new Runnable() {
            @Override
            public void run() {


                Date date = new Date();
                Long l = System.currentTimeMillis();
                File file = getOutputMediaFile(3);
                if (file == null) {
                    Log.d("Error at Encode Tiff", "Check Storage Permission");
                    return;
                }
                Message message = new Message();
                message.what = 2;
                Bundle bundle = new Bundle();
                message.setData(bundle);
                handler.sendMessage(message);

                bundle.putString("path", file.getAbsolutePath());
                message.setData(bundle);

                InputStream inputStream =  context.getResources().openRawResource(R.raw.header);


                try {
                    Log.d("Size",String.valueOf(inputStream.read()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte abyte0[] = new byte[1680];
                int i;
                FileOutputStream fileOutputStream;
                PrintStream printStream;
                Object aobj[];
                try {
                    inputStream.read(abyte0);
                } catch (IOException io) {
                    io.printStackTrace();
                }
                abyte0[567] = (byte) (0xff & 100 >>> 8);
                abyte0[566] = (byte) (0xff & 100);
                abyte0[1663] = (byte) (0xff & 1000 >>> 24);
                abyte0[1662] = (byte) (0xff & 1000 >>> 16);
                abyte0[1661] = (byte) (0xff & 1000 >>> 8);
                abyte0[1660] = (byte) (0xff & 1000);

                abyte0[355] = (byte) (0xff & 4212 >>> 8);
                abyte0[354] = (byte) (0xff & 4212);
                abyte0[367] = (byte) (0xff & 3120 >>> 8);
                abyte0[366] = (byte) (0xff & 3120);
                abyte0[535] = (byte) (0xff & 4212 >>> 8);
                abyte0[534] = (byte) (0xff & 4212);
                abyte0[537] = (byte) (0xff & 3120 >>> 8);
                abyte0[536] = (byte) (0xff & 3120);
                abyte0[439] = (byte) (0xff & 3120 >>> 8);
                abyte0[438] = (byte) (0xff & 3120);
                i = 2 * (4212 * 3120);
                abyte0[453] = (byte) (0xff & i >>> 24);
                abyte0[452] = (byte) (0xff & i >>> 16);
                abyte0[451] = (byte) (0xff & i >>> 8);
                abyte0[450] = (byte) (i & 0xff);
                abyte0[474] = 0;
                abyte0[475] = 1;
                abyte0[476] = 1;
                abyte0[477] = 2;
                String GeneralCamera = (new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")).format(date);
                //String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
                System.arraycopy(GeneralCamera.getBytes(), 0, abyte0, 624, 19);
                try {
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(abyte0);
                    if (rb.length >= 2) {
                        fileOutputStream.write(rb);
                    } else {
                        Thread.sleep(10);
                    }
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                printStream = System.out;
                aobj = new Object[1];
                aobj[0] = Long.valueOf(System.currentTimeMillis() - 1);
                printStream.println(String.format("File>SD: %d ms", aobj));

            }
        }).start();

    }

    private Runnable runnableLoad = new Runnable() {
        @Override
        public void run() {

        }
    };

    public static File getOutputMediaFile(int i)
    {
        Log.d("Error at Encode Tiff", "Gokusan");
       File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        String s1 = (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();

        if (i == 3)
            return new File((new StringBuilder(String.valueOf(s1))).append(".dng").toString());
        else
            return null;
    }

    public void byt(byte[] bytz)
    {
        Log.d("bytez",String.valueOf(bytz.length));
        rb = bytz;
    }
}
