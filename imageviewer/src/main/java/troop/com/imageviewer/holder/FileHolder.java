package troop.com.imageviewer.holder;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.LocationParameter;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.FileUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import troop.com.imageviewer.gridviewfragments.GridViewFragment;

/**
 * Created by troop on 12.12.2015.
 *
 * This class represent the State of GridImageview when its added or not to Gridview and updates the
 * GridviewItem when its visibile/invisible on screen
 */
public class FileHolder extends BaseHolder
{
    private File file;
    private static final String TAG = FileHolder.class.getSimpleName();
    private boolean isFolder = false;

    public FileHolder(File file)
    {
        this.file = file;
        if (file.isDirectory())
            isFolder=true;
    }

    public File getFile()
    {
        return file;
    }

    public boolean IsFolder()
    {
        return isFolder;
    }

    public static void readFilesFromFolder(File folder, List<FileHolder> list, GridViewFragment.FormatTypes formatsToShow) {
        File[] folderfiles = folder.listFiles();
        if (folderfiles == null)
            return;
        for (File f : folderfiles) {
            if (!f.isHidden()) {
                if (formatsToShow == GridViewFragment.FormatTypes.all && (
                        f.getAbsolutePath().endsWith(StringUtils.FileEnding.JPG)
                                || f.getAbsolutePath().endsWith(StringUtils.FileEnding.JPS)
                                || f.getAbsolutePath().endsWith(StringUtils.FileEnding.RAW)
                                || f.getAbsolutePath().endsWith(StringUtils.FileEnding.BAYER)
                                || f.getAbsolutePath().endsWith(StringUtils.FileEnding.DNG)
                                || f.getAbsolutePath().endsWith(StringUtils.FileEnding.MP4)
                ))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.dng && f.getAbsolutePath().endsWith(StringUtils.FileEnding.DNG))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.raw && f.getAbsolutePath().endsWith(StringUtils.FileEnding.RAW))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.raw && f.getAbsolutePath().endsWith(StringUtils.FileEnding.BAYER))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.jps && f.getAbsolutePath().endsWith(StringUtils.FileEnding.JPS))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.jpg && f.getAbsolutePath().endsWith(StringUtils.FileEnding.JPS))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.mp4 && f.getAbsolutePath().endsWith(StringUtils.FileEnding.MP4))
                    list.add(new FileHolder(f));
            }
        }
        SortFileHolder(list);
    }

    public static List<FileHolder> getDCIMFiles()
    {
        List<FileHolder> f = new ArrayList<FileHolder>();
        File internal = new File(StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder);
        if (internal != null)
            Logger.d(TAG, "InternalSDPath:" + internal.getAbsolutePath());
        FileHolder.readFilesFromFolder(internal, f, GridViewFragment.FormatTypes.all);

      //  if(!DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.hasNoExtSD) || !DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV)) {
      //      File external = new File(StringUtils.GetExternalSDCARD() + StringUtils.freedcamFolder);
      //      if (external != null)
      //          Logger.d(TAG, "ExternalSDPath:" + external.getAbsolutePath());
    //        else
    //            Logger.d(TAG, "No ExternalSDFound");
     //       FileHolder.readFilesFromFolder(external, f, GridViewFragment.FormatTypes.all);
   //     }

        SortFileHolder(f);
        return f;
    }

    public static List<FileHolder> getDCIMDirs()
    {
        File internalSDCIM = new File(StringUtils.GetInternalSDCARD() + StringUtils.DCIMFolder);
        File externalSDCIM = new File(StringUtils.GetExternalSDCARD() + StringUtils.DCIMFolder);
        ArrayList<FileHolder> list = new ArrayList<FileHolder>();
        File[] f = internalSDCIM.listFiles();
        if (f != null)
        {
            for (int i = 0; i < f.length; i++) {
                if (!f[i].isHidden())
                    list.add(new FileHolder(f[i]));
            }
        }
        try {
            f = externalSDCIM.listFiles();
            for (int i = 0; i< f.length; i++)
            {
                if (!f[i].isHidden())
                    list.add(new FileHolder(f[i]));
            }
        }
        catch (Exception ex) {
            Logger.d(TAG, "No external SD!");
        }
        SortFileHolder(list);
        return list;
    }

    public static void SortFileHolder(List<FileHolder> f)
    {
        Collections.sort(f, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
            }
        });
    }
}
