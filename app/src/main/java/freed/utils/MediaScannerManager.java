package freed.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 25.08.13.
 */
public class MediaScannerManager
{
    public final static String TAG = MediaScannerManager.class.getSimpleName();
    public static void ScanMedia(Context context, File file)
    {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    public static void ScanMedia(Context context, FileHolder[] fileHolders)
    {
        String paths[] =new String[fileHolders.length];
        for (int i=0; i < fileHolders.length; i++)
        {
            paths[i] = fileHolders[i].getFile().getAbsolutePath();
        }
        MediaScannerConnection.scanFile(
                context,
                paths,
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("grokkingandroid",
                                "file " + path + " was scanned seccessfully: " + uri);
                    }
                });
    }
}
