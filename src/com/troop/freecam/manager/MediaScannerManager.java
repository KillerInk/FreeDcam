package com.troop.freecam.manager;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

/**
 * Created by troop on 25.08.13.
 */
public class MediaScannerManager implements MediaScannerConnectionClient
{

    MediaScannerConnection scanner;
    Context context;
    String imgpath;

    public MediaScannerManager(Context context)
    {
        this.context = context;
    }

    public void startScan(String imgpath)
    {
        this.imgpath = imgpath;
        if(scanner!=null) scanner.disconnect();
        scanner = new MediaScannerConnection(context.getApplicationContext(),this);
        scanner.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        try{
            //set sec param to null cause jps is a not recognized mimetype or file will not added
            scanner.scanFile(imgpath, null);
        }
        catch (java.lang.IllegalStateException e)
        {
            Log.e("MediaSCanner ERROR", e.getMessage());
            scanner.disconnect();
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        scanner.disconnect();
    }
}
