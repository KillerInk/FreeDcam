package freed.viewer.screenslide;

import android.media.ExifInterface;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;

import freed.FreedApplication;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.file.holder.UriHolder;
import freed.image.ImageTask;
import freed.utils.Log;
import freed.viewer.screenslide.models.ExifViewModel;

public class ExifLoader extends ImageTask {

    private final BaseHolder file;
    private final ExifViewModel exifViewModel;

    public ExifLoader(BaseHolder file, ExifViewModel exifViewModel)
    {
        this.file = file;
        this.exifViewModel = exifViewModel;
    }

    @Override
    public boolean process() {
        ExifInterface exifInterface = null;
        try {
            //Log.d(TAG, "File: " + file.getName() + " DateModded: " + file.lastModified());
            if (file instanceof FileHolder && file.exists())
                exifInterface = new ExifInterface(((FileHolder)file).getFile().getAbsolutePath());
            else if (file instanceof UriHolder)
            {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && file.exists()) {
                    InputStream pfd = FreedApplication.getContext().getContentResolver().openInputStream(((UriHolder) file).getMediaStoreUri());
                    exifInterface = new ExifInterface(pfd);
                    pfd.close();
                }
            }

        } catch (IOException e) {
            Log.WriteEx(e);
        }
        catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            Log.WriteEx(ex);
        }
        if (exifInterface == null)
            return false;
        try {
            String expostring = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            if (expostring == null)
                exifViewModel.getShutter().setText("");
            else
            {
                exifViewModel.getShutter().setText("\uE00B" + getShutterStringSeconds(Double.parseDouble(expostring)));
            }
        }catch (NullPointerException e){
            Log.WriteEx(e);
            exifViewModel.getShutter().setText("");
        }
        try
        {
            String fnums = exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER);
            if (fnums != null)
                exifViewModel.getFnumber().setText("\ue003" + fnums);
            else
                exifViewModel.getFnumber().setText("");
        }catch (NullPointerException e){
            exifViewModel.getFnumber().setText("");
            Log.WriteEx(e);
        }
        try {
            String focs = exifInterface.getAttribute(ExifInterface.TAG_APERTURE_VALUE);
            if (focs == null)
            {
                exifViewModel.getFocal().setText("");
            }
            else {
                if (focs.contains("/"))
                {
                    String split[] = focs.split("/");
                    double numerator = Integer.parseInt(split[0]);
                    double denumerator = Integer.parseInt(split[1]);
                    double foc = numerator /denumerator;
                    focs = foc+"";
                }
                exifViewModel.getFocal().setText("\uE00c" + focs);
            }
        }catch (NullPointerException e){
            exifViewModel.getFocal().setText("");
            Log.WriteEx(e);
        }
        try {
            String isos = exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
            if (isos != null)
                exifViewModel.getIso().setText("\uE002" + isos);
            else
                exifViewModel.getIso().setText("");
        }catch (NullPointerException e){
            exifViewModel.getIso().setText("");
            Log.WriteEx(e);
        }
        return true;
    }

    private String getShutterStringSeconds(double val)
    {
        if (val >= 1) {
            return "" + (int)val;
        }
        int i = (int)(1 / val);
        return "1/" + Integer.toString(i);
    }
}
