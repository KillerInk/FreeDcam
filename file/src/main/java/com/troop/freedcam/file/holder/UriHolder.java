package com.troop.freedcam.file.holder;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class UriHolder extends BaseHolder {

    private Uri mediaStoreUri;
    private long ID;

    public UriHolder(Context context,Uri uri,String name,long id, long lastmodified, boolean isFolder,boolean external) {
        super(context,name, lastmodified, isFolder,external);
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

    /*@Override
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
        if (mediaStoreUri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            response = context.getContentResolver().loadThumbnail(mediaStoreUri,new Size(512, 384),null);
        else if (mediaStoreUri != null)
            response = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),ID, MediaStore.Images.Thumbnails.MINI_KIND, null );
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
    }*/

    @Override
    public boolean delete() {

        Uri deluri = Uri.parse(mediaStoreUri.toString().replace("/"+String.valueOf(ID),""));
        int del = context.getContentResolver().delete(
                deluri,
                MediaStore.Images.Media._ID +" = ?",
                new String[]{String.valueOf(ID) });

        return del > 0;
    }

    @Override
    public boolean exists() {
        //TODO
        try(InputStream stream = context.getContentResolver().openInputStream(getMediaStoreUri())) {
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
           return false;
        }
        catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public OutputStream getOutputStream() throws FileNotFoundException {
        return context.getContentResolver().openOutputStream(mediaStoreUri);
    }

    public ParcelFileDescriptor getParcelFileDescriptor() throws FileNotFoundException {
        return context.getContentResolver().openFileDescriptor(mediaStoreUri, "rw");
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return context.getContentResolver().openInputStream(mediaStoreUri);
    }
}
