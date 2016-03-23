package troop.com.imageviewer.holder;

import java.io.File;
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
        for (File f : folderfiles) {
            if (!f.isHidden()) {
                if (formatsToShow == GridViewFragment.FormatTypes.all && (
                        f.getAbsolutePath().endsWith("jpg")
                                || f.getAbsolutePath().endsWith("jps")
                                || f.getAbsolutePath().endsWith("raw")
                                || f.getAbsolutePath().endsWith("dng")
                                || f.getAbsolutePath().endsWith("mp4")
                ))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.dng && f.getAbsolutePath().endsWith("dng"))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.raw && f.getAbsolutePath().endsWith("raw"))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.jps && f.getAbsolutePath().endsWith("jps"))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.jpg && f.getAbsolutePath().endsWith("jpg"))
                    list.add(new FileHolder(f));
                else if (formatsToShow == GridViewFragment.FormatTypes.mp4 && f.getAbsolutePath().endsWith("mp4"))
                    list.add(new FileHolder(f));
            }
        }
    }
}
