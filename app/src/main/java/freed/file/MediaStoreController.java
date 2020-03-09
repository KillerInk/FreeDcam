package freed.file;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import freed.file.holder.BaseHolder;
import freed.file.holder.UriHolder;

public class MediaStoreController {



    private Context context;

    public MediaStoreController(Context context)
    {
        this.context = context;
    }

    public List<BaseHolder> getFolders()
    {
        List<BaseHolder> fileHolders = new ArrayList<>();
        getImageFolders(fileHolders);
        getMovieFolders(fileHolders);
        return fileHolders;
    }

    private void getImageFolders(List<BaseHolder> fileHolders) {
        List<String> folders = new ArrayList<>();
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
            for (String name : folders)
                if (name != null)
                    fileHolders.add(new UriHolder(null, name,0, 0,true,false));

        }
    }

    private void getMovieFolders(List<BaseHolder> fileHolders) {
        List<String> folders = new ArrayList<>();
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
            for (String name : folders)
                if (name != null)
                    fileHolders.add(new UriHolder(null, name,0, 0,true,false));

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
    }
}
