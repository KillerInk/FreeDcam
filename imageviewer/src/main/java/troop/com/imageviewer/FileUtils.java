package troop.com.imageviewer;

import java.io.File;
import java.util.List;

/**
 * Created by Ingo on 13.12.2015.
 */
public class FileUtils
{
    public static void readFilesFromFolder(File folder, List<FileHolder> list, GridViewFragment.FormatTypes formatsToShow)
    {
        File[] folderfiles = folder.listFiles();
        for (File f : folderfiles)
        {
            if (!f.isHidden())
            {
                if (formatsToShow == GridViewFragment.FormatTypes.all && (
                        f.getAbsolutePath().endsWith("jpg")
                                || f.getAbsolutePath().endsWith("jps")
                                ||f.getAbsolutePath().endsWith("raw")
                                ||f.getAbsolutePath().endsWith("dng")
                                ||   f.getAbsolutePath().endsWith("mp4")
                ))
                    list.add(new FileHolder(f));
                else if(formatsToShow == GridViewFragment.FormatTypes.dng && f.getAbsolutePath().endsWith("dng"))
                    list.add(new FileHolder(f));
                else if(formatsToShow == GridViewFragment.FormatTypes.raw && f.getAbsolutePath().endsWith("raw"))
                    list.add(new FileHolder(f));
                else if(formatsToShow == GridViewFragment.FormatTypes.jps && f.getAbsolutePath().endsWith("jps"))
                    list.add(new FileHolder(f));
                else if(formatsToShow == GridViewFragment.FormatTypes.jpg && f.getAbsolutePath().endsWith("jpg"))
                    list.add(new FileHolder(f));
                else if(formatsToShow == GridViewFragment.FormatTypes.mp4 && f.getAbsolutePath().endsWith("mp4"))
                    list.add(new FileHolder(f));
            }
        }
    }

    public static void readSubFolderFromFolder(File folder, List<File> folderList)
    {
        File[] folderfiles = folder.listFiles();
        for (File f : folderfiles)
        {
            if (f.isDirectory() && !f.isHidden())
                folderList.add(f);
        }
    }
}
