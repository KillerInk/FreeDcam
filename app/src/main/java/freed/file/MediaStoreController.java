package freed.file;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import freed.file.holder.BaseHolder;
import freed.file.holder.UriHolder;
import freed.utils.Log;
import freed.utils.StorageFileManager;

public class MediaStoreController {

    private final String TAG = MediaStoreController.class.getSimpleName();

    private Context context;

    public MediaStoreController(Context context)
    {
        this.context = context;
    }

    public Uri addImg(File name)
    {
        // Add a specific media item.
        ContentResolver resolver = context.getContentResolver();

        Uri extpath = getUri();

        // Publish a new img.
        ContentValues newImg = new ContentValues();
        newImg.put(MediaStore.MediaColumns.TITLE, name.getName());
        newImg.put(MediaStore.Images.Media.DISPLAY_NAME, name.getName());
        newImg.put(MediaStore.Images.Media.MIME_TYPE,"image/*");
        newImg.put(MediaStore.Images.Media.DATE_ADDED, (System.currentTimeMillis()/1000));
        newImg.put(MediaStore.Images.Media.DATE_TAKEN, (System.currentTimeMillis()/1000));
        newImg.put(MediaStore.Images.Media.DATE_MODIFIED, (System.currentTimeMillis()/1000));
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            newImg.put(MediaStore.Images.Media.DATA, name.getAbsolutePath());
        else {
            newImg.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/FreeDcam/");
        }
        Uri ur = resolver.insert(extpath, newImg);
        return ur;

    }

    public Uri addMovie(File name)
    {
        // Add a specific media item.
        ContentResolver resolver = context.getContentResolver();

        Uri extpath = getVideoUri();

        // Publish a new img.
        ContentValues newImg = new ContentValues();
        newImg.put(MediaStore.Video.Media.DISPLAY_NAME, name.getName());
        newImg.put(MediaStore.MediaColumns.TITLE, name.getName());
        newImg.put(MediaStore.Video.Media.MIME_TYPE,"video/*");
        newImg.put(MediaStore.Video.Media.DATE_ADDED, (System.currentTimeMillis() / 1000));
        newImg.put(MediaStore.Video.Media.DATE_TAKEN, (System.currentTimeMillis()/1000));
        newImg.put(MediaStore.Video.Media.DATE_MODIFIED, (System.currentTimeMillis()/1000));
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            newImg.put(MediaStore.Video.Media.DATA, name.getAbsolutePath());
        else {
            newImg.put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/FreeDcam/");
        }
        Uri ur = resolver.insert(extpath, newImg);
        return ur;

    }

