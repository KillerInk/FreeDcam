package freed.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.file.holder.UriHolder;

/**
 * Created by troop on 25.08.13.
 */
public class MediaScannerManager
{
    public final static String TAG = MediaScannerManager.class.getSimpleName();
    public static void ScanMedia(Context context, BaseHolder file)
    {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        if (file instanceof FileHolder)
            intent.setData(Uri.fromFile(((FileHolder) file).getFile()));
        else if (file instanceof UriHolder)
            intent.setData(((UriHolder) file).getMediaStoreUri());
        context.sendBroadcast(intent);
    }

    public static void ScanMedia(Context context, File file)
    {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    public static void ScanMedia(Context context, BaseHolder[] fileHolders)
    {
        if (fileHolders == null || fileHolders.length == 0 || fileHolders[0] instanceof UriHolder)
            return;
        String paths[] =new String[fileHolders.length];
        for (int i=0; i < fileHolders.length; i++)
        {
            paths[i] = ((FileHolder)fileHolders[i]).getFile().getAbsolutePath();
        }
        MediaScannerConnection.scanFile(
                context,
                paths,
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v(TAG,
                                "file " + path + " was scanned: " + uri);
                    }
                });
    }
}
