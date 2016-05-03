package troop.com.imageviewer.holder;

import com.troop.filelogger.Logger;
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
    private boolean isSDCard = false;

    public FileHolder(File file, boolean external)
    {
        this.file = file;
        if (file.isDirectory())
            this.isFolder=true;
        this.isSDCard = external;
    }

    public File getFile()
    {
        return file;
    }

    public boolean IsFolder()
    {
        return this.isFolder;
    }
    public boolean isExternalSD() { return isSDCard; }

    public static void readFilesFromFolder(File folder, List<FileHolder> list, GridViewFragment.FormatTypes formatsToShow, boolean external) {
        File[] folderfiles = folder.listFiles();
        if (folderfiles == null)
            return;
        for (File f : folderfiles) {
            if (!f.isHidden()) {
                if (formatsToShow == GridViewFragment.FormatTypes.all && (
                        f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG)
                                || f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4)
                ))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == GridViewFragment.FormatTypes.dng && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.DNG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == GridViewFragment.FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.RAW))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == GridViewFragment.FormatTypes.raw && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.BAYER))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == GridViewFragment.FormatTypes.jps && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPS))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == GridViewFragment.FormatTypes.jpg && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.JPG))
                    list.add(new FileHolder(f,external));
                else if (formatsToShow == GridViewFragment.FormatTypes.mp4 && f.getName().toLowerCase().endsWith(StringUtils.FileEnding.MP4))
                    list.add(new FileHolder(f,external));
            }
        }
        SortFileHolder(list);
    }

    public static List<FileHolder> getDCIMFiles()
    {
        List<FileHolder> f = new ArrayList<>();
        if (!StringUtils.IS_L_OR_BIG()) {
            File internal = new File(StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder);
            if (internal != null)
                Logger.d(TAG, "InternalSDPath:" + internal.getAbsolutePath());
            FileHolder.readFilesFromFolder(internal, f, GridViewFragment.FormatTypes.all, false);
            try {
                File fs = StringUtils.GetExternalSDCARD();
                if (fs != null && fs.exists()) {
                    File external = new File(fs + StringUtils.freedcamFolder);
                    if (external != null && external.exists())
                        Logger.d(TAG, "ExternalSDPath:" + external.getAbsolutePath());
                    else
                        Logger.d(TAG, "No ExternalSDFound");
                    FileHolder.readFilesFromFolder(external, f, GridViewFragment.FormatTypes.all, true);
                }
            } catch (NullPointerException ex) {
                Logger.e(TAG, "Looks like there is no External SD");
            }
        }
        else
        {
            List<FileHolder> dcims= getDCIMDirs();
            for (FileHolder fileHolder : dcims)
            {
                FileHolder.readFilesFromFolder(fileHolder.getFile(),f, GridViewFragment.FormatTypes.all, fileHolder.isExternalSD());
            }
        }

        SortFileHolder(f);
        return f;
    }

    public static List<FileHolder> getDCIMDirs()
    {
        ArrayList<FileHolder> list = new ArrayList<>();
        if (!StringUtils.IS_L_OR_BIG())
        {
            File internalSDCIM = new File(StringUtils.GetInternalSDCARD() + StringUtils.DCIMFolder);
            File[] f = internalSDCIM.listFiles();
            if (f != null) {
                for (File aF : f) {
                    if (!aF.isHidden())
                        list.add(new FileHolder(aF, false));
                }
            }
            try {
                File fs = StringUtils.GetExternalSDCARD();
                if (fs != null && fs.exists()) {
                    File externalSDCIM = new File(StringUtils.GetExternalSDCARD() + StringUtils.DCIMFolder);
                    f = externalSDCIM.listFiles();
                    for (File aF : f) {
                        if (!aF.isHidden())
                            list.add(new FileHolder(aF, true));
                    }
                }
            } catch (Exception ex) {
                Logger.d(TAG, "No external SD!");
            }
        }
        else
        {
            File[] files =  StringUtils.DIR_ANDROID_STORAGE.listFiles();
            boolean internalfound = false;
            boolean externalfound = false;
            for (File file : files)
            {
                if (file.getName().toLowerCase().equals("emulated"))
                {

                    File intDcim = new File(file.getAbsolutePath()+"/0/" + StringUtils.DCIMFolder);
                    if (intDcim.exists()  && !internalfound) {
                        internalfound = true;
                        list.add(new FileHolder(intDcim, false));
                    }
                    File extDcim = new File(file.getAbsolutePath()+"/1/" + StringUtils.DCIMFolder);
                    if (extDcim.exists() && !externalfound) {
                        externalfound = true;
                        list.add(new FileHolder(extDcim, true));
                    }
                }
                if (file.getName().toLowerCase().equals("sdcard0") && !internalfound)
                {
                    File intDcim = new File(file.getAbsolutePath() + StringUtils.DCIMFolder);
                    internalfound = true;
                    list.add(new FileHolder(intDcim, false));
                }
                if (file.getName().toLowerCase().equals("sdcard1") && !externalfound)
                {
                    File extDcim = new File(file.getAbsolutePath() + StringUtils.DCIMFolder);
                    if (extDcim.exists())
                    {
                        externalfound = true;
                        list.add(new FileHolder(extDcim, true));
                    }
                }
                if (!file.getName().toLowerCase().equals("emulated") && !file.getName().toLowerCase().equals("sdcard0") &&  !file.getName().toLowerCase().equals("sdcard1"))
                {
                    File extDcim = new File(file.getAbsolutePath() + StringUtils.DCIMFolder);
                    if (extDcim.exists())
                    {
                        externalfound = true;
                        list.add(new FileHolder(extDcim, true));
                    }
                }

            }
            ArrayList<FileHolder> subDcimFolders= new ArrayList<>();
            for (FileHolder dcim : list)
            {
                File[] subfolders = dcim.getFile().listFiles();
                if (subfolders != null)
                {
                    for (File f : subfolders)
                    {
                        if (!f.isHidden())
                            subDcimFolders.add(new FileHolder(f, dcim.isExternalSD()));
                    }
                }
            }
            list = subDcimFolders;

        }
        SortFileHolder(list);
        return list;
    }

    private static void SortFileHolder(List<FileHolder> f)
    {
        Collections.sort(f, new Comparator<FileHolder>() {
            public int compare(FileHolder f1, FileHolder f2) {
                return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
            }
        });
    }
}