    private Uri getUri() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            return MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        else
            return MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
    }

    private Uri getVideoUri() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            return MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        else
            return MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
    }


    public List<BaseHolder> getFolders()
    {
        List<BaseHolder> fileHolders = new ArrayList<>();
        List<String> folders = new ArrayList<>();
        getImageFolders(folders);
        getMovieFolders(folders);
        for (String name : folders)
            if (name != null)
                fileHolders.add(new UriHolder(null, name,0, 0,true,false));
        return fileHolders;
    }

    private void getImageFolders(List<String> folders) {
        String[] projection = new String[] {
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        };
        String sortOrder = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC";
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int foldername = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            //int idcol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

            while (cursor.moveToNext()) {
                // Get values of columns for a given image.
                //int id = cursor.getInt(idcol);
                String name = cursor.getString(foldername);
                if (!folders.contains(name))
                    folders.add(name);

                /*Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);*/

            }


        }
        catch (SecurityException ex)
        {
            Log.WriteEx(ex);
        }
    }

    private void getMovieFolders(List<String> folders) {
        String[] projection = new String[] {
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        };
        String sortOrder = MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " ASC";
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int foldername = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            //int idcol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

            while (cursor.moveToNext()) {
                // Get values of columns for a given image.
                //int id = cursor.getInt(idcol);
                String name = cursor.getString(foldername);
                if (!folders.contains(name))
                    folders.add(name);

                /*Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);*/

            }
        }
        catch (SecurityException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public List<BaseHolder> getFiles()
    {
        List<BaseHolder> fileHolders = new ArrayList<>();
        getImages(fileHolders);
        getMovies(fileHolders);
        return fileHolders;
    }

    private void getImages(List<BaseHolder> fileHolders) {
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
        };

        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int datetakenCol =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);

            int dateaddedCol =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

            Log.d(TAG,"Date Mod: " + dateaddedCol + " date added: " + dateaddedCol);

            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            //int relativPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);

            while (cursor.moveToNext()) {
                // Get values of columns for a given image.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                long datetaken  = cursor.getLong(datetakenCol);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                if (name != null)
                    fileHolders.add(new UriHolder(contentUri, name,id, datetaken,false,false));
            }

        }
        catch (SecurityException ex)
        {
            Log.WriteEx(ex);
        }
    }

    private void getMovies(List<BaseHolder> fileHolders) {
        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.SIZE,
        };

        String sortOrder = MediaStore.Video.Media.DATE_TAKEN + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int datetakenCol =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            //int relativPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);

            while (cursor.moveToNext()) {
                // Get values of columns for a given image.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                long datetaken  = cursor.getLong(datetakenCol);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                if (name != null)
                    fileHolders.add(new UriHolder(contentUri, name,id, datetaken,false,false));
            }

        }
        catch (SecurityException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public List<BaseHolder> getFilesFromFolder(String folder)
    {
        List<BaseHolder> fileHolders = new ArrayList<>();
        getImagesFromFolder(folder, fileHolders);
        getMoviesFromFolder(folder,fileHolders);
        return fileHolders;
    }

    private void getImagesFromFolder(String folder, List<BaseHolder> fileHolders) {
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        };

        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?";
        String selectionargs[] = {folder };

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionargs,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int datetakenCol =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int bucketdisplaynameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            //int relativPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);

            while (cursor.moveToNext()) {
                // Get values of columns for a given image.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                long datetaken  = cursor.getLong(datetakenCol);
                int size = cursor.getInt(sizeColumn);
                String bucketdisname = cursor.getString(bucketdisplaynameColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                if (name != null && folder.equals(bucketdisname))
                    fileHolders.add(new UriHolder(contentUri, name,id, datetaken,false,false));
            }
        }
        catch (SecurityException ex)
        {
            Log.WriteEx(ex);
        }
    }

    private void getMoviesFromFolder(String folder, List<BaseHolder> fileHolders) {
        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        };

        String sortOrder = MediaStore.Video.Media.DATE_MODIFIED + " DESC";
        String selection = MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " =?";
        String selectionargs[] = {folder };

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionargs,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int datetakenCol =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int bucketdisplaynameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            //int relativPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);

            while (cursor.moveToNext()) {
                // Get values of columns for a given image.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                long datetaken  = cursor.getLong(datetakenCol);
                int size = cursor.getInt(sizeColumn);
                String bucketdisname = cursor.getString(bucketdisplaynameColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                if (name != null && folder.equals(bucketdisname))
                    fileHolders.add(new UriHolder(contentUri, name,id, datetaken,false,false));
            }

        }
        catch (SecurityException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public String getNewFilePath(boolean externalSd, String fileEnding)
    {
        StringBuilder builder = new StringBuilder();

        if (fileEnding.equals(".jpg") || fileEnding.equals(".dng") || fileEnding.equals(".jps"))
            builder.append(File.separator).append("IMG_");
        if (fileEnding.equals(".mp4"))
            builder.append(File.separator).append("MOV_");
        builder.append(StorageFileManager.getStringDatePAttern().format(new Date()))
                .append(fileEnding);
        return builder.toString();
    }

    public String getNewFilePathBurst(boolean externalSd, String fileEnding, int hdrcount)
    {
        StringBuilder builder = new StringBuilder();
        if (fileEnding.equals(".jpg") || fileEnding.equals(".dng") || fileEnding.equals(".jps"))
            builder.append(File.separator).append("IMG_");
        if (fileEnding.equals(".mp4"))
            builder.append(File.separator).append("MOV_");
        Date date = new Date();
        builder.append(StorageFileManager.getStringDatePAttern().format(date));
        builder.append("_BURST" + hdrcount);
        builder.append(fileEnding);
        return builder.toString();
    }

    public void deleteFiles(List<BaseHolder> baseHolders)
    {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation contentProviderOperation;
        for (BaseHolder baseHolder: baseHolders)
        {
            UriHolder uriHolder = (UriHolder) baseHolder;
            contentProviderOperation = ContentProviderOperation.newDelete(uriHolder.getMediaStoreUri()).withSelection(MediaStore.Images.Media._ID +" = ?", new String[]{String.valueOf(uriHolder.getID())}).build();
            operationList.add(contentProviderOperation);
        }
        try {
            context.getContentResolver().applyBatch(MediaStore.AUTHORITY,operationList);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
