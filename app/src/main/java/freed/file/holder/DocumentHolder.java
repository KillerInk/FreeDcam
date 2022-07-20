package freed.file.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Size;

import androidx.documentfile.provider.DocumentFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import freed.FreedApplication;
import freed.jni.LibRawJniWrapper;
import freed.utils.Log;

public class DocumentHolder extends BaseHolder
{
    private final DocumentFile documentFile;
    public DocumentHolder(String name, long lastmodified, boolean isFolder, boolean isSDCard, DocumentFile documentFile) {
        super(name, lastmodified, isFolder, isSDCard);
        this.documentFile = documentFile;
    }

    public DocumentHolder(DocumentFile documentFile, boolean external) {
        super(documentFile.getName(), documentFile.lastModified(), documentFile.isDirectory(), external);
        this.documentFile = documentFile;
    }

    @Override
    public Class getHolderType() {
        return DocumentHolder.class;
    }

    @Override
    public Bitmap getBitmap(Context context, BitmapFactory.Options options) {
        Bitmap response = null;
        if (documentFile != null){
            try (ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(documentFile.getUri(), "r")) {
                response = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, options);
            } catch (IOException ex) {
                Log.WriteEx(ex);
            }
        }
        return response;
    }

    @Override
    public Bitmap getVideoThumb(Context context) throws IOException {
        Bitmap response = null;
        if (documentFile != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            response = context.getContentResolver().loadThumbnail(documentFile.getUri(),new Size(512, 384),null);
        return response;
    }

    @Override
    public Bitmap getBitmapFromDng(Context context) throws IOException {
        Bitmap response = null;
        if(documentFile != null) {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(documentFile.getUri(), "r");
            //detach it because we use it on jni side and close it there
            response = new LibRawJniWrapper().getBitmap(pfd.detachFd());
        }
        return response;
    }

    @Override
    public boolean delete(Context context) {
        return documentFile.delete();
    }

    @Override
    public boolean exists() {
        return documentFile.exists();
    }

    @Override
    public OutputStream getOutputStream() throws FileNotFoundException {
        return  FreedApplication.getContext().getContentResolver().openOutputStream(documentFile.getUri());
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return FreedApplication.getContext().getContentResolver().openInputStream(documentFile.getUri());
    }

    public DocumentFile getDocumentFile() {
        return documentFile;
    }
}
