package com.troop.freedcam.manager;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by troop on 25.08.13.
 */
public class MediaScannerManager
{

   /* MediaScannerConnection scanner;
    Context context;
    String imgpath;

    boolean connected = false;*/

    /*public MediaScannerManager(Context context)
    {
        this.context = context;
        scanner = new MediaScannerConnection(context.getApplicationContext(),this);
        scanner.connect();
    }

    public void startScan(String imgpath)
    {
        this.imgpath = imgpath;
        scanner.scanFile(imgpath, null);

    }

    @Override
    public void onMediaScannerConnected() {
        connected = true;
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        //scanner.disconnect();

    }

    public void close()
    {
        if (scanner != null && scanner.isConnected())
            scanner.disconnect();
    }*/

    public static void ScanMedia(Context context, File file)
    {
        Intent intent =
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }
}
