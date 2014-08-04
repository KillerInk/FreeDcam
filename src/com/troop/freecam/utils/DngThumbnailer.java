package com.troop.freecam.utils;

/**
 * Created by George on 6/13/14.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import com.troop.freecam.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DngThumbnailer {
    protected Camera mCamera;


    /* @Override
    public void onPreviewFrame(byte[] data, Camera camera){

        Camera.Parameters parameters = camera.getParameters();
       final int width = parameters.getPreviewSize().width;
       final int height = parameters.getPreviewSize().height;
        YuvImage yuvImage = new YuvImage(data,parameters.getPreviewFormat(),width,height,null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0,0,width,height),50,out);

        //byte[] bytes = out.toByteArray();
        //final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
       
        final byte[] data2 = data;
        new Thread(new Runnable() {

            private final byte dat[] = new byte[data2.length];
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                int ai[] = new int[691200];
                decodeYUV(ai,dat,width,height);


            }
        });





    } */

    public static void decodeYUV(int ai[], byte abyte0[], int i, int j)
            throws NullPointerException, IllegalArgumentException
    {
        long l;
        int k;
        int i1;
        int j1;
        int k1;
        l = System.currentTimeMillis();
        k = i * j;
        if (ai == null)
            throw new NullPointerException("buffer out is null");
        if (ai.length < k)
            throw new IllegalArgumentException((new StringBuilder("buffer out size ")).append(ai.length).append(" < minimum ").append(k).toString());
        if (abyte0 == null)
            throw new NullPointerException("buffer 'fg' is null");
        if (abyte0.length < k)
            throw new IllegalArgumentException((new StringBuilder("buffer fg size ")).append(abyte0.length).append(" < minimum ").append((k * 3) / 2).toString());
        i1 = 0;
        j1 = 0;
        k1 = 0;

        if (k1 >= j)
        {
            PrintStream printstream = System.out;
            Object aobj[] = new Object[1];
            aobj[0] = Long.valueOf(System.currentTimeMillis() - l);
            printstream.println(String.format("YUV422SP->YV12 time: %d ms", aobj));
            return;
        }
        int l1 = k1 * i;
        int i2 = k1 >> 1;
        int j2 = 0;
        int k2 = l1;
        do
        {
            label0:
            {
                if (j2 < i)
                    break label0;
                k1++;
            }
            if (true)
                continue;
            int l2 = abyte0[k2];
            if (l2 < 0)
                l2 += 255;
            if ((j2 & 1) != 1)
            {
                int i4 = k + i2 * i + 2 * (j2 >> 1);
                byte byte0 = abyte0[i4];
                if (byte0 < 0)
                    j1 = byte0 + 127;
                else
                    j1 = byte0 - 128;
                byte byte1 = abyte0[i4 + 1];
                if (byte1 < 0)
                    i1 = byte1 + 127;
                else
                    i1 = byte1 - 128;
            }
            int i3 = l2 + i1 + (i1 >> 2) + (i1 >> 3) + (i1 >> 5);
            if (i3 < 0)
                i3 = 0;
            else
            if (i3 > 255)
                i3 = 255;
            int j3 = (((l2 - (j1 >> 2)) + (j1 >> 4) + (j1 >> 5)) - (i1 >> 1)) + (i1 >> 3) + (i1 >> 4) + (i1 >> 5);
            if (j3 < 0)
                j3 = 0;
            else
            if (j3 > 255)
                j3 = 255;
            int k3 = l2 + j1 + (j1 >> 1) + (j1 >> 2) + (j1 >> 6);
            if (k3 < 0)
                k3 = 0;
            else
            if (k3 > 255)
                k3 = 255;
            int l3 = k2 + 1;
            ai[k2] = i3 + (0xff000000 + (k3 << 16) + (j3 << 8));
            j2++;
            k2 = l3;
        } while (true);
       // if (true)

    }

}
