package freed.file.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.IOException;

import freed.jni.RawUtils;

public class UriHolder extends BaseHolder {

    private Uri mediaStoreUri;
    private long ID;

    public UriHolder(Uri uri,String name,long id, long lastmodified, boolean isFolder,boolean external) {
        super(name, lastmodified, isFolder,external);
        this.mediaStoreUri = uri;
        this.ID = id;
    }

    public Uri getMediaStoreUri()
    {
        return mediaStoreUri;
    }

    public long getID()
    {
        return ID;
    }

    @Override
    public Bitmap getBitmap(Context context, BitmapFactory.Options options) {
        Bitmap response = null;
        if (mediaStoreUri != null){
            try (ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(mediaStoreUri, "r")) {
                response = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    public Bitmap getVideoThumb(Context context) throws IOException {
        Bitmap response = null;
        if (mediaStoreUri != null)
            response = context.getContentResolver().loadThumbnail(mediaStoreUri,null,null);
        return response;
    }

    @Override
    public Bitmap getBitmapFromDng(Context context) throws IOException {
        Bitmap response = null;
        if(mediaStoreUri != null) {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(mediaStoreUri, "r");
            response = new RawUtils().UnPackRAWFD(pfd.getFd());
            pfd.close();
        }
        return response;
    }

    @Override
    public boolean delete(Context context) {

        context.getContentResolver().delete(
                mediaStoreUri,
                MediaStore.Images.Media._ID +"=?",
                new String[]{String.valueOf(ID) });

        return true;
    }
}
